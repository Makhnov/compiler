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
import fr.ensimag.ima.pseudocode.instructions.BRA;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IfThenElse extends AbstractInst {

    private final AbstractExpr condition;
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getThenBranch() {
        return thenBranch;
    }

    public ListInst getElseBranch() {
        return elseBranch;
    }

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    // protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
    //     // throw new UnsupportedOperationException("verifyInst not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    //     condition.verifyCondition(compiler, localEnv, currentClass);
    //     body.verifyListInst(compiler, localEnv, currentClass, returnType);
    // }
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        // Regle (3.22)
        condition.verifyCondition(compiler, localEnv, currentClass);
        thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        // throw new UnsupportedOperationException("codeGenInst not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
        int idLabel = compiler.labels().getLabelID();

        // Code (C, faux, E_Sinon.id) 
        condition.codeGenBranch(compiler, false, compiler.labels().labelSinon(idLabel));

        // Code (then)
        thenBranch.codeGenListInst(compiler);

        // BRA E_Fin.id (On ne lit pas le else)
        compiler.addInstruction(new BRA(compiler.labels().labelFin(idLabel)));

        // E_Sinon.id (Label du else)
        compiler.addLabel(compiler.labels().labelSinon(idLabel));

        // Code (else)
        elseBranch.codeGenListInst(compiler);

        // E_Fin.id (Label de fin)
        compiler.addLabel(compiler.labels().labelFin(idLabel));
    }

    @Override
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRLocation loc = IRLocation.from(getLocation());
        int idLabel = compiler.labels().getLabelID();

        condition.irGenBranch(compiler, ir, false, compiler.labels().irLabelSinon(idLabel));

        thenBranch.irGenListInst(compiler, ir);
        ir.addInstruction(new IRJump(loc, compiler.labels().irLabelFin(idLabel)));

        ir.addInstruction(new IRLabelDef(loc, compiler.labels().irLabelSinon(idLabel)));
        elseBranch.irGenListInst(compiler, ir);

        ir.addInstruction(new IRLabelDef(loc, compiler.labels().irLabelFin(idLabel)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if(");
        getCondition().decompile(s);
        s.println("){");

        s.indent();
        getThenBranch().decompile(s);
        s.unindent();

        s.println("} else {");

        s.indent();
        getElseBranch().decompile(s);
        s.unindent();

        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
