package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.ima.pseudocode.Label;

/**
 * AST node representing the logical AND operator ({@literal &&}).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override 
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        if (!b) {
            // Il suffit que l'un des deux soit faux pour que l'AND soit faux.
            getLeftOperand().codeGenBranch(compiler, false, label);
            getRightOperand().codeGenBranch(compiler, false, label);
            return;
        }

        // ici les deux doivent être vrais
        int id = compiler.labels().getLabelID();
        Label end = compiler.labels().labelFin(id);

        getLeftOperand().codeGenBranch(compiler, false, end);
        getRightOperand().codeGenBranch(compiler, true, label);
        compiler.addLabel(end);
    }

    @Override
    protected void irGenBranch(DecacCompiler compiler, IRProgram ir, boolean b, IRLabel label) throws GenerationError {
        IRLocation loc = IRLocation.from(getLocation());

        if (!b) {
            getLeftOperand().irGenBranch(compiler, ir, false, label);
            getRightOperand().irGenBranch(compiler, ir, false, label);
            return;
        }

        int id = compiler.labels().getLabelID();
        IRLabel end = compiler.labels().irLabel("E_AndFin", id);
        getLeftOperand().irGenBranch(compiler, ir, false, end);
        getRightOperand().irGenBranch(compiler, ir, true, label);
        ir.addInstruction(new IRLabelDef(loc, end));
    }
}
