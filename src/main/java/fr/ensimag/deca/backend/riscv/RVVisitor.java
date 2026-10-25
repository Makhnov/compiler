package fr.ensimag.deca.backend.riscv;

import fr.ensimag.deca.backend.riscv.env.RVFormatter;
import fr.ensimag.deca.backend.riscv.env.RVDataManager;
import fr.ensimag.deca.ir.IRVisitor;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.*;
import fr.ensimag.deca.ir.operands.*;

/**
 * Visiteur qui parcourt les instructions IR pour générer l'assembleur.
 * Utilise le RVGenerator comme boîte à outils.
 */
public final class RVVisitor extends IRVisitor {

    private final RVGenerator gen;
    private final boolean noCheck;

    private boolean isDone = false;
    private int labelID = 0;

    public RVVisitor(RVGenerator gen, boolean noCheck) {
        this.gen = gen;
        this.noCheck = noCheck;
    }

    public boolean done() {
        return isDone;
    }

    // Helpers
    private RVFormatter format() {
        return gen.format();
    }

    private RVDataManager data() {
        return gen.data();
    }

    private String newLabel(String p) {
        return p + "_" + (labelID++);
    }

    private String offset(IRScratch s) {
        return gen.stack().offset(s) + "(sp)";
    }

    public void end() {
        gen.errors().emitSyscallExit();
        isDone = true;
    }

    // Helpers visiteurs
    @Override
    public void visit(IRAssign a) throws IRError {
        data().store(a.src(), a.dst());
    }

    @Override
    public void visit(IRLoad l) throws IRError {
        data().loadToScratch(l.src(), l.dst(), l.type());
    }

    @Override
    public void visit(IRConvFloat c) throws IRError {
        data().loadInt(c.src(), "t0");
        format().instruction("fcvt.s.w", "ft0", "t0");
        format().instruction("fsw", "ft0", gen.stack().offset(c.dst()) + "(sp)");
    }

    @Override
    public void visit(IRUnaryExpr u) throws IRError {
        String name = u.name();
        String dstOff = offset(u.dest());

        if (name.endsWith("_INT")) {
            data().loadInt(u.src(), "t0");
            if ("NEG_INT".equals(name)) {
                if (!noCheck)
                    gen.errors().negOverflow("t0");
                format().instruction("sub", "t1", "x0", "t0");
                format().instruction("sw", "t1", dstOff);
            } else {
                throw new IRError("Unsupported: " + name, u.getLocation());
            }
        } else if (name.endsWith("_FLOAT")) {
            data().loadFloat(u.src(), "ft0");
            if ("NEG_FLOAT".equals(name)) {
                format().instruction("fsgnjn.s", "ft1", "ft0", "ft0");
                if (!noCheck)
                    gen.errors().invalidFloat("ft1", "overflow_error");
                format().instruction("fsw", "ft1", dstOff);
            } else {
                throw new IRError("Unsupported: " + name, u.getLocation());
            }
        }
    }

    @Override
    public void visit(IRBinaryExpr b) throws IRError {
        String op = b.name();
        String dstOff = offset(b.dst());

        if (op.endsWith("_INT") || op.endsWith("_BOOLEAN")) {
            data().loadInt(b.left(), "t0");
            data().loadInt(b.right(), "t1");

            switch (op) {
            case "ADD_INT" -> {
                format().instruction("add", "t2", "t0", "t1");
                if (!noCheck)
                    gen.errors().addOverflow("t0", "t1", "t2", newLabel("add_ok"));
            }
            case "SUB_INT" -> {
                format().instruction("sub", "t2", "t0", "t1");
                if (!noCheck)
                    gen.errors().subOverflow("t0", "t1", "t2", newLabel("sub_ok"));
            }
            case "MUL_INT" -> {
                format().instruction("mul", "t2", "t0", "t1");
                if (!noCheck)
                    gen.errors().mulOverflow("t0", "t1", "t2");
            }
            case "DIV_INT" -> {
                if (!noCheck) {
                    gen.errors().divByZero("t1");
                    gen.errors().divOverflow("t0", "t1", newLabel("div_ok"));
                }
                format().instruction("div", "t2", "t0", "t1");
            }
            case "MOD_INT" -> {
                if (!noCheck) {
                    gen.errors().divByZero("t1");
                    gen.errors().divOverflow("t0", "t1", newLabel("mod_ok"));
                }
                format().instruction("rem", "t2", "t0", "t1");
            }
            default -> throw new IRError("INT op not supported: " + op, b.getLocation());
            }
            format().instruction("sw", "t2", dstOff);
            return;
        }

        if (op.endsWith("_FLOAT")) {
            data().loadFloat(b.left(), "ft0");
            data().loadFloat(b.right(), "ft1");
            switch (op) {
            case "ADD_FLOAT" -> format().instruction("fadd.s", "ft2", "ft0", "ft1");
            case "SUB_FLOAT" -> format().instruction("fsub.s", "ft2", "ft0", "ft1");
            case "MUL_FLOAT" -> format().instruction("fmul.s", "ft2", "ft0", "ft1");
            case "DIV_FLOAT" -> format().instruction("fdiv.s", "ft2", "ft0", "ft1");
            default -> throw new IRError("FLOAT op not supported: " + op, b.getLocation());
            }
            if (!noCheck)
                gen.errors().invalidFloat("ft2", "overflow_error");
            format().instruction("fsw", "ft2", dstOff);
        }
    }

