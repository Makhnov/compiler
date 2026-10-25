package fr.ensimag.deca.context;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractLValue;
import fr.ensimag.deca.tree.Assign;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Location;

/**
 * Test for the Assign node using mockito
 * Regle 3.32 et Regle 3.28
 * 
 * @author Ensimag
 * @date 01/01/2026
 */
public class TestAssignUnitaire {

    Type INT, FLOAT, BOOL, STR;
    //final Type VOID;

    @Mock
    AbstractLValue intL;
    @Mock
    AbstractExpr intR;
    @Mock
    AbstractLValue floatL;
    @Mock
    AbstractExpr floatR;
    @Mock
    AbstractLValue boolL;
    @Mock
    AbstractExpr boolR;
    @Mock
    AbstractLValue strL;
    @Mock
    AbstractExpr strR;

    Assign assign;
    DecacCompiler compiler;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);

        INT = new IntType(compiler.createSymbol("int"));
        FLOAT = new FloatType(compiler.createSymbol("float"));
        BOOL = new BooleanType(compiler.createSymbol("boolean"));
        STR = new StringType(compiler.createSymbol("string"));

        when(intL.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(intR.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(floatL.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        when(floatR.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        when(boolL.verifyExpr(compiler, null, null)).thenReturn(BOOL);
        when(boolR.verifyExpr(compiler, null, null)).thenReturn(BOOL);
        when(strL.verifyExpr(compiler, null, null)).thenReturn(STR);
        when(strR.verifyExpr(compiler, null, null)).thenReturn(STR);

        when(intL.getType()).thenReturn(INT);
        when(intR.getType()).thenReturn(INT);
        when(floatL.getType()).thenReturn(FLOAT);
        when(floatR.getType()).thenReturn(FLOAT);
        when(boolL.getType()).thenReturn(BOOL);
        when(boolR.getType()).thenReturn(BOOL);
        when(strL.getType()).thenReturn(STR);
        when(strR.getType()).thenReturn(STR);

        when(intL.getLocation()).thenReturn(Location.BUILTIN);
        when(intR.getLocation()).thenReturn(Location.BUILTIN);
        when(floatL.getLocation()).thenReturn(Location.BUILTIN);
        when(floatR.getLocation()).thenReturn(Location.BUILTIN);
        when(boolL.getLocation()).thenReturn(Location.BUILTIN);
        when(boolR.getLocation()).thenReturn(Location.BUILTIN);
        when(strL.getLocation()).thenReturn(Location.BUILTIN);
        when(strR.getLocation()).thenReturn(Location.BUILTIN);

        doCallRealMethod().when(intR).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(floatR).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(boolR).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(strR).verifyRValue(any(), any(), any(), any());
    }

    private void endVerif() throws ContextualError {
        assertNotNull(assign);
        verify(assign.getLeftOperand()).verifyExpr(compiler, null, null);
    }

    @Test
    public void testIntInt() throws ContextualError {
        // int x = 2; OK
        assign = new Assign(intL, intR);

        Type t = assign.verifyExpr(compiler, null, null);

        assertTrue(t.isInt());
        endVerif();
    }

    @Test
    public void testIntFloat() throws ContextualError {
        // int x = 2.0; PAS OK
        assign = new Assign(intL, floatR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testIntBool() throws ContextualError {
        // int x = true; PAS OK
        assign = new Assign(intL, boolR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testIntStr() throws ContextualError {
        // int x = "poke"; PAS OK
        assign = new Assign(intL, strR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testFloatInt() throws ContextualError {
        // float x = 2; OK
        assign = new Assign(floatL, intR);

        Type t = assign.verifyExpr(compiler, null, null);

        assertTrue(t.isFloat());

        assertTrue(assign.getRightOperand() instanceof ConvFloat);
        assertSame(intR, ((ConvFloat) assign.getRightOperand()).getOperand());
        endVerif();
    }

    @Test
    public void testFloatFloat() throws ContextualError {
        // float x = 2.0; OK
        assign = new Assign(floatL, floatR);

        Type t = assign.verifyExpr(compiler, null, null);

        assertTrue(t.isFloat());
        endVerif();
    }

    @Test
    public void testFloatBool() throws ContextualError {
        // float x = true; PAS OK
        assign = new Assign(floatL, boolR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testFloatStr() throws ContextualError {
        // float x = "poke"; PAS OK
        assign = new Assign(floatL, strR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolInt() throws ContextualError {
        // boolean x = 2; PAS OK
        assign = new Assign(boolL, intR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolFloat() throws ContextualError {
        // boolean x = 2.0; PAS OK
        assign = new Assign(boolL, floatR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testBoolBool() throws ContextualError {
        // boolean x = true; OK
        assign = new Assign(boolL, boolR);

        Type t = assign.verifyExpr(compiler, null, null);

        assertTrue(t.isBoolean());
        endVerif();
    }

    @Test
    public void testBoolStr() throws ContextualError {
        // boolean x = "poke"; PAS OK
        assign = new Assign(boolL, strR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testStrInt() throws ContextualError {
        // String x = 2; PAS OK
        assign = new Assign(strL, intR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testStrFloat() throws ContextualError {
        // String x = 2.0; PAS OK
        assign = new Assign(strL, floatR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testStrBool() throws ContextualError {
        // String x = true; PAS OK
        assign = new Assign(strL, boolR);

        assertThrows(ContextualError.class, () -> {
            assign.verifyExpr(compiler, null, null);
        });
    }

    @Test
    public void testStrStr() throws ContextualError {
        // String x = "poke"; OK
        assign = new Assign(strL, strR);

        Type t = assign.verifyExpr(compiler, null, null);

        assertTrue(t.isString());
        endVerif();
    }

}
