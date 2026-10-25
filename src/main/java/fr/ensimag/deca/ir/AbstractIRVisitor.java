package fr.ensimag.deca.ir;

import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRConvFloat;
import fr.ensimag.deca.ir.instructions.IRExit;
import fr.ensimag.deca.ir.instructions.IRJump;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.instructions.IRLoad;
import fr.ensimag.deca.ir.instructions.IROpCmp;
import fr.ensimag.deca.ir.instructions.IRPrintChar;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.instructions.IRUnaryExpr;

/**
 * Visitor pattern interface for traversing IR instructions.
 * Defines visit methods for each type of intermediate representation instruction.
 *
 * @author gl25
 * @date 01/01/2026
 * @see IRVisitor
 * @see IRAbstractInst
 */
public interface AbstractIRVisitor {

    /**
     * Visits a generic IR instruction.
     *
     * @param inst the instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRAbstractInst inst) throws IRError;

    /**
     * Visits an assignment instruction.
     *
     * @param inst the assignment instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRAssign inst) throws IRError;

    /**
     * Visits a memory load instruction.
     *
     * @param inst the load instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRLoad inst) throws IRError;

    /**
     * Visits a binary expression.
     *
     * @param inst the binary expression to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRBinaryExpr inst) throws IRError;

    /**
     * Visits a unary expression.
     *
     * @param inst the unary expression to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRUnaryExpr inst) throws IRError;

    /**
     * Visits a float conversion instruction.
     *
     * @param inst the conversion instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRConvFloat inst) throws IRError;

    /**
     * Visits a value print instruction.
     *
     * @param inst the print instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRPrintValue inst) throws IRError;

    /**
     * Visits a string print instruction.
     *
     * @param inst the print instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRPrintString inst) throws IRError;

    /**
     * Visits a character print instruction.
     *
     * @param inst the print instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRPrintChar inst) throws IRError;

    /**
     * Visits a label definition.
     *
     * @param inst the label definition to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRLabelDef inst) throws IRError;

    /**
     * Visits a jump instruction.
     *
     * @param inst the jump instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRJump inst) throws IRError;

    /**
     * Visits a comparison instruction.
     *
     * @param inst the comparison instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IROpCmp inst) throws IRError;

    /**
     * Visits a program exit instruction.
     *
     * @param inst the exit instruction to visit
     * @throws IRError if an error occurs during visitation
     */
    void visit(IRExit inst) throws IRError;
}