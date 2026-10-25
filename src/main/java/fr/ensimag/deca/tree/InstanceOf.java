package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;

public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier type;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier type) {
        this.expr = expr;
        this.type = type;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.40)
        Type type1 = expr.verifyExpr(compiler, localEnv, currentClass);
        Type type2 = type.verifyType(compiler);

        if (type1.isClassOrNull() && type2.isClass()) {
            Type t = compiler.environmentType.BOOLEAN;
            setType(t);
            return t;
        }
        throw new ContextualError("Invalid instanceOf expression", getLocation());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expr.decompile(s);
        s.print(" instanceof ");
        type.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        type.iter(f);
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        int id = compiler.labels().getLabelID();
        Label lLoop    = compiler.labels().label("instof_loop", id);
        Label lMatch   = compiler.labels().label("instof_match", id);
        Label lNoMatch = compiler.labels().label("instof_nomatch", id);
        Label lEnd     = compiler.labels().label("instof_end", id);

        // On évalue l'expression et on place l'adresse dans R0
        GPRegister tmp = Register.getR(compiler.registers().getScatchMin());
        compiler.stack().push(tmp);
        expr.codeGenExpr(compiler, tmp.getNumber());
        compiler.registers().load(tmp, Register.R0);
        compiler.stack().pop(tmp);

        // On gère le cas null directement (échec)
        compiler.registers().load(new NullOperand(), Register.R1);
        compiler.addInstruction(new CMP(Register.R1, Register.R0));
        compiler.addInstruction(new BEQ(lNoMatch));

        // On récupère l'adresse dans la VTable
        compiler.registers().load(new RegisterOffset(0, Register.R0), Register.R0);

        // On boucle pour remonter la chaîne d'héritage
        compiler.addLabel(lLoop);

        // On compare la classe actuelle avec la classe cible
        ClassDefinition targetDef = type.getClassDefinition();
        compiler.addInstruction(new LEA(targetDef.address(), Register.R1));
        compiler.addInstruction(new CMP(Register.R1, Register.R0));
        compiler.addInstruction(new BEQ(lMatch));

        // On passe à la super-classe (offset 0 dans la VTable)
        compiler.registers().load(new RegisterOffset(0, Register.R0), Register.R0);

        // Si on atteint le sommet de la hiérarchie on branche à l'échec
        compiler.registers().load(new NullOperand(), Register.R1);
        compiler.addInstruction(new CMP(Register.R1, Register.R0));
        compiler.addInstruction(new BEQ(lNoMatch));

        compiler.addInstruction(new BRA(lLoop));

        // Sinon c'est un succès
        compiler.addLabel(lMatch);
        if (b) {
            compiler.addInstruction(new BRA(label));
        }
        compiler.addInstruction(new BRA(lEnd));

        // Branchement échec
        compiler.addLabel(lNoMatch);
        if (!b) {
            compiler.addInstruction(new BRA(label));
        }
        compiler.addLabel(lEnd);
    }

    /**
     * Evaluation directe de l'instanceof (le booléen est une donnée)
     */
    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        int id = compiler.labels().getLabelID();
        Label lTrue  = compiler.labels().label("instof_true", id);
        Label lEnd   = compiler.labels().label("instof_end", id);   
        // On génère le code de branchement
        codeGenBranch(compiler, true, lTrue);
        // Faux
        compiler.registers().load(new ImmediateInteger(0), Register.getR(n));
        compiler.addInstruction(new BRA(lEnd));
        // Vrai
        compiler.addLabel(lTrue);
        compiler.registers().load(new ImmediateInteger(1), Register.getR(n));
        // Fin
        compiler.addLabel(lEnd);
    }
}

