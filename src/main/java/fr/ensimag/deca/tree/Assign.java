package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue) super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public boolean predictable() {
        return false;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.32)

        // On récupère la Lvalue
        AbstractExpr lhs = getLeftOperand();

        // On récupère le type de la Lvalue
        Type lType = lhs.verifyExpr(compiler, localEnv, currentClass);

        // On récupère la Rvalue
        AbstractExpr rhs = getRightOperand();
        // On vérifie la Rvalue, on la convertit si besoin, on récupère le résultat (si pas d'erreur)
        rhs = rhs.verifyRValue(compiler, localEnv, currentClass, lType);

        // Mise à jour de la Rvalue à sa position dans l'arbre
        setRightOperand(rhs);

        // On confirme le type de l'assign
        setType(lType);

        // On le renvoie
        return lType;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        AbstractLValue lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();
        int n = compiler.registers().getScatchMin();

        if (compiler.optis().basic()) {
            if (rhs.codeGenInPlace(compiler, lhs, n)) {
                return;
            }       
        }

        rhs.codeGenExpr(compiler, n);
        lhs.codeGenStore(compiler, Register.getR(n));
    }

    /**
     * Méthode qui génère le code d'une affectation.
     * 
     * @see fr.ensimag.deca.tree.AbstractLValue#codeGenStore(fr.ensimag.deca.DecacCompiler, fr.ensimag.ima.pseudocode.GPRegister)
     * @see fr.ensimag.deca.tree.Identifier#codeGenStore(fr.ensimag.deca.DecacCompiler, fr.ensimag.ima.pseudocode.GPRegister)
     * 
     * @param compiler
     * @param n Register number where to store the result of the expression
     * @throws GenerationError
     * 
     */
    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        AbstractLValue lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        if (compiler.optis().basic()) {
            if (rhs.codeGenInPlace(compiler, lhs, n)) {
                GPRegister dest = lhs.getRegister(compiler);
                if (dest != null) {
                    if (!dest.equals(Register.getR(n))) {
                        compiler.registers().load(dest, Register.getR(n));
                    }
                } else {
                    // compiler.registers().load(lhs.getDAddr(compiler), Register.getR(n)); // A FAIRE : Vérifier la modif
                    lhs.codeGenExpr(compiler, n);
                }
                return;
            }
        }
        
        rhs.codeGenExpr(compiler, n);
        lhs.codeGenStore(compiler, Register.getR(n));
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDVal rhs = getRightOperand().irGenExpr(compiler, ir);
        IRDAddr lhs = getLeftOperand().irGenAddr(compiler, ir);
        
        ir.addInstruction(new IRAssign(IRLocation.from(getLocation()), rhs, lhs));
        return rhs;
    }

    @Override
    protected String getOperatorName() {
        return " = ";
    }
}
