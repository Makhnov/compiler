package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * IR Address operand that points to a memory location.
 * Differs from the relation between DAddr and DVal in IMA pseudocode
 * since IRDAddr is a direct subclass of IROperand, its not an IRDVal.
 * More explanations about this design choice can be found in the documentation.
 * 
 * @see fr.ensimag.ima.pseudocode.DAddr
 * @author Nico
 * @date 15/01/2026
 */
public abstract class IRDAddr extends IROperand {
    public abstract void accept(AbstractDAddrVisitor visitor) throws IRError;
}

