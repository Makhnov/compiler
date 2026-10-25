package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.VoidType;
import fr.ensimag.deca.context.NullType;
import fr.ensimag.deca.context.IntType;
import fr.ensimag.deca.context.FloatType;
import fr.ensimag.deca.context.StringType;
import fr.ensimag.deca.context.BooleanType;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * Test for the Type node using mockito, using @Mock and @Before annotations.
 * Int, Float, String, Boolean, Void, Null
 * 
 * @author Nathan
 * @date 08/01/2026
 */
public class TestTypeUnitaire {

    Type INT, FLOAT, BOOLEAN, STRING, VOID, NULL, INT2, FLOAT2, BOOLEAN2, STRING2, VOID2, NULL2;

    DecacCompiler compiler;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
        INT = new IntType(compiler.createSymbol("int"));
        FLOAT = new FloatType(compiler.createSymbol("float"));
        BOOLEAN = new BooleanType(compiler.createSymbol("boolean"));
        VOID = new VoidType(compiler.createSymbol("void"));
        STRING = new StringType(compiler.createSymbol("string"));
        NULL = new NullType(compiler.createSymbol("null"));
        INT2 = new IntType(compiler.createSymbol("int"));
        FLOAT2 = new FloatType(compiler.createSymbol("float"));
        BOOLEAN2 = new BooleanType(compiler.createSymbol("boolean"));
        STRING2 = new StringType(compiler.createSymbol("string"));
        NULL2 = new NullType(compiler.createSymbol("null"));
        VOID2 = new VoidType(compiler.createSymbol("void"));
    }

    @Test
    public void testIsVoid() throws ContextualError {
        // check VoidType.isVoid method
        assertTrue(VOID.isVoid());
        assertFalse(INT.isVoid());
        assertFalse(FLOAT.isVoid());
        assertFalse(STRING.isVoid());
        assertFalse(BOOLEAN.isVoid());
        assertFalse(NULL.isVoid());
    }

    @Test
    public void testSameTypeVoid() throws ContextualError {
        // check VoidType.sameType method
        assertTrue(VOID.sameType(VOID2));
        assertFalse(VOID.sameType(INT));
        assertFalse(VOID.sameType(FLOAT));
        assertFalse(VOID.sameType(BOOLEAN));
        assertFalse(VOID.sameType(STRING));
        assertFalse(VOID.sameType(NULL));
    }

    @Test
    public void testIsNull() throws ContextualError {
        // check NullType.isNull method
        assertTrue(NULL.isNull());
        assertFalse(VOID.isNull());
        assertFalse(INT.isNull());
        assertFalse(FLOAT.isNull());
        assertFalse(STRING.isNull());
        assertFalse(BOOLEAN.isNull());
    }

    @Test
    public void testSameTypeNull() throws ContextualError {
        // check NullType.isNull method
        assertTrue(NULL.sameType(NULL2));
        assertFalse(NULL.sameType(VOID));
        assertFalse(NULL.sameType(INT));
        assertFalse(NULL.sameType(FLOAT));
        assertFalse(NULL.sameType(BOOLEAN));
        assertFalse(NULL.sameType(STRING));
    }

    @Test
    public void testIsInt() throws ContextualError {
        // check IntType.isInt method
        assertTrue(INT.isInt());
        assertFalse(VOID.isInt());
        assertFalse(NULL.isInt());
        assertFalse(FLOAT.isInt());
        assertFalse(STRING.isInt());
        assertFalse(BOOLEAN.isInt());
    }

    @Test
    public void testSameTypeInt() throws ContextualError {
        // check IntType.sameType method
        assertTrue(INT.sameType(INT2));
        assertFalse(INT.sameType(VOID));
        assertFalse(INT.sameType(NULL));
        assertFalse(INT.sameType(FLOAT));
        assertFalse(INT.sameType(BOOLEAN));
        assertFalse(INT.sameType(STRING));
    }

    @Test
    public void testIsFloat() throws ContextualError {
        // check FloatType.isFloat method
        assertTrue(FLOAT.isFloat());
        assertFalse(VOID.isFloat());
        assertFalse(INT.isFloat());
        assertFalse(NULL.isFloat());
        assertFalse(STRING.isFloat());
        assertFalse(BOOLEAN.isFloat());
    }

    @Test
    public void testSameTypeFloat() throws ContextualError {
        // check FloatType.sameType method
        assertTrue(FLOAT.sameType(FLOAT2));
        assertFalse(FLOAT.sameType(VOID));
        assertFalse(FLOAT.sameType(INT));
        assertFalse(FLOAT.sameType(NULL));
        assertFalse(FLOAT.sameType(BOOLEAN));
        assertFalse(FLOAT.sameType(STRING));
    }

    @Test
    public void testIsString() throws ContextualError {
        // check StringType.isString method
        assertTrue(STRING.isString());
        assertFalse(VOID.isString());
        assertFalse(INT.isString());
        assertFalse(FLOAT.isString());
        assertFalse(NULL.isString());
        assertFalse(BOOLEAN.isString());
    }

    @Test
    public void testSameTypeString() throws ContextualError {
        // check StringType.sameType method
        assertTrue(STRING.sameType(STRING2));
        assertFalse(STRING.sameType(VOID));
        assertFalse(STRING.sameType(INT));
        assertFalse(STRING.sameType(FLOAT));
        assertFalse(STRING.sameType(BOOLEAN));
        assertFalse(STRING.sameType(NULL));
    }

    @Test
    public void testIsBoolean() throws ContextualError {
        // check BooleanType.isBoolean method
        assertTrue(BOOLEAN.isBoolean());
        assertFalse(VOID.isBoolean());
        assertFalse(INT.isBoolean());
        assertFalse(FLOAT.isBoolean());
        assertFalse(STRING.isBoolean());
        assertFalse(NULL.isBoolean());
    }

    @Test
    public void testSameTypeBoolean() throws ContextualError {
        // check BooleanType.sameType method
        assertTrue(BOOLEAN.sameType(BOOLEAN2));
        assertFalse(BOOLEAN.sameType(VOID));
        assertFalse(BOOLEAN.sameType(INT));
        assertFalse(BOOLEAN.sameType(FLOAT));
        assertFalse(BOOLEAN.sameType(NULL));
        assertFalse(BOOLEAN.sameType(STRING));
    }

}