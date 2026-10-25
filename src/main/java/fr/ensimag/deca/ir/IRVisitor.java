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
 * Abstract class implementing the Visitor pattern for IR instructions.
 * Provides a default implementation that delegates to the generic visit method,
 * allowing subclasses to override only the methods they need.
 *
 * @author gl25
 * @date 01/01/2026
 * @see AbstractIRVisitor
 */
public abstract class IRVisitor implements AbstractIRVisitor {

    @Override
    public void visit(IRAbstractInst inst) throws IRError {
        throw new IRError("IRVisitor: no handler for " + inst.getClass().getName(), inst.getLocation());
    }

    @Override
    public void visit(IRAssign inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRLoad inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRBinaryExpr inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRUnaryExpr inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRConvFloat inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }


    @Override
    public void visit(IRPrintValue inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRPrintString inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRPrintChar inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }


    @Override
    public void visit(IRLabelDef inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRJump inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IROpCmp inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
    @Override
    public void visit(IRExit inst) throws IRError {
        visit((IRAbstractInst) inst); 
    }
}