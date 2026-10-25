package fr.ensimag.deca.codegen;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;

/**
 * Exception raised when a generation error is found.
 *
 * @author nico25
 * @date 01/01/2026
 */
public class GenerationError extends LocationException {
    private static final long serialVersionUID = -8122514996569278579L;

    public GenerationError(String message, Location location) {
        super(message, location);
    }
}
