package fr.ensimag.deca.ir.operands;

import fr.ensimag.deca.ir.control.IRError;

/**
 * Operand that contains a value.
 * 
 * @author Nico
 * @date 15/01/2026
 */
public abstract class IRDVal extends IROperand {
    public abstract void accept(AbstractDValVisitor visitor) throws IRError;
}

