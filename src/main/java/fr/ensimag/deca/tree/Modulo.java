package fr.ensimag.deca.tree;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
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
import fr.ensimag.ima.pseudocode.instructions.REM;

/**
 * AST node representing the modulo operator (%).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected OptionalInt foldInt(int a, int b) {
        return b == 0 ? OptionalInt.empty() : OptionalInt.of(a % b);
    }

    @Override
    protected OptionalDouble foldFloat(double a, double b) {
        return OptionalDouble.empty();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type typeL = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type typeR = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!typeL.isInt() || !typeR.isInt()) {
            throw new ContextualError("Modulo Operator Only Takes Integers", getLocation());
        }

        this.setType(compiler.environmentType.INT);
        return compiler.environmentType.INT;
    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        // On génère le code de lhs
        lhs.codeGenExpr(compiler, n);
        GPRegister target = Register.getR(n);

        // On récupère un registre scratch pour l'utilisation de l'opération pow2
        int rmax = compiler.registers().getScatchMax();
        GPRegister scratch = (n < rmax) ? Register.getR(n + 1) : Register.R0;

        // On essaie d'optimiser le cas où le RHS est une puissance de 2
        if (compiler.optis().pow2Mod(target, rhs, scratch)) {
            return;
        }

        // Cas général
        DVal rhsOperand = codeGenRightOperand(compiler, n);
        compiler.addInstruction(new REM(rhsOperand, target));
        compiler.errors().divByZero();
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

        // On récupère un registre scratch pour l'utilisation de l'opération pow2
        GPRegister scratch = Register.R0;
        if (scratch.equals(dest)) {
            // Si dest==R0, on ne fait pas l'optimisation
            scratch = null;
        }

        if (scratch != null && compiler.optis().pow2Mod(dest, getRightOperand(), scratch)) {
            target.modified(compiler);
            return true;
        }

        DVal rVal = rhs.getDVal(compiler);

        // Fallback en place si RHS est une valeur directe
        if (rVal == null) {
            return false;
        }


        // Cas général
        compiler.addInstruction(new REM(rVal, dest));
        compiler.errors().divByZero();

        target.modified(compiler);
        return true;
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDVal lhs = getLeftOperand().irGenExpr(compiler, ir);
        IRDVal rhs = getRightOperand().irGenExpr(compiler, ir);

        IRType type = getIRType();
        if (type != IRType.INT) {
            throw new UnsupportedOperationException("MODULO expects INT, got: " + type);
        }

        IRScratch dst = ir.scratch(IRType.INT);
        ir.addInstruction(new IRBinaryExpr(IRLocation.from(getLocation()), "MOD_INT", dst, lhs, rhs));
        return dst;
    }
}
