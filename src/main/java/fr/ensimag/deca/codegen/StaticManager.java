package fr.ensimag.deca.codegen;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.MethodDefinition;

/**
 * Gestionnaire de la mémoire statique pour les tables de méthodes
 * 
 * @author Nico
 * @date 18/01/2026
 * 
 */
public final class StaticManager {
    private final DecacCompiler compiler;

    private boolean VTable = false;
    private int VTableSize;

    private final Map<ClassDefinition, Integer> VTOffsets = new HashMap<>();
    private int nextIndex;

    public StaticManager(DecacCompiler compiler) {
        this.compiler = compiler;
    }

    public void beginVtable() {
        Validate.isTrue(!VTable, "There is already a VTable in progress");
        VTOffsets.clear();
        nextIndex = 1;
    }

    public void endVtable() {
        Validate.isTrue(!VTable, "There is already a VTable in progress");
        VTableSize = nextIndex - 1;
        VTable = true;
    }

    public int size() {
        Validate.isTrue(VTable, "No ongoing VTable actually running");
        return VTableSize;
    }

    public int offset() {
        Validate.isTrue(VTable, "No ongoing VTable actually running");
        return 1 + VTableSize;
    }

    public DAddr getVTAddr(ClassDefinition cDef) throws GenerationError {
        int index = getIndex(cDef);
        DAddr addr = new RegisterOffset(index, Register.GB);
        return addr;
    }

    public void assignClass(ClassDefinition cDef) throws GenerationError {
        Validate.notNull(cDef);
        if (VTOffsets.containsKey(cDef)) {
            return;
        }

        int n = cDef.getNumberOfMethods();
        if (n < 0) {
            throw new GenerationError("Negative numberOfMethods for " + cDef.getType().getName().getName(), Location.BUILTIN);
        }

        int index = nextIndex;
        VTOffsets.put(cDef, index);
        cDef.setAddress(new RegisterOffset(index, Register.GB));

        nextIndex += 1 + n;
    }

    public void generateObjectTable(ClassDefinition objDef, MethodDefinition equalsDef) throws GenerationError {
        int index = getIndex(objDef);

        // slot 0 = null (pas de super)
        store(new NullOperand(), index);

        // equals est index 1
        store(new LabelOperand(equalsDef.getLabel()), index + equalsDef.getIndex());
    }

    public void codeGenObjectMethods(MethodDefinition eqDef) throws GenerationError {
        Validate.notNull(eqDef, "equals MethodDefinition cannot be null");
        Validate.notNull(eqDef.getLabel(), "equals label must be set");

        IMAProgram block = compiler.pushNewBlock();
        compiler.stack().reset();

        try {
            // Labels internes
            int id = compiler.labels().getLabelID();
            Label lTrue = compiler.labels().label("E_Equals", id);
            Label lEnd  = compiler.labels().labelFin(id);

            // Registre scratch
            int n = compiler.registers().getScatchMin();
            compiler.registers().check(n);
            GPRegister GPReg = Register.getR(n);

            // On sauvegarde le registre scratch
            compiler.stack().push(GPReg);

            // on charge this
            compiler.registers().load(new RegisterOffset(-2, Register.LB), GPReg);

            // compare param0 (Object) avec this (param0 = -3(LB))
            compiler.addInstruction(new CMP(new RegisterOffset(-3, Register.LB), GPReg));
            compiler.addInstruction(new BEQ(lTrue));

            // branchement si faux
            compiler.registers().load(new ImmediateInteger(0), Register.R0);
            compiler.addInstruction(new BRA(lEnd));

            // branchement si vrai
            compiler.addLabel(lTrue);
            compiler.registers().load(new ImmediateInteger(1), Register.R0);

            // sortie unique (cf. prologue)
            compiler.addLabel(lEnd);
            compiler.stack().pop(GPReg);
            compiler.addInstruction(new RTS());

            // Prologue TSTO/BOV (on connait maintenant la taille nécessaire)
            int size = compiler.stack().size();
            compiler.errors().stack(size);
            compiler.addFirstLabel(eqDef.getLabel());

        } finally {
            // On s'assure que le bloc en cours d'écriture est bien "pop" par le compilateur à la fin de la génération quoi qu'il arrive
            compiler.popBlock(block);
        }
    }

    public void beginClassTable(ClassDefinition cDef, ClassDefinition superDef) throws GenerationError {
        int index = getIndex(cDef);

        if (superDef == null) {
            // cas Object uniquement
            store(new NullOperand(), index);
            return;
        }

        int indexSuper = getIndex(superDef);

        // On copie l'adresse de la table mère dans R0
        compiler.addInstruction(new LEA(new RegisterOffset(indexSuper, Register.GB), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index, Register.GB)));

        // On copie les registres de la table mère
        int superN = superDef.getNumberOfMethods();
        for (int i = 1; i <= superN; i++) {
            compiler.addInstruction(new LOAD(new RegisterOffset(indexSuper + i, Register.GB), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index + i, Register.GB)));
        }
    }

    // Permet de copier une méthode dans la table de la classe courante
    public void setMethodEntry(ClassDefinition cDef, MethodDefinition mDef) throws GenerationError {
        int index = getIndex(cDef);
        store(new LabelOperand(mDef.getLabel()), index + mDef.getIndex());
    }

    // Récupère l'index de la classe dans la table des offsets
    public int getIndex(ClassDefinition cDef) throws GenerationError {
        if (!VTOffsets.containsKey(cDef)) {
            throw new GenerationError("Class " + cDef.getType().getName().getName() + " not assigned in static table", Location.BUILTIN);
        }
        return VTOffsets.get(cDef);
    }

    // On stocke une valeur DVal à l'index i de la table des méthodes
    private void store(DVal v, int i) {
        compiler.addInstruction(new LOAD(v, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(i, Register.GB)));
    }
}
