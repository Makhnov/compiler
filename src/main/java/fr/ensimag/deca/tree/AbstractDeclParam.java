package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Abstract base class for parameter declarations.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractDeclParam extends Tree {
    AbstractIdentifier paramType;
    AbstractIdentifier paramName;

    @Override
    protected void iterChildren(TreeFunction f) {
        paramType.iter(f);
        paramName.iter(f);
    }

    /**
     * Verify the parameter declaration.
     * @param compiler the DECA compiler
     * @return the type of the parameter
     * @throws ContextualError if a contextual error is detected
     */
    protected abstract Type verifyDeclParam(DecacCompiler compiler) throws ContextualError;

    /**
     * Verify the parameter initialization in method environment.
     * @param compiler the DECA compiler
     * @param methodEnv the method environment
     * @throws ContextualError if a contextual error is detected
     */
    protected abstract void verifyDeclParamInit(DecacCompiler compiler, EnvironmentExp methodEnv) throws ContextualError;

    /**
     * Get the type of this parameter.
     * @return the parameter type
     */
    public Type getType() {
        return paramType.getType();
    }

}
