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
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.QUO;

/**
 * AST node representing the division operator (/).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    protected OptionalInt foldInt(int a, int b) {
        return b == 0 ? OptionalInt.empty() : OptionalInt.of(a / b);
    }

    @Override
    protected OptionalDouble foldFloat(double a, double b) {
        float fb = (float) b;
        float fa = (float) a;
        if (fb == 0.0f) {
            return OptionalDouble.empty(); // Division par zéro
        }
        float r = fa / fb;
        if (Float.isInfinite(r) || Float.isNaN(r)) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(r);
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        // On génère LHS dans Rn
        getLeftOperand().codeGenExpr(compiler, n);
        GPRegister target = Register.getR(n);

        // Optimisation pow2 pour les ints
        if (getType().isInt() && compiler.optis().pow2Div(target, getRightOperand())) {
            return;
        }

        // Optimisation slash2 pour les floats
        if (getType().isFloat() && compiler.optis().slash2(target, getRightOperand())) {
            compiler.errors().overflow();
            return;
        }

        // Sinon, cas général
        DVal operand = codeGenRightOperand(compiler, n);

        if (getType().isFloat()) {
            compiler.addInstruction(new DIV(operand, target));
            compiler.errors().overflow();
        } else if (getType().isInt()) {
            compiler.addInstruction(new QUO(operand, target));
            compiler.errors().divByZero();
        } else {
            Type t = getType();
            throw new UnsupportedOperationException("Division must be of type int or float, not: " + t);
        }
    }

    @Override
    protected boolean codeGenInPlace(DecacCompiler compiler, AbstractLValue target) throws GenerationError {
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        // On vérifie que rhs n'a pas d'effet de bord
        if (!rhs.predictable()) {
            return false;
        }

        // On vérifie que la destination est dans un registre
        GPRegister dest = target.getRegister(compiler);
        if (!compiler.registers().compare(lhs.getRegister(compiler), dest)) {
            return false;
        }

        // Optimisation pow2 (runtime-proof) : gère aussi le cas dest<0 via QUO #2^k
        if (compiler.optis().pow2Div(dest, getRightOperand())) {
            target.modified(compiler);
            return true;
        }

        // Fallback in-place uniquement si RHS est directement adressable
        DVal rVal = rhs.getDVal(compiler);
        if (rVal == null) {
            return false;
        }

        if (getType().isInt()) {
            compiler.addInstruction(new QUO(rVal, dest));
            compiler.errors().divByZero();
        } else if (getType().isFloat()) {
            compiler.addInstruction(new DIV(rVal, dest));
            compiler.errors().overflow();
        } else {
            Type t = getType();
            throw new UnsupportedOperationException("Division must be of type int or float, not: " + t);
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
            throw new UnsupportedOperationException("DIVIDE unsupported IRType: " + type);
        }

        IRScratch dst = ir.scratch(type);
        String name = (type == IRType.INT) ? "DIV_INT" : "DIV_FLOAT";
        ir.addInstruction(new IRBinaryExpr(IRLocation.from(getLocation()), name, dst, lhs, rhs));
        return dst;
    }    
}
