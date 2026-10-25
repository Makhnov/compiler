package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class Selection extends AbstractLValue {
    AbstractExpr object;
    AbstractIdentifier field;

    public Selection(AbstractExpr object, AbstractIdentifier field) {
        this.object = object;
        this.field = field;
    }

    @Override
    public boolean predictable() {
        return false;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.34)
        // Regle (3.65)
        // Regle (3.66)
        // Regle (3.70)
        Type class2Type = object.verifyExpr(compiler, localEnv, currentClass);
        if (!class2Type.isClass()) {
            throw new ContextualError("Fields Selections must be done on Classes", getLocation());
        }
        ClassDefinition class2Def = (ClassDefinition) compiler.environmentType.defOfType(class2Type.getName());
        if (class2Def == null) {
            throw new ContextualError("This Class is not declared", getLocation());
        }
        EnvironmentExp env2 = class2Def.getMembers();

        Type fieldType = field.verifyExpr(compiler, env2, currentClass);
        if (!field.getDefinition().isField()) {
            throw new ContextualError("Fields Selections must be Fields", getLocation());
        }
        FieldDefinition fieldDef = field.getFieldDefinition();
        Visibility visib = fieldDef.getVisibility();
        ClassDefinition fieldClass = fieldDef.getContainingClass();

        if (visib == Visibility.PROTECTED) {
            if (currentClass == null || !class2Type.isSubTypeOf(currentClass.getType()) || !currentClass.getType().isSubTypeOf(fieldClass.getType())) {
                throw new ContextualError("This Field is PROTECTED thus can't be reached", getLocation());
            }
        }
        setType(fieldType);
        return fieldType;
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        GPRegister GPReg = Register.getR(n);
        
        // On évalue l'objet directement
        object.codeGenExpr(compiler, n);

        // Check de non-nullité
        compiler.addInstruction(new CMP(new NullOperand(), GPReg));
        compiler.errors().nullDeref();

        // On remplace l'adresse de l'objet par la valeur du champ
        FieldDefinition fDef = field.getFieldDefinition();
        if (fDef == null) {
            throw new GenerationError("Selection.field definition is null (contextual pass missing?)", getLocation());
        }
        compiler.registers().load(new RegisterOffset(fDef.getIndex(), GPReg), GPReg);
    }

    @Override
    public void codeGenStore(DecacCompiler compiler, GPRegister valueReg) throws GenerationError {
        // On sauvegarde la valeur sur la pile pour libérer le registre
        compiler.stack().push(valueReg);

        // On calcule l'adresse de l'objet cible
        object.codeGenExpr(compiler, valueReg.getNumber());

        // Check de non-nullité
        compiler.addInstruction(new CMP(new NullOperand(), valueReg));
        compiler.errors().nullDeref();

        // On déplace l'adresse dans un registre scratch pour récupérer la valeur depuis la pile
        compiler.registers().load(valueReg, Register.R0);
        compiler.stack().pop(valueReg);

        // On écrit la valeur dans le champ adéquat (l'info est dans R0)
        FieldDefinition fDef = field.getFieldDefinition();
        if (fDef == null) {
            throw new GenerationError("Selection.field definition is null", getLocation());
        }

        compiler.addInstruction(new STORE(valueReg, new RegisterOffset(fDef.getIndex(), Register.R0)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        object.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        object.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        object.iter(f);
        field.iter(f);
    }
}
