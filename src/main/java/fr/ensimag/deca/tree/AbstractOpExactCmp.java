package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Abstract base class for exact comparison operators (==, !=).
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    /**
     * Construct an exact comparison operator.
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     */
    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.33)
        // Regle (3.56)
        // Regle (3.57)
        // Regle (3.58)
        // Regle (3.59)
        // Regle (3.60)
        // Regle (3.61)

        /* Regles :
        1. boolean x boolean -> boolean
        2. int x int -> boolean
        3. float x float -> boolean
        4. int x float -> boolean + conversion (et vice versa)
        5. class x class -> boolean
        6. class x null -> boolean (et vice versa)
        7. null x null -> boolean
        8. string x string -> refusé (à vérifier)
        9. autres types -> refusé
        */

        // Opérandes
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        // Types
        Type typeL = lhs.verifyExpr(compiler, localEnv, currentClass);
        Type typeR = rhs.verifyExpr(compiler, localEnv, currentClass);

        // 0. Validité de la comparaison
        boolean valid = false;

        // 2. 3. 4.
        Type result = bothNumeric(compiler, typeL, typeR);
        if (result != null) {
            valid = true;
            if (result.isFloat()) {
                if (typeL.isInt()) {
                    setLeftOperand(ConvFloat.implicitConvFloat(compiler, lhs));
                }
                if (typeR.isInt()) {
                    setRightOperand(ConvFloat.implicitConvFloat(compiler, rhs));
                }
            }
        }

        // 1.
        else if (bothBoolean(typeL, typeR)) {
            valid = true;
        }

        // 5. 6. 7.
        else if (bothClassOrNull(typeL, typeR)) {
            valid = true;
            // throw new ContextualError("Comparaison d'objets pas encore implémentée", getLocation());
        }

        // 8. 9.
        if (!valid) {
            throw new ContextualError("Incompatible types for " + getOperatorName() + ": " + typeL + " and " + typeR, getLocation());
        }

        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }
}
