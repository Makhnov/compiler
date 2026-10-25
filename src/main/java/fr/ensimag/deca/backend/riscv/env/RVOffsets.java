package fr.ensimag.deca.backend.riscv.env;

import java.util.Map;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRVariable;

/**
 * Objet contenant le plan d'adressage d'une pile RISC-V.
 */
public class RVOffsets {

    private final Map<String, Integer> variables;
    private final Map<Integer, Integer> scratches;
    private final int size;

    public RVOffsets(Map<String, Integer> variables, Map<Integer, Integer> scratches, int size) {
        this.variables = variables;
        this.scratches = scratches;
        this.size = size;
    }

    public int size() {
        return size;
    }

    public int get(IRVariable v) {
        Integer o = variables.get(v.name());
        if (o == null) throw new IllegalStateException("No stack slot for var: " + v.name());
        return o;
    }

    public int get(IRScratch s) {
        Integer o = scratches.get(s.id());
        if (o == null) throw new IllegalStateException("No stack slot for scratch: s" + s.id());
        return o;
    }
}