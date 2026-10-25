package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

/**
 * Deca Type (internal representation of the compiler)
 *
 * @author gl25
 * @date 01/01/2026
 */

public abstract class Type {

    /**
     * True if this and otherType represent the same type (in the case of
     * classes, this means they represent the same class).
     */
    public abstract boolean sameType(Type otherType);

    private final Symbol name;

    public Type(Symbol name) {
        this.name = name;
    }

    public Symbol getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName().toString();
    }

    public boolean isClass() {
        return false;
    }

    public boolean isInt() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isVoid() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isClassOrNull() {
        return false;
    }

    public boolean isNumeric() {
        return isInt() || isFloat();
    }

    /**
     * Returns the same object, as type ClassType, if possible. Throws
     * ContextualError(errorMessage, l) otherwise.
     *
     * Can be seen as a cast, but throws an explicit contextual error when the
     * cast fails.
     * @param errorMessage error message used in the exception
     * @param l            location for error reporting
     * @return this type cast as a {@link ClassType}
     * @throws ContextualError if this type is not a class type
     */
    public ClassType asClassType(String errorMessage, Location l) throws ContextualError {
        throw new ContextualError(errorMessage, l);
    }

    /**
    * Checks whether this type is a subtype of another type.
    * @param type the supertype candidate
    * @return true if this is a subtype of {@code type}, false otherwise
    */
    public boolean isSubTypeOf(Type type) {
        if (this.isClass() && type.isClass()) {
            try {
                ClassType thisClass = this.asClassType("Failed ClassType cast", null);
                ClassType thatClass = type.asClassType("Failed ClassType cast", null);
                return thisClass.isSubClassOf(thatClass);
            } catch (ContextualError e) {
                return false;
            }
        }

        return this.sameType(type);
    }

    /**
    * Checks whether assigning a value of type {@code t2} to a variable of this type is allowed.
    * (i.e. {@code this = t2} is valid).
    * @param t2 type of the assigned expression
    * @return true if assignment is compatible, false otherwise
    */
    public boolean assignCompatible(Type t2) {
        if (this.isFloat() && t2.isInt()) {
            return true;
        }
        return t2.isSubTypeOf(this);
    }

    /**
     * Checks whether a cast between this type and {@code t2} is allowed.
     * @param t2 target type of the cast
     * @return true if the cast is compatible, false otherwise
     */
    public boolean castCompatible(Type t2) {
        if (this.isVoid()) {
            return false;
        }
        return this.assignCompatible(t2) || t2.assignCompatible(this);
    }

}
