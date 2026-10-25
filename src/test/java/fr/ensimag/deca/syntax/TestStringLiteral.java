package fr.ensimag.deca.syntax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Test for strings (with special characters)
 * Regle 3.31 | Regle 3.30 | Regle 3.21
 * 
 * @author Nico
 * @date 10/01/2026
 */
public class TestStringLiteral {

    private SyntaxManager syntax = new SyntaxManager();
    private Token createToken(String text) {
        return new CommonToken(DecaLexer.STRING, text);
    }

    private Token createMultiLineToken(String text) {
        return new CommonToken(DecaLexer.MULTI_LINE_STRING, text);
    }

    private void assertLexingFails(String input) {
        DecaLexer lexer = new DecaLexer(CharStreams.fromString(input));
        try {
            new CommonTokenStream(lexer).fill();
            fail("Expected lexing to fail for: " + input);
        } catch (ParseCancellationException | DecaRecognitionException e) {
            // OK: votre infra de gestion d'erreur remonte l'une de ces deux formes
        }
    }

    @Test
    public void testSimpleString() {
        Token t = createToken("\"Hello world!\""); 

        // Résultat attendu en mémoire: Hello world!
        assertEquals("Hello world!", syntax.getString(t));
    }

    @Test
    public void testEscapedQuote() {
        Token t = createToken("\"a\\\"b\"");
        
        // Résultat attendu en mémoire: a"b (longueur 3)
        assertEquals("a\"b", syntax.getString(t));
    }

    @Test
    public void testEscapedBackslash() {
        Token t = createToken("\"a\\\\b\"");
        
        // Résultat attendu en mémoire: a\b (longueur 3)
        assertEquals("a\\b", syntax.getString(t));
    }

    @Test
    public void testMixedEscape() {
        Token t = createToken("\"a\\\\\\\"b\"");
        
        // Résultat attendu: a\"b
        assertEquals("a\\\"b", syntax.getString(t));
    }

    @Test
    public void testEmptyString() {
        Token t = createToken("\"\"");

        // Résultat attendu en mémoire: chaîne vide
        assertEquals("", syntax.getString(t));
    }

    @Test
    public void testOnlyEscapedQuote() {        
        Token t = createToken("\"\\\"\"");

        // Résultat attendu en mémoire: "
        assertEquals("\"", syntax.getString(t));
    }

    @Test
    public void testOnlyEscapedBackslash() {        
        Token t = createToken("\"\\\\\"");

        // Résultat attendu en mémoire: \
        assertEquals("\\", syntax.getString(t));
    }

    @Test
    public void testMultiLineKeepsNewline() {
        Token t = createMultiLineToken("\"a\nb\"");

        // Résultat attendu en mémoire: a\nb
        assertEquals("a\nb", syntax.getString(t));
    }

    @Test
    public void testLexerRejectsInvalidEscape() {
        
        // Séquence d'échappement non reconnue: \q
        assertLexingFails("\"a\\q\"");
    }

    @Test
    public void testLexerRejectsUnterminatedString() {

        // Chaîne non terminée
        assertLexingFails("\"abc");
    }    
}