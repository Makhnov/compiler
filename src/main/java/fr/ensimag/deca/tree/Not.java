package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRJump;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
/**
 * AST node representing the logical NOT operator (!).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.37)
        // Regle (3.63)
        // verifie si le type est valide : Boolean
        Type type = getOperand().verifyExpr(compiler, localEnv, currentClass);
        if ((!type.isBoolean())) {
            throw new ContextualError("Unary Not Only Takes Boolean", getLocation());
        }
        this.setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {// A FACTORISER
        int id = compiler.labels().getLabelID();
        Label lNot = compiler.labels().label("E_Not.", id);
        Label lEnd  = compiler.labels().labelFin(id);

        // operand == false (!operand == true)
        getOperand().codeGenBranch(compiler, false, lNot);
        
        // operand == true (on charge 0)
        compiler.registers().load(new ImmediateInteger(0), Register.getR(n));
        compiler.addInstruction(new BRA(lEnd));

        // operand == false (on charge 1)
        compiler.addLabel(lNot);
        compiler.registers().load(new ImmediateInteger(1), Register.getR(n));

        // Fin du branch
        compiler.addLabel(lEnd);
    }

    @Override
    protected fr.ensimag.deca.ir.operands.IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError { // A FACTORISER
        IRLocation loc = IRLocation.from(getLocation());
        int id = compiler.labels().getLabelID();

        // Résultat de l'expression (le booléen est une donnée 0 ou 1)
        IRScratch dst = ir.scratch(IRType.BOOLEAN);

        IRLabel lNot = compiler.labels().irLabel("E_Not.", id);
        IRLabel lEnd  = compiler.labels().irLabelFin(id);

        // On initialise dst à 0
        ir.addInstruction(new IRBinaryExpr(loc, "SUB_INT", dst, new IRImmediateInt(0), new IRImmediateInt(0)));

        // si !operand est vrai, LTrue
        getOperand().irGenBranch(compiler, ir, false, lNot);

        // Saut inconditionnel à la fin
        ir.addInstruction(new IRJump(loc, lEnd));

        // operand == false (dst = 1)
        ir.addInstruction(new IRLabelDef(loc, lNot));
        ir.addInstruction(new IRBinaryExpr(loc, "ADD_INT", dst, new IRImmediateInt(0), new IRImmediateInt(1)));

        // Fin de l'expression
        ir.addInstruction(new IRLabelDef(loc, lEnd));
        return dst;
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        // Si on souhaite brancher si l'expression est vraie, alors l'operande doit etre faux et inversement
        getOperand().codeGenBranch(compiler, !b, label);
    }
    
    @Override
    protected void irGenBranch(DecacCompiler compiler, IRProgram ir, boolean b, IRLabel label) throws GenerationError {
        getOperand().irGenBranch(compiler, ir, !b, label);
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }

}
