package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.instructions.IRUnaryExpr;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

public class TestIRUnaryExpr {

    @Test
    public void testNeg1() {
        // Test: -1
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateInt(1)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("-1", simulator.getOutput());
    }

    @Test
    public void testNeg0() {
        // Test: -0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateInt(0)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput());
    }

    @Test
    public void testNegNegative() {
        // Test: -(-5)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateInt(-5)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("5", simulator.getOutput());
    }

    @Test
    public void testNegNegFloat() {
        // Test: -(-3.14)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateFloat(-3.14f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("3.14", simulator.getOutput());
    }

    @Test
    public void testNot0() {
        // Test: !0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.BOOLEAN);
        program.addInstruction(new IRUnaryExpr(null, "NOT", s0, new IRImmediateInt(0)));
        program.addInstruction(new IRPrintValue(null, IRType.BOOLEAN, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput());
    }

    @Test
    public void testNot1() {
        // Test: !1
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.BOOLEAN);
        program.addInstruction(new IRUnaryExpr(null, "NOT", s0, new IRImmediateInt(1)));
        program.addInstruction(new IRPrintValue(null, IRType.BOOLEAN, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput());
    }

    @Test
    public void testNotNonZero() {
        // Test: !42
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.BOOLEAN);
        program.addInstruction(new IRUnaryExpr(null, "NOT", s0, new IRImmediateInt(42)));
        program.addInstruction(new IRPrintValue(null, IRType.BOOLEAN, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput());
    }

    @Test
    public void testNotNonZeroFloat() {
        // Test: !3.14
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.BOOLEAN);
        program.addInstruction(new IRUnaryExpr(null, "NOT", s0, new IRImmediateFloat(3.14f)));
        program.addInstruction(new IRPrintValue(null, IRType.BOOLEAN, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput());
    }

    @Test
    public void testNotNeg() {
        // Test: !-7
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.BOOLEAN);
        program.addInstruction(new IRUnaryExpr(null, "NOT", s0, new IRImmediateInt(-7)));
        program.addInstruction(new IRPrintValue(null, IRType.BOOLEAN, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput());
    }

    @Test
    public void testNotNegFloat() {
        // Test: !-3.14
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.BOOLEAN);
        program.addInstruction(new IRUnaryExpr(null, "NOT", s0, new IRImmediateFloat(-3.14f)));
        program.addInstruction(new IRPrintValue(null, IRType.BOOLEAN, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput());
    }

    @Test
    public void testNegLargeInt() {
        // Test: -(1910111213)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateInt(1910111213)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("-1910111213", simulator.getOutput());
    }

    @Test
    public void testNegLargeFloat() {
        // Test: -(3.4028235E38)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateFloat(3.4028235E38f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("-3.4028235E38", simulator.getOutput());
    }

    @Test
    public void testNegMaxInt() {
        // Test: -(2147483647)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateInt(2147483647)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("-2147483647", simulator.getOutput());
    }

    @Test
    public void testNegMinInt() {
        // Test: -(-2147483648) // Overflow => -2147483648
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateInt(-2147483648)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("-2147483648", simulator.getOutput());
    }

    @Test
    public void testNegNegMaxFloat() {
        // Test: -(-3.4028235E38)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateFloat(-3.4028235E38f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("3.4028235E38", simulator.getOutput());
    }

    @Test
    public void testNegNegMinFloat() {
        // Test: -(-1.401298E-45)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRUnaryExpr(null, "NEG", s0, new IRImmediateFloat(-1.401298E-45f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1.4E-45", simulator.getOutput());
    }
}