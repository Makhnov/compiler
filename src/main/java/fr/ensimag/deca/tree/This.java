package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * AST node representing the 'this' keyword reference to the current object.
 *
 * @author gl25
 * @date 04/01/2026
 */
public class This extends AbstractExpr {
    private boolean impl = true;

    public This(boolean impl) {
        this.impl = impl;
    }

    @Override
    boolean implicit() {
        return impl;
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        // this est le premier paramètre, adresse -2(LB)
        return new RegisterOffset(-2, Register.LB);
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.43)
        if (currentClass != null) {
            Type classType = currentClass.getType();
            setType(classType);
            return classType;
        }
        throw new ContextualError("This can not be called in null class", getLocation());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (!impl) {
            s.print("this");
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // No children
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // No children
    }

}
