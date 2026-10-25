package fr.ensimag.deca.syntax;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.deca.tree.Tree;

import java.io.PrintStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.log4j.Logger;

/**
 * The super class of the generated parser. It is extended by the generated
 * code because of the superClass option in the .g file.
 *
 * @author gl25, Based on template by Jim Idle - Temporal Wave LLC - jimi@temporal-wave.com
 * @date 01/01/2026
 */
public abstract class AbstractDecaParser extends Parser {
    Logger LOG = Logger.getLogger(AbstractDecaParser.class);

    private SyntaxManager syntax = new SyntaxManager();
    private DecacCompiler decacCompiler;

    /**
     * Set the DECA compiler for this parser.
     * @param decacCompiler the DECA compiler
     */
    public void setDecacCompiler(DecacCompiler decacCompiler) {
        this.decacCompiler = decacCompiler;
    }

    /**
     * Get the syntax manager for error handling.
     * @return the syntax manager
     */
    protected SyntaxManager syntax() {
        return syntax;
    }

    /**
     * Get the DECA compiler associated with this parser.
     * @return the DECA compiler
     */
    protected DecacCompiler getDecacCompiler() {
        return decacCompiler;
    }

    /**
     * Parse the program from the input stream.
     * @return the parsed abstract program
     */
    protected abstract AbstractProgram parseProgram();

    /**
     * Parse the program and handle any errors.
     * @param err the error output stream
     * @return the parsed program, or null if errors occurred
     */
    public AbstractProgram parseProgramAndManageErrors(PrintStream err) {
        try {
            AbstractProgram result = parseProgram();
            assert (result != null);
            return result;
        } catch (ParseCancellationException e) {
            // LOG.debug("ParseCancellationException raised while compiling file:", e);
            if (e.getCause() instanceof LocationException) {
                ((LocationException) e.getCause()).display(err);
                return null;
            } else {
                throw new DecacInternalError("Parsing cancelled", e);
            }
        } catch (DecaRecognitionException e) {
            e.display(err);
            return null;
        }
    }

    /**
     * Extract the Location of a token.
     *
     * @param token the token to extract location from
     * @return the location of the token
     */
    protected Location tokenLocation(Token token) {
        return new Location(token.getLine(), token.getCharPositionInLine(), token.getInputStream().getSourceName());
    }

    /**
     * Sets the location of Tree to the one in Token.
     * This is a simple convenience wrapper around {@link Tree#setLocation(Location)}.
     *
     * @param tree the tree node to set the location on
     * @param token the token to extract the location from
     */
    protected void setLocation(Tree tree, Token token) {
        tree.setLocation(tokenLocation(token));
    }

    /**
     * Create a new parser instance, pre-supplying the input token stream.
     *
     * @param input The stream of tokens that will be pulled from the lexer
     */
    @SuppressWarnings("this-escape")
    protected AbstractDecaParser(TokenStream input) {
        super(input);
        setErrorHandler(new DefaultErrorStrategy() {
            @Override
            public void reportError(Parser recognizer, RecognitionException e) {
                // LOG.debug("Error found by ANTLR =>" + e.getMessage());
                if (e instanceof DecaRecognitionException) {
                    Token offendingToken = e.getOffendingToken();
                    if (offendingToken == null) {
                        offendingToken = recognizer.getCurrentToken();
                    }
                    recognizer.notifyErrorListeners(offendingToken, e.getMessage(), e);
                } else {
                    super.reportError(recognizer, e);
                }
            }
        });
        removeErrorListeners();
        addErrorListener(new DecacErrorListner(input));
    }
}
