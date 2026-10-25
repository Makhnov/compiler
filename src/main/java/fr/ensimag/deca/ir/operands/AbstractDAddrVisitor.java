package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * Visitor pattern interface for traversing IR address operands (IRDAddr).
 *
 * @author gl25
 * @date 01/01/2026
 * @see DAddrVisitor
 */
public interface AbstractDAddrVisitor {
    /**
     * Visit an IR address operand.
     * @param v the IR address to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRDAddr v) throws IRError;

    /**
     * Visit an IR variable operand.
     * @param v the IR variable to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRVariable v) throws IRError;
}
