package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.ir.IRProgram;

/**
 * Main block of a Deca program.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractMain extends Tree {

    /**
     * Generate code for the main block.
     * @param compiler the DECA compiler
     * @throws GenerationError if code generation fails
     */
    protected abstract void codeGenMain(DecacCompiler compiler) throws GenerationError;

    /**
     * Generate IR for the main block.
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @throws GenerationError if code generation fails
     */
    protected abstract void irGenMain(DecacCompiler compiler, IRProgram ir) throws GenerationError;

    /**
     * Implements non-terminal "main" of [SyntaxeContextuelle] in pass 3.
     * @param compiler the DECA compiler
     * @throws ContextualError if a contextual error is detected
     */
    protected abstract void verifyMain(DecacCompiler compiler) throws ContextualError;


        
}
