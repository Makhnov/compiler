package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.IntStream;

public class LexerError extends DecaRecognitionException {
    private static final long serialVersionUID = -8122514996560278579L;
    private String message;

    public LexerError(AbstractDecaLexer recognizer, IntStream input, String message) {
        super(recognizer, input);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
