package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRLabel;

/**
 * IR instruction for printing a string to standard output.
 * References a string constant stored in the data section via a label.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRPrintString extends IRAbstractInst {
    private final IRLabel label;

    public IRPrintString(IRLocation loc, IRLabel label) {
        super(loc);
        Validate.notNull(label);
        this.label = label;
    }

    public IRLabel label() {
        return label;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst("PRINT_STRING", label.name());
    }
}
