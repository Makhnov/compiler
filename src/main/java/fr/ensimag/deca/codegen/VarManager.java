package fr.ensimag.deca.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * Gestionnaire des variables et rematérialisation (squelette)
 * 
 * @author Nico
 * @date 30/12/2025
 * 
 */
public class VarManager {
    private DecacCompiler compiler;

    // Ordre des déclarations locales (FIFO de base)
    private final List<VariableDefinition> localVars = new ArrayList<>();

    // Association Définition / Variables réservées
    private final Map<VariableDefinition, ReservedVar> reservedVars = new LinkedHashMap<>();

    // On réserve un minimum de registres dans le plus hauts (R15, R14, ...)
    private final int DEFAULT_MIN_RESERVED = 6;

    // On définit un minimum de registres à conserver pour les expressions (scratch)
    private final int DEFAULT_MIN_EXPR = 2;    

    public VarManager(DecacCompiler compiler) {
        this.compiler = compiler;
    }

    public DecacCompiler getCompiler() {
        return compiler;
    }

    public void register(VariableDefinition def) {
        localVars.add(def);
    }

    // Au début du Main, on réserve un certain nombre de variables dans les derniers registres
    public int reserve() {
        // Pas d'optimisation en mode NONE
        if (!compiler.optis().maximum()) {
            return 0;
        }

        if (localVars.isEmpty()) return 0;

        // On calcule le nombre de registres disponibles pour la réservation
        int sMin = compiler.registers().getScatchMin();
        int sMax = compiler.registers().getScatchMax();
        
        // On compte le nombre de registres scratch disponibles
        int sCount = sMax - sMin + 1;
        
        // On s'assure de laisser au moins 'DEFAULT_MIN_EXPR' registres disponibles pour les expressions
        int available = Math.max(0, sCount - DEFAULT_MIN_EXPR);

        // On prend le min entre la limite par défaut, le nombre de variables locales et le maximum possible (cf. calculs précédents)
        int reserved = Math.min(DEFAULT_MIN_RESERVED, Math.min(localVars.size(), available));
        
        // On réserve les registres dans le gestionnaire dédié
        compiler.registers().reserve(reserved);

        // Initilisation des variables réservées (et enregistrement)
        for (int i = 0; i < reserved; i++) {

            // On récupère la définition de la variable à réserver
            VariableDefinition def = localVars.get(i);
            DAddr addr = def.getDAddr(compiler);

            // On récupère le registre réservé (R15, R14, ...)
            GPRegister GPReg = compiler.registers().getReservedRegister(i);
            
            // On charge la variable en mémoire dans le registre
            compiler.addInstruction(new LOAD(addr, GPReg));
            
            // On stocke l'état de la variable réservée
            reservedVars.put(def, new ReservedVar(GPReg));
        }

        return reserved;
    }

    public boolean isReserved(VariableDefinition def) {
        return reservedVars.containsKey(def);
    }

    public GPRegister getRegister(VariableDefinition def) {
        ReservedVar rv = reservedVars.get(def);
        return (rv == null) ? null : rv.getRegister();
    }
    
    // Indique que la variable a été modifiée récemment
    public void modified(VariableDefinition def) {
        ReservedVar rv = reservedVars.get(def);
        if (rv != null) {
            rv.setDirty(true);
        }
    }
    
    // Sauvegarde toutes les variables réservées récentes dans la mémoire
    public void save() {
        for (Map.Entry<VariableDefinition, ReservedVar> entry : reservedVars.entrySet()) {
            VariableDefinition def = entry.getKey();
            ReservedVar rv = entry.getValue();

            // On vérifie si la variable a été modifiée récemment
            if (rv.isDirty()) {
                DAddr addr = def.getDAddr(compiler);
                if (addr == null) {
                    throw new DecacInternalError("Pinned variable has no DAddr during flush");
                }
                
                // On sauvegarde la valeur dans la mémoire
                compiler.addInstruction(new STORE(rv.getRegister(), addr));
                
                // La variable est de nouveau propre
                rv.setDirty(false);
            }
        }
    }
}
