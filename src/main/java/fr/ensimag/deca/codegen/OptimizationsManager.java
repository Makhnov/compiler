package fr.ensimag.deca.codegen;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BLT;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.QUO;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.SHL;
import fr.ensimag.ima.pseudocode.instructions.SHR;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * Manager pour les optimisations 
 * // A COMPLETER D'UN POINT DE VUE CONCEPTUEL (Runtime ? Compile time ?)
 */
public class OptimizationsManager {

    private final DecacCompiler compiler;
    private final int optMode;

    public OptimizationsManager(DecacCompiler compiler) {
        this.compiler = compiler;
        CompilerOptions options = compiler.options();
        this.optMode = (options == null) ? 0 : options.optimization();
    }

    public int optMode() {
        return optMode;
    }
    
    public boolean basic() {
        return optMode >= CompilerOptions.BASIC;
    }

    public boolean medium() {
        return optMode >= CompilerOptions.MEDIUM;
    }

    public boolean maximum() {
        return optMode >= CompilerOptions.MAXIMUM;
    }

    private boolean powerOf2(int x) {
        return x > 0 && (x & (x - 1)) == 0;
    }

    public boolean powerOf2Expr(AbstractExpr expr) { // Multiply pow2, level : basic
        if (!basic()) {
            return false;
        }
        if (!expr.predictable()) {
            return false;
        }

        OptionalInt ox = expr.getIntegerValue(compiler);
        return ox.isPresent() && powerOf2(ox.getAsInt());
    }

    /**
     * Division par 2^k , avec fallback (Vérification runtime)
     *
     * @param target registre contenant déjà la valeur de gauche
     * @param rhs diviseur (doit être une constante puissance de 2)
     * @return true si l'optimisation (avec fallback) a été appliquée
     */
    public boolean pow2Div(GPRegister target, AbstractExpr rhs) {
        if (!maximum()) {
            return false;
        }
        OptionalInt ox = rhs.getIntegerValue(compiler);
        if (!ox.isPresent()) {
            return false;
        }

        int x = ox.getAsInt();
        if (!powerOf2(x)) {
            return false;
        }

        int pow = Integer.numberOfTrailingZeros(x);
        if (pow == 0) {            
            return true;
        }

        int id = compiler.labels().getLabelID();
        Label lFallback = compiler.labels().label("Pow2_Div_Fallback.", id);
        Label lEnd = compiler.labels().label("Pow2_Div_End.", id);

        // target < 0, on annule l'optimisation
        compiler.addInstruction(new CMP(new ImmediateInteger(0), target));
        compiler.addInstruction(new BLT(lFallback));

        // target >= 0, on utilise SHR
        for (int i = 0; i < pow; i++) {
            compiler.addInstruction(new SHR(target));
        }
        compiler.addInstruction(new BRA(lEnd));

        // Pas d'opti, on utilise QUO normalement
        compiler.addLabel(lFallback);
        compiler.addInstruction(new QUO(new ImmediateInteger(x), target));
        compiler.addLabel(lEnd);

        return true;
    }

    /**
     * Multiplication par 2^k , a priori toujours valide, à vérifier (pas de runtime donc)
     * 
     * @param target registre contenant déjà la valeur de gauche 
     * @param rhs multiplicateur (doit être une constante puissance de 2)
     * @return true si l'optimisation a été appliquée (pas de fallback ici)
     */
    public boolean pow2Mul(GPRegister target, AbstractExpr rhs) {
        if (!basic()) {
            return false;
        }
        OptionalInt ox = rhs.getIntegerValue(compiler);
        if (!ox.isPresent()) {
            return false;
        }

        int x = ox.getAsInt();
        if (!powerOf2(x)) {
            return false;
        }

        int pow = Integer.numberOfTrailingZeros(x);
        for (int i = 0; i < pow; i++) {
            compiler.addInstruction(new SHL(target));
        }
        return true;
    }

