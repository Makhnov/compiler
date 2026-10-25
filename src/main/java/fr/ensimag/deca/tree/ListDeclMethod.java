package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

public class ListDeclMethod extends TreeList<AbstractDeclMethod> {

    public void verifyListDeclMethod(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError {
        // Regle (2.6)
        for (AbstractDeclMethod m : this.getList()) {
            m.verifyDeclMethod(compiler, classDef);
        }
    }

    public void verifyListDeclMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp classEnv) throws ContextualError {
        // Regle (3.10)
        for (AbstractDeclMethod m : this.getList()) {
            m.verifyDeclMethodBody(compiler, currentClass, classEnv);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
        }
    }

    public void assignMethodLabels(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError {
        for (AbstractDeclMethod m : getList()) {
            m.assignMethodLabel(compiler, currentClass);
        }
    }

    public void generateMethodEntries(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError {
        for (AbstractDeclMethod m : getList()) {
            m.generateMethodEntry(compiler, currentClass);
        }
    }

    public void codeGenMethods(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError {
        for (AbstractDeclMethod m : getList()) {
            m.codeGenMethod(compiler, currentClass);
        }
    }
}
