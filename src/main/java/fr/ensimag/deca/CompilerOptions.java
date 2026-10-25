package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class CompilerOptions {
    private final int MAX_REG = 16;

    public static final int QUIET = 0;
    public static final int INFO = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public static final int NONE = 0;
    public static final int BASIC = 1;
    public static final int MEDIUM = 2;
    public static final int MAXIMUM = 3;

    // AJOUT RISCV
    public static final int RARS = 1;
    public static final int IMA = 2;
    public static final int QBE = 3;

    public int ir() {
        return irMode;
    }
    // FIN AJOUT RISCV

    public int debug() {
        return debug;
    }

    public boolean nocheck() {
        return noCheck;
    }

    public boolean parallel() {
        return parallel;
    }

    public boolean banner() {
        return banner;
    }

    public boolean verification() {
        return verification;
    }

    public boolean parsing() {
        return parsing;
    }

    public int optimization() {
        return opti;
    }

    public boolean register() {
        return register;
    }

    public int getRMax() {
        return (register) ? rMax : MAX_REG;
    }

    public boolean warning() {
        return warning;
    }

    public Set<File> files() {
        return Collections.unmodifiableSet(sourceFiles);
    }

    // usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]
    // Regles : Cumulables (commutatif) :
    // -r avec -d, -n, -P
    // -p avec -d et -P
    // -v avec -d et -P
    // -d avec -n, -P
    // -n avec -P

    private boolean banner = false;
    private boolean register = false;
    private boolean noCheck = false;
    private boolean parallel = false;
    private boolean verification = false;
    private boolean parsing = false;
    private boolean warning = false;

    // AJOUT RISCV
    private boolean ir = false;
    private int irMode = NONE;
    // FIN AJOUT RISCV

    private int opti = NONE;
    private int debug = QUIET;
    private int rMax = 0;
    private Set<File> sourceFiles = new LinkedHashSet<File>();
    private List<String> warnings = new ArrayList<String>();

    public void parseArgs(String[] args) throws CLIException {
        if (args.length == 0) {
            displayUsage();
            return;
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {

            // AJOUT RISCV
            case "-ir": // OK
                if (ir || i + 1 >= args.length) {
                    throw new CLIException("Error: Option -ir must be followed by an integer between 1 and 3 (inclusive).");
                }
                String irModeStr = args[++i];
                if (irModeStr.matches("[0-9]+")) {
                    int irTempMode = Integer.parseInt(irModeStr);
                    if (irTempMode < 1 || irTempMode > 3) {
                        throw new CLIException("Error: Option -ir must be followed by an integer between 1 and 3.");
                    }
                    ir = true;
                    irMode = irTempMode;                    
                } else {
                    throw new CLIException("Error: Option -ir must be followed by an integer between 1 and 3.");
                }
                break;
            // FIN AJOUT RISCV

            case "-w": // Warning OK
                if (warning) {
                    throw new CLIException("Error: Option -w can only be specified once.");
                }
                warning = true;
                break;
            case "-o": // Optimisation OK
                opti++;
                break;
            case "-d": // debug OK
                debug++;
                break;

            case "-v": // Vérification OK
                if (parsing || verification) {
                    throw new CLIException("Error: Options -v and -p cannot be combined.");
                }
                verification = true;
                break;

            case "-r": // Limit registers OK
                if (i + 1 >= args.length || register) {
                    throw new CLIException("Error: Option -r must be followed by an integer between 4 and 16 (inclusive).");
                }
                String next = args[++i];
                if (next.matches("[0-9]+")) {
                    int rTemp = Integer.parseInt(next);
                    if (rTemp < 4 || rTemp > 16) {
                        throw new CLIException("Error: The number of registers must be between 4 and 16 (inclusive).");
                    }
                    rMax = rTemp;
                    register = true;
                } else {
                    throw new CLIException("Error: Option -r must be followed by an integer between 4 and 16.");
                }
                break;

            case "-n": // No guard in assembly OK
                if (noCheck) {
                    throw new CLIException("Error: Option -n can only be specified once.");
                }
                noCheck = true;
                break;

            case "-p": // Parsing OK                 
                if (verification || parsing) {
                    throw new CLIException("Error: Options -p and -v cannot be combined.");
                }
                parsing = true;
                break;

            case "-P": // Parallel
                if (parallel) {
                    throw new CLIException("Error: Option -P can only be specified once.");
                }
                parallel = true;
                break;

            case "-b": // Affichage de la banière OK
                if (args.length != 1) {
                    throw new CLIException("Error: Option -b cannot be combined with any other option.");
                }
                banner = true;
                return;

            default:
                // Vérificaiton du fichier
                int check = checkFile(arg);
                if (check == 1) {
                    throw new CLIException("Error: File " + arg + " does not exist at the specified location.");
                }
                if (check == 2) {
                    throw new CLIException("Error: " + arg + " is not a valid filename.");
                }
                if (check == 3) {
                    throw new CLIException("Error: File " + arg + " is unreadable (check permissions).");
                }
                if (check == 4) {
                    throw new CLIException("Error: File " + arg + " has wrong format (.deca extension expected).");
                }
                if (!sourceFiles.add(new File(arg))) {
                    String msg = "Le fichier " + arg + " a été spécifié plusieurs fois, une seule compilation sera effectuée.";
                    warnings.add(msg);
                }
                break;
            }
        }

        if (sourceFiles.size() == 0) {
            throw new CLIException("Error: At least one file is required for this option");
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (debug()) {
        case QUIET:
            logger.setLevel(Level.OFF);
            break;
        case INFO:
            logger.setLevel(Level.INFO);
            break;
        case DEBUG:
            logger.setLevel(Level.DEBUG);
            break;
        case TRACE:
            logger.setLevel(Level.TRACE);
            break;
        default:
            logger.setLevel(Level.ALL);
            break;
        }
        // logger.info("Decac compiler started");
        // logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            // logger.info("Java assertions enabled");
        } else {
            // logger.info("Java assertions disabled");
        }

        if (warning) {
            for (String warning : warnings) {
                System.err.println("WARNING " + warning);
            }
        }
        //throw new UnsupportedOperationException("not yet implemented");
    }

    private int checkFile(String arg) {
        // Java natif
        File file = new File(arg);
        if (!file.exists()) {
            return 1;
        }
        if (!file.isFile()) {
            return 2;
        }
        if (!file.canRead()) {
            return 3;
        }
        if (!file.getName().endsWith(".deca")) {
            return 4;
        }
        return 0;
    }

    protected void displayUsage() {
        // throw new UnsupportedOperationException("displayUsage not yet implemented");
        System.out.println("usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println("");
        System.out.println("Description des options :");
        System.out.println("  -b (banner)       : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("  -p (parse)        : arrête decac après l'étape de construction de");
        System.out.println("                      l'arbre, et affiche la décompilation de ce dernier");
        System.out.println("                      (i.e. s'il n'y a qu'un fichier source à compiler,");
        System.out.println("                      la sortie doit être un programme deca syntaxiquement correct)");
        System.out.println("  -v (verification) : arrête decac après l'étape de vérifications");
        System.out.println("                      (ne produit aucune sortie en l'absence d'erreur)");
        System.out.println("  -n (no check)     : supprime les tests à l'exécution spécifiés dans");
        System.out.println("                      les points 11.1 et 11.3 de la sémantique de Deca.");
        System.out.println("  -r X (registers)  : limite les registres banalisés disponibles à");
        System.out.println("                      R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("  -d (debug)        : active les traces de debug. Répéter l'option");
        System.out.println("                      plusieurs fois pour avoir plus de traces.");
        System.out.println("  -P (parallel)     : s'il y a plusieurs fichiers sources, lance la");
        System.out.println("                      compilation des fichiers en parallèle.");
    }
}
