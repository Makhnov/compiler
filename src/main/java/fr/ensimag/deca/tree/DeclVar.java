package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

/**
 * AST node representing a variable declaration.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class DeclVar extends AbstractDeclVar {

    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.17)
        Type decType = type.verifyType(compiler);
        if (decType.isVoid()) {
            throw new ContextualError("Forbidden declaration (void)", getLocation());
        }
        // Avant l'insertion, on vérifie l'initialisation
        initialization.verifyInitialization(compiler, decType, localEnv, currentClass);

        // Insertion dans l'environnement
        VariableDefinition varDef = new VariableDefinition(decType, getLocation());
        try {
            localEnv.declare(varName.getName(), varDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable already declared: " + varName.getName().getName(), getLocation());
        }

        varName.setDefinition(varDef);
        varName.setType(decType);
    }

    @Override
    protected void codeGenDeclVar(DecacCompiler compiler, int offset, Register base, boolean registerInVarManager) throws GenerationError {
        // On génère le code de l'initialisation
        VariableDefinition varDef = varName.getVariableDefinition();
        RegisterOffset addr = new RegisterOffset(offset, base);
        varDef.setDAddr(addr);

        // On enregistre la variable dans le gestionnaire
        if (registerInVarManager) {
            compiler.vars().register(varDef);
        }

        // A compléter (valeur initiale ?)
        initialization.codeGenInit(compiler, addr, varName.getType());
    }

    @Override
    protected void irGenDeclVar(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDAddr rhs = ir.addStack(varName.getName().getName(), varName.getIRType());        
        initialization.irGenInit(compiler, ir, rhs, varName.getIRType());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
