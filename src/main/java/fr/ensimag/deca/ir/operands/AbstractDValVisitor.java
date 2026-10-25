package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * Visitor pattern interface for traversing IR value operands (IRDVal).
 *
 * @author gl25
 * @date 01/01/2026
 * @see DValVisitor
 */
public interface AbstractDValVisitor {
    /**
     * Visit an IR value operand.
     * @param v the IR value to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRDVal v) throws IRError;

    /**
     * Visit an IR scratch register operand.
     * @param v the scratch register to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRScratch v) throws IRError;

    /**
     * Visit an IR immediate integer operand.
     * @param v the immediate integer to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRImmediateInt v) throws IRError;

    /**
     * Visit an IR immediate float operand.
     * @param v the immediate float to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRImmediateFloat v) throws IRError;
}
