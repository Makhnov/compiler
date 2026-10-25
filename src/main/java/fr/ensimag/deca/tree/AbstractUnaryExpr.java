package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Unary expression.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractUnaryExpr extends AbstractExpr {

    /**
     * Get the operand of this unary expression.
     * @return the operand
     */
    public AbstractExpr getOperand() {
        return operand;
    }

    private AbstractExpr operand;

    /**
     * Construct a unary expression.
     * @param operand the operand
     */
    public AbstractUnaryExpr(AbstractExpr operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }

    /**
     * Get the operator name for decompilation.
     * @return the operator symbol as a string
     */
    protected abstract String getOperatorName();

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(getOperatorName());
        getOperand().decompile(s);
        s.print(")");
    }

    protected void isBoolean(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, String name) throws ContextualError {
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!type.isBoolean()) {
            throw new ContextualError("Operator " + name + " expects a boolean (got: " + type + ")", getLocation());
        }
    }

    protected Type isNumeric(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, String name) throws ContextualError {
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!type.isInt() && !type.isFloat()) {
            throw new ContextualError("Operator " + name + " expects a number (got: " + type + ")", getLocation());
        }
        return type;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }

}
