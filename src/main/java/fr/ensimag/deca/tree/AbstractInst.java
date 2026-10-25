package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Instruction
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractInst extends Tree {
    
    /**
     * Implements non-terminal "inst" of [SyntaxeContextuelle] in pass 3.
     *
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute (null in the main bloc)
     * @param returnType corresponds to the "return" attribute (void in the main bloc)
     * @throws ContextualError if a contextual error is detected
     */
    protected abstract void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType)
            throws ContextualError;

    /**
     * Generate assembly code for the instruction.
     *
     * @param compiler the DECA compiler
     * @throws GenerationError if code generation fails
     */
    protected abstract void codeGenInst(DecacCompiler compiler) throws GenerationError;

    /**
     * Generate IR code for this instruction.
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @throws GenerationError if code generation fails
     */
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        throw new UnsupportedOperationException("irGenInst not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Generate assembly code for the expression (in register n).
     *
     * @param compiler the DECA compiler
     * @param n the target register number
     * @throws GenerationError if code generation fails
     */
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        throw new UnsupportedOperationException("codeGenExprInternal not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Generate assembly code for the expression (wrapper).
     * @param compiler the DECA compiler
     * @param n the target register number
     * @throws GenerationError if code generation fails
     */
    protected void codeGenExpr(DecacCompiler compiler, int n) throws GenerationError {
        throw new UnsupportedOperationException("codeGenExpr not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Generate IR code for the expression.
     *
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @return the IR value representing the expression result
     * @throws GenerationError if code generation fails
     */
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        throw new UnsupportedOperationException("irGenExprInternal not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Generate IR code for the expression (wrapper).
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @return the IR value representing the expression result
     * @throws GenerationError if code generation fails
     */
    protected IRDVal irGenExpr(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        throw new UnsupportedOperationException("irGenExpr not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Generate IR code for a branch instruction based on the value of the expression.
     *
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @param b branch condition (true=branch if non-zero, false=branch if zero)
     * @param label target IR label for the branch
     * @throws GenerationError if code generation fails
     */
    protected void irGenBranch(DecacCompiler compiler, IRProgram ir, boolean b, IRLabel label) throws GenerationError {
        throw new UnsupportedOperationException("irGenBranch not yet implemented for " + this.getClass().getSimpleName() + " @ " + String.valueOf(getLocation()));
    }
 
    /**
     * Decompile the tree, considering it as an instruction.
     * In most cases, this simply calls decompile(), but it may add a semicolon if needed.
     *
     * @param s the output stream for decompilation
     */
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
    }
}
