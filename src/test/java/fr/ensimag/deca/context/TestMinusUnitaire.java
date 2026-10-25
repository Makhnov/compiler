package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Minus;
import fr.ensimag.deca.tree.Location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * Test for the Minus node using mockito, using @Mock and @Before annotations.
 * Context Rule (3.33), (3.50)
 * 
 * @author Ensimag
 * @date 06/01/2026
 */
public class TestMinusUnitaire {

    Type INT, FLOAT, BOOLEAN;

    @Mock
    AbstractExpr intexpr1;
    @Mock
    AbstractExpr intexpr2;
    @Mock
    AbstractExpr floatexpr1;
    @Mock
    AbstractExpr floatexpr2;
    @Mock
    AbstractExpr boolexpr1;
    @Mock
    AbstractExpr boolexpr2;

    DecacCompiler compiler;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
        INT = new IntType(compiler.createSymbol("int"));
        FLOAT = new FloatType(compiler.createSymbol("float"));
        BOOLEAN = new BooleanType(compiler.createSymbol("boolean"));

        when(intexpr1.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(intexpr2.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(floatexpr1.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        when(floatexpr2.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        when(boolexpr1.verifyExpr(compiler, null, null)).thenReturn(BOOLEAN);
        when(boolexpr2.verifyExpr(compiler, null, null)).thenReturn(BOOLEAN);

        when(intexpr1.getLocation()).thenReturn(Location.BUILTIN);
        when(intexpr2.getLocation()).thenReturn(Location.BUILTIN);
        when(floatexpr1.getLocation()).thenReturn(Location.BUILTIN);
        when(floatexpr2.getLocation()).thenReturn(Location.BUILTIN);
        when(boolexpr1.getLocation()).thenReturn(Location.BUILTIN);
        when(boolexpr2.getLocation()).thenReturn(Location.BUILTIN);
    }

    @Test
    public void testIntInt() throws ContextualError {
        Minus t = new Minus(intexpr1, intexpr2);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isInt());
        assertTrue(t.getType().isInt());
        // check that the mocks have been called properly.
        verify(intexpr1).verifyExpr(compiler, null, null);
        verify(intexpr2).verifyExpr(compiler, null, null);
    }

    @Test
    public void testIntFloat() throws ContextualError {
        Minus t = new Minus(intexpr1, floatexpr1);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isFloat());
        assertTrue(t.getType().isFloat());
        // ConvFloat should have been inserted on the right side
        assertTrue(t.getLeftOperand() instanceof ConvFloat);
        assertFalse(t.getRightOperand() instanceof ConvFloat);
        // check that the mocks have been called properly.
        verify(intexpr1).verifyExpr(compiler, null, null);
        verify(floatexpr1).verifyExpr(compiler, null, null);
    }

    @Test
    public void testFloatInt() throws ContextualError {
        Minus t = new Minus(floatexpr1, intexpr1);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isFloat());
        assertTrue(t.getType().isFloat());
        // ConvFloat should have been inserted on the right side
        assertTrue(t.getRightOperand() instanceof ConvFloat);
        assertFalse(t.getLeftOperand() instanceof ConvFloat);
        // check that the mocks have been called properly.
        verify(intexpr1).verifyExpr(compiler, null, null);
        verify(floatexpr1).verifyExpr(compiler, null, null);
    }

    @Test
    public void testFloatFloat() throws ContextualError {
        Minus t = new Minus(floatexpr1, floatexpr2);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isFloat());
        assertTrue(t.getType().isFloat());
        // check that the mocks have been called properly.
        verify(floatexpr1).verifyExpr(compiler, null, null);
        verify(floatexpr2).verifyExpr(compiler, null, null);
    }

    @Test
    public void testIntBool() throws ContextualError {
        Minus t = new Minus(intexpr1, boolexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testFloatBool() throws ContextualError {
        Minus t = new Minus(floatexpr1, boolexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolInt() throws ContextualError {
        Minus t = new Minus(boolexpr1, intexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolFloat() throws ContextualError {
        Minus t = new Minus(boolexpr1, floatexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolBool() throws ContextualError {
        Minus t = new Minus(boolexpr1, boolexpr2);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }
}