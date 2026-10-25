package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * IR operand representing an immediate integer constant.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRImmediateInt extends IRDVal {
    private final int value;

    public IRImmediateInt(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public void accept(AbstractDValVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "#" + value;
    }
}
