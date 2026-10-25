package fr.ensimag.riscv.pseudocode;

import java.io.PrintStream;
import java.util.Objects;

/**
 * Line of code in an RiscV program.
 *
 * @author Nico
 * @date 03/01/2026
 */
public class Line extends AbstractLine {
    private final String content;

    public Line(String content) {
        this.content = Objects.requireNonNull(content, "Line content cannot be null");
    }

    @Override
    void display(PrintStream s) {
        s.println(content);
    }

    @Override
    public String toString() {
        return content;
    }
}
