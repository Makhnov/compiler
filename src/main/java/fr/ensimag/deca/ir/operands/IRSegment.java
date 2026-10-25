package fr.ensimag.deca.ir.operands;

/**
 * Enumeration of memory segments for variable storage in the IR.
 *
 * @author gl25
 * @date 01/01/2026
 */
public enum IRSegment {
    /** Stack-allocated variables (local variables) */
    STACK,
    /** Heap-allocated variables (dynamic allocation) */
    HEAP,
    /** Static segment for global variables */
    STATIC
}

