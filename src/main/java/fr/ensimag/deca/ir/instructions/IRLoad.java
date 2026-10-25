package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

/**
 * IR instruction representing a memory load operation.
 * Loads a typed value from a source address into a scratch register.
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class IRLoad extends IRAbstractInst {
    private final IRScratch dst;
    private final IRDAddr src;
    private final IRType type;

    public IRLoad(IRLocation loc, IRType type, IRScratch dst, IRDAddr src) {
        super(loc);
        Validate.notNull(type);
        Validate.notNull(dst);
        Validate.notNull(src);
        Validate.isTrue(dst.type() == type, "IRLoad type mismatch");
        this.dst = dst;
        this.src = src;
        this.type = type;
    }

    public IRScratch dst() {
        return dst;
    }

    public IRDAddr src() {
        return src;
    }

    public IRType type() {
        return type;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst("LOAD_" + type, dst.toString(), src.toString());
    }
}
