package fr.ensimag.deca.codegen;

import fr.ensimag.ima.pseudocode.GPRegister;

public class ReservedVar {
    private final GPRegister register;
    private boolean dirty;

    public ReservedVar(GPRegister register) {
        this.register = register;
        this.dirty = false;
    }

    public GPRegister getRegister() {
        return register;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
