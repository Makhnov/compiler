package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Abstract base class for method declarations.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractDeclMethod extends Tree {
    AbstractIdentifier returnType;
    AbstractIdentifier methodName;
    ListDeclParam params;
    AbstractMethodBody body;

    @Override
    protected void iterChildren(TreeFunction f) {
        returnType.iter(f);
        methodName.iter(f);
        params.iter(f);
        body.iter(f);
    }

    /**
     * Verify the method declaration (pass 2).
     * @param compiler the DECA compiler
     * @param classDef the class definition containing this method
     * @throws ContextualError if a contextual error is detected
     */
    protected abstract void verifyDeclMethod(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError;

    /**
     * Verify the method body (pass 3).
     * @param compiler the DECA compiler
     * @param currentClass the current class definition
     * @param classEnv the class environment
     * @throws ContextualError if a contextual error is detected
     */
    protected abstract void verifyDeclMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp classEnv) throws ContextualError;

    /**
     * Assign a label for this method.
     * @param compiler the DECA compiler
     * @param currentClass the current class definition
     * @throws GenerationError if code generation fails
     */
    protected abstract void assignMethodLabel(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError;

    /**
     * Generate method entry code in the method table.
     * @param compiler the DECA compiler
     * @param currentClass the current class definition
     * @throws GenerationError if code generation fails
     */
    protected abstract void generateMethodEntry(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError;

    /**
     * Generate code for this method body.
     * @param compiler the DECA compiler
     * @param currentClass the current class definition
     * @throws GenerationError if code generation fails
     */
    protected abstract void codeGenMethod(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError;
}
