package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Abstract base class for method bodies.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractMethodBody extends Tree {
    ListDeclVar vars;
    ListInst insts;

    /**
     * Verify the method body for contextual errors.
     * @param compiler the DECA compiler
     * @param currentClass the current class definition
     * @param localEnv the local environment
     * @param returnType the expected return type
     * @throws ContextualError if a contextual error is detected
     */
    public abstract void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv, Type returnType) throws ContextualError;

    /**
     * Generate code for the method body.
     * @param compiler the DECA compiler
     * @throws GenerationError if code generation fails
     */
    public abstract void codeGenMethod(DecacCompiler compiler) throws GenerationError;

    /**
     * Get the stack size required for this method.
     * @return the stack size in words
     */
    public abstract int size();

    @Override
    protected void iterChildren(TreeFunction f) {
        vars.iter(f);
        insts.iter(f);
    }
}
