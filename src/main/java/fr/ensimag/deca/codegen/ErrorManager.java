package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.ERROR;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

/**
 * Gestion centralisée des erreurs pendant la génération de code
 * 
 * @author Nico
 * @date 30/12/2025
 * 
 */
public class ErrorManager {
    private final DecacCompiler compiler;
    private final boolean noCheck;

    private boolean init = false;

    public ErrorManager(DecacCompiler compiler) {
        this.compiler = compiler;
        this.noCheck = (compiler.options() != null) ? noCheck() : false;
    }

    private boolean noCheck() {
        return compiler.options().nocheck();
    }

    // Version non statique thrread-safe, chaque instance possède ses propres labels (qui ne bougent pas de toute façon)
    private final Label IO = new Label("io_error");
    private final Label OVERFLOW = new Label("overflow_error");
    private final Label DIV_BY_ZERO = new Label("div_by_zero_error");
    private final Label INVALID_CAST = new Label("invalid_cast_error");
    private final Label STACK_OVERFLOW = new Label("stack_overflow_error");
    private final Label HEAP_OVERFLOW = new Label("heap_overflow_error");
    private final Label NULL_DEREF = new Label("null_dereference_error");

    // A REWORK (lors du passage à l'objet)
    public void stack(int size) {
        if (!noCheck) {
            compiler.addFirstInstruction(new BOV(STACK_OVERFLOW));
            compiler.addFirstInstruction(new TSTO(size));
        }
    }

    public void io() {
        compiler.addInstruction(new BOV(IO));
    }

    public void overflow() {
        if (!noCheck) {
            compiler.addInstruction(new BOV(OVERFLOW));
        }
    }

    public void divByZero() {
        if (!noCheck) {
            compiler.addInstruction(new BOV(DIV_BY_ZERO));
        }
    }

    public void stackOverflow() {
        if (!noCheck) {
            compiler.addInstruction(new BOV(STACK_OVERFLOW));
        }
    }

    public void heapOverflow() {
        if (!noCheck) {
            compiler.addInstruction(new BOV(HEAP_OVERFLOW));
        }
    }

    public void nullDeref() {
        if (!noCheck) {
            compiler.addInstruction(new BEQ(NULL_DEREF)); // si CMP #null, Rx a été fait juste avant
        }
    }

    public void invalidCast() {
        if (!noCheck) {
            compiler.addInstruction(new BRA(INVALID_CAST));
        }
    }

    public void init() {
        if (init) {
            return;
        }

        init = true;

        // IO Error
        compiler.addLabel(IO);
        compiler.addInstruction(new WSTR("Error: Input/Output error"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        if (noCheck) {
            return;
        }

        // Overflow Error
        compiler.addLabel(OVERFLOW);
        compiler.addInstruction(new WSTR("Error: Overflow during arithmetic operation"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        // Division by Zero Error
        compiler.addLabel(DIV_BY_ZERO);
        compiler.addInstruction(new WSTR("Error: Division by zero"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        // Stack Overflow Error
        compiler.addLabel(STACK_OVERFLOW);
        compiler.addInstruction(new WSTR("Error: Stack Overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
        
        // Heap overflow
        compiler.addLabel(HEAP_OVERFLOW);
        compiler.addInstruction(new WSTR("Error: Heap Overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        // Null dereference
        compiler.addLabel(NULL_DEREF);
        compiler.addInstruction(new WSTR("Error: Null dereference"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        // Invalid cast
        compiler.addLabel(INVALID_CAST);
        compiler.addInstruction(new WSTR("Error: Invalid cast"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
}
