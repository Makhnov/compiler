package fr.ensimag.deca.tree;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    /**
     * Construct an arithmetic operator.
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     */
    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /* CONSTANT FOLDING */

    /**
     * Fold integer constant values at compile time.
     * @param left the left integer value
     * @param right the right integer value
     * @return the folded result, or empty if not foldable
     */
    protected abstract OptionalInt foldInt(int left, int right);

    /**
     * Fold float constant values at compile time.
     * @param left the left float value
     * @param right the right float value
     * @return the folded result, or empty if not foldable
     */
    protected abstract OptionalDouble foldFloat(double left, double right);

    @Override 
    public boolean predictable() {
        return getLeftOperand().predictable() && getRightOperand().predictable();
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) {
        if (!compiler.optis().medium()) {
            return null;
        }

        Type type = getType();
        if (type == null) {
            return null;
        }

        if (type.isInt()) {
            OptionalInt oi = getIntegerValue(compiler);
            return oi.isPresent() ? new ImmediateInteger(oi.getAsInt()) : null;
        }

        if (type.isFloat()) {
            OptionalDouble od = getFloatValue(compiler);
            return od.isPresent() ? new ImmediateFloat((float) (od.getAsDouble())) : null;
        }

        return null;
    }

    @Override
    public IRDVal getIRDVal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        if (!compiler.optis().medium()) {
            return null;
        }

        IRType type = getIRType();
        if (type == null) {
            return null;
        }

        if (type == IRType.INT) {
            OptionalInt oi = getIntegerValue(compiler);
            return oi.isPresent() ? new IRImmediateInt(oi.getAsInt()) : null;
        }

        if (type == IRType.FLOAT) {
            OptionalDouble od = getFloatValue(compiler);
            return od.isPresent() ? new IRImmediateFloat((float) od.getAsDouble()) : null;
        }
        return null;
    }

    @Override
    public OptionalInt getIntegerValue(DecacCompiler compiler) {
        if (!compiler.optis().medium()) {
            return OptionalInt.empty();
        }

        if (!predictable()) {
            return OptionalInt.empty();
        }

        Type type = getType();
        if (type == null || !type.isInt()) {
            return OptionalInt.empty();
        }

        OptionalInt lValue = getLeftOperand().getIntegerValue(compiler);
        OptionalInt rValue = getRightOperand().getIntegerValue(compiler);
        if (!lValue.isPresent() || !rValue.isPresent()) {
            return OptionalInt.empty();
        }

        return foldInt(lValue.getAsInt(), rValue.getAsInt());
    }

    @Override
    public OptionalDouble getFloatValue(DecacCompiler compiler) {
        if (!compiler.optis().medium()) {
            return OptionalDouble.empty();
        }

        if (!predictable()) {
            return OptionalDouble.empty();
        }
        
        Type type = getType();
        if (type == null || !type.isFloat()) {
            return OptionalDouble.empty();
        }

        OptionalDouble lValue = getLeftOperand().getFloatValue(compiler);
        OptionalDouble rValue = getRightOperand().getFloatValue(compiler);
        if (!lValue.isPresent() || !rValue.isPresent()) {
            return OptionalDouble.empty();
        }

        return foldFloat(lValue.getAsDouble(), rValue.getAsDouble());
    }

    /* CONSTANT FOLDING END */

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.33)
        // Regle (3.49)
        // Regle (3.50)
        // Regle (3.51)
        // Regle (3.52)
        // Regle (3.53)

        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        Type typeL = lhs.verifyExpr(compiler, localEnv, currentClass);
        Type typeR = rhs.verifyExpr(compiler, localEnv, currentClass);

        Type result = bothNumeric(compiler, typeL, typeR);
        if (result == null) {
            throw new ContextualError("Arithmetic operation operands must be numeric", getLocation());
        }

        if (result.isFloat()) {
            if (typeL.isInt()) {
                setLeftOperand(ConvFloat.implicitConvFloat(compiler, lhs));
            }
            if (typeR.isInt()) {
                setRightOperand(ConvFloat.implicitConvFloat(compiler, rhs));
            }
        }

        setType(result);
        return result;
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        throw new UnsupportedOperationException("irGenExprInternal not yet implemented for " + this.getClass().getSimpleName() + " @ " + String.valueOf(getLocation()));
    }
}