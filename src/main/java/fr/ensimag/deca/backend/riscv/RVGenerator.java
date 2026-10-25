package fr.ensimag.deca.backend.riscv;

import fr.ensimag.deca.backend.riscv.env.RVContext;
import fr.ensimag.deca.backend.riscv.env.RVErrorManager;
import fr.ensimag.deca.backend.riscv.env.RVDataManager;
import fr.ensimag.deca.backend.riscv.env.RVStackManager;
import fr.ensimag.deca.backend.riscv.env.RVFormatter;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.*;
import fr.ensimag.deca.ir.operands.*;
import fr.ensimag.riscv.pseudocode.RVProgram;

/**
 * Code generator that emits RISC-V assembly from IR.
 * Manages the generation context and coordinates the visitor-based code generation.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class RVGenerator {

    // Le contexte de génération
    private RVContext context;

    public RVContext getContext() {
        return context;
    }

    public RVStackManager stack() {
        return context.getStack();
    }
    
    public RVDataManager data() {
        return context.getData();
    }

    public RVErrorManager errors() {
        return context.getErrors();
    }
 
    public RVFormatter format() {
        return context.getFormatter();
    }

    public IRProgram ir() {
        return context.getIR();
    }

    public RVProgram out() {
        return context.getOut();
    }


    public void generate(IRProgram ir, RVProgram ass, boolean noCheck) throws IRError {

        // Contexte de génération
        this.context = new RVContext(this, ir, ass);

        // On boucle sur les données
        for (IRData data : ir.data()) {
            format().data(data.label().name(), ".asciz \"" + format().escape(data.value()) + "\"");
        }
        errors().emitRuntimeData();

        // On ajoute le prologue
        format().comment("prologue");
        format().instruction("addi", "sp", "sp", "-" + stack().size());

        // On boucle sur les instructions
        RVVisitor visitor = new RVVisitor(this, noCheck);        
        for (IRAbstractInst inst : ir.text()) {
            inst.accept(visitor);
        }

        // Sortie implicite
        if (!visitor.done()) {
            visitor.end();
        }

        // Bloc d'erreurs runtime
        errors().emitRuntime(noCheck);
    }
}

