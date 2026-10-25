package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.RTS;

public class DeclMethod extends AbstractDeclMethod {

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName, ListDeclParam params, AbstractMethodBody body) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.body = body;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") ");
        body.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);

    }

    @Override
    protected void verifyDeclMethod(DecacCompiler compiler, ClassDefinition classDef) throws ContextualError {
        // Regle (2.7)
        Type t = returnType.verifyType(compiler);
        Signature sig = params.verifyListDeclParam(compiler);
        ClassDefinition superDef = classDef.getSuperClass();
        
        // si redefinition d'une methode de la superClasse        
        ExpDefinition superMethod = null;
        if (superDef != null) {
            superMethod = superDef.getMembers().get(methodName.getName());
        }
        
        int index;
        if (superMethod != null) {
            if (!superMethod.isMethod()) {
                throw new ContextualError("The Overriden Method " + methodName.getName().getName() + " was not a method", getLocation());
            }
            MethodDefinition superMethodDef = superMethod.asMethodDefinition("Failed Method Cast", getLocation());

            // verif de la signature
            if (!superMethodDef.getSignature().sameSignature(sig)) {
                throw new ContextualError("Override Method " + methodName.getName().getName() + " must have the same signature as the overridden method", getLocation());
            }
            // verif du type de retour
            if (!t.isSubTypeOf(superMethodDef.getType())) {
                throw new ContextualError("Override Method " + methodName.getName().getName() + " must return a subtype of the overridden method's return type", getLocation());
            }

            index = superMethodDef.getIndex(); // TODO verif la logique des index de methodes
        } else {
            // nouvelle methode
            index = classDef.incNumberOfMethods();
        }
        MethodDefinition methodDef = new MethodDefinition(t, getLocation(), sig, index);

        try {
            classDef.getMembers().declare(methodName.getName(), methodDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Method " + methodName.getName().getName() + " already defined", getLocation());
        }

        methodName.setDefinition(methodDef);
        methodName.setType(t);
    }

    @Override
    protected void verifyDeclMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp classEnv) throws ContextualError {
        // Regle (3.11)
        Type type = returnType.verifyType(compiler);
        EnvironmentExp methodEnv = params.verifyListDeclParamInit(compiler, classEnv);
        body.verifyMethodBody(compiler, currentClass, methodEnv, type);
    }

    @Override
    protected void assignMethodLabel(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError {
        MethodDefinition mDef = methodName.getMethodDefinition();

        // label = code.<Class>.<method>
        String cName = currentClass.getType().getName().getName();
        String mName = methodName.getName().getName();

        mDef.setLabel(compiler.labels().labelMethod(cName, mName));
    }

    @Override
    protected void generateMethodEntry(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError {
        MethodDefinition mDef = methodName.getMethodDefinition();
        compiler.methods().setMethodEntry(currentClass, mDef);
    }

    @Override
    protected void codeGenMethod(DecacCompiler compiler, ClassDefinition currentClass) throws GenerationError {
        MethodDefinition mDef = methodName.getMethodDefinition();

        // Bloc isolé pour calculer stack().size() + injecter TSTO/BOV au bon endroit
        IMAProgram block = compiler.pushNewBlock();
        compiler.stack().reset();

        // On créé un nouveau label de fin pour la méthode
        String cName = currentClass.getType().getName().getName();
        String mName = methodName.getName().getName();
        Label mel = compiler.labels().labelFin(cName, mName);
        compiler.labels().pushLabelFin(mel);

        // On s'assure que le label de fin de méthode est bien "pop" par le manager à la fin de la génération
        try {
            // P1 Paramètres (DAddr en LB)
            params.codeGenParam(compiler);

            // P2 Corps
            body.codeGenMethod(compiler);

            // Ici c'est le point de sortie unique pour la méthode (avec ou sans return)
            compiler.addLabel(mel);
            int stackSize = body.size();
            if (stackSize > 0) {
                compiler.stack().subsp(stackSize); // Libération de l'espace nécessaire aux variables locales
            }
            // Retour de la méthode
            compiler.addInstruction(new RTS());

            // Prologue (on connait maintenant la taille nécessaire)
            int size = compiler.stack().size();
            compiler.errors().stack(size);
            compiler.addFirstLabel(mDef.getLabel());

        } finally {
            compiler.labels().popLabelFin();
            compiler.popBlock(block);
        }
    }
}
