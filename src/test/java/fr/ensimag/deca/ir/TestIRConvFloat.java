package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRConvFloat;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

/**
 * Tests unitaires pour les opérations arithmétiques sur entiers (IR).
 */
public class TestIRConvFloat {
    @Test
    public void testConvIntToFloat() {
        // Test: s0 = (float) 42; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);

        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(42)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42.0", simulator.getOutput());
    }

    @Test
    public void testConvNegativeIntToFloat() {
        // Test: s0 = (float) -7; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(-7)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("-7.0", simulator.getOutput());
    }

    @Test
    public void testConvZeroIntToFloat() {
        // Test: s0 = (float) 0; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(0)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("0.0", simulator.getOutput());
    }

    @Test
    public void testConvLargeIntToFloat() {
        // Test: s0 = (float) 123456789; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(123456789)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        // 123456789 => float : 1.2345679E8
        assertEquals("1.2345679E8", simulator.getOutput());
    }

    @Test
    public void testConvMinIntToFloat() {
        // Test: s0 = (float) Integer.MIN_VALUE; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(Integer.MIN_VALUE)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        // valeur min int : -2147483648 => float : -2.1474836E9
        assertEquals("-2.1474836E9", simulator.getOutput());
    }

    @Test
    public void testConvMaxIntToFloat() {
        // Test: s0 = (float) Integer.MAX_VALUE; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(Integer.MAX_VALUE)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        // valeur max int : 2147483647 => float : 2.1474836E9
        assertEquals("2.1474836E9", simulator.getOutput());
    }

    @Test
    public void testMultipleConvIntToFloat() {
        // Test: s0 = (float) 10; s1 = (float)
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        IRScratch s1 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(10)));
        program.addInstruction(new IRConvFloat(null, s1, new IRImmediateInt(-20)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s1));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("10.0-20.0", simulator.getOutput());
    }

    @Test
    public void testConvIntToFloatOneRegister() {
        // Test: s0 = (float) 1; s0 = (float) s0 + 1; print s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(1)));
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(2)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("2.0", simulator.getOutput());
    }

    @Test
    public void testConvIntToFloatWithOtherInstructions() {
        // Test: s0 = (float) 5; s1 = s0 + 3; print s1
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        IRScratch s1 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRConvFloat(null, s0, new IRImmediateInt(5)));
        program.addInstruction(new IRBinaryExpr(null, "ADD", s1, s0, new IRImmediateFloat(3)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s1));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("8.0", simulator.getOutput());
    }
}