package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;

/**
 * IR instruction representing an integer to float conversion.
 * Converts a source value to floating-point and stores it in the destination.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRConvFloat extends IRAbstractInst {
    private final IRScratch dst;
    private final IRDVal src;

    // On respecte les conventions risc-V : destination, source
    public IRConvFloat(IRLocation loc, IRScratch dst, IRDVal src) {
        super(loc);
        Validate.notNull(dst);
        Validate.notNull(src);
        this.dst = dst;
        this.src = src;
    }

    public IRScratch dst() {
        return dst;
    }

    public IRDVal src() {
        return src;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst("CONV_FLOAT", dst.toString(), src.toString());
    }
}
