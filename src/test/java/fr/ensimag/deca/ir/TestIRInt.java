package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

/**
 * Tests unitaires pour les opérations arithmétiques sur entiers (IR).
 */
public class TestIRInt {

    @Test
    public void testAddIntegers() {
        // Test: s0 = 15 + 27
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(15), new IRImmediateInt(27)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testSubIntegers() {
        // Test: s0 = 100 - 58
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRBinaryExpr(null, "SUB", s0, new IRImmediateInt(100), new IRImmediateInt(58)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testMulIntegers() {
        // Test: s0 = 6 * 7
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRBinaryExpr(null, "MUL", s0, new IRImmediateInt(6), new IRImmediateInt(7)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testDivIntegers() {
        // Test: s0 = 84 / 2
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRBinaryExpr(null, "DIV", s0, new IRImmediateInt(84), new IRImmediateInt(2)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testModIntegers() {
        // Test: s0 = 47 % 5 = 2
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRBinaryExpr(null, "MOD", s0, new IRImmediateInt(47), new IRImmediateInt(5)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("2", simulator.getOutput());
    }

    @Test
    public void testComplexExpression() {
        // Test: s0 = (10 + 5) * 3 - 3 = 45 - 3 = 42
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        IRScratch s1 = program.scratch(IRType.INT);
        IRScratch s2 = program.scratch(IRType.INT);

        // s0 = 10 + 5 = 15
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(10), new IRImmediateInt(5)));

        // s1 = s0 * 3 = 45
        program.addInstruction(new IRBinaryExpr(null, "MUL", s1, s0, new IRImmediateInt(3)));

        // s2 = s1 - 3 = 42
        program.addInstruction(new IRBinaryExpr(null, "SUB", s2, s1, new IRImmediateInt(3)));

        program.addInstruction(new IRPrintValue(null, IRType.INT, s2));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testNegativeNumbers() {
        // Test: s0 = 10 - 52 = -42
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRBinaryExpr(null, "SUB", s0, new IRImmediateInt(10), new IRImmediateInt(52)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("-42", simulator.getOutput());
    }

    @Test
    public void testChainedOperations() {
        // Test: s3 = ((20 + 10) / 2) * 3 = 45
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        IRScratch s1 = program.scratch(IRType.INT);
        IRScratch s2 = program.scratch(IRType.INT);

        // s0 = 20 + 10 = 30
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(20), new IRImmediateInt(10)));

        // s1 = s0 / 2 = 15
        program.addInstruction(new IRBinaryExpr(null, "DIV", s1, s0, new IRImmediateInt(2)));

        // s2 = s1 * 3 = 45
        program.addInstruction(new IRBinaryExpr(null, "MUL", s2, s1, new IRImmediateInt(3)));

        program.addInstruction(new IRPrintValue(null, IRType.INT, s2));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("45", simulator.getOutput());
    }
}
