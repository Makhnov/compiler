package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRJump;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * AST node representing a while loop statement.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        // Regle (3.25)
        condition.verifyCondition(compiler, localEnv, currentClass);
        body.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        // throw new UnsupportedOperationException("codeGenInst not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
        int idLabel = compiler.labels().getLabelID();

        // BRA E_Cond.n (on saute à la condition)
        compiler.addInstruction(new BRA(compiler.labels().labelCond(idLabel)));

        // E_Debut.n (on pose le label du début de la boucle)
        compiler.addLabel(compiler.labels().labelDebut(idLabel));
        body.codeGenListInst(compiler);

        // E_Cond.n (on pose le label de la condition)
        compiler.addLabel(compiler.labels().labelCond(idLabel));

        // Code(C, vrai, E_Debut.n) On génère le code de la condition
        condition.codeGenBranch(compiler, true, compiler.labels().labelDebut(idLabel));
    }

    @Override
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRLocation loc = IRLocation.from(getLocation());
        int idLabel = compiler.labels().getLabelID();

        ir.addInstruction(new IRJump(loc, compiler.labels().irLabelCond(idLabel)));

        ir.addInstruction(new IRLabelDef(loc, compiler.labels().irLabelDebut(idLabel)));
        body.irGenListInst(compiler, ir);

        ir.addInstruction(new IRLabelDef(loc, compiler.labels().irLabelCond(idLabel)));
        condition.irGenBranch(compiler, ir, true, compiler.labels().irLabelDebut(idLabel));

        ir.addInstruction(new IRLabelDef(loc, compiler.labels().irLabelFin(idLabel)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while(");
        getCondition().decompile(s);
        s.println("){");

        s.indent();
        getBody().decompile(s);
        s.unindent();

        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }
}