    /**
     * Modulo 2^k, avec fallback (Vérification runtime)
     *
     * @param target registre contenant déjà la valeur de gauche
     * @param rhs modulo (doit être une constante puissance de 2)
     * @param scratch registre temporaire (doit être différent de target)
     * @return true si l'optimisation (avec fallback) a été appliquée
     */
    public boolean pow2Mod(GPRegister target, AbstractExpr rhs, GPRegister scratch) {
        if (!maximum()) {
            return false;
        }
        OptionalInt ox = rhs.getIntegerValue(compiler);
        if (!ox.isPresent()) {
            return false;
        }

        int x = ox.getAsInt();
        if (!powerOf2(x)) {
            return false;
        }

        int pow = Integer.numberOfTrailingZeros(x);

        // Cas trivial : a % 1 = 0
        if (pow == 0) {
            compiler.addInstruction(new SUB(target, target));
            return true;
        }

        // Par sécurité on ne fait pas l'optimisation si on n'a pas de registre scratch différent
        if (scratch == null || scratch.equals(target)) {
            return false;
        }

        int id = compiler.labels().getLabelID();
        Label lFallback = compiler.labels().label("Pow2_Mod_Fallback.", id);
        Label lEnd = compiler.labels().label("Pow2_Mod_End.", id);

        // target < 0, on annule l'optimisation
        compiler.addInstruction(new CMP(new ImmediateInteger(0), target));
        compiler.addInstruction(new BLT(lFallback));

        // target >= 0 : r = a - ((a >> pow) << pow)
        compiler.addInstruction(new LOAD(target, scratch));
        for (int i = 0; i < pow; i++) {
            compiler.addInstruction(new SHR(scratch));
        }
        for (int i = 0; i < pow; i++) {
            compiler.addInstruction(new SHL(scratch));
        }
        compiler.addInstruction(new SUB(scratch, target));
        compiler.addInstruction(new BRA(lEnd));

        // Pas d'opti, on utilise REM normalement
        compiler.addLabel(lFallback);
        compiler.addInstruction(new REM(new ImmediateInteger(x), target));
        compiler.addLabel(lEnd);

        return true;
    }

    /**
     * Division par 2 pour les floats (compile time)
     * 
     * @param target registre contenant déjà la valeur de gauche
     * @param rhs diviseur (doit être la constante 2.0)
     * @return true si l'optimisation a été appliquée (pas de fallback)
     */
    public boolean slash2(GPRegister target, AbstractExpr rhs){
        if (!basic()) {
            return false;
        }

        if (!rhs.predictable()) {
            return false;
        }

        OptionalDouble ox = rhs.getFloatValue(compiler);
        if (!ox.isPresent()) {
            return false;
        }

        if (ox.getAsDouble() != 2.0) {
            return false;
        }

        compiler.addInstruction(new MUL(new ImmediateFloat(0.5f), target));
        return true;
    }

    /**
     * Commutativité, on inverse l'ordre de calculs si la partie droite est plus complexe 
     * et/ou que la partie gauche est déjà dans un registre réservé
     * 
     * @param lhs
     * @param rhs
     * @param n registre cible
     * @return true si la commutation est possible
     */
    public DVal commute(AbstractExpr lhs, AbstractExpr rhs, int n) throws GenerationError {
        // Mode d'optimisation basique requis        
        if (!basic()) {
            return null;
        }
        // On ne peux pas commuter une opération arithmétique si l'une des deux expressions est imprévisible (exemple de fichier test : side_effects_arith.deca)
        if (!lhs.predictable() || !rhs.predictable()) {
            return null;
        }

        DVal lValue = lhs.getDVal(compiler);
        DVal Rvalue = rhs.getDVal(compiler);

        if (lValue == null) {
            return null;
        }

        if (Rvalue != null) {
            return null;
        }

        // On utilise le manager de registres pour récupérer le registre source
        GPRegister src = null;        
        src = compiler.registers().toGPRegister(lValue);

        if (src != null) {
            // On ne peut pas utiliser le registre de gauche pour calculer celui de droite
            if (src.equals(Register.getR(n))) {
                return null;
            }
            // Par sécurité on ne valide l'optimisation que si la source est dans un registre réservé
            if (src.getNumber() <= compiler.registers().getScatchMax()) {
                return null;
            }
        }

        return lValue;
    }
}
