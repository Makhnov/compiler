package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Absence of initialization (e.g. "int x;" as opposed to "int x =
 * 42;").
 *
 * @author gl25
 * @date 01/01/2026
 */
public class NoInitialization extends AbstractInitialization {

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t, EnvironmentExp localEnv, ClassDefinition currentClass)
    throws ContextualError {
        // Regle (3.9)
        // Rien à faire ?
    }

    @Override
    protected void codeGenInit(DecacCompiler compiler, DAddr addr, Type t) throws GenerationError{
        // nothing
    }
   
    @Override
    protected void codeGenFieldInit(DecacCompiler compiler, int fieldIndex, Type t) throws GenerationError {
        // nothing
    }

   
    @Override
    protected void irGenInit(DecacCompiler compiler, IRProgram ir, IRDAddr addr, IRType t) throws GenerationError {
        // nothing
    }
    
    /**
     * Node contains no real information, nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
