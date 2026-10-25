package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.ima.pseudocode.Label;

/**
 * AST node representing the logical OR operator (||).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override 
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        if (b) {
            // Il suffit que l'un des deux soit vrai pour que l'OR soit vrai.
            getLeftOperand().codeGenBranch(compiler, true, label);
            getRightOperand().codeGenBranch(compiler, true, label);
            return;
        }

        // ici les deux doivent être faux
        int id = compiler.labels().getLabelID();
        Label end = compiler.labels().labelFin(id);

        getLeftOperand().codeGenBranch(compiler, true, end);
        getRightOperand().codeGenBranch(compiler, false, label);
        compiler.addLabel(end);
    }

    @Override
    protected void irGenBranch(DecacCompiler compiler, IRProgram ir, boolean b, IRLabel label) throws GenerationError {
        IRLocation loc = IRLocation.from(getLocation());

        if (b) {
            getLeftOperand().irGenBranch(compiler, ir, true, label);
            getRightOperand().irGenBranch(compiler, ir, true, label);
            return;
        }

        int id = compiler.labels().getLabelID();
        IRLabel end = compiler.labels().irLabel("E_OrFin", id);
        getLeftOperand().irGenBranch(compiler, ir, true, end);
        getRightOperand().irGenBranch(compiler, ir, false, label);
        ir.addInstruction(new IRLabelDef(loc, end));
    }
}
