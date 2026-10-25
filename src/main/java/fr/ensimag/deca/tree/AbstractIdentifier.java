package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.SymbolTable;

/**
 * Abstract base class for identifier expressions (variables, types, methods).
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractIdentifier extends AbstractLValue {

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @return the definition as a ClassDefinition
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a class definition.
     */
    public abstract ClassDefinition getClassDefinition();

    /**
     * Get the definition associated with this identifier.
     * @return the definition
     */
    public abstract Definition getDefinition();

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @return the definition as a FieldDefinition
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a field definition.
     */
    public abstract FieldDefinition getFieldDefinition();

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @return the definition as a MethodDefinition
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a method definition.
     */
    public abstract MethodDefinition getMethodDefinition();

    /**
     * Get the symbol name of this identifier.
     * @return the symbol
     */
    public abstract SymbolTable.Symbol getName();

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @return the definition as an ExpDefinition
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not an expression definition.
     */
    public abstract ExpDefinition getExpDefinition();

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     *
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     *
     * @return the definition as a VariableDefinition
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a variable definition.
     */
    public abstract VariableDefinition getVariableDefinition();

    /**
     * Set the definition for this identifier.
     * @param definition the definition to set
     */
    public abstract void setDefinition(Definition definition);

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes.
     *
     * @param compiler contains "env_types" attribute
     * @return the type corresponding to this identifier
     *         (corresponds to the "type" attribute)
     * @throws ContextualError if a contextual error is detected
     */
    public abstract Type verifyType(DecacCompiler compiler) throws ContextualError;

    /**
     * Verify this identifier as a method type.
     *
     * @param compiler the DECA compiler
     * @param localEnv the local environment
     * @param currentClass the current class definition
     * @return the type of the method
     * @throws ContextualError if a contextual error is detected
     */
    public abstract Type verifyMethodType(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError;
}
