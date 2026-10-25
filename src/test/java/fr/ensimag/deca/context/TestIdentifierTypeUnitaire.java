package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.Identifier;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

/**
 * Test for the Identifier node using mockito.
 * Regle (0.2)
 *
 * @author Ensimag
 * @date 05/01/2026
 */
public class TestIdentifierTypeUnitaire {
    DecacCompiler compiler;
    Identifier ident;
    Symbol symbol;
    final Type INT = new IntType(null);
    TypeDefinition typeDef;

    @Mock
    EnvironmentType envTypeMock;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);

        // Injection de envTypeMockMock dans compiler.environmentType qui est final.
        // "compiler.environmentType = envTypeMock"
        Field field = DecacCompiler.class.getDeclaredField("environmentType");
        field.setAccessible(true);
        field.set(compiler, envTypeMock);

        symbol = compiler.createSymbol("int");
        ident = new Identifier(symbol);

        typeDef = new TypeDefinition(INT, Location.BUILTIN) {
        };
    }

    @Test
    public void testCorrectCond() throws ContextualError {
        // Cas type dans l'env
        when(envTypeMock.defOfType(symbol)).thenReturn(typeDef);

        Type resultType = ident.verifyType(compiler);

        assertTrue(resultType.isInt(), "Le type retourné doit être int");
        assertSame(typeDef, ident.getDefinition(), "La définition doit être attachée à l'identifiant");
        verify(envTypeMock).defOfType(symbol);
    }

    @Test
    public void testIncorrectCond() {
        // Cas type pas dans l'env
        when(envTypeMock.defOfType(symbol)).thenReturn(null);

        Exception exception = assertThrows(ContextualError.class, () -> {
            ident.verifyType(compiler);
        });

        verify(envTypeMock).defOfType(symbol);
    }
}