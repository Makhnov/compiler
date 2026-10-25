package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;

/**
 * AST node representing a boolean literal (true or false).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class BooleanLiteral extends AbstractExpr {

    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        return new ImmediateInteger(value ? 1 : 0);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.38)
        // Regle (3.47)
        Type t = compiler.environmentType.BOOLEAN;
        this.setType(t);
        return t;
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        if (value == b) {
            compiler.addInstruction(new BRA(label));
        }        
    }
    
    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        return new IRImmediateInt(value ? 1 : 0);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
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
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

}
