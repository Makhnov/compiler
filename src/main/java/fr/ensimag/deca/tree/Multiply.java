package fr.ensimag.deca.tree;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.MUL;

/**
 * AST node representing the multiplication operator (*).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected OptionalInt foldInt(int a, int b) {
        return OptionalInt.of(a * b);
    }

    @Override
    protected OptionalDouble foldFloat(double a, double b) {
        float r = (float) a * (float) b;
        if (Float.isInfinite(r) || Float.isNaN(r)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(r);
    }

    @Override
    protected String getOperatorName() {
        return "*";
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        // Optim pow2 sur int, en tenant compte de la commutativité
        if (getType().isInt()) {
            // cas a * 2^k
            if (compiler.optis().powerOf2Expr(rhs)) {
                lhs.codeGenExpr(compiler, n);
                GPRegister target = Register.getR(n);
                if (compiler.optis().pow2Mul(target, rhs)) {
                    return;
                }
            }
            // cas 2^k * a
            else if (compiler.optis().powerOf2Expr(lhs)) {
                rhs.codeGenExpr(compiler, n);
                GPRegister target = Register.getR(n);
                if (compiler.optis().pow2Mul(target, lhs)) {
                    return;
                }
            }
        }
        
        // Commutativité
        Type type = getType();
        DVal lValue = compiler.optis().commute(lhs, rhs, n);        
        if (lValue != null) {
            rhs.codeGenExpr(compiler, n);
            compiler.addInstruction(new MUL(lValue, Register.getR(n)));
            if (type.isFloat()) {
                compiler.errors().overflow();
            }
            return;
        }
        
        // Cas général

        // On génère LHS dans Rn
        lhs.codeGenExpr(compiler, n);
        GPRegister target = Register.getR(n);

        // On génère RHS sans écraser LHS (avec scratching si besoin)
        DVal operand = codeGenRightOperand(compiler, n);

        // On effectue la multiplication
        compiler.addInstruction(new MUL(operand, target));
        if (getType().isFloat()) {
            compiler.errors().overflow();
        }
    }

    @Override
    protected boolean codeGenInPlace(DecacCompiler compiler, AbstractLValue target) throws GenerationError {
        GPRegister dest = target.getRegister(compiler);
        if (dest == null) {
            return false;
        }

        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        // In-place possible si dest correspond à l'un des deux opérandes (commutativité)
        GPRegister lhsReg = lhs.getRegister(compiler);
        GPRegister rhsReg = rhs.getRegister(compiler);

        boolean lhsIsDest = (lhsReg != null && lhsReg.equals(dest));
        boolean rhsIsDest = (rhsReg != null && rhsReg.equals(dest));

        if (!lhsIsDest && !rhsIsDest) {
            return false;
        }

        // other = l'autre opérande (celle qui n'est pas dans dest)
        AbstractExpr other = lhsIsDest ? rhs : lhs;

        // On vérifie que l'autre opérande n'a pas d'effet de bord
        if (!other.predictable()) {
            return false;
        }

        // Optimisation pow2 (pour les ints)
        if (getType().isInt() && compiler.optis().pow2Mul(dest, other)) {
            target.modified(compiler);
            return true;
        }

        // Fallback si "other" est directement un DVal
        DVal otherVal = other.getDVal(compiler);
        if (otherVal == null) {
            return false;
        }

        // Multiplication en place
        compiler.addInstruction(new MUL(otherVal, dest));
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
            throw new UnsupportedOperationException("MULTIPLY unsupported IRType: " + type);
        }

        IRScratch dst = ir.scratch(type);
        String name = (type == IRType.INT) ? "MUL_INT" : "MUL_FLOAT";
        ir.addInstruction(new IRBinaryExpr(IRLocation.from(getLocation()), name, dst, lhs, rhs));
        return dst;
    }
}
