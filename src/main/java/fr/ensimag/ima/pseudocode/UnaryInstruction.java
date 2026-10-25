package fr.ensimag.ima.pseudocode;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Instruction with a single operand.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public abstract class UnaryInstruction extends Instruction {
    private Operand operand;

    @Override
    void displayOperands(PrintStream s) {
        s.print(" ");
        s.print(operand);
    }

    /**
     * Builds a unary instruction with the given operand.
     *
     * @param operand operand of the instruction (must not be {@code null})
     * @throws IllegalArgumentException if {@code operand} is {@code null}
     */

    protected UnaryInstruction(Operand operand) {
        Validate.notNull(operand);
        this.operand = operand;
    }

    /**
     * @return the operand of this instruction
     */
    public Operand getOperand() {
        return operand;
    }

}
