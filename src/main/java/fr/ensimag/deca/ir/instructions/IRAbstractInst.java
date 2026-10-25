package fr.ensimag.deca.ir.instructions;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;

/**
 * Abstract base class for all IR (Intermediate Representation) instructions.
 * Each instruction carries its source location for error reporting and debugging.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class IRAbstractInst {
    private final IRLocation location;

    public IRAbstractInst(IRLocation loc) {
        this.location = (loc == null) ? IRLocation.unknown() : loc;
    }

    public IRLocation getLocation() {
        return location;
    }

    public abstract void accept(AbstractIRVisitor visitor) throws IRError;
    public abstract void prettyPrint(IRPrinter p);
}

