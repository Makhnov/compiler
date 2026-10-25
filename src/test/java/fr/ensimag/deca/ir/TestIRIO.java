package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.instructions.IRLoad;
import fr.ensimag.deca.ir.instructions.IRPrintChar;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRSegment;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.deca.ir.operands.IRVariable;

/**
 * Tests unitaires pour les opérations d'entrée/sortie (IR).
 */
public class TestIRIO {

    @Test
    public void testPrintChar() {
        // Test: print('A')
        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new IRPrintChar(null, 'A'));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("A", simulator.getOutput());
    }

    @Test
    public void testPrintCharFromVariable() {
        // Test: char c = 'B'; print(c);
        IRProgram program = new IRProgram(new CompilerOptions());
        char c = 'c';
        program.addInstruction(new IRPrintChar(null, c));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("c", simulator.getOutput());
    }

    @Test
    public void testPrintMultipleChars() {
        // Test: print('H'); print('i'); print('!');
        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new IRPrintChar(null, 'H'));
        program.addInstruction(new IRPrintChar(null, 'i'));
        program.addInstruction(new IRPrintChar(null, '!'));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("Hi!", simulator.getOutput());
    }

    @Test
    public void testPrintCharAndString() {
        // Test: print('A'); print("BC");
        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new IRPrintChar(null, 'A'));
        IRLabel strLabel = program.string("BC");
        program.addInstruction(new IRPrintString(null, strLabel));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("ABC", simulator.getOutput());
    }

    @Test
    public void testPrintCharSpecialCharacters() {
        // Test: print('\n'); print('\t'); print('X');
        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new IRPrintChar(null, '\n'));
        program.addInstruction(new IRPrintChar(null, '\t'));
        program.addInstruction(new IRPrintChar(null, 'X'));
        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }
        assertEquals("\n\tX", simulator.getOutput());
    }

    @Test
    public void testPrintInt() {
        // Test: print(42)
        IRProgram program = new IRProgram(new CompilerOptions());

        program.addInstruction(new IRPrintValue(null, IRType.INT, new IRImmediateInt(42)));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("42", simulator.getOutput());
    }

    @Test
    public void testPrintFloat() {
        // Test: print(3.14)
        IRProgram program = new IRProgram(new CompilerOptions());

        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, new IRImmediateFloat(3.14f)));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("3.14", simulator.getOutput());
    }

    @Test
    public void testPrintString() {
        // Test: print("Hello, World!")
        IRProgram program = new IRProgram(new CompilerOptions());

        IRLabel strLabel = program.string("Hello, World!");
        program.addInstruction(new IRPrintString(null, strLabel));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("Hello, World!", simulator.getOutput());
    }

    @Test
    public void testMultiplePrints() {
        // Test: print("Answer: "); print(42); print("!");
        IRProgram program = new IRProgram(new CompilerOptions());

        IRLabel str1 = program.string("Answer: ");
        program.addInstruction(new IRPrintString(null, str1));

        program.addInstruction(new IRPrintValue(null, IRType.INT, new IRImmediateInt(42)));

        IRLabel str2 = program.string("!");
        program.addInstruction(new IRPrintString(null, str2));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("Answer: 42!", simulator.getOutput());
    }

    @Test
    public void testPrintFromVariable() {
        // Test: int x = 100; print(x);
        IRProgram program = new IRProgram(new CompilerOptions());

        IRVariable varX = new IRVariable("x", IRType.INT, IRSegment.STACK);
        IRScratch s0 = program.scratch(IRType.INT);

        program.addInstruction(new IRAssign(null, new IRImmediateInt(100), varX));
        program.addInstruction(new IRLoad(null, IRType.INT, s0, varX));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("100", simulator.getOutput());
    }

    @Test
    public void testPrintMultipleValues() {
        // Test: print(1); print(2); print(3);
        IRProgram program = new IRProgram(new CompilerOptions());

        program.addInstruction(new IRPrintValue(null, IRType.INT, new IRImmediateInt(1)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, new IRImmediateInt(2)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, new IRImmediateInt(3)));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("123", simulator.getOutput());
    }

    @Test
    public void testPrintFormattedOutput() {
        // Test: print("x = "); print(42); print(", y = "); print(3.14);
        IRProgram program = new IRProgram(new CompilerOptions());

        program.addInstruction(new IRPrintString(null, program.string("x = ")));

        program.addInstruction(new IRPrintValue(null, IRType.INT, new IRImmediateInt(42)));

        program.addInstruction(new IRPrintString(null, program.string(", y = ")));

        program.addInstruction(new IRPrintValue(null, IRType.FLOAT, new IRImmediateFloat(3.14f)));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("x = 42, y = 3.14", simulator.getOutput());
    }

    @Test
    public void testPrintEmptyString() {
        // Test: print("")
        IRProgram program = new IRProgram(new CompilerOptions());

        IRLabel strLabel = program.string("");
        program.addInstruction(new IRPrintString(null, strLabel));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("", simulator.getOutput());
    }

    @Test
    public void testPrintSpecialCharacters() {
        // Test: print("Hello\nWorld")
        IRProgram program = new IRProgram(new CompilerOptions());

        IRLabel strLabel = program.string("Hello\nWorld");
        program.addInstruction(new IRPrintString(null, strLabel));

        IRSimulator simulator = new IRSimulator(program);
        try {
            simulator.run();
        } catch (IRError e) {
            throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
        }

        assertEquals("Hello\nWorld", simulator.getOutput());
    }
}
