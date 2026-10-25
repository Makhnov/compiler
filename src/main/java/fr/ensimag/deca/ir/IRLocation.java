package fr.ensimag.deca.ir;

import java.io.Serializable;

import fr.ensimag.deca.tree.Location;

/**
 * Represents a source code position for IR instructions.
 * Contains line number, column position and filename information
 * for error tracking and debugging purposes.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRLocation implements Serializable {
    private static final long serialVersionUID = -2906437662280660298L;
    
    public static final String NO_SOURCE_NAME = "<no source file>";
    public static final IRLocation BUILTIN = new IRLocation(-1, -1, NO_SOURCE_NAME);

    public static IRLocation unknown() {
        return BUILTIN;
    }

    // Convert a Location to an IRLocation
    public static IRLocation from(Location loc) {
        if (loc == null || loc == Location.BUILTIN) {
            return unknown();
        }
        return new IRLocation(loc.getLine(), loc.getPositionInLine(), loc.getFilename());
    }

    private final int line;
    private final int positionInLine;
    private final String filename;

    public IRLocation(int line, int positionInLine, String filename) {
        super();
        this.line = line;
        this.positionInLine = positionInLine;
        this.filename = filename;
    }

    public String filename() {
        return filename;
    }

    public int line() {
        return line;
    }

    public int positionInLine() {
        return positionInLine;
    }

    public boolean isUnknown() {
        return this == BUILTIN;
    }

    @Override
    public String toString() {
        if (isUnknown()) {
            return "<unknown>";
        }
        String f = (filename == null) ? "<nofile>" : filename;
        if (line >= 0 && positionInLine >= 0) {
            return f + ":" + line + ":" + positionInLine;
        }
        if (line >= 0) {
            return f + ":" + line;
        }
        return f;
    }
}

