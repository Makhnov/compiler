package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.IntStream;

public class ParserError extends DecaRecognitionException {
    private static final long serialVersionUID = -8132514996569278579L;
    private String message;

    public ParserError(AbstractDecaLexer recognizer, IntStream input, String message) {
        super(recognizer, input);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
