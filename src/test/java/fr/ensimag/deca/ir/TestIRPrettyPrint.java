package fr.ensimag.deca.ir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.display.IRPrettyPrinter;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

public class TestIRPrettyPrint {
    @Test
    public void testPrettyPrint() {
        // Programme IR :
        // s0 = ADD #1, #2
        // PRINT_INT s0
        IRProgram program = new IRProgram(new CompilerOptions());
        IRScratch s0 = program.scratch(IRType.INT);
        program.addInstruction(new IRBinaryExpr(null, "ADD", s0, new IRImmediateInt(1), new IRImmediateInt(2)));
        program.addInstruction(new IRPrintValue(null, IRType.INT, s0));

        // Capture la sortie du pretty-printer
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new IRPrettyPrinter().display(program, ps);
        String expectedOutput = """
                .data

                .stack
                \t(empty)

                .heap
                \t(empty)

                .text
                \tADD s0, #1, #2
                \tPRINT_INT s0
                """;

        assertEquals(expectedOutput, baos.toString());
    }

    @Test
    public void testPrettyPrintEmptyProgram() {
        // Programme IR vide
        IRProgram program = new IRProgram(new CompilerOptions());
        // Capture la sortie du pretty-printer
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new IRPrettyPrinter().display(program, ps);
        String expectedOutput = """
                .data

                .stack
                \t(empty)

                .heap
                \t(empty)

                .text
                """;
        assertEquals(expectedOutput, baos.toString());
    }
}