package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Or;
import fr.ensimag.deca.tree.Location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * Test for the Or node using mockito, using @Mock and @Before annotations.
 * Context Rule (3.33), (3.55)
 * 
 * @author Nathan
 * @date 06/01/2026
 */
public class TestOrUnitaire {

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
    public void testBoolBool() throws ContextualError {
        Or t = new Or(boolexpr1, boolexpr2);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isBoolean());
        assertTrue(t.getType().isBoolean());
        // check that the mocks have been called properly.
        verify(boolexpr1).verifyExpr(compiler, null, null);
        verify(boolexpr2).verifyExpr(compiler, null, null);
    }

    @Test
    public void testIntBool() throws ContextualError {
        Or t = new Or(intexpr1, boolexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testFloatBool() throws ContextualError {
        Or t = new Or(floatexpr1, boolexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolInt() throws ContextualError {
        Or t = new Or(boolexpr1, intexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolFloat() throws ContextualError {
        Or t = new Or(boolexpr1, floatexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testIntFloat() throws ContextualError {
        Or t = new Or(intexpr1, floatexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testFloatInt() throws ContextualError {
        Or t = new Or(floatexpr1, intexpr1);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testIntInt() throws ContextualError {
        Or t = new Or(intexpr1, intexpr2);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testFloatFloat() throws ContextualError {
        Or t = new Or(floatexpr1, floatexpr2);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }
}