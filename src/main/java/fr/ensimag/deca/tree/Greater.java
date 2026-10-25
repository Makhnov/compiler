package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.ima.pseudocode.instructions.BGT;
import fr.ensimag.ima.pseudocode.instructions.BLE;

/**
 * AST node representing the greater-than comparison operator ({@literal >}).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected String IRBranchOnTrue() {
        return "BGT";
    }

    @Override
    protected String IRBranchOnFalse() {
        return "BLE";
    }

    @Override
    protected String getOperatorName() {
        return ">";
    }

    @Override
    protected void codeGenGoTo(DecacCompiler compiler, boolean b, Label l) throws GenerationError {
        compiler.addInstruction(b ? new BGT(l) : new BLE(l));
    }
}