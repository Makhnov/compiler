package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRDVal;

/**
 * IR instruction representing an assignment operation.
 * Stores a value from a source operand into a destination address.
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class IRAssign extends IRAbstractInst {
    private final IRDVal src;
    private final IRDAddr dst;

    public IRAssign(IRLocation loc, IRDVal src, IRDAddr dst) {
        super(loc);
        Validate.notNull(src);
        Validate.notNull(dst);
        this.src = src;
        this.dst = dst;
    }

    public IRDVal src() {
        return src;
    }

    public IRDAddr dst() {
        return dst;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        // Plus "assembly-like" pour une assignation
        p.inst("STORE", src.toString(), dst.toString());
    }
}
