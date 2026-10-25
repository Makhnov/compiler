package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;

/**
 * List of declarations (e.g. int x; float y,z).
 * 
 * @author gl25
 * @date 01/01/2026
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar var : this.getList()) {
            var.decompile(s);
        }
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv 
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to 
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to 
     *      the "env_exp_r" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.16)
        for (AbstractDeclVar v : getList()) {
            v.verifyDeclVar(compiler, localEnv, currentClass);
        }
    }

    public void codeGenDeclVars(DecacCompiler compiler, int offset) throws GenerationError {
        codeGenDeclVars(compiler, offset, Register.GB, true);
    }
        
    public void codeGenDeclVars(DecacCompiler compiler, int startOffset, Register base, boolean registerInVarManager)
            throws GenerationError {

        int o = startOffset;
        for (AbstractDeclVar v : getList()) {
            v.codeGenDeclVar(compiler, o++, base, registerInVarManager);
        }
    }

    public void irGenListDeclVariable(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        for (AbstractDeclVar v : getList()) {
            v.irGenDeclVar(compiler, ir);
        }
    }
}
