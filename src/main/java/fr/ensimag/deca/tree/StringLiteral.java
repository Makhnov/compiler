package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRPrintChar;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

import fr.ensimag.deca.syntax.SyntaxManager;

/**
 * String literal
 *
 * @author gl25
 * @date 01/01/2026
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.38)
        // Regle (3.46)
        Type t = compiler.environmentType.STRING;
        this.setType(t);
        return t;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) throws GenerationError {
        compiler.addInstruction(new WSTR(new ImmediateString(value)));
    }

    @Override
    protected void irGenPrint(DecacCompiler compiler, IRProgram ir, boolean newline) throws GenerationError {
        IRLabel lab = ir.string(getValue());
        IRLocation loc = IRLocation.from(getLocation());
        ir.addInstruction(new IRPrintString(loc, lab));        
    }

    @Override
    public void decompile(IndentPrintStream s) {
        String str;
        // On remplace d'abord les antislashs (pour pouvoir récupérer les guillemets échappés des guillemets précédés d'un antislash)
        str = value.replace("\\", "\\\\");
        // Puis on remplace les guillemets
        str = str.replace("\"", "\\\"");
        s.print("\"" + str + "\"");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "StringLiteral (" + value + ")";
    }

}
