package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRJump;
import fr.ensimag.deca.ir.instructions.IRLabelDef;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;

/**
 * Abstract base class for boolean binary operators (AND, OR).
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    /**
     * Construct a boolean operator.
     * @param leftOperand the left operand
     * @param rightOperand the right operand
     */
    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.33)
        // Regle (3.54)
        // Regle (3.55)

        AbstractExpr lhs = getLeftOperand();
        AbstractExpr rhs = getRightOperand();
        Type typeL = lhs.verifyExpr(compiler, localEnv, currentClass);
        Type typeR = rhs.verifyExpr(compiler, localEnv, currentClass);
        if (bothBoolean(typeL, typeR)) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        throw new ContextualError("Boolean operator can only be applied to boolean values", getLocation());
    }

    /**
     * Ajout nécessaire dans le cas ou le booléen n'est pas un flot de contrôle mais une expression
     * On continue à utiliser codeGenBranch pour la génération de code (et préserver les court-circuit)
     */
    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError { // A FACTORISER
        int id = compiler.labels().getLabelID();
        Label lBool = compiler.labels().label("E_Bool.", id);
        Label lEnd = compiler.labels().labelFin(id);

        // Si l'expression booléenne est vraie on va directement au label du booléen vrai
        codeGenBranch(compiler, true, lBool);

        // Faux : On saute directement à la fin et on charge 0
        compiler.registers().load(new ImmediateInteger(0), Register.getR(n));
        compiler.addInstruction(new BRA(lEnd));

        // Vrai : On place le label et on charge 1
        compiler.addLabel(lBool);
        compiler.registers().load(new ImmediateInteger(1), Register.getR(n));

        // Fin du branch
        compiler.addLabel(lEnd);
    }

    /**
     * Ici le booléen n'est pas un flot de contrôle mais une expression IR version
     * 
     */
    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRLocation loc = IRLocation.from(getLocation());
        int id = compiler.labels().getLabelID();

        // Résultat booléen matérialisé en 0/1
        IRScratch dst = ir.scratch(IRType.BOOLEAN);

        IRLabel lTrue = compiler.labels().irLabel("E_Bool", id);
        IRLabel lEnd  = compiler.labels().irLabelFin(id);

        // On initialise dst à 0
        ir.addInstruction(new IRBinaryExpr(loc, "SUB_INT", dst, new IRImmediateInt(0), new IRImmediateInt(0)));

        // On branche si l'expression est vraie
        irGenBranch(compiler, ir, true, lTrue);

        // L'expression est fausse
        ir.addInstruction(new IRJump(loc, lEnd));

        // L'expression est vraie
        ir.addInstruction(new IRLabelDef(loc, lTrue));
        ir.addInstruction(new IRBinaryExpr(loc, "ADD_INT", dst, new IRImmediateInt(0), new IRImmediateInt(1)));

        // Fin de l'expression
        ir.addInstruction(new IRLabelDef(loc, lEnd));
        return dst;
    }
}
