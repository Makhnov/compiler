package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Abstract base class for field declarations.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractDeclField extends Tree {
    Visibility visibility;
    AbstractIdentifier fieldType;
    AbstractIdentifier fieldName;
    AbstractInitialization init;

    /**
     * Verify the field declaration (pass 2).
     * @param compiler the DECA compiler
     * @param classDef the class definition containing this field
     * @throws ContextualError if a contextual error is detected
     */
    public abstract void verifyDeclField(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError;

    /**
     * Verify the field initialization (pass 3).
     * @param compiler the DECA compiler
     * @param classEnv the class environment
     * @param currentClass the current class definition
     * @throws ContextualError if a contextual error is detected
     */
    public abstract void verifyDeclFieldInit(DecacCompiler compiler, EnvironmentExp classEnv, ClassDefinition currentClass) throws ContextualError;

    /**
     * Generate code for field initialization.
     * @param compiler the DECA compiler
     * @throws GenerationError if code generation fails
     */
    public abstract void codeGenFieldInit(DecacCompiler compiler) throws GenerationError;

    /**
     * Generate code for field declaration.
     * @param compiler the DECA compiler
     * @throws GenerationError if code generation fails
     */
    public abstract void codeGenField(DecacCompiler compiler) throws GenerationError;

    @Override
    protected void iterChildren(TreeFunction f) {
        fieldType.iter(f);
        fieldName.iter(f);
        init.iter(f);
    }

}
