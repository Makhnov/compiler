package fr.ensimag.deca.ir.operands;

import org.apache.commons.lang.Validate;

/**
 * Represents a label in the IR for branch targets and data references.
 * Label names must start with a letter and contain only alphanumeric characters,
 * underscores, or dots.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRLabel {
    private final String name;

    public IRLabel(String name) {
        Validate.notEmpty(name);
        Validate.isTrue(name.length() <= 1024, "Label name too long, not supported");
        Validate.isTrue(name.matches("^[a-zA-Z][a-zA-Z0-9_.]*$"), "Invalid label name " + name);
        this.name = name;
    }
    
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