    @Override
    public void visit(IRPrintString ps) {
        format().instruction("la", "a0", ps.label().name());
        format().instruction("li", "a7", "4");
        format().instruction("ecall");
    }

    @Override
    public void visit(IRPrintChar pc) {
        format().instruction("li", "a0", String.valueOf((int) pc.c()));
        format().instruction("li", "a7", "11");
        format().instruction("ecall");
    }

    @Override
    public void visit(IRPrintValue p) throws IRError {
        switch (p.type()) {
        case INT -> {
            data().loadInt(p.value(), "a0");
            format().instruction("li", "a7", "1");
            format().instruction("ecall");
        }
        case FLOAT -> {
            data().loadFloat(p.value(), "fa0");
            format().instruction("li", "a7", "2");
            format().instruction("ecall");
            if (p.hex()) {
                format().instruction("fmv.x.w", "a0", "fa0");
                format().instruction("li", "a7", "34");
                format().instruction("ecall");
            }
        }
        case STRING -> {
            data().loadInt(p.value(), "a0");
            format().instruction("li", "a7", "4");
            format().instruction("ecall");
        }
        case BOOLEAN -> {
            // On charge la valeur booléenne
            data().loadInt(p.value(), "t0");
            String lFalse = newLabel("bool_false");
            String lPrint = newLabel("bool_print");

            // Si FALSE, on va directement au label concerné (on saute le TRUE)
            format().branch("beq", "t0", "x0", lFalse);

            // Cas TRUE (A la fin, on saute directement à l'impression)
            format().instruction("la", "a0", "msg_true");
            format().instruction("j", lPrint);

            // Cas FALSE
            format().label(lFalse);
            format().instruction("la", "a0", "msg_false");

            // Impression
            format().label(lPrint);
            format().instruction("li", "a7", "4");
            format().instruction("ecall");
        }
        }
    }

    @Override
    public void visit(IRLabelDef l) {
        format().label(l.label().name());
    }

    @Override
    public void visit(IRJump j) {
        format().instruction("j", j.target().name());
    }

    @Override
    public void visit(IROpCmp c) throws IRError {
        String name = c.name();
        String target = c.target().name();

        if (name.endsWith("_INT")) {
            data().loadInt(c.left(), "t0");
            data().loadInt(c.right(), "t1");
            if (name.startsWith("BEQ")) {
                format().branch("beq", "t0", "t1", target);
            } else if (name.startsWith("BNE")) {
                format().branch("bne", "t0", "t1", target);
            } else if (name.startsWith("BLT")) {
                format().branch("blt", "t0", "t1", target);
            } else if (name.startsWith("BGE")) {
                format().branch("bge", "t0", "t1", target);
            } else if (name.startsWith("BGT")) {
                format().branch("blt", "t1", "t0", target);
            } else if (name.startsWith("BLE")) {
                format().branch("bge", "t1", "t0", target);
            }
        } else if (name.endsWith("_FLOAT")) {
            data().loadFloat(c.left(), "ft0");
            data().loadFloat(c.right(), "ft1");
            if (name.startsWith("BEQ")) {
                format().instruction("feq.s", "t0", "ft0", "ft1");
                format().branch("bne", "t0", "x0", target);
            } else if (name.startsWith("BNE")) {
                format().instruction("feq.s", "t0", "ft0", "ft1");
                format().branch("beq", "t0", "x0", target);
            } else if (name.startsWith("BLT")) {
                format().instruction("flt.s", "t0", "ft0", "ft1");
                format().branch("bne", "t0", "x0", target);
            } else if (name.startsWith("BGE")) {
                format().instruction("flt.s", "t0", "ft0", "ft1");
                format().branch("beq", "t0", "x0", target);
            } else if (name.startsWith("BGT")) {
                format().instruction("flt.s", "t0", "ft1", "ft0");
                format().branch("bne", "t0", "x0", target);
            } else if (name.startsWith("BLE")) {
                format().instruction("flt.s", "t0", "ft1", "ft0");
                format().branch("beq", "t0", "x0", target);
            }
        }
    }

    @Override
    public void visit(IRExit e) {
        end();
        isDone = true;
    }
}