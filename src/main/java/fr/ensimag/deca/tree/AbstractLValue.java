package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.DecacCompiler;

/**
 * Left-hand side value of an assignment.
 * 
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractLValue extends AbstractExpr {

    /**
     * Méthode 
     * 
     * @param compiler
     * @param GPReg
     * @throws GenerationError
     */
    protected void codeGenStore(DecacCompiler compiler, GPRegister GPReg) throws GenerationError {
        // Comportement par défaut pour les Lvalues qui ne sont pas des Identifiers et les identifiers qui ne sont pas dans des registres
        DAddr addr = getDAddr(compiler);
        compiler.addInstruction(new STORE(GPReg, addr));
    }

    /**
     * Méthode qui retourne l'adresse de la LValue.
     * @param compiler
     * @return
     * @throws GenerationError
     */
    public DAddr codeGenAddr(DecacCompiler compiler) throws GenerationError {
        throw new UnsupportedOperationException("codeGenAddr not implemented for " + getClass().getSimpleName() + " @ " + getLocation());
    }

    /**
     * Méthode qui retourne l'adresse de la LValue dans la représentation intermédiaire.
     * 
     * @param compiler
     * @return fr.ensimag.deca.ir.IRDAddr
     * @throws GenerationError
     */
    public IRDAddr irGenAddr(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        throw new UnsupportedOperationException("irGenAddr not implemented for " + getClass().getSimpleName() + " @ " + getLocation());
    }
}
