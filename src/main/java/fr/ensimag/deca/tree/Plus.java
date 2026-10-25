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
import fr.ensimag.ima.pseudocode.instructions.ADD;

/**
 * AST node representing the addition operator (+).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "+";
    }

    @Override
    protected OptionalInt foldInt(int a, int b) {
        return OptionalInt.of(a + b);
    }

    @Override
    protected OptionalDouble foldFloat(double a, double b) {
        float r = (float) a + (float) b;
        if (Float.isInfinite(r) || Float.isNaN(r)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(r);
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();
        
        // Commutativité ?
        Type type = getType();
        DVal lValue = compiler.optis().commute(lhs, rhs, n);        
        if (lValue != null) {
            rhs.codeGenExpr(compiler, n);
            compiler.addInstruction(new ADD(lValue, Register.getR(n)));
            if (type.isFloat()) {
                compiler.errors().overflow();
            }
            return;
        }

        lhs.codeGenExpr(compiler, n);
        GPRegister target = Register.getR(n);
        
        DVal operand = codeGenRightOperand(compiler, n);
        
        compiler.addInstruction(new ADD(operand, target));
        if (getType().isFloat()) {
            compiler.errors().overflow();
        }
    }

    @Override
    protected boolean codeGenInPlace(DecacCompiler compiler, AbstractLValue target) throws GenerationError {

        // On vérifie que la destination est dans un registre
        GPRegister dest = target.getRegister(compiler);
        if (dest == null) {
            return false;
        }

        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        boolean lhsIsDest = compiler.registers().compare(lhs.getRegister(compiler), dest);
        boolean rhsIsDest = compiler.registers().compare(rhs.getRegister(compiler), dest);
        if (!lhsIsDest && !rhsIsDest) {
            return false;
        }

        // L'opérande qui n'est pas la destination
        AbstractExpr other = lhsIsDest ? rhs : lhs;

        // On vérifie que l'autre opérande n'a pas d'effet de bord
        if (!other.predictable()) {
            return false;
        }
        
        // Si l'autre opérande n'est pas dans un DVal, on la génère dans Rn
        DVal value = other.getDVal(compiler);
        if (value == null) {
            int n = compiler.registers().getScatchMin();
            if (dest.equals(Register.getR(n))) {
                return false;
            }
            other.codeGenExpr(compiler, n);
            value = Register.getR(n);
        }

        // On effectue l'addition et on identifie la destination cibcle comme ayant été récemment modifiée
        compiler.addInstruction(new ADD(value, dest));
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
            throw new UnsupportedOperationException("PLUS unsupported IRType: " + type);
        }

        IRScratch dst = ir.scratch(type);
        String name = (type == IRType.INT) ? "ADD_INT" : "ADD_FLOAT";
        ir.addInstruction(new IRBinaryExpr(IRLocation.from(getLocation()), name, dst, lhs, rhs));
        return dst;
    }
}