package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;

public class DeclParam extends AbstractDeclParam {

    public DeclParam(AbstractIdentifier paramType, AbstractIdentifier paramName) {
        this.paramName = paramName;
        this.paramType = paramType;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        paramType.decompile(s);
        s.print(" ");
        paramName.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        paramType.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, true);

    }

    @Override
    protected Type verifyDeclParam(DecacCompiler compiler) throws ContextualError {
        // Regle (2.9)
        Type t = paramType.verifyType(compiler);

        if (t.isVoid()) {
            throw new ContextualError("Forbidden parameter type (void)", getLocation());
        }

        ParamDefinition paramDef = new ParamDefinition(t, getLocation());
        paramName.setDefinition(paramDef);
        paramName.setType(t);
        return t;
    }

    @Override
    protected void verifyDeclParamInit(DecacCompiler compiler, EnvironmentExp methodEnv) throws ContextualError {
        // Regle (3.13)
        Type type = paramType.verifyType(compiler);

        ParamDefinition paramDef = new ParamDefinition(type, getLocation());
        try {
            methodEnv.declare(paramName.getName(), paramDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Parameter already declared: ", getLocation());
        }

        paramName.setDefinition(paramDef);
        paramName.setType(type);
    }
}
