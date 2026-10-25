package fr.ensimag.deca.ir.operands;

import org.apache.commons.lang.Validate;

/**
 * Represents a data entry in the IR data section.
 * Associates a label with a string value for storage in the data segment.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRData {
    private final IRLabel label;
    private final String value;

    public IRData(IRLabel label, String value) {
        Validate.notNull(label);
        Validate.notNull(value);
        this.label = label;
        this.value = value;
    }

    public IRLabel label() {
        return label;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return label.name() + ": \"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}

