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

public class MethodBody extends AbstractMethodBody {

    public MethodBody(ListDeclVar vars, ListInst insts) {
        this.vars = vars;
        this.insts = insts;
    }

    @Override
    public int size() {
        return vars.getList().size();
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp paramsEnv, Type returnType) throws ContextualError {
        // Regle (3.14)
        // Regle (3.18)
        vars.verifyListDeclVariable(compiler, paramsEnv, currentClass);
        insts.verifyListInst(compiler, paramsEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");

        s.indent();
        vars.decompile(s);
        insts.decompile(s);
        s.unindent();

        s.println("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        vars.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }

    @Override
    public void codeGenMethod(DecacCompiler compiler) throws GenerationError {
        int stackSize = vars.getList().size();

        if (stackSize > 0) {
            compiler.stack().addsp(stackSize); // réserve et compte pour TSTO
        }

        vars.codeGenDeclVars(compiler, 1, Register.LB, false); // locals en LB, pas VarManager

        insts.codeGenListInst(compiler);
    }
}
