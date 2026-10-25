package fr.ensimag.deca.ir;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.ir.display.IRPrettyPrinter;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.operands.IRData;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRSegment;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.deca.ir.operands.IRVariable;

/**
 * Abstract representation of a RISC-V program, i.e. set of Lines.
 *
 * @author Nico
 * @date 15/01/2026
 */
public class IRProgram {
    private final CompilerOptions options;

    public IRProgram(CompilerOptions options) {
        this.options = options;
    }
    
    public CompilerOptions options() {
        return options;
    }
    
    private final LinkedList<IRData> data = new LinkedList<>();
    private final LinkedList<IRAbstractInst> text = new LinkedList<>();

    private final Map<String, String> strings = new HashMap<>();
    private final Map<String, IRLabel> labels = new HashMap<>();

    private final LinkedHashMap<String, IRVariable> stack = new LinkedHashMap<>();
    private final LinkedHashMap<String, IRVariable> heap = new LinkedHashMap<>();

    private int id = 0;
    private int sid = 0;

    public void reset() {
        data.clear();
        text.clear();
        labels.clear();
        strings.clear();
        stack.clear();
        heap.clear();
        id = 0;
        sid = 0;
    }

    public int scratches() {
        return sid;
    }

    public LinkedList<IRData> data() {
        return data;
    }

    public LinkedList<IRAbstractInst> text() {
        return text;
    }

    public LinkedHashMap<String, IRVariable> stack() {
        return stack;
    }

    public LinkedHashMap<String, IRVariable> heap() {
        return heap;
    }

    public void addInstruction(IRAbstractInst inst) {
        text.add(inst);
    }

    public String getString(IRLabel label) {
        return strings.get(label.name());
    }

    public IRScratch scratch(IRType type) {
        return new IRScratch(sid++, type);
    }

    public IRLabel label(String prefix) {
        Objects.requireNonNull(prefix);
        String name = prefix + "_" + (id++);
        return new IRLabel(name);
    }

    public IRLabel string(String value) {
        Objects.requireNonNull(value);
        IRLabel current = labels.get(value);
        if (current != null) {
            return current;
        }
        IRLabel lab = label("str");
        data.add(new IRData(lab, value));
        labels.put(value, lab);
        strings.put(lab.name(), value);
        return lab;
    }

    public IRVariable addStack(String name, IRType type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        IRVariable existing = stack.get(name);
        if (existing != null)
            return existing;
        IRVariable v = new IRVariable(name, type, IRSegment.STACK);
        stack.put(name, v);
        return v;
    }

    public IRVariable addHeap(String name, IRType type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        IRVariable existing = heap.get(name);
        if (existing != null)
            return existing;
        IRVariable v = new IRVariable(name, type, IRSegment.HEAP);
        heap.put(name, v);
        return v;
    }

    public void display(PrintStream s) {
        IRPrettyPrinter p = new IRPrettyPrinter();
        p.display(this, s);
    }
}
