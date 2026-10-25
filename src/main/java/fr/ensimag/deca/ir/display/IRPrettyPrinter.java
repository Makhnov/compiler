package fr.ensimag.deca.ir.display;

import java.io.PrintStream;

import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.operands.IRData;
import fr.ensimag.deca.ir.operands.IRVariable;

/**
 * Pretty printer for displaying IR programs in a human-readable format.
 * Outputs the data, stack, heap, and text sections.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRPrettyPrinter {

    public void display(IRProgram ir, PrintStream ps) {
        display(ir, new IRPrinter(ps));
    }

    private void display(IRProgram ir, IRPrinter p) {
        p.section(".data");
        p.indent();
        for (IRData d : ir.data()) {
            p.data(d.label(), d.value());
        }
        p.unindent();

        p.blank();

        p.section(".stack");
        p.indent();
        boolean noStack = true;
        for (IRVariable v : ir.stack().values()) {
            noStack = false;
            p.inst(v.name() + ":", v.type().toString());
        }
        if (noStack) {
            p.inst("(empty)");
        }
        p.unindent();

        p.blank();

        p.section(".heap");
        p.indent();
        boolean noHeap = true;
        for (IRVariable v : ir.heap().values()) {
            noHeap = false;
            p.inst(v.name() + ":", v.type().toString());
        }
        if (noHeap) {
            p.inst("(empty)");
        }
        p.unindent();

        p.blank();

        p.section(".text");
        p.indent();
        for (IRAbstractInst i : ir.text()) {
            i.prettyPrint(p);
        }
        p.unindent();
    }
}

