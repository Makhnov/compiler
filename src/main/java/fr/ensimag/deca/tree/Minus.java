package fr.ensimag.deca.tree;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * AST node representing the subtraction operator (-).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected OptionalInt foldInt(int a, int b) {
        return OptionalInt.of(a - b);
    }

    @Override
    protected OptionalDouble foldFloat(double a, double b) {
        float r = (float) a - (float) b;
        if (Float.isInfinite(r) || Float.isNaN(r)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(r);
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        getLeftOperand().codeGenExpr(compiler, n);
        GPRegister target = Register.getR(n);

        DVal operand = codeGenRightOperand(compiler, n);

        compiler.addInstruction(new SUB(operand, target));
        if (getType().isFloat()) {
            compiler.errors().overflow();
        }
    }

    @Override
    protected boolean codeGenInPlace(DecacCompiler compiler, AbstractLValue target) throws GenerationError {
        AbstractExpr rhs = getRightOperand();

        // On vérifie que rhs n'a pas d'effet de bord
        if (!rhs.predictable()) {
            return false;
        }

        // On vérifie que la destination est bien le registre cible
        GPRegister dest = target.getRegister(compiler);
        if (dest == null) {
            return false;
        }

        AbstractExpr lhs = getLeftOperand();
        
        // a = b - a
        if (!compiler.registers().compare(lhs.getRegister(compiler), dest)) {
            return false;
        }

        // (on ne gère que a = a - ...)
        DVal value = getRightOperand().getDVal(compiler);
        if (value == null) {
            int n = compiler.registers().getScatchMin();
            if (dest.equals(Register.getR(n))) {
                return false;
            }
            rhs.codeGenExpr(compiler, n);
            value = Register.getR(n);
        }

        compiler.addInstruction(new SUB(value, dest));
        if (getType().isFloat()) {
            compiler.errors().overflow();
        }

        target.modified(compiler);
        return true;
    }
    
    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDVal lhs = getLeftOperand().irGenExpr(compiler, ir);
        IRDVal rhs = getRightOperand().irGenExpr(compiler, ir);

        IRType type = getIRType();
        if (type != IRType.INT && type != IRType.FLOAT) {
            throw new UnsupportedOperationException("MINUS unsupported IRType: " + type);
        }

        IRScratch dst = ir.scratch(type);
        String name = (type == IRType.INT) ? "SUB_INT" : "SUB_FLOAT";
        ir.addInstruction(new IRBinaryExpr(IRLocation.from(getLocation()), name, dst, lhs, rhs));
        return dst;
    }    
}