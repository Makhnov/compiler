package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.InlinePortion;

public class MethodAsmBody extends AbstractMethodBody {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);
    String asmCode; // beark (Nico) On devrait utiliser les champs de AbstractMethodBody ...

    public MethodAsmBody(String asmCode) {
        this.asmCode = asmCode;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("asm(");

        s.indent();
        s.println(asmCode);
        s.unindent();

        s.println(");");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // No children
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // No children
    }

    @Override
    public void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv, Type returnType) throws ContextualError {
        // Regle (3.15)
        // Regle (3.18)
        // No verification needed for asm body
        LOG.warn("ASM method body verification is not implemented.");
    }

    @Override
    public void codeGenMethod(DecacCompiler compiler) throws GenerationError {
        IMAProgram asmBlock = compiler.pushNewBlock();
        try {
            for (String line : asmCode.split("\n")) {
                compiler.add(new InlinePortion("\t" + line));
            }
        } catch (Exception e) {
            throw new GenerationError("Error generating ASM code", getLocation());
        } finally {
            compiler.popBlock(asmBlock);
        }
    }
}
