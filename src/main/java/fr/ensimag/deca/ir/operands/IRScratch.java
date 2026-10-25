package fr.ensimag.deca.ir.operands;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.control.IRError;

/**
 * Scratch operand used for temporary values in IR instructions.
 * 
 * @see fr.ensimag.ima.pseudocode.Register
 * @author Nico
 * @date 15/01/2026
 */
public class IRScratch extends IRDVal {
    private final int id;
    private final IRType type;

    public IRScratch(int id, IRType type) {    
        Validate.notNull(type);
        Validate.isTrue(id >= 0, "Scratch id cannot be negative");
        this.id = id;
        this.type = type;
    }
    
    public int id() {
        return id;
    }

    public IRType type() {
        return type;
    }

    @Override
    public void accept(AbstractDValVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "s" + id;
    }
}

