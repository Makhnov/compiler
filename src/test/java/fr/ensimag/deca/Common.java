package fr.ensimag.deca;

import fr.ensimag.deca.tools.DecacInternalError;

public class Common {
    // Codes ANSI pour les couleurs
    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String BLUE   = "\u001B[34m";

    public static void logInfo(String msg) {
        System.out.println("[" + BLUE + "INFO" + RESET + "] " + msg);
    }

    public static void logDot(String msg) {
        System.out.println("[" + BLUE + "..." + RESET + "] " + msg);
    }

    public static void logSuccess(String msg) {
        System.out.println("[" + GREEN + "SUCCESS" + RESET + "] " + msg);
    }

    public static void logFail(String msg) {
        System.out.println("[" + RED + "ERROR" + RESET + "] " + msg);
    }

    public static void assertTrue(boolean condition, String reason) {
        if (!condition) {
            logFail("Assertion failed: " + reason);
            throw new DecacInternalError("Assertion failed: " + reason);
        }
    }    
}
