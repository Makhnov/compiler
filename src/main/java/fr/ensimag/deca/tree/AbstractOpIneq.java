package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Abstract base class for inequality comparison operators ({@literal <}, {@literal >}, {@literal <=}, {@literal >=}).
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractOpIneq extends AbstractOpCmp {

    /**
     * Construct an inequality comparison operator.
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     */
    public AbstractOpIneq(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        Type typeL = lhs.verifyExpr(compiler, localEnv, currentClass);
        Type typeR = rhs.verifyExpr(compiler, localEnv, currentClass);

        Type result = bothNumeric(compiler, typeL, typeR);
        if (result == null) {
            throw new ContextualError("Inequality operator can only be applied to numeric values", getLocation());
        }
        if (result.isFloat()) {
            if (typeL.isInt()) {
                setLeftOperand(ConvFloat.implicitConvFloat(compiler, lhs));
            }
            if (typeR.isInt()) {
                setRightOperand(ConvFloat.implicitConvFloat(compiler, rhs));
            }
        }

        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }
}
