package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRLoad;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRSegment;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.deca.ir.operands.IRVariable;

public class TestIRBasic {

    @Test
    public void testSimplePrint() {
        // 1. SETUP : On construit un petit programme IR à la main
        // Équivalent Deca : 
        //    int i = 42;
        //    print(i);

        IRProgram program = new IRProgram(new CompilerOptions());

        // Définition des éléments
        IRVariable varI = new IRVariable("i", IRType.INT, IRSegment.STACK);
        IRScratch reg0 = program.scratch(IRType.INT); // Crée un registre temporaire

        // Instruction 1: i = 42
        // Note: null pour la Location car on est en test manuel
        program.addInstruction(new IRAssign(null, new IRImmediateInt(42), varI));

        // Instruction 2: LOAD i -> reg0
        program.addInstruction(new IRLoad(null, IRType.INT, reg0, varI));

        // Instruction 3: PRINT reg0
        program.addInstruction(new IRPrintValue(null, IRType.INT, reg0));

        // 2. EXECUTION : On lance le simulateur
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        // 3. ASSERT : On vérifie la sortie
        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testAddition() {
        // Test : a = 10 + 20; print(a)
        IRProgram program = new IRProgram(new CompilerOptions());

        IRScratch s0 = program.scratch(IRType.INT);

        // s0 = ADD #10, #20
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(10), new IRImmediateInt(20)));

        // PRINT s0
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("30", simulator.getOutput());
    }

    @Test
    public void testMultipleOperations() {
        // Test : result = (5 + 3) * 2; print(result)
        IRProgram program = new IRProgram(new CompilerOptions());

        IRScratch s0 = program.scratch(IRType.INT);
        IRScratch s1 = program.scratch(IRType.INT);

        // s0 = ADD #5, #3  => s0 = 8
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(5), new IRImmediateInt(3)));

        // s1 = MUL s0, #2  => s1 = 16
        program.addInstruction(new IRBinaryExpr(null, "MUL", s1, s0, new IRImmediateInt(2)));

        // PRINT s1
        program.addInstruction(new IRPrintValue(null, IRType.INT, s1));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("16", simulator.getOutput());
    }

    @Test
    public void testFloatOperations() {
        // Test : f = 3.5 + 2.5; print(f)
        IRProgram program = new IRProgram(new CompilerOptions());

        IRScratch s0 = program.scratch(IRType.FLOAT);

        // s0 = ADD #3.5, #2.5
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateFloat(3.5f), new IRImmediateFloat(2.5f)));

        // PRINT s0
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
    public void testPrintString() {
        // Test : print("Hello World")
        IRProgram program = new IRProgram(new CompilerOptions());

        // Créer une chaîne dans le segment de données
        IRLabel strLabel = program.string("Hello World");

        // PRINT_STRING strLabel
        program.addInstruction(new IRPrintString(null, strLabel));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("Hello World", simulator.getOutput());
    }

    @Test
    public void testComplexProgram() {
        // Test : 
        //   int x = 10;
        //   int y = 20;
        //   int sum = x + y;
        //   print("Sum: ");
        //   print(sum);

        IRProgram program = new IRProgram(new CompilerOptions());

        IRVariable varX = new IRVariable("x", IRType.INT, IRSegment.STACK);
        IRVariable varY = new IRVariable("y", IRType.INT, IRSegment.STACK);
        IRScratch s0 = program.scratch(IRType.INT);
        IRScratch s1 = program.scratch(IRType.INT);
        IRScratch s2 = program.scratch(IRType.INT);

        // x = 10
        program.addInstruction(new IRAssign(null, new IRImmediateInt(10), varX));

        // y = 20
        program.addInstruction(new IRAssign(null, new IRImmediateInt(20), varY));

        // LOAD x -> s0
        program.addInstruction(new IRLoad(null, IRType.INT, s0, varX));

        // LOAD y -> s1
        program.addInstruction(new IRLoad(null, IRType.INT, s1, varY));

        // s2 = ADD s0, s1
        program.addInstruction(new IRBinaryExpr(null, "ADD", s2, s0, s1));

        // PRINT_STRING "Sum: "
        IRLabel strLabel = program.string("Sum: ");
        program.addInstruction(new IRPrintString(null, strLabel));

        // PRINT s2
        program.addInstruction(new IRPrintValue(null, IRType.INT, s2));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("Sum: 30", simulator.getOutput());
    }

    @Test
    public void testDivision() {
        // Test : result = 100 / 4; print(result)
        IRProgram program = new IRProgram(new CompilerOptions());

        IRScratch s0 = program.scratch(IRType.INT);

        // s0 = DIV #100, #4
        program.addInstruction(new IRBinaryExpr(null, "DIV", s0, new IRImmediateInt(100), new IRImmediateInt(4)));

        // PRINT s0
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("25", simulator.getOutput());
    }

    @Test
    public void testModulo() {
        // Test : result = 17 % 5; print(result)
        IRProgram program = new IRProgram(new CompilerOptions());

        IRScratch s0 = program.scratch(IRType.INT);

        // s0 = MOD #17, #5
        program.addInstruction(new IRBinaryExpr(null, "MOD", s0, new IRImmediateInt(17), new IRImmediateInt(5)));

        // PRINT s0
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("2", simulator.getOutput());
    }
}