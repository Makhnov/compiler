package fr.ensimag.deca.backend.riscv.env;

import fr.ensimag.deca.backend.riscv.RVGenerator;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.riscv.pseudocode.RVProgram;

/**
 * Contexte de travail pour la génération RISC-V.
 * Gestionnaire des différents managers et accès aux structures globales
 */
public class RVContext {
    
    private final RVGenerator generator;
    private final IRProgram ir;
    private final RVProgram out;

    // Les Managers
    private final RVStackManager stack;
    private final RVDataManager data; 
    private final RVErrorManager errors;
    private final RVFormatter formatter;

    public RVContext(RVGenerator generator, IRProgram ir, RVProgram out) {
        this.generator = generator;
        this.ir = ir;
        this.out = out;

        // Instanciation simple des managers
        this.formatter = new RVFormatter(out);
        this.stack = new RVStackManager(generator, ir);
        this.data = new RVDataManager(generator);
        this.errors = new RVErrorManager(generator);        
    }

    public RVStackManager getStack() {
        return stack;
    }

    public RVDataManager getData() {
        return data;
    }

    public RVErrorManager getErrors() {
        return errors;
    }

    public IRProgram getIR() {
        return ir;
    }

    public RVProgram getOut() {
        return out;
    }

    public RVGenerator getGenerator() {
        return generator;
    }

    public RVFormatter getFormatter() {
        return formatter;
    }
}