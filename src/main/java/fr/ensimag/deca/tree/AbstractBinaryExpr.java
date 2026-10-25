package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

/**
 * Binary expressions.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractBinaryExpr extends AbstractExpr {

    /**
     * Get the left operand of this binary expression.
     * @return the left operand
     */
    public AbstractExpr getLeftOperand() {
        return leftOperand;
    }

    /**
     * Get the right operand of this binary expression.
     * @return the right operand
     */
    public AbstractExpr getRightOperand() {
        return rightOperand;
    }

    /**
     * Set the left operand of this binary expression.
     * @param leftOperand the new left operand
     */
    protected void setLeftOperand(AbstractExpr leftOperand) {
        Validate.notNull(leftOperand);
        this.leftOperand = leftOperand;
    }

    /**
     * Set the right operand of this binary expression.
     * @param rightOperand the new right operand
     */
    protected void setRightOperand(AbstractExpr rightOperand) {
        Validate.notNull(rightOperand);
        this.rightOperand = rightOperand;
    }

    private AbstractExpr leftOperand;
    private AbstractExpr rightOperand;

    /**
     * Construct a binary expression with left and right operands.
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     */
    public AbstractBinaryExpr(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        Validate.notNull(leftOperand, "left operand cannot be null");
        Validate.notNull(rightOperand, "right operand cannot be null");
        Validate.isTrue(leftOperand != rightOperand, "Sharing subtrees is forbidden");
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(getOperatorName());
        getRightOperand().decompile(s);
        s.print(")");
    }

    /**
     * Get the operator name for decompilation.
     * @return the operator symbol as a string
     */
    abstract protected String getOperatorName();

    @Override
    protected void iterChildren(TreeFunction f) {
        leftOperand.iter(f);
        rightOperand.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        leftOperand.prettyPrint(s, prefix, false);
        rightOperand.prettyPrint(s, prefix, true);
    }

    /**
     * Check if both operands are boolean types.
     *
     * @param l the left operand type
     * @param r the right operand type
     * @return true if both are booleans, false otherwise
     */
    protected boolean bothBoolean(Type l, Type r) {
        return l.isBoolean() && r.isBoolean();
    }

    /**
     * Check if both operands are class types or null.
     *
     * @param l the left operand type
     * @param r the right operand type
     * @return true if both are class types or null, false otherwise
     */
    protected boolean bothClassOrNull(Type l, Type r) {
        return l.isClassOrNull() && r.isClassOrNull();
    }

    /**
     * Check if both operands are numeric types (int or float).
     *
     * @param compiler the DECA compiler
     * @param l the left operand type
     * @param r the right operand type
     * @return INT if both are int, FLOAT if at least one is float, null if not numeric
     */
    protected Type bothNumeric(DecacCompiler compiler, Type l, Type r) {
        if ((!l.isNumeric()) || (!r.isNumeric())) {
            return null;
        }

        if (l.isInt() && r.isInt()) {
            return compiler.environmentType.INT;
        }

        return compiler.environmentType.FLOAT;
    }

    /**
     * Generate code for the right operand, handling register bounds.
     *
     * @param compiler the DECA compiler
     * @param n the current register number
     * @return the right operand as a DVal
     * @throws GenerationError if code generation fails
     */
    protected DVal codeGenRightOperand(DecacCompiler compiler, int n) throws GenerationError {
        
        AbstractExpr rhs = getRightOperand();

        // On récupère directement la valeur si elle est disponible
        DVal rValue = rhs.getDVal(compiler);
        if (rValue != null) {            
            return rValue;
        }
        
        // On vérifie si on a deux registres disponibles pour le swap
        if (compiler.registers().canSwap(n) && rhs.predictable()) {
            rhs.codeGenExpr(compiler, n + 1);
            return Register.getR(n + 1);
        }

        // Fallback. On réserve un registre pour l'opérande droite 
        GPRegister GPreg = Register.getR(n);
        
        // On empile l'opérande gauche
        compiler.stack().push(GPreg);
        
        // On calcule l'opérande droite directement dans Rn
        rhs.codeGenExpr(compiler, n);
        
        // On charge le résultat dans un registre scratch (ici R0)
        compiler.registers().load(GPreg, Register.R0);
        
        // On dépile lhs
        compiler.stack().pop(GPreg);

        // On renvoie rhs (dans R0)
        return Register.R0;
    }
}
