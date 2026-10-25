package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;

/**
 * Represents the null type in the DECA type system.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class NullType extends Type {

    public NullType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean sameType(Type otherType) {
        // throw new UnsupportedOperationException("not yet implemented");
        return otherType.isNull(); // DONE
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public boolean isClassOrNull() {
        return true;
    }

    @Override
    public boolean isSubTypeOf(Type type) {
        return type.isClassOrNull();
    }
}
