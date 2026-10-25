package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * Abstract class implementing the Visitor pattern for IR address operands.
 * Provides default implementations that delegate to the generic visit method.
 *
 * @author gl25
 * @date 01/01/2026
 * @see AbstractDAddrVisitor
 */
public abstract class DAddrVisitor implements AbstractDAddrVisitor {
    @Override
    public void visit(IRDAddr v) throws IRError {
        throw new UnsupportedOperationException("DAddrVisitor: no handler for " + v.getClass().getName());
    }

    @Override
    public void visit(IRVariable v) throws IRError {
        visit((IRDAddr) v);
    }
    
}
