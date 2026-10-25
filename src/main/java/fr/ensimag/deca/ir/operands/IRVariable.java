package fr.ensimag.deca.ir.operands;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.control.IRError;

/**
 * IR Variable operand representing a variable in memory.
 * 
 * @see fr.ensimag.ima.pseudocode.RegisterOffset
 * @author Nico
 * @date 15/01/2026
 */
public class IRVariable extends IRDAddr {
    
    private final String name;
    private final IRType type;
    private final IRSegment segment;

    public IRVariable(String name, IRType type, IRSegment segment) {
        Validate.notEmpty(name);
        Validate.notNull(type);
        Validate.notNull(segment);
        this.name = name;
        this.type = type;
        this.segment = segment;
    }
    public String name() {
        return name;
    }
    public IRType type() {
        return type;
    }
    public IRSegment segment() {
        return segment;
    }

    @Override
    public void accept(AbstractDAddrVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return name;
    }    
}

