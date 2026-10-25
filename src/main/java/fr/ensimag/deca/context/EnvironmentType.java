package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;

import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;

// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl25
 * @date 01/01/2026
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {

        envTypes = new HashMap<Symbol, TypeDefinition>();

        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // not added to envTypes, it's not visible for the user.

        Symbol nullSymb = compiler.createSymbol("null");
        NULL = new NullType(nullSymb);
        // not added to envTypes, it's not visible for the user.

        // Ajout de la Class 'Object' et de sa méthode 'equals'
        Symbol objectSymbol = compiler.createSymbol("Object");
        ClassType objectType = new ClassType(objectSymbol, Location.BUILTIN, null);
        ClassDefinition objectDef = objectType.getDefinition();

        Signature eqSig = new Signature();
        eqSig.add(objectType);
        MethodDefinition eqDef = new MethodDefinition(BOOLEAN, Location.BUILTIN, eqSig, 1);
        eqDef.setLabel(new fr.ensimag.ima.pseudocode.Label("code.Object.equals"));

        Symbol eqSymbol = compiler.createSymbol("equals");
        try {
            objectDef.getMembers().declare(eqSymbol, eqDef);
            objectDef.setNumberOfMethods(1);
        } catch (DoubleDefException e) {
            throw new InternalError("builtin type already exists");
        }
        envTypes.put(objectSymbol, objectDef);
        OBJECT = objectDef;
        EQUALS = eqDef;
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public void declareClass(Symbol className, ClassDefinition classDef) throws DoubleDefException {
        if (envTypes.containsKey(className)) {
            throw new DoubleDefException();
        }
        envTypes.put(className, classDef);
    }

    public final ClassDefinition OBJECT;
    public final MethodDefinition EQUALS;

    public final VoidType VOID;
    public final IntType INT;
    public final FloatType FLOAT;
    public final StringType STRING;
    public final BooleanType BOOLEAN;
    public final NullType NULL;
}
