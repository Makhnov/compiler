package fr.ensimag.deca.ir.control;

import java.io.PrintStream;

import fr.ensimag.deca.ir.IRLocation;

/**
 * Exeption raised when an error occurs during IR generation.
 * 
 * @author Nico
 * @date 21/01/2026
 */
public class IRError extends Exception {
    private static final long serialVersionUID = -8122594996569278575L;

    private final IRLocation location;

    public IRError(String message, IRLocation location) {
        super(message);
        this.location = (location == null) ? IRLocation.unknown() : location;
    }

    public IRLocation getLocation() {
        return location;
    }

    public void display(PrintStream s) {
        if (s == null) {
            return;
        }
        s.println(location.toString() + ": " + getMessage());
    }
}
