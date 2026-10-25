package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BGE;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.deca.codegen.GenerationError;
/**
 * AST node representing the less-than comparison operator ({@literal <}).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected String IRBranchOnTrue() {
        return "BLT";
    }

    @Override
    protected String IRBranchOnFalse() {
        return "BGE";
    }

    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    protected void codeGenGoTo(DecacCompiler compiler, boolean b, Label l) throws GenerationError {
        compiler.addInstruction(b ? new BLT(l) : new BGE(l));
    }
}
