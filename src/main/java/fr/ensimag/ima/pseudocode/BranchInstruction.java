package fr.ensimag.ima.pseudocode;

/**
 * Base class for branch instructions targeting a {@link Label}.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class BranchInstruction extends UnaryInstruction {

    /**
     * Creates a branch instruction to the given label.
     *
     * @param op target label
     */
    public BranchInstruction(Label op) {
        super(op);
    }

}
