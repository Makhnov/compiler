package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.ima.pseudocode.instructions.BLE;
import fr.ensimag.ima.pseudocode.instructions.BGT;

/**
 * AST node representing the less-than-or-equal comparison operator ({@literal <=}).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected String IRBranchOnTrue() {
        return "BLE";
    }

    @Override
    protected String IRBranchOnFalse() {
        return "BGT";
    }

    @Override
    protected String getOperatorName() {
        return "<=";
    }

    @Override
    protected void codeGenGoTo(DecacCompiler compiler, boolean b, Label l) throws GenerationError {
        compiler.addInstruction(b ? new BLE(l) : new BGT(l));
    }

}
