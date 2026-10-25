package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Initialization (of variable, field, ...)
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractInitialization extends Tree {
    
    /**
     * Implements non-terminal "initialization" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param t corresponds to the "type" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass 
     *          corresponds to the "class" attribute (null in the main bloc).
     */
    protected abstract void verifyInitialization(DecacCompiler compiler, Type t, EnvironmentExp localEnv, ClassDefinition currentClass)
    throws ContextualError;

    /**
     * Generate code for the initialization, storing the value at the given address.
     * 
     * @param compiler
     * @param addr  Address where to store the initialized value.
     * @param t     Type of the variable being initialized.
     */
    protected abstract void codeGenInit(DecacCompiler compiler, DAddr addr, Type t) throws GenerationError;
    protected abstract void codeGenFieldInit(DecacCompiler compiler, int index, Type t) throws GenerationError;

    /**
     * generate IR code for the initialization, storing the value at the given address.
     * 
     * @param compiler
     * @param ir
     * @param addr
     * @param t
     * @throws GenerationError
     */
    protected abstract void irGenInit(DecacCompiler compiler, IRProgram ir, IRDAddr addr, IRType t) throws GenerationError;
}
