package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 * Definition associated to identifier in expressions.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class ExpDefinition extends Definition {

    public void setDAddr(DAddr operand) {
        this.operand = operand;
    }

    public DAddr getDAddr(DecacCompiler compiler) {
        return operand;
    }

    private DAddr operand;

    public ExpDefinition(Type type, Location location) {
        super(type, location);
    }

    public GPRegister getRegister(DecacCompiler compiler) {
        return null;
    }

    public void modified(DecacCompiler compiler) {
        // Nothing by default
    }
}
