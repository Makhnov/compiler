package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.ReadFloat;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

/**
 * Test for the ReadFloat node using mockito, using @Mock and @Before annotations.
 * Context Rule (3.35)
 * 
 * @author Nathan
 * @date 07/01/2026
 */
public class TestReadFloatUnitaire {
    DecacCompiler compiler;

    @BeforeEach
    public void setup() throws ContextualError {
        // MockitoAnnotations.initMocks(this); Déprécié
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
    }

    @Test
    public void testInt() throws ContextualError {
        ReadFloat t = new ReadFloat();
        // check the result
        assertTrue(t.verifyExpr(compiler, null, null).isFloat());
        // check that the mocks have been called properly.
    }
}