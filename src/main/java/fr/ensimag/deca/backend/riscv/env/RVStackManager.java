package fr.ensimag.deca.backend.riscv.env;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import fr.ensimag.deca.backend.riscv.RVGenerator;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRVariable;


/**
 * Manager responsable du calcul de l'allocation de la pile.
 * 
 */
public class RVStackManager {
    
    private final RVGenerator generator;
    private final RVOffsets offsets;

    public RVStackManager(RVGenerator generator, IRProgram ir) {
        this.generator = generator;

        Map<String, Integer> vMap = new HashMap<>();
        Map<Integer, Integer> sMap = new HashMap<>();
        int index = 0;

        // Offsets des variables
        ArrayList<IRVariable> vars = new ArrayList<>(ir.stack().values());
        vars.sort(Comparator.comparing(IRVariable::name));
        
        for (IRVariable v : vars) {
            vMap.put(v.name(), index);
            index += 4;
        }

        // Offsets des registres scratch
        int n = ir.scratches();
        for (int sid = 0; sid < n; sid++) {
            sMap.put(sid, index);
            index += 4;
        }

        // Alignement à 16 bytes
        int max = index;
        int size = ((max + 15) / 16) * 16;

        // On créé notre objet "Offsets"
        this.offsets = new RVOffsets(vMap, sMap, size);
    }

    public int size() {
        return offsets.size();
    }

    public int offset(IRVariable v) {
        return offsets.get(v);
    }

    public int offset(IRScratch s) {
        return offsets.get(s);
    }

    public RVGenerator getGenerator() {
        return generator;
    }
}