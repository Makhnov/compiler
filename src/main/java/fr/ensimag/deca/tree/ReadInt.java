package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RINT;
import fr.ensimag.ima.pseudocode.Register;

import java.io.PrintStream;

/**
 * AST node representing the readInt() expression for reading an integer from input.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class ReadInt extends AbstractReadExpr {

    @Override
    public boolean predictable() {
        return false;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.35)
        IntType t = compiler.environmentType.INT; // Cf. IntLiteral (la vraie vérification se fait lors de l'assign)
        this.setType(t);
        return t;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        compiler.addInstruction(new RINT());
        compiler.errors().io();
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        compiler.addInstruction(new RINT());
        compiler.errors().io();        
        compiler.addInstruction(new LOAD(Register.R1, Register.getR(n)));
    }
    
@Override
    public void decompile(IndentPrintStream s) {
        s.print("readInt()");
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
