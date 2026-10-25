package fr.ensimag.deca.tree;

import java.util.Iterator;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

public class ListDeclParam extends TreeList<AbstractDeclParam> {

    public Signature verifyListDeclParam(DecacCompiler compiler) throws ContextualError {
        // Regle (2.8)
        Signature sig = new Signature();
        for (AbstractDeclParam p : this.getList()) {
            sig.add(p.verifyDeclParam(compiler));
        }
        return sig;
    }

    public EnvironmentExp verifyListDeclParamInit(DecacCompiler compiler, EnvironmentExp classEnv) throws ContextualError {
        // Regle (3.12)
        EnvironmentExp methodEnv = new EnvironmentExp(classEnv);
        for (AbstractDeclParam p : this.getList()) {
            p.verifyDeclParamInit(compiler, methodEnv);
        }
        return methodEnv;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        Iterator<AbstractDeclParam> p = this.getList().iterator();
        while (p.hasNext()) {
            p.next().decompile(s);
            if (p.hasNext()) {
                s.print(",");
            }
        }
    }

    public void codeGenParam(DecacCompiler compiler) {
        int offset = -3; // (LB, retour, this)
        for (AbstractDeclParam p : getList()) {
            ExpDefinition def = p.paramName.getExpDefinition();
            def.setDAddr(new RegisterOffset(offset--, Register.LB));
        }
    }
}
