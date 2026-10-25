package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.instructions.IROpCmp;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;

public class TestIROpCmp {
    @Test
    public void testInfTrue() {
        // Test: print(50 < 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "blt", new IRImmediateInt(50), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testSupTrue() {
        // Test: print(150 > 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bgt", new IRImmediateInt(150), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testInfNegative() {
        // Test: print(-100 < -50);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "blt", new IRImmediateInt(-100), new IRImmediateInt(-50), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testSupNegative() {
        // Test: print(-50 > -100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bgt", new IRImmediateInt(-50), new IRImmediateInt(-100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testInfFalse() {
        // Test: print(100 < 50);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "blt", new IRImmediateInt(100), new IRImmediateInt(50), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput()); // Expect false branch executed
    }

    @Test
    public void testSupFalse() {
        // Test: print(50 > 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bgt", new IRImmediateInt(50), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput()); // Expect false branch executed
    }

    @Test
    public void testEqTrue() {
        // Test: print(100 == 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "beq", new IRImmediateInt(100), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testEqFalse() {
        // Test: print(50 == 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "beq", new IRImmediateInt(50), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput()); // Expect false branch executed
    }

    @Test
    public void testNeqTrue() {
        // Test: print(50 != 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bne", new IRImmediateInt(50), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testNeqFalse() {
        // Test: print(100 != 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bne", new IRImmediateInt(100), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput()); // Expect false branch executed
    }

    @Test
    public void testEqNegative() {
        // Test: print(-50 == -50);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "beq", new IRImmediateInt(-50), new IRImmediateInt(-50), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testNeqNegative() {
        // Test: print(-50 != -100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bne", new IRImmediateInt(-50), new IRImmediateInt(-100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    // Advanced tests with, weird cases can be added here
    @Test
    public void testInfEqual() {
        // Test: print(100 < 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "blt", new IRImmediateInt(100), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput()); // Expect false branch executed
    }

    @Test
    public void testSupEqual() {
        // Test: print(100 > 100);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bgt", new IRImmediateInt(100), new IRImmediateInt(100), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("0", simulator.getOutput()); // Expect false branch executed
    }

    // Specific edge cases can be added here
    @Test
    public void testZeroComparison() {
        // Test: print(0 < 1);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "blt", new IRImmediateInt(0), new IRImmediateInt(1), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testLargeNumbers() {
        // Test: print(1000000 > 999999);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bgt", new IRImmediateInt(1000000), new IRImmediateInt(999999), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testMaxIntComparison() {
        // Test: print(Integer.MAX_VALUE > Integer.MAX_VALUE - 1);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "bgt", new IRImmediateInt(Integer.MAX_VALUE), new IRImmediateInt(Integer.MAX_VALUE - 1), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }

    @Test
    public void testMinIntComparison() {
        // Test: print(Integer.MIN_VALUE < Integer.MIN_VALUE + 1);
        IRProgram program = new IRProgram(new CompilerOptions());
        IRLabel labelTrue = new IRLabel("L1");
        IRAbstractInst cmpInst = new IROpCmp(null, "blt", new IRImmediateInt(Integer.MIN_VALUE), new IRImmediateInt(Integer.MIN_VALUE + 1), labelTrue);
        program.addInstruction(cmpInst);
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("1", simulator.getOutput()); // Expect true branch executed
    }
}