package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.IntLiteral;
import fr.ensimag.deca.tree.FloatLiteral;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

import org.junit.jupiter.api.Test;

// Helper constants for registers
import static fr.ensimag.ima.pseudocode.Register.getR;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.OptionalInt;
import java.util.OptionalDouble;

/**
 * Tests for OptimizationsManager
 * Covers optimization levels and various optimization methods
 *
 * @author gl25
 * @date 15/01/2026
 */
public class TestOptimizationsManager {

    private DecacCompiler compilerNoOpt;
    private DecacCompiler compilerBasic;
    private DecacCompiler compilerMedium;
    private DecacCompiler compilerMaximum;

    @Mock
    private AbstractExpr mockExpr;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Compiler sans optimisation
        compilerNoOpt = new DecacCompiler(null, null);

        // Compiler avec optimisation basic (-o)
        CompilerOptions optBasic = new CompilerOptions();
        optBasic.parseArgs(new String[] { "-o", "src/test/deca/codegen/valid/created/sans-objet/do_nothing.deca" });
        compilerBasic = new DecacCompiler(optBasic, null);

        // Compiler avec optimisation medium (-o -o)
        CompilerOptions optMedium = new CompilerOptions();
        optMedium.parseArgs(new String[] { "-o", "-o", "src/test/deca/codegen/valid/created/sans-objet/do_nothing.deca" });
        compilerMedium = new DecacCompiler(optMedium, null);

