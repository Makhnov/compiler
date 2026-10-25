package fr.ensimag.ima.pseudocode;

/**
 * Base class for unary IMA instructions targeting a general-purpose register.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class UnaryInstructionToReg extends UnaryInstruction {

    public UnaryInstructionToReg(GPRegister op) {
        super(op);
    }

}
