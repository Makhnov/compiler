package fr.ensimag.deca.context;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.AbstractInst;
import fr.ensimag.deca.tree.AbstractPrint;
import fr.ensimag.deca.tree.Assign;
import fr.ensimag.deca.tree.ListExpr;
import fr.ensimag.deca.tree.ListInst;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.Print;
import fr.ensimag.deca.tree.Println;

/**
 * Test for the Assign node using mockito
 * Regle 3.31 | Regle 3.30 | Regle 3.21
 * 
 * @author Ensimag
 * @date 01/01/2026
 */
public class TestPrintUnitaire {
    DecacCompiler compiler;

    Type INT, FLOAT, BOOL, STR, VOID;

    @Mock
    AbstractExpr exprInt;
    @Mock
    AbstractExpr exprFloat;
    @Mock
    AbstractExpr exprBool;
    @Mock
    AbstractExpr exprStr;
    @Mock
    AbstractExpr exprVoid;

    AbstractPrint print;

    @BeforeEach
    public void setup() throws ContextualError {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);

        INT = new IntType(compiler.createSymbol("int"));
        FLOAT = new FloatType(compiler.createSymbol("float"));
        BOOL = new BooleanType(compiler.createSymbol("boolean"));
        STR = new StringType(compiler.createSymbol("string"));
        VOID = new VoidType(compiler.createSymbol("void"));

        when(exprInt.verifyExpr(compiler, null, null)).thenReturn(INT);
        when(exprFloat.verifyExpr(compiler, null, null)).thenReturn(FLOAT);
        when(exprBool.verifyExpr(compiler, null, null)).thenReturn(BOOL);
        when(exprStr.verifyExpr(compiler, null, null)).thenReturn(STR);
        when(exprVoid.verifyExpr(compiler, null, null)).thenReturn(VOID);

        when(exprInt.getType()).thenReturn(INT);
        when(exprFloat.getType()).thenReturn(FLOAT);
        when(exprBool.getType()).thenReturn(BOOL);
        when(exprStr.getType()).thenReturn(STR);
        when(exprVoid.getType()).thenReturn(VOID);

        when(exprInt.getLocation()).thenReturn(Location.BUILTIN);
        when(exprFloat.getLocation()).thenReturn(Location.BUILTIN);
        when(exprBool.getLocation()).thenReturn(Location.BUILTIN);
        when(exprStr.getLocation()).thenReturn(Location.BUILTIN);
        when(exprVoid.getLocation()).thenReturn(Location.BUILTIN);

        doCallRealMethod().when(exprInt).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(exprFloat).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(exprBool).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(exprStr).verifyRValue(any(), any(), any(), any());
        doCallRealMethod().when(exprVoid).verifyRValue(any(), any(), any(), any());
    }

    @Test
    public void testEmpty() throws ContextualError {
        // print(); OK
        ListExpr list = new ListExpr();

        ListInst printInst = new ListInst();
        printInst.add(new Println(false, list));

        printInst.verifyListInst(compiler, null, null, null);
    }

    @Test
    public void testInt() throws ContextualError {
        // print(2); OK
        ListExpr list = new ListExpr();
        list.add(exprInt);

        ListInst printInst = new ListInst();
        printInst.add(new Print(true, list));

        printInst.verifyListInst(compiler, null, null, null);

        verify(exprInt).verifyExpr(compiler, null, null);
    }

    @Test
    public void testFloat() throws ContextualError {
        // print(2.0); OK
        ListExpr list = new ListExpr();
        list.add(exprFloat);

        ListInst printInst = new ListInst();
        printInst.add(new Println(false, list));

        printInst.verifyListInst(compiler, null, null, null);

        verify(exprFloat).verifyExpr(compiler, null, null);
    }

    @Test
    public void testBool() throws ContextualError {
        // print(true); PAS OK
        ListExpr list = new ListExpr();
        list.add(exprBool);

        ListInst printInst = new ListInst();
        printInst.add(new Print(false, list));

        assertThrows(ContextualError.class, () -> {
            printInst.verifyListInst(compiler, null, null, null);
        });
    }

    @Test
    public void testStr() throws ContextualError {
        // print("poke"); OK
        ListExpr list = new ListExpr();
        list.add(exprStr);

        ListInst printInst = new ListInst();
        printInst.add(new Print(true, list));

        printInst.verifyListInst(compiler, null, null, null);

        verify(exprStr).verifyExpr(compiler, null, null);
    }

    @Test
    public void testVoid() throws ContextualError {
        // print(void); PAS OK
        ListExpr list = new ListExpr();
        list.add(exprVoid);

        ListInst printInst = new ListInst();
        printInst.add(new Print(false, list));

        assertThrows(ContextualError.class, () -> {
            printInst.verifyListInst(compiler, null, null, null);
        });
    }

    @Test
    public void testIntBool() throws ContextualError {
        // print(2, true); PAS OK
        ListExpr list = new ListExpr();
        list.add(exprInt);
        list.add(exprBool);

        ListInst printInst = new ListInst();
        printInst.add(new Print(false, list));

        assertThrows(ContextualError.class, () -> {
            printInst.verifyListInst(compiler, null, null, null);
        });
    }

    @Test
    public void testIntFloatBool() throws ContextualError {
        // print(2, 2.0, true); PAS OK
        ListExpr list = new ListExpr();
        list.add(exprInt);
        list.add(exprFloat);
        list.add(exprBool);

        ListInst printInst = new ListInst();
        printInst.add(new Println(true, list));

        assertThrows(ContextualError.class, () -> {
            printInst.verifyListInst(compiler, null, null, null);
        });
    }

    @Test
    public void testIntFloat() throws ContextualError {
        // print(2, 2.0); OK
        ListExpr list = new ListExpr();
        list.add(exprInt);
        list.add(exprFloat);

        ListInst printInst = new ListInst();
        printInst.add(new Print(false, list));

        printInst.verifyListInst(compiler, null, null, null);

        verify(exprInt).verifyExpr(compiler, null, null);
        verify(exprFloat).verifyExpr(compiler, null, null);

    }
}
