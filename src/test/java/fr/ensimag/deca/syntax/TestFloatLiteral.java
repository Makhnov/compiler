package fr.ensimag.deca.syntax;

import static org.junit.jupiter.api.Assertions.*;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for float literals (decimal + hexadecimal) underflow/overflow, etc.
 * Regles pp 53 - 54
 *
 * @author Nico
 * @date 11/01/2026
 */
public class TestFloatLiteral {

    SyntaxManager syntax;

    @BeforeEach
    public void setup() {
        syntax = new SyntaxManager();
    }

    /** Lex le premier token non-EOF, ou renvoie EOF si vide/blancs. */
    private Token firstToken(String input) {
        DecaLexer lexer = new DecaLexer(CharStreams.fromString(input));
        Token t = lexer.nextToken();
        // Le lexer peut produire des tokens de skip via actions, mais nextToken() ne les renvoie pas.
        return t;
    }

    /** Lex le 2e token (utile pour vérifier "pas un FLOAT" => séquence INT, DOT, ...). */
    private Token secondToken(String input) {
        DecaLexer lexer = new DecaLexer(CharStreams.fromString(input));
        lexer.nextToken();
        return lexer.nextToken();
    }

    private void assertIsFloatToken(String lit) {
        Token t = firstToken(lit);
        assertEquals(DecaLexer.FLOAT, t.getType(), "Est un FLOAT: " + lit);
    }

    private void assertIsNotFloatToken(String lit) {
        Token t = firstToken(lit);
        assertNotEquals(DecaLexer.FLOAT, t.getType(), "N'est PAS un FLOAT: " + lit);
    }

    private void assertFloatValueEquals(String lit, float expected, float eps) {
        assertIsFloatToken(lit);
        float v = syntax.getFloat(lit);
        assertEquals(expected, v, eps, "Valeur inattendue pour: " + lit);
    }

    @Test
    public void testValidDecimalNoExp() {
        // Cas simples sans exposant
        assertFloatValueEquals("12.34", 12.34f, 1e-4f);
        assertFloatValueEquals("0.0", 0.0f, 0.0f);
        assertFloatValueEquals("0012.3400", 12.34f, 1e-4f);
    }

    @Test
    public void testValidDecimalExp() {
        // Cas simples avec exposant
        assertFloatValueEquals("1.25e2", 125.0f, 1e-3f);
        assertFloatValueEquals("1.25E+2", 125.0f, 1e-3f);
        assertFloatValueEquals("1.25E-2", 0.0125f, 1e-6f);
    }

    @Test
    public void testValidDecimalSuffixF() {
        // Le suffixe f/F est autorisé et ignoré en décimal
        assertFloatValueEquals("1.0f", 1.0f, 0.0f);
        assertFloatValueEquals("1.0F", 1.0f, 0.0f);
        assertFloatValueEquals("3.4e38f", 3.4e38f, 1e34f);
    }

    @Test
    public void testValidHexBasics() {
        // Exemple simple: 0x1.8p1 = 1.5 * 2 = 3.0
        assertFloatValueEquals("0x1.8p1", 3.0f, 0.0f);
        assertFloatValueEquals("0X1.0P0", 1.0f, 0.0f);
        assertFloatValueEquals("0x0.0p0", 0.0f, 0.0f);
    }

    @Test
    public void testValidHexSuffixF() {
        // Le suffixe f/F est autorisé et ignoré en hexadécimal
        assertFloatValueEquals("0x1.0p0f", 1.0f, 0.0f);
        assertFloatValueEquals("0X1.0P0F", 1.0f, 0.0f);
    }

    @Test
    public void testOverflowDecimalMustFailLexically() {
        // > MAX_VALUE => overflow attendu
        assertThrows(LexerError.class, () -> firstToken("1.0e39"));
        assertThrows(LexerError.class, () -> firstToken("3.4028236e38"));
    }

    @Test
    public void testOverflowHexMustFailLexically() {
        // Infinity => overflow attendu
        assertThrows(LexerError.class, () -> firstToken("0x1.0p200"));
        assertThrows(LexerError.class, () -> firstToken("0X1.0P200F"));
    }

    @Test
    public void testUnderflowDecimalMustFailLexically() {
        // Arrondi a 0 => underflow attendu (décimal)
        assertThrows(LexerError.class, () -> firstToken("1.0e-50"));
        assertThrows(LexerError.class, () -> firstToken("9.9e-999"));
    }

    @Test
    public void testUnderflowHexMustFailLexically() {
        // Arrondi a 0 => underflow attendu (hexadécimal)
        assertThrows(LexerError.class, () -> firstToken("0x1.0p-200"));
        assertThrows(LexerError.class, () -> firstToken("0X0.1P-999"));        
        assertThrows(LexerError.class, () -> firstToken("0x0.F0p-150"));
    }

    @Test
    public void testLiteralZeroWithExponentMustBeAccepted() {
        // Zéros acceptés
        assertIsFloatToken("0.0e-999");
        assertEquals(0.0f, syntax.getFloat("0.0e-999"), 0.0f);

        assertIsFloatToken("0x0.0p-999");
        assertEquals(0.0f, syntax.getFloat("0x0.0p-999"), 0.0f);
    }

    @Test
    public void testInvalidFormsNotTokenizedAsFloat() {
        // Pas de chiffre après le point
        assertIsNotFloatToken("1.");
        assertEquals(DecaLexer.INT, firstToken("1.").getType());
        assertEquals(DecaLexer.DOT, secondToken("1.").getType());

        // Pas de chiffre avant le point
        assertIsNotFloatToken(".1");
        assertEquals(DecaLexer.DOT, firstToken(".1").getType());
        assertEquals(DecaLexer.INT, secondToken(".1").getType());

        // Pas de chiffre après le 'p'/'P'
        assertIsNotFloatToken("1e2");
        assertEquals(DecaLexer.INT, firstToken("1e2").getType());
        assertEquals(DecaLexer.IDENT, secondToken("1e2").getType());

        // Pas de chiffre après le 'e'/'E'
        assertIsNotFloatToken("0x1.0");
        assertNotEquals(DecaLexer.FLOAT, firstToken("0x1.0").getType());

        // Pas de chiffre avant le 'p'/'P'
        assertIsNotFloatToken("0x1.0e2");
        assertNotEquals(DecaLexer.FLOAT, firstToken("0x1.0e2").getType());
    }
}