package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.ir.IRProgram;

/**
 * Entry point for contextual verifications and code generation from outside the package.
 * 
 * @author gl25
 * @date 01/01/2026
 *
 */
public abstract class AbstractProgram extends Tree {
    /**
     * Verify the program for contextual errors.
     * @param compiler the DECA compiler
     * @throws ContextualError if a contextual error is detected
     */
    public abstract void verifyProgram(DecacCompiler compiler) throws ContextualError;

    /**
     * Generate code for the entire program.
     * @param compiler the DECA compiler
     * @throws GenerationError if code generation fails
     */
    public abstract void codeGenProgram(DecacCompiler compiler) throws GenerationError;

    /**
     * Generate IR for the entire program.
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @throws GenerationError if code generation fails
     */
    public abstract void irGenProgram(DecacCompiler compiler, IRProgram ir) throws GenerationError;
}
