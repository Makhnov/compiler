package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;

public class Return extends AbstractInst {
    private AbstractExpr expr;

    public Return(AbstractExpr expr) {
        this.expr = expr;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        int r = compiler.registers().getScatchMin();
        expr.codeGenExpr(compiler, r);
        compiler.registers().load(Register.getR(r), Register.R0); // Résultat en R0

        // On n'ajoute pas de RTS, on branche juste vers la fin de la méthode en cours
        compiler.addInstruction(new BRA(compiler.labels().currentLabelFin()));
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        // Regle (3.24)
        if (returnType.isVoid()) {
            throw new ContextualError("Cannot return a value from a void method", getLocation());
        }
        AbstractExpr e = expr.verifyRValue(compiler, localEnv, currentClass, returnType);
        expr = e;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.println(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
    }
}