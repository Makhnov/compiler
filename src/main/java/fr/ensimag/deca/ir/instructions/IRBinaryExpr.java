package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;

/**
 * IR instruction representing a binary expression.
 * Computes an operation on two operands and stores the result in a scratch register.
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class IRBinaryExpr extends IRAbstractInst {
    private final String name;
    private final IRScratch dst;
    private final IRDVal left;
    private final IRDVal right;

    public IRBinaryExpr(IRLocation loc, String name, IRScratch dst, IRDVal left, IRDVal right) {
        super(loc);
        Validate.notEmpty(name);
        Validate.notNull(dst);
        Validate.notNull(left);
        Validate.notNull(right);
        this.name = name;
        this.dst = dst;
        this.left = left;
        this.right = right;
    }

    public String name() {
        return name;
    }

    public IRScratch dst() {
        return dst;
    }

    public IRDVal left() {
        return left;
    }

    public IRDVal right() {
        return right;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst(name, dst.toString(), left.toString(), right.toString());
    }
}
