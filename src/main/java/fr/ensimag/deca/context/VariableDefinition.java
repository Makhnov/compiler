package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 * Definition of a variable.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class VariableDefinition extends ExpDefinition {
    public VariableDefinition(Type type, Location location) {
        super(type, location);
    }

    @Override
    public String getNature() {
        return "variable";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public GPRegister getRegister(DecacCompiler compiler) {
        return compiler.vars().getRegister(this);
    }

    @Override
    public void modified(DecacCompiler compiler) {
        compiler.vars().modified(this);
    }
}
