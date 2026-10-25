package fr.ensimag.deca.ir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRExit;

/**
 * Tests unitaires pour les opérations arithmétiques sur entiers (IR).
 */
public class TestIRExit {
    @Test
    public void testExit() {
        // Test: exit

        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new IRExit(null));

        IRSimulator simulator = new IRSimulator(program);

        try {
            try {
                simulator.run();
            } catch (IRError e) {
                throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
            }
            assertEquals("A", simulator.getOutput());
        } catch (RuntimeException e) {
            assertEquals("Simulation terminée avec IRExit", e.getMessage());
        }
    }

    @Test
    public void testExitAfterPrint() {
        // Test: print 'A'; exit
        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new fr.ensimag.deca.ir.instructions.IRPrintChar(null, 'A'));
        program.addInstruction(new IRExit(null));
        IRSimulator simulator = new IRSimulator(program);
        try {
            try {
                simulator.run();
            } catch (IRError e) {
                throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
            }
            assertEquals("A", simulator.getOutput());
        } catch (RuntimeException e) {
            assertEquals("Simulation terminée avec IRExit", e.getMessage());
        }
    }

    @Test
    public void testMultipleExits() {
        // Test: exit; exit; print 'B'
        IRProgram program = new IRProgram(new CompilerOptions());
        program.addInstruction(new IRExit(null));
        program.addInstruction(new IRExit(null));
        program.addInstruction(new fr.ensimag.deca.ir.instructions.IRPrintChar(null, 'B'));
        IRSimulator simulator = new IRSimulator(program);
        try {
            try {
                simulator.run();
            } catch (IRError e) {
                throw new RuntimeException("Erreur durant la simulation : " + e.getMessage());
            }
        } catch (RuntimeException e) {
            assertEquals("Simulation terminée avec IRExit", e.getMessage());
        }
    }
}