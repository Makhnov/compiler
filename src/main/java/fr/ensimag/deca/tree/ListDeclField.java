package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclField extends TreeList<DeclField> {

    public void verifyListDeclField(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError {
        // Regle (2.4)
        for (DeclField f : this.getList()) {
            f.verifyDeclField(compiler, classDef);
        }
    }

    public void verifyListDeclFieldInit(DecacCompiler compiler, EnvironmentExp classEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.6)
        for (DeclField f : this.getList()) {
            f.verifyDeclFieldInit(compiler, classEnv, currentClass);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (DeclField f : getList()) {
            f.decompile(s);
        }
    }

    public void codeGenFieldsInit(DecacCompiler compiler) throws GenerationError {
        for (DeclField f : getList()) {
            f.codeGenFieldInit(compiler);
        }
    }

    public void codeGenFields(DecacCompiler compiler) throws GenerationError {
        for (DeclField f : getList()) {
            f.codeGenField(compiler);
        }
    }

}
