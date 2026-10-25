package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;

/**
 * Represents the floating-point type in the DECA type system.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class FloatType extends Type {

    public FloatType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isFloat() {
        return true;
    }

    @Override
    public boolean sameType(Type otherType) {
        // throw new UnsupportedOperationException("not yet implemented");
        return otherType.isFloat(); // DONE
    }

}
