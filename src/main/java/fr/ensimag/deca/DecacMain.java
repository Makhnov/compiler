package fr.ensimag.deca;

import org.apache.log4j.MDC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class DecacMain {
    public static void main(String[] args) {
        // example log4j message. Commenté car ne devraitp pas apparaitre avant que l'utilisateur n'ait choisi le niveau de -d.
        // LOG.info("Decac compiler started");
        boolean error = false;

        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n" + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }

        if (options.banner()) {
            // throw new UnsupportedOperationException("decac -b not yet implemented");
            System.out.println("gl25");
            System.exit(0);
        }

        // On récupère le nombre de fichiers à compiler
        List<File> files = new ArrayList<>(options.files());
        int nbFiles = files.size();
        
        // COMPILATION PARALLÈLE
        if (options.parallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            // throw new UnsupportedOperationException("Parallel build not yet implemented");

            // 1. On vérifie qu'il y a bien plusieurs fichiers sources
            if (nbFiles >= 2) {            
                // 2. Le nombre de tâches est défini par le min entre le nb de procs et le nb de fichiers
                int nbProcs = Runtime.getRuntime().availableProcessors();
                int nbTasks = nbProcs > nbFiles ? nbFiles : nbProcs;

                // 3. On crée notre pool de threads
                ExecutorService executor = Executors.newFixedThreadPool(nbTasks);
                
                // 4. On prépare notre liste de résultats
                List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>(nbTasks);

                // 5. On boucle sur nos tâches
                for (File file : files) {
                    futures.add(executor.submit(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            try {
                                DecacCompiler compiler = new DecacCompiler(options, file);
                                String name = file.getName();
                                MDC.put("file", name);
                                return compiler.compile();
                            } finally {
                                MDC.remove("file");
                            }
                        }
                    }));
                }

                // 6. On récupère les résultats
                for (Future<Boolean> future : futures) {
                    try {
                        error |= future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println("Error during parallel compilation:\n" + e.getMessage());
                        error = true;
                    }
                }

                // 7. On termine proprement (pas besoin d'attendre vu qu'on est déjà syncro sur les futures)
                executor.shutdown();
                
                System.exit(error ? 1 : 0);
            }
        
            if (options.warning()) {
                System.err.println("Warning: Demande de compilation parallèle avec un seul fichier, compilation simple effectuée à la place.");
            }
        }

        // COMPILATION SÉQUENTIELLE
        for (File file : files) {
            DecacCompiler compiler = new DecacCompiler(options, file);
            if (compiler.compile()) {
                error = true;
            }
        }
        
        System.exit(error ? 1 : 0);
    }
}
