package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRJump;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.instructions.IROpCmp;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Abstract base class for comparison operators.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {
    /**
     * Get the IR branch instruction name for true condition.
     * @return the IR branch instruction name
     */
    protected abstract String IRBranchOnTrue();

    /**
     * Get the IR branch instruction name for false condition.
     * @return the IR branch instruction name
     */
    protected abstract String IRBranchOnFalse();

    /**
     * Construct a comparison operator.
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     */
    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    /**
     * Generate branch code based on comparison result.
     * @param compiler the DECA compiler
     * @param b branch condition
     * @param label target label
     * @throws GenerationError if code generation fails
     */
    protected abstract void codeGenGoTo(DecacCompiler compiler, boolean b, Label label) throws GenerationError;

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Evaluation de l'erpression (le booléen est une donnée)
     */
    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {// A FACTORISER  
        int id = compiler.labels().getLabelID();
        Label lCmp = compiler.labels().label("E_Cmp.", id);
        Label lEnd = compiler.labels().labelFin(id);
        
        AbstractExpr lhs = getLeftOperand();        

        // Evaluation et comparaison
        lhs.codeGenExpr(compiler, n);
        DVal rValue = codeGenRightOperand(compiler, n);
        compiler.addInstruction(new CMP(rValue, Register.getR(n)));

        // On branche l'expression "vraie"
        codeGenGoTo(compiler, true, lCmp);

        // L'expression est fausse
        compiler.registers().load(new ImmediateInteger(0), Register.getR(n));
        compiler.addInstruction(new BRA(lEnd));

        // L'expression est vraie
        compiler.addLabel(lCmp);
        compiler.registers().load(new ImmediateInteger(1), Register.getR(n));

        // Fin de l'expression
        compiler.addLabel(lEnd);
    }

    /**
     * Evaluation de l'erpression (le booléen est une donnée) IR version 
     */
    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRLocation loc = IRLocation.from(getLocation());
        int id = compiler.labels().getLabelID();

        // Résultat booléen matérialisé en 0/1
        IRScratch dst = ir.scratch(IRType.BOOLEAN);

        IRLabel lTrue = compiler.labels().irLabel("E_Cmp", id);
        IRLabel lEnd  = compiler.labels().irLabelFin(id);

        // On initialise dst à 0
        ir.addInstruction(new IRBinaryExpr(loc, "SUB_INT", dst, new IRImmediateInt(0), new IRImmediateInt(0)));

        // On branche si l'expression est vraie
        irGenBranch(compiler, ir, true, lTrue);

        // L'expression est fausse
        ir.addInstruction(new IRJump(loc, lEnd));

        // L'expression est vraie
        ir.addInstruction(new IRLabelDef(loc, lTrue));
        ir.addInstruction(new IRBinaryExpr(loc, "ADD_INT", dst, new IRImmediateInt(0), new IRImmediateInt(1)));

        // Fin de l'expression
        ir.addInstruction(new IRLabelDef(loc, lEnd));
        return dst;
    }

    /**
     * Génération de branchement (le booléen est un flot de contrôle)
     */
    @Override
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        AbstractExpr lhs = getLeftOperand();        
        int n = compiler.registers().getScatchMin();

        // Evaluation et comparaison
        lhs.codeGenExpr(compiler, n);
        DVal rValue = codeGenRightOperand(compiler, n);
        compiler.addInstruction(new CMP(rValue, Register.getR(n)));

        // On branche selon le résultat
        codeGenGoTo(compiler, b, label);
    }

    @Override
    protected void irGenBranch(DecacCompiler compiler, IRProgram ir, boolean b, IRLabel target) throws GenerationError {
        IRDVal left  = getLeftOperand().irGenExpr(compiler, ir);
        IRDVal right = getRightOperand().irGenExpr(compiler, ir);

        IRType t = getLeftOperand().getIRType(); // après ConvFloat éventuel
        String base = b ? IRBranchOnTrue() : IRBranchOnFalse();
        String suffix = (t == IRType.FLOAT) ? "_FLOAT" : "_INT";

        ir.addInstruction(new IROpCmp(IRLocation.from(getLocation()), base + suffix, left, right, target));
    }
}
