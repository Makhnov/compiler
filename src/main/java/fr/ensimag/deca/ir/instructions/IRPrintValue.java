package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRType;

/**
 * IR instruction for printing a typed value to standard output.
 * Supports integer and float types with optional hexadecimal format.
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class IRPrintValue extends IRAbstractInst {
    private final IRType type;
    private final IRDVal value;
    private boolean printHex = false;

    public IRPrintValue(IRLocation loc, IRType type, IRDVal value) {
        super(loc);
        Validate.notNull(type);
        Validate.notNull(value);
        this.type = type;
        this.value = value;
    }

    public IRPrintValue(IRLocation loc, IRType type, IRDVal value, boolean printHex) {
        super(loc);
        Validate.notNull(type);
        Validate.notNull(value);
        this.type = type;
        this.value = value;
        this.printHex = printHex;
    }

    public IRType type() {
        return type;
    }

    public IRDVal value() {
        return value;
    }

    public boolean hex() {
        return printHex;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        if (type == IRType.BOOLEAN) {
            throw new UnsupportedOperationException("Not printable in deca");
        }
        switch (type) {
        case INT:
            p.inst("PRINT_INT", value.toString());
            break;

        case FLOAT:
            p.inst(printHex ? "PRINT_FLOATX" : "PRINT_FLOAT", value.toString());
            break;

        case STRING:
            throw new UnsupportedOperationException("PRINT_VALUE(STRING) forbidden: use PRINT_STRING label");

        default:
            throw new UnsupportedOperationException("PRINT_VALUE for type " + type + " not supported");
        }
    }
}