        // Compiler avec optimisation maximum (-o -o -o)
        CompilerOptions optMax = new CompilerOptions();
        optMax.parseArgs(new String[] { "-o", "-o", "-o", "src/test/deca/codegen/valid/created/sans-objet/do_nothing.deca" });
        compilerMaximum = new DecacCompiler(optMax, null);
    }

    // ==================== Tests des niveaux d'optimisation ====================

    @Test
    public void testOptModeNone() {
        OptimizationsManager opt = compilerNoOpt.optis();
        assertEquals(0, opt.optMode());
        assertFalse(opt.basic());
        assertFalse(opt.medium());
        assertFalse(opt.maximum());
    }

    @Test
    public void testOptModeBasic() {
        OptimizationsManager opt = compilerBasic.optis();
        assertEquals(1, opt.optMode());
        assertTrue(opt.basic());
        assertFalse(opt.medium());
        assertFalse(opt.maximum());
    }

    @Test
    public void testOptModeMedium() {
        OptimizationsManager opt = compilerMedium.optis();
        assertEquals(2, opt.optMode());
        assertTrue(opt.basic());
        assertTrue(opt.medium());
        assertFalse(opt.maximum());
    }

    @Test
    public void testOptModeMaximum() {
        OptimizationsManager opt = compilerMaximum.optis();
        assertEquals(3, opt.optMode());
        assertTrue(opt.basic());
        assertTrue(opt.medium());
        assertTrue(opt.maximum());
    }

    // ==================== Tests powerOf2Expr ====================

    @Test
    public void testPowerOf2Expr_NoOptimization() {
        OptimizationsManager opt = compilerNoOpt.optis();
        IntLiteral lit = new IntLiteral(8);
        assertFalse(opt.powerOf2Expr(lit));
    }

    @Test
    public void testPowerOf2Expr_WithBasicOpt_PowerOf2() {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral lit = new IntLiteral(8); // 2^3
        assertTrue(opt.powerOf2Expr(lit));
    }

    @Test
    public void testPowerOf2Expr_WithBasicOpt_NotPowerOf2() {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral lit = new IntLiteral(7);
        assertFalse(opt.powerOf2Expr(lit));
    }

    @ParameterizedTest @ValueSource(ints = { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024 })
    public void testPowerOf2Expr_VariousPowersOf2(int value) {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral lit = new IntLiteral(value);
        assertTrue(opt.powerOf2Expr(lit));
    }

    @ParameterizedTest @ValueSource(ints = { 0, 3, 5, 6, 7, 9, 10, 12, 15, 100 })
    public void testPowerOf2Expr_NotPowersOf2(int value) {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral lit = new IntLiteral(value);
        assertFalse(opt.powerOf2Expr(lit));
    }

    @Test
    public void testPowerOf2Expr_Unpredictable() {
        OptimizationsManager opt = compilerBasic.optis();
        when(mockExpr.predictable()).thenReturn(false);
        assertFalse(opt.powerOf2Expr(mockExpr));
    }

    // ==================== Tests pow2Mul ====================

    @Test
    public void testPow2Mul_NoOptimization() {
        OptimizationsManager opt = compilerNoOpt.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Mul(target, rhs));
    }

    @Test
    public void testPow2Mul_WithBasicOpt_PowerOf2() {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral rhs = new IntLiteral(8); // 2^3
        GPRegister target = getR(2);
        assertTrue(opt.pow2Mul(target, rhs));
    }

    @Test
    public void testPow2Mul_WithBasicOpt_PowerOf1() {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral rhs = new IntLiteral(1); // 2^0
        GPRegister target = getR(2);
        assertTrue(opt.pow2Mul(target, rhs)); // Should do nothing but return true
    }

    @Test
    public void testPow2Mul_WithBasicOpt_NotPowerOf2() {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral rhs = new IntLiteral(7);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Mul(target, rhs));
    }

    @Test
    public void testPow2Mul_WithNonIntExpr() {
        OptimizationsManager opt = compilerBasic.optis();
        when(mockExpr.getIntegerValue(compilerBasic)).thenReturn(OptionalInt.empty());
        GPRegister target = getR(2);
        assertFalse(opt.pow2Mul(target, mockExpr));
    }

    // ==================== Tests pow2Div ====================

    @Test
    public void testPow2Div_NoOptimization() {
        OptimizationsManager opt = compilerNoOpt.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Div(target, rhs));
    }

    @Test
    public void testPow2Div_WithMediumOpt_NotEnough() {
        OptimizationsManager opt = compilerMedium.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Div(target, rhs)); // Requires MAXIMUM
    }

    @Test
    public void testPow2Div_WithMaxOpt_PowerOf2() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(8); // 2^3
        GPRegister target = getR(2);
        assertTrue(opt.pow2Div(target, rhs));
    }

    @Test
    public void testPow2Div_WithMaxOpt_PowerOf1() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(1); // 2^0, division by 1
        GPRegister target = getR(2);
        assertTrue(opt.pow2Div(target, rhs)); // Should return true without doing anything
    }

    @Test
    public void testPow2Div_WithMaxOpt_NotPowerOf2() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(7);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Div(target, rhs));
    }

    @Test
    public void testPow2Div_WithNonIntExpr() {
        OptimizationsManager opt = compilerMaximum.optis();
        when(mockExpr.getIntegerValue(compilerMaximum)).thenReturn(OptionalInt.empty());
        GPRegister target = getR(2);
        assertFalse(opt.pow2Div(target, mockExpr));
    }

    // ==================== Tests pow2Mod ====================

    @Test
    public void testPow2Mod_NoOptimization() {
        OptimizationsManager opt = compilerNoOpt.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        GPRegister scratch = getR(3);
        assertFalse(opt.pow2Mod(target, rhs, scratch));
    }

    @Test
    public void testPow2Mod_WithMediumOpt_NotEnough() {
        OptimizationsManager opt = compilerMedium.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        GPRegister scratch = getR(3);
        assertFalse(opt.pow2Mod(target, rhs, scratch)); // Requires MAXIMUM
    }

    @Test
    public void testPow2Mod_WithMaxOpt_PowerOf2() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(8); // 2^3
        GPRegister target = getR(2);
        GPRegister scratch = getR(3);
        assertTrue(opt.pow2Mod(target, rhs, scratch));
    }

    @Test
    public void testPow2Mod_WithMaxOpt_ModBy1() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(1); // x % 1 = 0
        GPRegister target = getR(2);
        GPRegister scratch = getR(3);
        assertTrue(opt.pow2Mod(target, rhs, scratch));
    }

    @Test
    public void testPow2Mod_WithMaxOpt_NotPowerOf2() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(7);
        GPRegister target = getR(2);
        GPRegister scratch = getR(3);
        assertFalse(opt.pow2Mod(target, rhs, scratch));
    }

    @Test
    public void testPow2Mod_NullScratch() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Mod(target, rhs, null));
    }

    @Test
    public void testPow2Mod_SameScratchAsTarget() {
        OptimizationsManager opt = compilerMaximum.optis();
        IntLiteral rhs = new IntLiteral(8);
        GPRegister target = getR(2);
        assertFalse(opt.pow2Mod(target, rhs, target)); // scratch == target
    }

    // ==================== Tests slash2 (float division by 2) ====================

    @Test
    public void testSlash2_NoOptimization() {
        OptimizationsManager opt = compilerNoOpt.optis();
        FloatLiteral rhs = new FloatLiteral(2.0f);
        GPRegister target = getR(2);
        assertFalse(opt.slash2(target, rhs));
    }

    @Test
    public void testSlash2_WithBasicOpt_DivBy2() {
        OptimizationsManager opt = compilerBasic.optis();
        FloatLiteral rhs = new FloatLiteral(2.0f);
        GPRegister target = getR(2);
        assertTrue(opt.slash2(target, rhs));
    }

    @Test
    public void testSlash2_WithBasicOpt_NotDivBy2() {
        OptimizationsManager opt = compilerBasic.optis();
        FloatLiteral rhs = new FloatLiteral(3.0f);
        GPRegister target = getR(2);
        assertFalse(opt.slash2(target, rhs));
    }

    @Test
    public void testSlash2_Unpredictable() {
        OptimizationsManager opt = compilerBasic.optis();
        when(mockExpr.predictable()).thenReturn(false);
        GPRegister target = getR(2);
        assertFalse(opt.slash2(target, mockExpr));
    }

    @Test
    public void testSlash2_NonFloatExpr() {
        OptimizationsManager opt = compilerBasic.optis();
        when(mockExpr.predictable()).thenReturn(true);
        when(mockExpr.getFloatValue(compilerBasic)).thenReturn(OptionalDouble.empty());
        GPRegister target = getR(2);
        assertFalse(opt.slash2(target, mockExpr));
    }

    // ==================== Tests commute ====================

    @Test
    public void testCommute_NoOptimization() throws GenerationError {
        OptimizationsManager opt = compilerNoOpt.optis();
        IntLiteral lhs = new IntLiteral(5);
        IntLiteral rhs = new IntLiteral(10);
        assertNull(opt.commute(lhs, rhs, 2));
    }

    @Test
    public void testCommute_Unpredictable() throws GenerationError {
        OptimizationsManager opt = compilerBasic.optis();
        IntLiteral lhs = new IntLiteral(5);
        when(mockExpr.predictable()).thenReturn(false);
        assertNull(opt.commute(lhs, mockExpr, 2));
    }

    @Test
    public void testCommute_BothUnpredictable() throws GenerationError {
        OptimizationsManager opt = compilerBasic.optis();
        AbstractExpr mockLhs = mock(AbstractExpr.class);
        when(mockLhs.predictable()).thenReturn(false);
        when(mockExpr.predictable()).thenReturn(true);
        assertNull(opt.commute(mockLhs, mockExpr, 2));
    }

    // ==================== Test avec null options ====================

    @Test
    public void testNullOptions() {
        DecacCompiler compilerNull = new DecacCompiler(null, null);
        OptimizationsManager opt = compilerNull.optis();
        assertEquals(0, opt.optMode());
        assertFalse(opt.basic());
        assertFalse(opt.medium());
        assertFalse(opt.maximum());
    }
}
