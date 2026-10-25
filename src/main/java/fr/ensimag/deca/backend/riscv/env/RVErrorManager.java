package fr.ensimag.deca.backend.riscv.env;

import fr.ensimag.deca.backend.riscv.RVGenerator;

/**
 * Centralise la gestion des erreurs à l'exécution.
 * Liste non exhaustive, plus à des fins pédagogiques.
 */
public class RVErrorManager {

    private final RVGenerator gen;

    public RVErrorManager(RVGenerator generator) {
        this.gen = generator;
    }

    private RVFormatter format() {
        return gen.format(); 
    }

    // public void stack(int size) 
    // public void io() 
    // public void overflow() 
    // public void divByZero() 
    // public void stackOverflow() 
    // public void heapOverflow() 
    // public void nullDeref() 
    // public void invalidCast() 
    // private final Label IO = new Label("io_error");
    // private final Label OVERFLOW = new Label("overflow_error");
    // private final Label DIV_BY_ZERO = new Label("div_by_zero_error");
    // private final Label INVALID_CAST = new Label("invalid_cast_error");
    // private final Label STACK_OVERFLOW = new Label("stack_overflow_error");
    // private final Label HEAP_OVERFLOW = new Label("heap_overflow_error");
    // private final Label NULL_DEREF = new Label("null_dereference_error");

    // Vérifications
    public void divByZero(String den) {
        format().branch("beq", den, "x0", "div_by_zero_error");
    }

    public void divOverflow(String num, String den, String label) {
        format().instruction("li", "t3", "0x80000000");
        format().branch("bne", num, "t3", label);
        format().instruction("li", "t4", "-1");
        format().branch("beq", den, "t4", "overflow_error");
        format().label(label);
    }

    public void negOverflow(String reg) {
        format().instruction("li", "t3", "0x80000000");
        format().branch("beq", reg, "t3", "overflow_error");
    }

    public void addOverflow(String a, String b, String r, String label) {
        format().instruction("xor", "t3", a, b);
        format().branch("blt", "t3", "x0", label);
        format().instruction("xor", "t3", a, r);
        format().branch("bge", "t3", "x0", label);
        format().instruction("j", "overflow_error");
        format().label(label);
    }

    public void subOverflow(String a, String b, String r, String label) {
        format().instruction("xor", "t3", a, b);
        format().branch("bge", "t3", "x0", label);
        format().instruction("xor", "t3", a, r);
        format().branch("bge", "t3", "x0", label);
        format().instruction("j", "overflow_error");
        format().label(label);
    }

    public void mulOverflow(String a, String b, String r) {
        format().instruction("mulh", "t3", a, b);
        format().instruction("srai", "t4", r, "31");
        format().branch("bne", "t3", "t4", "overflow_error");
    }

    public void invalidFloat(String f, String label) {
        int invalid = 1 + 128 + 256; // infini négatif, infini positif, NaN
        format().instruction("fclass.s", "t0", f);
        format().instruction("andi", "t0", "t0", String.valueOf(invalid));
        format().branch("bne", "t0", "x0", label);
    }

    // Emissions des flags d'erreurs
    public void emitSyscallExit() {
        format().instruction("li", "a0", "0");
        format().instruction("li", "a7", "93");
        format().instruction("ecall");
    }

    public void emitRuntimeData() {
        format().data("nl", ".asciz \"\\n\"");
        format().data("err_div0", ".asciz \"Error: Division by zero\"");
        format().data("err_overflow", ".asciz \"Error: Overflow during arithmetic operation\"");
        format().data("msg_true", ".asciz \"true\"");
        format().data("msg_false", ".asciz \"false\"");
    }

    public void emitRuntime(boolean noCheck) {
        if (noCheck) return;

        // Handler Div0
        format().label("div_by_zero_error");
        format().instruction("la", "a0", "err_div0");
        format().instruction("li", "a7", "4");
        format().instruction("ecall");
        emitSyscallExit();

        // Handler Overflow
        format().label("overflow_error");
        format().instruction("la", "a0", "err_overflow");
        format().instruction("li", "a7", "4");
        format().instruction("ecall");
        emitSyscallExit();
    }
}