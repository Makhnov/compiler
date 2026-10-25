package fr.ensimag.deca.tree;


import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;

/**
 * AST node representing the equality comparison operator (==).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
    
    @Override
    protected String IRBranchOnTrue() {
        return "BEQ";
    }

    @Override
    protected String IRBranchOnFalse() {
        return "BNE";
    }

    @Override
    protected String getOperatorName() {
        return "==";
    }    
    
    @Override
    protected void codeGenGoTo(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {    
        int n = compiler.registers().getScatchMin();
        
        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();

        if (compiler.optis().basic()) {
            // Cas 1, rhs est 0        
            OptionalInt oxR = rhs.getIntegerValue();
            if (oxR.isPresent()) {
                int xR = oxR.getAsInt();
                GPRegister GPRegL = lhs.getRegister(compiler);
                if (GPRegL != null) {
                    compiler.addInstruction(new CMP(new ImmediateInteger(xR), GPRegL));
                    compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
                    return;
                }
                if (xR == 0) {
                    lhs.codeGenExpr(compiler, n); // Calcule x, met à jour les flags Z et N
                    // A FAIRE on peut optimiser ici en évitant le load, vérifier si c'est vraiment faisable ou trop risqué
                    if (!compiler.optis().medium()) {
                        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(n))); // Potentiellement inutile                        
                    }
                    compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
                    return;
                }
            }

            // Cas 2, lhs est 0
            OptionalInt oxL = lhs.getIntegerValue();
            if (oxL.isPresent()) {
                int xL = oxL.getAsInt();
                GPRegister GPRegR = rhs.getRegister(compiler);
                if (GPRegR != null) {
                    compiler.addInstruction(new CMP(new ImmediateInteger(xL), GPRegR));
                    compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
                    return;
                }
                if (xL == 0) {
                    rhs.codeGenExpr(compiler, n); // Calcule x, met à jour les flags Z et N
                    if (!compiler.optis().medium()) {
                        compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.getR(n))); // Potentiellement inutile                        
                    }
                    compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
                    return;
                }
            }
        }
                
        DVal lValue = lhs.getDVal(compiler);
        DVal rValue = rhs.getDVal(compiler);

        if (compiler.optis().medium()) {
            // Cas 3, propagation de constante (lhs)
            if (lValue != null) {
                rhs.codeGenExpr(compiler, n);
                compiler.addInstruction(new CMP(lValue, Register.getR(n)));
                compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
                return;
            }

            // Cas 4, propagation de constante (rhs)
            if (rValue != null) {
                lhs.codeGenExpr(compiler, n);
                compiler.addInstruction(new CMP(rValue, Register.getR(n)));
                compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
                return;
            }
        }

        // Cas général
        lhs.codeGenExpr(compiler, n);
        DVal rhsVal = codeGenRightOperand(compiler, n);
        compiler.addInstruction(new CMP(rhsVal, Register.getR(n)));
        compiler.addInstruction(b ? new BEQ(label) : new BNE(label));
    }
}