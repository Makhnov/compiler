package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRLabel;

/**
 * IR instruction representing a comparison operation with conditional branch.
 * Compares two operands and jumps to a target label based on the result.
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class IROpCmp extends IRAbstractInst {
    private final String name;
    private final IRDVal left;
    private final IRDVal right;
    private final IRLabel target;

    public IROpCmp(IRLocation loc, String name, IRDVal left, IRDVal right, IRLabel target) {
        super(loc);
        Validate.notEmpty(name);
        Validate.notNull(left);
        Validate.notNull(right);
        Validate.notNull(target);
        this.name = name;
        this.left = left;
        this.right = right;
        this.target = target;
    }

    public String name() {
        return name; 
    }
    
    public IRDVal left() {
        return left; 
    }
    
    public IRDVal right() {
        return right; 
    }
    
    public IRLabel target() {
        return target; 
    }
    

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst(name, left.toString(), right.toString(), target.name());
    }
}