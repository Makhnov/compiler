package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.Token;

public class SyntaxManager {

    public String getString(Token token) {
        String str = token.getText();
        return bound(unescape(str));
    }

    public String unescape(String str) {
        str = str.replaceAll("\\\\\"", "\"");
        str = str.replaceAll("\\\\\\\\", "\\\\");
        return str;
    }

    public String bound(String str) {
        return str.substring(1, str.length() - 1);
    }

    public int getInt(String str) {
        return Integer.parseInt(str);
    }

    public float getFloat(String str) {
        return Float.parseFloat(str);
    }

    public void checkInt(AbstractDecaLexer lexer) {
        String str = lexer.getText();
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // LOG.error(e);
            String msg = "Error during lexical analysis (integer) => [Overflow for input: \"" + str + "\"]";
            throw new LexerError(lexer, lexer.getInputStream(), msg);
        }
    }

    public void checkFloat(AbstractDecaLexer lexer, boolean isHex) {
        String str = lexer.getText();
        try {

            Float f = Float.parseFloat(str);

            if (f.isInfinite()) {
                throw new NumberFormatException("Overflow for input: \"" + str + "\"");
            }

            if (f == 0.0f && !isZero(str, isHex)) {
                throw new NumberFormatException("Underflow for input: \"" + str + "\"");
            }

        } catch (NumberFormatException e) {
            // LOG.error(e);
            String msg = "Error during lexical analysis (float) => [" + e.getMessage() + "]";
            throw new LexerError(lexer, lexer.getInputStream(), msg);
        }
    }

    private boolean isZero(String str, boolean isHex) {

        // Plus simple à analyser avec tout en maj
        String t = str.toUpperCase();

        // On enlève le suffixe
        if (t.endsWith("F")) {
            t = t.substring(0, t.length() - 1);
        }

        // Coupe au début de l'exposant (P pour hex, E pour décimal)
        int expPos = isHex ? t.indexOf('P') : t.indexOf('E');
        if (expPos != -1) {
            t = t.substring(0, expPos);
        }

        // Zéro littéral ssi aucune valeur significative dans la mantisse
        return !t.matches(".*[1-9A-F].*");
    }
}
