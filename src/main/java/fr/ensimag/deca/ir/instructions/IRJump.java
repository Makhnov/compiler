package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRLabel;

/**
 * IR instruction representing an unconditional jump.
 * Transfers control flow to the specified target label.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRJump extends IRAbstractInst {
    private final IRLabel target;

    public IRJump(IRLocation loc, IRLabel target) {
        super(loc);
        Validate.notNull(target);
        this.target = target;
    }

    public IRLabel target() {
        return target;
    }

    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst("JUMP", target.name());
    }
}