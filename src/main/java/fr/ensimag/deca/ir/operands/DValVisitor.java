package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * Abstract class implementing the Visitor pattern for IR value operands.
 * Provides default implementations that delegate to the generic visit method.
 *
 * @author gl25
 * @date 01/01/2026
 * @see AbstractDValVisitor
 */
public abstract class DValVisitor implements AbstractDValVisitor {
    @Override
    public void visit(IRDVal v) throws IRError {
        throw new UnsupportedOperationException("DValVisitor: no handler for " + v.getClass().getName());
    }

    @Override
    public void visit(IRScratch v) throws IRError {
        visit((IRDVal) v);
    }

    @Override
    public void visit(IRImmediateInt v) throws IRError {
        visit((IRDVal) v);
    }

    @Override
    public void visit(IRImmediateFloat v) throws IRError {
        visit((IRDVal) v);
    }
    
}
