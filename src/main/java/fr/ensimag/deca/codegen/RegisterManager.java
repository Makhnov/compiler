package fr.ensimag.deca.codegen;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Gestionnaire des registres (squelette)
 * 
 * @author Nico
 * @date 30/12/2025
 * 
 */
public class RegisterManager {

    private DecacCompiler compiler;
    private final int MAX_REG = 16;
    private final int MIN_REG = 2;

    // Compteur de registres réservés
    private int reserved = 0;

    // Nombre maximum de registres disponibles (option -r)
    private int max = MAX_REG;

    private final void setRMax(int max) {
        Validate.isTrue(max <= MAX_REG);
        Validate.isTrue(max >= MIN_REG);
        Validate.isTrue((max - reserved - 1) >= MIN_REG);
        this.max = max;
    }

    public int getScatchMax() {
        return (max - reserved - 1);
    }

    public RegisterManager(DecacCompiler compiler, int rMax) {
        this.compiler = compiler;        
        setRMax(rMax);
    }

    public DecacCompiler getCompiler() {
        return compiler;
    }

    public GPRegister toGPRegister(DVal v) {
        return (v instanceof GPRegister) ? (GPRegister) v : null;
    }

    public boolean compare(GPRegister src, GPRegister dest) {
        if (src == null || dest == null) {
            return false;
        }
        return (dest.equals(src) || src == dest);
    }

    public void load(DVal src, GPRegister dest) {
        if (src == dest || dest.equals(src)) {
            return;
        }
        compiler.addInstruction(new LOAD(src, dest));
    }

    public void reserve(int n) {                
        Validate.isTrue(n <= (max - MIN_REG - 1));
        Validate.isTrue(n >= 0);
        reserved = n;
    }

    public int getScatchMin() {
        return MIN_REG;
    }

    public GPRegister getReservedRegister(int i) {     
        Validate.isTrue(i >= 0 && i < reserved);   
        return GPRegister.getR(max - 1 - i);
    }

    public int getReservedCount() {
        return reserved;
    }

    public void check(int n) {
        int rmax = getScatchMax();
        int rstart = MIN_REG;
        if (n < rstart || n > rmax) {
            throw new DecacInternalError("Register R" + n + " is not available. Available registers are between R" + rstart + " and R" + rmax + ".");
        }
    }

    public boolean canSwap(int n) {
        int rmax = getScatchMax();
        int rstart = MIN_REG;
        // On vérifie qu'on a bien 2 registres disponibles
        return (((n + 1) <= rmax) && (n >= rstart));
    }
}
