package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * AST node representing an empty statement (no operation).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class NoOperation extends AbstractInst {

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        // Regle (3.23)
        // Rien à faire ?
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        // C'était pas le code le plus dur !
        // throw new UnsupportedOperationException("codeGenInst not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        // C'était pas le code le plus dur !
    }

}
