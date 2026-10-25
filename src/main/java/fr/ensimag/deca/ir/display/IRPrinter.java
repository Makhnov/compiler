package fr.ensimag.deca.ir.display;

import java.io.PrintStream;

import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Utility class for printing IR instructions and data in assembly-like format.
 * Handles indentation, escaping of special characters, and formatting.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRPrinter {
    private final IndentPrintStream out;

    public IRPrinter(PrintStream ps) {
        this.out = new IndentPrintStream(ps);
    }

    public void section(String name) {
        out.println(name);
    }

    public void blank() {
        out.println();
    }

    public void indent() {
        out.indent();
    }

    public void unindent() {
        out.unindent();
    }

    public void data(IRLabel label, String value) {        
        out.println(label.name() + ": .string \"" + escapeString(value) + "\"");
    }

    public void inst(String name) {
        out.println(name);
    }

    public void inst(String name, String operand) {
        out.println(name + " " + operand);
    }

    public void inst(String name, String op1, String op2) {
        out.println(name + " " + op1 + ", " + op2);
    }

    public void inst(String name, String op1, String op2, String op3) {
        out.println(name + " " + op1 + ", " + op2 + ", " + op3);
    }
    
    public String escapeString(String s) {
        StringBuilder b = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> b.append("\\\\");
                case '"'  -> b.append("\\\"");
                case '\n' -> b.append("\\n");
                case '\t' -> b.append("\\t");
                case '\r' -> b.append("\\r");
                default -> b.append(c);
            }
        }
        return b.toString();
    }

    public String escapeChar(char c) {
        return switch (c) {
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '\b' -> "\\b";
            case '\f' -> "\\f";
            case '\\' -> "\\\\";
            case '\'' -> "\\'";
            case '"'  -> "\\\"";
            default -> {
                if (c < 32 || c > 126) yield String.format("\\u%04x", (int) c);
                yield Character.toString(c);
            }
        };
    }
}

