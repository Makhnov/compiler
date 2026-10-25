package fr.ensimag.ima.pseudocode.instructions;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.UnaryInstructionToReg;

/**
 * IMA SGE instruction: set register to 1 if greater or equal, 0 otherwise.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class SGE extends UnaryInstructionToReg {

    public SGE(GPRegister op) {
        super(op);
    }

}
