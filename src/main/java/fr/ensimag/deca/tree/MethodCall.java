package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.List;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

public class MethodCall extends AbstractExpr {
    AbstractExpr object;
    AbstractIdentifier methodName;
    ListExpr params;

    public MethodCall(AbstractExpr object, AbstractIdentifier methodName, ListExpr params) {
        this.object = object;
        this.methodName = methodName;
        this.params = params;
    }

    @Override
    public boolean predictable() {
        return false;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.41)
        // Regle (3.71)
        // Regle (3.72)
        // Regle (3.73)
        // Regle (3.74)

        // verif si l'obj est bien une classe de l'environnement global
        Type classType = object.verifyExpr(compiler, localEnv, currentClass);
        if (!classType.isClass()) {
            throw new ContextualError("Method call must be on a class", getLocation());
        }
        ClassDefinition classDef = ((ClassType) classType).getDefinition();
        if (classDef == null) {
            throw new ContextualError("This Class is not in the environment", getLocation());
        }

        //  verif si la methode est bien dans la classe de l'object
        Type type = methodName.verifyMethodType(compiler, classDef.getMembers(), currentClass);
        Definition methodDef = methodName.getMethodDefinition();
        if (methodDef == null) {
            throw new ContextualError("This method is not defined", getLocation());
        }
        if (!methodDef.isMethod()) {
            throw new ContextualError("This method Call is not a method", getLocation());
        }

        Signature sig = methodDef.asMethodDefinition("Failed Method Cast", getLocation()).getSignature();

        // verfi des parametres
        if (params.size() != sig.size()) {
            throw new ContextualError("Wrong number of parameters", getLocation());
        }
        List<AbstractExpr> paramList = params.getList();
        for (int i = 0; i < params.size(); i++) {
            Type sigType = sig.paramNumber(i);
            params.set(i, paramList.get(i).verifyRValue(compiler, localEnv, currentClass, sigType));
        }

        setType(type);
        return type;
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        int m = compiler.registers().getScatchMin();
        GPRegister GPReg = Register.getR(m);
        compiler.registers().check(m);
        
        // On réserve l'espace dans la pile
        int nArgs = params.size();
        compiler.stack().addsp(nArgs + 1);        

        // On génère l'objet et on le place au sommet de la pile
        object.codeGenExpr(compiler, m);
        if (!compiler.optis().medium()) {
            compiler.addInstruction(new CMP(new NullOperand(), GPReg));
        }
        compiler.errors().nullDeref();
        compiler.addInstruction(new STORE(GPReg, new RegisterOffset(0, Register.SP)));

        // On génère et on empile chaque argument dans l'ordre
        int indexSp = -1;
        for (AbstractExpr arg : params.getList()) {
            arg.codeGenExpr(compiler, m);
            compiler.addInstruction(new STORE(GPReg, new RegisterOffset(indexSp, Register.SP)));
            indexSp--;
        }

        // On récupère l'adresse de la table des méthodes (VTable) depuis l'objet
        MethodDefinition mDef = methodName.getMethodDefinition();
        int idx = mDef.getIndex();
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), GPReg));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, GPReg), GPReg));
        
        // On effectue l'appel dynamique à l'index correspondant dans la VTable
        compiler.stack().bsr(); 
        compiler.addInstruction(new BSR(new RegisterOffset(idx, GPReg)));

        // On nettoie la pile et on récupère le résultat (si besoin)
        compiler.stack().subsp(nArgs + 1);
        if (!getType().isVoid()) {
            compiler.addInstruction(new LOAD(Register.R0, Register.getR(n)));
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if (object != null) {
            object.decompile(s);
            s.print(".");
        }
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        object.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        object.iter(f);
        methodName.iter(f);
        params.iter(f);
    }
}
