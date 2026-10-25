package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Print statement (print, println, ...).
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractPrint extends AbstractInst {

    private boolean printHex;
    private ListExpr arguments = new ListExpr();

    /**
     * Get the suffix for this print variant (empty for print, "ln" for println).
     * @return the suffix string
     */
    abstract String getSuffix();

    /**
     * Construct a print statement.
     * @param printHex true to print floats in hexadecimal
     * @param arguments the list of expressions to print
     */
    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        // Regle (3.21)
        // Regle (3.26)
        // Regle (3.27)
        // Regle (3.30)
        // Regle (3.31)

        // On ne peut pas afficher un booléen ? ni un objet ? NICO // A FAIRE
        for (AbstractExpr expr : getArguments().getList()) {
            Type type = expr.verifyExpr(compiler, localEnv, currentClass);
            if (!type.isInt() && !type.isFloat() && !type.isString()) {
                throw new ContextualError("print: type " + type + " not allowed", getLocation());
            }
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        for (AbstractExpr a : getArguments().getList()) {
            a.codeGenPrint(compiler, printHex);
        }
    }

    @Override
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        for (AbstractExpr a : getArguments().getList()) {
            a.irGenPrint(compiler, ir, printHex);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print" + getSuffix());
        if (printHex) {
            s.print("x");
        }
        s.print("(");
        getArguments().decompile(s);
        s.println(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
