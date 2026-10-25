package fr.ensimag.deca.syntax;

import static org.junit.jupiter.api.Assertions.*;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for integer literals overflow, tokenization, etc.
 * Regles pp 53 - 54
 *
 * @author Nico
 * @date 11/01/2026
 */
public class TestIntLiteral {

    SyntaxManager syntax;

    @BeforeEach
    public void setup() {
        syntax = new SyntaxManager();
    }

    private Token nthToken(String input, int n) {
        DecaLexer lexer = new DecaLexer(CharStreams.fromString(input));
        Token t = null;
        for (int i = 0; i < n; i++) {
            t = lexer.nextToken();
        }
        return t;
    }

    private Token firstToken(String input) {
        return nthToken(input, 1);
    }

    private Token secondToken(String input) {
        return nthToken(input, 2);
    }

    private Token thirdToken(String input) {
        return nthToken(input, 3);
    }

    private void assertIsIntToken(String lit) {
        Token t = firstToken(lit);
        assertEquals(DecaLexer.INT, t.getType(), "Est un INT: " + lit);
    }

    private void assertIntValueEquals(String lit, int expected) {
        assertIsIntToken(lit);
        int v = syntax.getInt(lit);
        assertEquals(expected, v, "Valeur inattendue pour: " + lit);
    }

    @Test
    public void testValidSimpleInts() {
        // Cas simples
        assertIntValueEquals("0", 0);
        assertIntValueEquals("1", 1);
        assertIntValueEquals("42", 42);
        assertIntValueEquals("2147483647", 2147483647);
    }

    @Test
    public void testOverflowMustFailLexically() {
        // > MAX_INT => overflow attendu
        assertThrows(LexerError.class, () -> firstToken("2147483648"));
        assertThrows(LexerError.class, () -> firstToken("999999999999999999999"));
    }

    @Test
    public void testMinusIsNotPartOfIntLiteral() {
        // Le '-' est un token séparé
        assertEquals(DecaLexer.MINUS, firstToken("-1").getType());
        assertEquals(DecaLexer.INT, secondToken("-1").getType());

        // Overflow, renvoie le token INT("2147483648") après le '-'
        assertThrows(LexerError.class, () -> secondToken("-2147483648"));
    }

    @Test
    public void testLeadingZerosAreNotOneIntToken() {
        // "00" => INT("0") puis INT("0")
        assertEquals(DecaLexer.INT, firstToken("00").getType());
        assertEquals(DecaLexer.INT, secondToken("00").getType());
        assertEquals(Token.EOF, thirdToken("00").getType());

        // "0012" => INT("0") INT("0") INT("12")
        assertEquals(DecaLexer.INT, firstToken("0012").getType());
        assertEquals(DecaLexer.INT, secondToken("0012").getType());
        assertEquals(DecaLexer.INT, thirdToken("0012").getType());
    }

    @Test
    public void testNotIntForms() {
        // "1." => INT puis DOT
        assertEquals(DecaLexer.INT, firstToken("1.").getType());
        assertEquals(DecaLexer.DOT, secondToken("1.").getType());

        // ".1" => DOT puis INT
        assertEquals(DecaLexer.DOT, firstToken(".1").getType());
        assertEquals(DecaLexer.INT, secondToken(".1").getType());

        // "1e2" => pas un INT unique (INT puis IDENT)
        assertEquals(DecaLexer.INT, firstToken("1e2").getType());
        assertEquals(DecaLexer.IDENT, secondToken("1e2").getType());
    }
}
