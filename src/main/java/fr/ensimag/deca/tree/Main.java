package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * AST node representing the main block of a DECA program.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Main extends AbstractMain {

    private ListDeclVar declVariables;
    private ListInst insts;

    public Main(ListDeclVar declVariables, ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    public ListInst getInsts() {
        return insts;
    }

    public ListDeclVar getDeclVars() {
        return declVariables;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        // Regle (3.4)
        // Regle (3.18)

        // Variables locales
        EnvironmentExp env = new EnvironmentExp(null);

        // Vérification des déclarations
        declVariables.verifyListDeclVariable(compiler, env, null);

        // Vérification des instructions
        insts.verifyListInst(compiler, env, null, compiler.environmentType.VOID);
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) throws GenerationError {
        // A FAIRE: traiter les déclarations de variables. // EN COURS

        // Offset :
        int offset = compiler.methods().offset();

        // Espace pour les variables objets (A FAIRE)
        int objectSize = 0;

        // Espace pour les variables globales (la table des méthodes) 
        int VTableSize = compiler.methods().size();

        // Espace pour les variables locales
        int localSize = declVariables.size();

        // Espace total pour les variables
        int variablesSize = localSize + objectSize + VTableSize;

        /* On génère le code avant de calculer la taille nécessaire (tempSize) */

        // Génération du code des déclarations
        compiler.addComment("Beginning of main declarations:");
        declVariables.codeGenDeclVars(compiler, offset);

        // Réservation des variables dans des registres dédiés
        int reserved = compiler.vars().reserve();
        compiler.addComment("Info: " + reserved + " variables réservées dans des registres dédiés.");

        // Génération du code des instructions
        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);

        // Espace temporaire nécessaire en cas de débordement de pile
        int tempSize = compiler.stack().size();

        // Espace de sauvegarde de registres (A FAIRE)
        int sparedSize = 0;

        // Espace total à réserver pour la pile
        int totalSize = variablesSize + sparedSize + tempSize;

        compiler.addFirstInstruction(new ADDSP(variablesSize + sparedSize)); // On n'ajotue pas l'espace temporaire

        // N.B. addFirst est une pile, on ajoute les instructions dans l'ordre inverse
        compiler.errors().stack(totalSize);

        compiler.addFirstComment("start main prologue");

        /* OBJETS, penser à incrémenter offset */
    }

    @Override
    protected void irGenMain(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        declVariables.irGenListDeclVariable(compiler, ir);
        insts.irGenListInst(compiler, ir);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");

        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();

        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }


}
