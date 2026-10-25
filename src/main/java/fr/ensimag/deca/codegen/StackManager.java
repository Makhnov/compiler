package fr.ensimag.deca.codegen;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

/**
 * Gestionnaire de pile en cas d'utilisation de registres temporaires (> 15)
 * 
 * @author Nico
 * @date 30/12/2025
 * 
 */
public class StackManager {
    private DecacCompiler compiler;
    private int current = 0;
    private int max = 0;

    public StackManager(DecacCompiler compiler) {
        this.compiler = compiler;
    }

    public int size() {
        return max;
    }

    public void reset() {
        current = 0;
        max = 0;
    }

    // Empile 1 mot
    public void push(GPRegister reg) {
        compiler.addInstruction(new PUSH(reg));
        push(1);
    }

    // Dépile 1 mot
    public void pop(GPRegister reg) {
        compiler.addInstruction(new POP(reg));
        pop(1);
    }

    // On wrap ADDSP pour réserver de l'espace de pile en augmentant d'autant le compteur courant
    public void addsp(int n) {
        Validate.isTrue(n >= 0, "n must be non-negative");
        compiler.addInstruction(new ADDSP(n));
        push(n);
    }

    // On wrap SUBSP pour simuler la libération de la pile dans le compteur courant
    public void subsp(int n) {
        compiler.addInstruction(new SUBSP(n));
        pop(n);
    }

    // Un BSR empile 2 mots. L'adresse de retour et l'ancien LB (p. 109) il peut donc impacter le max de pile d'autant
    public void bsr() {
        push(2);
        pop(2);
    }

    // L'incrémentation de la pile permet de calculer sa profondeur maximale
    private void push(int n) {
        current += n;
        if (current > max) {
            max = current;
        }
    }

    // On vérifie la "correctness" de notre émulation de pile une fois qu'on libère l'espace
    private void pop(int n) {
        current -= n;
        if (current < 0) {
            throw new DecacInternalError("StackManager underflow: negative depth");
        }
    }
}
