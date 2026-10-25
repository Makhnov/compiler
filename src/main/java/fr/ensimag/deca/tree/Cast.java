package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public class Cast extends AbstractExpr {
    AbstractIdentifier type;
    AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.39)
        Type type1 = type.verifyType(compiler);
        Type type2 = expr.verifyExpr(compiler, localEnv, currentClass);

        if (!type2.castCompatible(type1)) {
            throw new ContextualError("Incompatible types for cast", getLocation());
        }
        setType(type1);
        return type1;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") (");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        // On évalue expr dans Rn
        expr.codeGenExpr(compiler, n);

        Type dst = getType();
        Type src = expr.getType();

        // (float)(int) et (int)(float)
        if (dst.isFloat() && src.isInt()) {
            compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)), "Explicit conversion int -> float");
            return;
        }

        if (dst.isInt() && src.isFloat()) {
            compiler.addInstruction(new INT(Register.getR(n), Register.getR(n)), "Explicit conversion float -> int");
            return;
        }

        // Cast classe: (B)(a)
        if (dst.isClass()) {
            // On accepte null 

            // On enrobe pour capturer les erreurs contextuelles et les renvoyer en tant qu'erreurs de génération par sécurité
            try {
                ClassType srcClass = src.asClassType("Cast source not a class type", getLocation());
                ClassType dstClass = dst.asClassType("Cast target not a class type", getLocation());

                // Si src n'est PAS un sous-type de dst (donc c'est un downcast), il faut vérifier à l'exécution.
                if (!srcClass.isSubClassOf(dstClass)) {
                    castCheck(compiler, Register.getR(n), dstClass);
                }
            } catch (ContextualError e) {
                // src ou dst n'est pas un ClassType
                throw new GenerationError("Cast entre types non-classes", getLocation());
            }
        }

        // Tout les autres casts, on ne fait rien (dont le upcast entr autres)
    }

    private void castCheck(DecacCompiler compiler, GPRegister objReg, ClassType targetClass) throws GenerationError {

        // labels
        int id = compiler.labels().getLabelID();
        Label lEnd = compiler.labels().labelFin(id);
        Label lCast = compiler.labels().labelCast(id);
        Label lFail = compiler.labels().labelEchec(id);

        // Si l'objet est null, le cast réussit toujours
        compiler.addInstruction(new CMP(new NullOperand(), objReg));
        compiler.addInstruction(new BEQ(lEnd));

        // On met la classe réeele dans R0 et la classe cible dans R1
        compiler.addInstruction(new LOAD(new RegisterOffset(0, objReg), Register.R0));
        DAddr adTarget = compiler.methods().getVTAddr(targetClass.getDefinition());
        compiler.addInstruction(new LEA(adTarget, Register.R1));

        compiler.addLabel(lCast);
        // Si la classe actuelle correspond à la cible, on valide le cast
        compiler.addInstruction(new CMP(Register.R1, Register.R0));
        compiler.addInstruction(new BEQ(lEnd));

        // Si on arrive à la racine, le cast échoue
        compiler.addInstruction(new CMP(new NullOperand(), Register.R0));
        compiler.addInstruction(new BEQ(lFail));

        // Sinon on charge la superclasse et on rééssaie
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R0), Register.R0));
        compiler.addInstruction(new BRA(lCast));

        compiler.addLabel(lFail);
        compiler.errors().invalidCast();
        compiler.addLabel(lEnd);
    }
}
