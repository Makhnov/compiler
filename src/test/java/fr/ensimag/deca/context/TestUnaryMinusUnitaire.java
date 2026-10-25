package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.UnaryMinus;
import fr.ensimag.deca.tree.Location;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * Test for the UnaryMinus node using mockito, using @Mock and @Before annotations.
 * Context Rule (3.37), (3.62)
 * 
 * @author Nathan
 * @date 06/01/2026
 */
public class TestUnaryMinusUnitaire {

    Type INT, FLOAT, BOOLEAN;

    @Mock
    AbstractExpr intexpr;
    @Mock
    AbstractExpr floatexpr;
    @Mock
    AbstractExpr boolexpr;

    DecacCompiler compiler;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
        INT = new IntType(compiler.createSymbol("int"));
        FLOAT = new FloatType(compiler.createSymbol("float"));
        BOOLEAN = new BooleanType(compiler.createSymbol("boolean"));

        when(intexpr.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(floatexpr.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        when(boolexpr.verifyExpr(compiler, null, null)).thenReturn(BOOLEAN);

        when(intexpr.getLocation()).thenReturn(Location.BUILTIN);
        when(floatexpr.getLocation()).thenReturn(Location.BUILTIN);
        when(boolexpr.getLocation()).thenReturn(Location.BUILTIN);
    }

    @Test
    public void testInt() throws ContextualError {
        UnaryMinus t = new UnaryMinus(intexpr);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isInt());
        // check that the mocks have been called properly.
        verify(intexpr).verifyExpr(compiler, null, null);
    }

    @Test
    public void testFloat() throws ContextualError {
        UnaryMinus t = new UnaryMinus(floatexpr);
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isFloat());
        // check that the mocks have been called properly.
        verify(floatexpr).verifyExpr(compiler, null, null);
    }

    @Test
    public void testBool() throws ContextualError {
        UnaryMinus t = new UnaryMinus(boolexpr);
        // check the result
        assertThrows(ContextualError.class, () -> {
            t.verifyExpr(compiler, null, null);
        });
    }
}