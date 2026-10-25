package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

/**
 * Tests unitaires pour les opérations sur flottants et conversions (IR).
 */
public class TestIRFloat {

    @Test
    public void testAddFloats() {
        // Test: s0 = 3.5 + 2.5 = 6.0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateFloat(3.5f), new IRImmediateFloat(2.5f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("6.0", simulator.getOutput());
    }

    @Test
    public void testSubFloats() {
        // Test: s0 = 10.5 - 3.25 = 7.25
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "SUB", s0, new IRImmediateFloat(10.5f), new IRImmediateFloat(3.25f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("7.25", simulator.getOutput());
    }

    @Test
    public void testMulFloats() {
        // Test: s0 = 2.5 * 4.0 = 10.0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "MUL", s0, new IRImmediateFloat(2.5f), new IRImmediateFloat(4.0f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("10.0", simulator.getOutput());
    }

    @Test
    public void testDivFloats() {
        // Test: s0 = 10.0 / 4.0 = 2.5
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "DIV", s0, new IRImmediateFloat(10.0f), new IRImmediateFloat(4.0f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("2.5", simulator.getOutput());
    }

    @Test
    public void testMixedIntFloat() {
        // Test: s0 = 5 (int) + 2.5 (float) = 7.5
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(5), new IRImmediateFloat(2.5f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("7.5", simulator.getOutput());
    }

    @Test
    public void testComplexFloatExpression() {
        // Test: s2 = (3.0 + 2.0) * 1.5 = 7.5
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        IRScratch s1 = program.scratch(IRType.FLOAT);

        // s0 = 3.0 + 2.0 = 5.0
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateFloat(3.0f), new IRImmediateFloat(2.0f)));

        // s1 = s0 * 1.5 = 7.5
        program.addInstruction(new IRBinaryExpr(null, "MUL", s1, s0, new IRImmediateFloat(1.5f)));

        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s1));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("7.5", simulator.getOutput());
    }

    @Test
    public void testNegativeFloats() {
        // Test: s0 = 5.0 - 10.5 = -5.5
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "SUB", s0, new IRImmediateFloat(5.0f), new IRImmediateFloat(10.5f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("-5.5", simulator.getOutput());
    }

    @Test
    public void testFloatPrecision() {
        // Test: s0 = 0.1 + 0.2 (vérifie que le résultat est proche de 0.3)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateFloat(0.1f), new IRImmediateFloat(0.2f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        // Le résultat devrait commencer par "0.3"
        String output = simulator.getOutput();
        assertTrue(output.startsWith("0.3"), "Expected result to start with 0.3, got: " + output);
    }
}
