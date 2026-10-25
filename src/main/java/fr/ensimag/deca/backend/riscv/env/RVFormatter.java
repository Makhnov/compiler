package fr.ensimag.deca.backend.riscv.env;

import java.util.Objects;

import org.apache.commons.lang.Validate;

import fr.ensimag.riscv.pseudocode.RVProgram;

/**
 * Utility class for formatting RISC-V assembly output.
 * Handles instruction formatting, labels, comments, and string escaping.
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class RVFormatter {
    private static final String INDENT = "    ";
    private final RVProgram out;
    
    public RVFormatter(RVProgram out) {
        Validate.notNull(out);
        this.out = out;
    }

    // Label sans indentation ni commentaire
    public void label(String name) {
        Validate.notNull(name);
        out.addText(name.endsWith(":") ? name : (name + ":"));
    }

    // Copie brute du texte directement dans .text
    public void asmText(String line) {
        Validate.notNull(line);
        out.addText(line);
    }

    /* INSTRUCTIONS */
    public void instruction(String name) {
        out.addText(INDENT + name);
    }

    public void instruction(String name, String op1) {
        out.addText(INDENT + name + " " + op1);
    }

    public void instruction(String name, String op1, String op2) {
        out.addText(INDENT + name + " " + op1 + ", " + op2);
    }

    public void instruction(String name, String op1, String op2, String op3) {
        out.addText(INDENT + name + " " + op1 + ", " + op2 + ", " + op3);
    }

    // Branchements
    public void branch(String name, String rs1, String rs2, String targetLabel) {
        instruction(name, rs1, rs2, targetLabel);
    }

    // Commentaires
    public void comment(String text) {
        Objects.requireNonNull(text);
        out.addText(INDENT + "# " + text);
    }

    // Pareil que asmText mais dans .data
    public void asmData(String line) {
        Validate.notNull(line);
        out.addData(line);
    }

    // Définition de données
    public void data(String label, String directiveAndArgs) {
        Validate.notNull(label);
        Validate.notNull(directiveAndArgs);
        out.addData(label + ": " + directiveAndArgs);
    }

    // Échappement des chaînes de caractères
    public String escape(String s) {
        StringBuilder b = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> b.append("\\\\");
                case '\"' -> b.append("\\\"");
                case '\n' -> b.append("\\n");
                case '\t' -> b.append("\\t");
                case '\r' -> b.append("\\r");
                default -> b.append(c);
            }
        }
        return b.toString();
    }
}
