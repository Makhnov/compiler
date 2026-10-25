package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

/**
 * Test for the ParamDefinition node using mockito, using @Mock and @Before annotations.
 * 
 * @author Nathan
 * @date 08/01/2026
 */
public class TestExpDefinitionUnitaire {

    Type INT;
    Location location;
    ExpDefinition testDef;
    DecacCompiler compiler;
    DAddr testDAddr;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        compiler = new DecacCompiler(null, null);
        // Setup de l'environnement pour les tests
        location = new Location(1, 1, "TEST"); // Localisation random
        INT = new IntType(compiler.createSymbol("int"));

        // Mock de la classe ExpDefinition
        testDef = mock(ExpDefinition.class);
        testDAddr = mock(DAddr.class);

        // Def des méthodes abstraites pour le test
        when(testDef.getLocation()).thenReturn(location);
        when(testDef.getType()).thenReturn(INT);
    }

    @Test
    public void testLocationAndType() {
        assertEquals(location, testDef.getLocation());
        assertEquals(INT, testDef.getType());
    }

    /*
    @Test
    public void testSetAndGetDAddr() {
        // Tester setDAddr et getDAddr
        // Crée un objet DAddr random
        testDef.setDAddr(testDAddr);
        assertEquals(testDAddr, testDef.getDAddr(null)); // Vérifier que l'adresse est correctement assignée et récupérée
    }
    */
}