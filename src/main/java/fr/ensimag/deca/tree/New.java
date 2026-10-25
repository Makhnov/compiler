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
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * AST node representing the 'new' expression for object instantiation.
 *
 * @author gl25
 * @date 04/01/2026
 */
public class New extends AbstractExpr {
    private AbstractIdentifier ident;

    public New(AbstractIdentifier ident) {
        this.ident = ident;
    }

    @Override
    public boolean predictable() {
        return false;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.42)
        Type t = ident.verifyType(compiler);

        if (!t.isClass()) {
            throw new ContextualError("'new' must be followed by a class identifier", getLocation());
        }

        setType(t);
        return t;
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        ClassType ct = (ClassType) getType();
        ClassDefinition cDef = ct.getDefinition();

        // On calcule la taille nécessaire (+1 mot pour l'adresse de la VTable)
        int size = 1 + cDef.getNumberOfFields();
        compiler.addInstruction(new NEW(new ImmediateInteger(size), Register.getR(n)));
        compiler.errors().heapOverflow();

        // On note l'adresse de la VTable au début
        compiler.addInstruction(new LEA(cDef.address(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, Register.getR(n))));

        // On sauvegarde l'objet sur la pile
        compiler.stack().push(Register.getR(n));

        // On appelle le programme d'initialisation de la classe
        String cName = cDef.getType().getName().getName();
        compiler.stack().bsr(); 
        compiler.addInstruction(new BSR(compiler.labels().labelInit(cName)));
        
        // On récupère l'adresse de l'objet dans le registre cible
        compiler.stack().pop(Register.getR(n));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        ident.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        ident.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        ident.iter(f);
    }

}
