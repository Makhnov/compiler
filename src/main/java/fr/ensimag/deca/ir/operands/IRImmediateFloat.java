package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * IR operand representing an immediate floating-point constant.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRImmediateFloat extends IRDVal {
    private final float value;

    public IRImmediateFloat(float value) {
        this.value = value;
    }

    public float value() {
        return value;
    }

    @Override
    public void accept(AbstractDValVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "#" + Float.toString(value);
    }

}
