package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRExit;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.ima.pseudocode.instructions.RTS;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Program extends AbstractProgram {

    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }

    public ListDeclClass getClasses() {
        return classes;
    }

    public AbstractMain getMain() {
        return main;
    }

    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        // pass 1:
        // Regle (1.1)
        classes.verifyListClass(compiler);

        // pass 2:      Regle (2.1)
        classes.verifyListClassMembers(compiler);

        // pass 3:      Regle (3.1) 
        classes.verifyListClassBody(compiler);
        main.verifyMain(compiler);
    }
    
    @Override
    public void codeGenProgram(DecacCompiler compiler) throws GenerationError {        
    /*
        2.1 Rôle de chaque passe
        Passe 1
        La première passe consiste à parcourir les classes du programme en s’intéressant à leurs méthodes,
        de façon à générer du code pour construire la table des méthodes (« virtual methods table », souvent
        abbrégé « vtable » en anglais). Cette première passe comporte plus précisément les points suivants :
        — construction du tableau des étiquettes des méthodes ;
        — génération de code permettant de construire la table des méthodes.
        Passe 2
        La deuxième passe comporte les points suivants :
        — codage des champs de chaque classe (initialisation) ;
        — codage des méthodes de chaque classe (déclarations et instructions) ;
        — codage du programme principal (déclarations et instructions).
    */

        /* PASSE 1.1 */

        // Initialisation du tableau des méthodes
        compiler.methods().beginVtable();

        // On assigne l'espace pour la classe Object
        ClassDefinition object = compiler.environmentType.OBJECT;
        compiler.methods().assignClass(object);
        
        // On assigne l'espace pour toutes les autres classes
        classes.assignTable(compiler);

        // La taille des tables est maintenant connue, on réinitialise les paramètres
        compiler.methods().endVtable();

        /* PASSE 1.2 */

        // On génère la table des méthodes pour la classe Object
        MethodDefinition eqDef = compiler.environmentType.EQUALS;
        compiler.methods().generateObjectTable(object, eqDef);

        // On génère la table des méthodes pour toutes les autres classes
        classes.generateTable(compiler);

        /* PASSE 2.3 (avant les méthodes, plus propre) */

        // Génération du code du programme principal
        compiler.addComment("Main program");
        main.codeGenMain(compiler);

        // Sauvegarde des variables réservées
        compiler.vars().save();

        // Fin du programme principal
        compiler.addInstruction(new HALT());

        // Initialisation des erreurs
        compiler.errors().init();
        
        /* PASSE 2.1 */

        // Label d'initialisation de la classe Object
        compiler.addLabel(compiler.labels().labelInit("Object"));
        compiler.addInstruction(new RTS());
        
        // Initialisation de toutes les autres classes
        classes.codeGenClasses(compiler);
        
        /* PASSE 2.2 */

        // Génération de la méthode equals de la classe Object
        compiler.methods().codeGenObjectMethods(eqDef);
        
        // Génération des méthodes de toutes les autres classes
        classes.codeGenMethods(compiler);
    }

    @Override
    public void irGenProgram(DecacCompiler compiler, IRProgram ir) throws GenerationError {        
        main.irGenMain(compiler, ir);
        ir.addInstruction(new IRExit(IRLocation.from(getLocation())));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
