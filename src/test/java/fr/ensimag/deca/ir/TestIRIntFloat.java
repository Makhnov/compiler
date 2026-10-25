package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Tests unitaires pour les opérations arithmétiques sur entiers (IR).
 */
public class TestIRIntFloat {

    @Test
    public void testAdd() {
        // Test: s0 = 10 + 2.5 = 12.5
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(10), new IRImmediateFloat(2.5f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("12.5", simulator.getOutput());
    }

    @Test
    public void testMul() {
        // Test: s0 = 4 * 2.5 = 10.0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRBinaryExpr(null, "MUL", s0, new IRImmediateInt(4), new IRImmediateFloat(2.5f)));
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
    public void testSub() {
        // Test: s0 = 10 - 2.5 = 7.5
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRBinaryExpr(null, "SUB", s0, new IRImmediateInt(10), new IRImmediateFloat(2.5f)));
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
    public void testDiv() {
        // Test: s0 = 10 / 2.5 = 4.0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.FLOAT);
        program.addInstruction(new IRBinaryExpr(null, "DIV", s0, new IRImmediateInt(10), new IRImmediateFloat(2.5f)));
        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, s0));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("4.0", simulator.getOutput());
    }
}