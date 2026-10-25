package fr.ensimag.deca.ir.instructions;

import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;

/**
 * IR instruction representing program termination.
 * Signals the end of program execution.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRExit extends IRAbstractInst {

    public IRExit(IRLocation loc) {
        super(loc);
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst("EXIT");
    }
}
