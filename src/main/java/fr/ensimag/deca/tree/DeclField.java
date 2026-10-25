package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class DeclField extends AbstractDeclField {

    public DeclField(Visibility visibility, AbstractIdentifier fieldType, AbstractIdentifier fieldName, AbstractInitialization init) {
        this.visibility = visibility;
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.init = init;
    }

    @Override
    public void verifyDeclField(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError {
        // Regle (2.5)
        Type t = fieldType.verifyType(compiler);
        if (t.isVoid()) {
            throw new ContextualError("Field " + fieldName.getName().getName() + " cannot be of type void", getLocation());
        }
        ClassDefinition superDef = classDef.getSuperClass();
        // si shadowing d'un champ de la superClasse
        ExpDefinition superField = null;
        if (superDef != null) {
            superField = superDef.getMembers().get(fieldName.getName());
        }

        if (superField != null && !superField.isField()) {
            throw new ContextualError("The Overriden Field " + fieldName.getName().getName() + " was not a field", getLocation());
        } // TODO verif le concept de shadowing

        int index = classDef.incNumberOfFields();
        FieldDefinition fieldDef = new FieldDefinition(t, getLocation(), visibility, classDef, index);

        try {
            classDef.getMembers().declare(fieldName.getName(), fieldDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Field " + fieldName.getName().getName() + " already defined", getLocation());
        }

        fieldName.setDefinition(fieldDef);
        fieldName.setType(t);
    }

    public void verifyDeclFieldInit(DecacCompiler compiler, EnvironmentExp classEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.7)
        Type type = fieldType.verifyType(compiler);
        init.verifyInitialization(compiler, type, classEnv, currentClass);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (visibility == Visibility.PROTECTED) {
            s.print("protected");
        }
        s.print(" ");
        fieldType.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        init.decompile(s);
        s.println(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        fieldType.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        init.prettyPrint(s, prefix, true);
    }

    @Override
    protected String prettyPrintNode() {
        return "[visibility=" + this.visibility + "] DeclField";
    }

    @Override
    public void codeGenFieldInit(DecacCompiler compiler) throws GenerationError {
        // On récupère la position de la variable et sa valeur par défaut
        FieldDefinition fDef = fieldName.getFieldDefinition();
        int idx = fDef.getIndex();
        DVal dv = defaultValue(fDef.getType());

        // On écrit la valeur par défaut directement
        compiler.registers().load(new RegisterOffset(-2, Register.LB), Register.R1);
        compiler.registers().load(dv, Register.R0);
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(idx, Register.R1)));
    }

    private DVal defaultValue(Type t) {
        if (t.isFloat()) {
            return new ImmediateFloat(0.0f);
        }
        if (t.isClassOrNull()) {
            return new NullOperand();
        }
        // int / boolean
        return new ImmediateInteger(0);
    }

    @Override
    public void codeGenField(DecacCompiler compiler) throws GenerationError {
        FieldDefinition fDef = fieldName.getFieldDefinition();
        init.codeGenFieldInit(compiler, fDef.getIndex(), fDef.getType());
    }
}

