package fr.ensimag.ima.pseudocode;

/**
 * Base class for unary IMA instructions with an immediate integer operand.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public abstract class UnaryInstructionImmInt extends UnaryInstruction {

    protected UnaryInstructionImmInt(ImmediateInteger operand) {
        super(operand);
    }

    protected UnaryInstructionImmInt(int i) {
        super(new ImmediateInteger(i));
    }

}
