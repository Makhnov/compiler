package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * Test for the Identifier node using mockito.
 * Regle (0.1)
 *
 * @author Ensimag
 * @date 05/01/2026
 */
public class TestIdentifierUnitaire {

    DecacCompiler compiler;
    Identifier ident;
    Symbol symbol;
    final Type INT = new IntType(null);

    @Mock
    EnvironmentExp localEnv;

    @Mock
    ExpDefinition expDefinition;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
        symbol = compiler.createSymbol("x");

        when(expDefinition.getType()).thenReturn(INT);
        when(expDefinition.isExpression()).thenReturn(true);

        ident = new Identifier(symbol);
    }

    @Test
    public void testIdentifierDefined() throws ContextualError {
        // Cas symbol dans l'env
        when(localEnv.get(symbol)).thenReturn(expDefinition);

        Type resultType = ident.verifyExpr(compiler, localEnv, null);

        assertTrue(resultType.isInt());

        assertSame(expDefinition, ident.getDefinition());
        verify(localEnv).get(symbol);
    }

    @Test
    public void testIdentifierUndefined() {
        // Cas symbol pas dans l'env
        when(localEnv.get(symbol)).thenReturn(null);

        assertThrows(ContextualError.class, () -> {
            ident.verifyExpr(compiler, localEnv, null);
        });

        verify(localEnv).get(symbol);
    }
}