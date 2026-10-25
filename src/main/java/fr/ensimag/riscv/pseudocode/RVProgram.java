package fr.ensimag.riscv.pseudocode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * Abstract representation of a RISC-V program, i.e. set of Lines.
 *
 * @author Nico
 * @date 03/01/2026
 */
public class RVProgram {
    private final LinkedList<AbstractLine> data = new LinkedList<AbstractLine>();
    private final LinkedList<AbstractLine> text = new LinkedList<AbstractLine>();

    public void addData(AbstractLine line) {
        data.add(line);
    }

    public void addData(String line) {
        addData(new Line(line));
    }

    public void addText(AbstractLine line) {
        text.add(line);
    }

    public void addText(String line) {
        addText(new Line(line));
    }

    public void addComment(String c) {
        addText(new Line("    # " + c));
    }

    public void assembly(PrintStream s) {
        s.println(".data");
        for (AbstractLine ld: data) {
            ld.display(s);
        }
        s.println();
        s.println(".text");
        s.println(".globl main");
        s.println("main:");
        for (AbstractLine lt: text) {
            lt.display(s);
        }
    }

    public String display() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream s = new PrintStream(out);
        assembly(s);
        return out.toString();
    }
} 

