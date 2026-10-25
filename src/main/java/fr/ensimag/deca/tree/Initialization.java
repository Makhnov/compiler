package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * AST node representing a variable initialization with an expression.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.8)
        AbstractExpr verifiedExpr = expression.verifyRValue(compiler, localEnv, currentClass, t);
        setExpression(verifiedExpr);
    }

    @Override
    protected void codeGenInit(DecacCompiler compiler, DAddr addr, Type t) throws GenerationError {
        int n = compiler.registers().getScatchMin();
        expression.codeGenExpr(compiler, n);
        compiler.addInstruction(new STORE(Register.getR(n), addr));
    }

    @Override
    protected void codeGenFieldInit(DecacCompiler compiler, int index, Type t) throws GenerationError {
        int n = compiler.registers().getScatchMin();
        expression.codeGenExpr(compiler, n); 

        GPRegister GPReg = Register.getR(n);
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
        compiler.addInstruction(new STORE(GPReg, new RegisterOffset(index, Register.R1)));
    }

    @Override
    protected void irGenInit(DecacCompiler compiler, IRProgram ir, IRDAddr addr, IRType t) throws GenerationError {
        IRDVal rhs = expression.irGenExpr(compiler, ir);
        ir.addInstruction(new IRAssign(IRLocation.from(getLocation()), rhs, addr));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("decompile not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
        if (this.expression != null) {
            s.print(" = ");
            this.expression.decompile(s);
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
