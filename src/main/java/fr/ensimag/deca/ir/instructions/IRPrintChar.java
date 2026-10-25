package fr.ensimag.deca.ir.instructions;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;

/**
 * IR instruction for printing a single character to standard output.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRPrintChar extends IRAbstractInst {
    private final char c;

    public IRPrintChar(IRLocation loc, char c) {
        super(loc);
        this.c = c;
    }

    public char c() {
        return c;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError{
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst("PRINT_CHAR", "'" + p.escapeChar(c) + "'");
    }
}
