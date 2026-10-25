package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.UnaryInstruction;

/**
 * IMA instruction.
 * @author Ensimag
 * @date 01/01/2026
 */
public class PEA extends UnaryInstruction {

    public PEA(DAddr operand) {
        super(operand);
    }

}
