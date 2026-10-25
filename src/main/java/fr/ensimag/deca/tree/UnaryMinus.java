package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRUnaryExpr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.OPP;

/**
 * AST node representing the unary minus operator (-).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.37)
        // Regle (3.62)
        // verifie si le type est valide : int ou float
        Type type = isNumeric(compiler, localEnv, currentClass, "-");
        this.setType(type);
        return type;
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        getOperand().codeGenExpr(compiler, n);
        compiler.addInstruction(new OPP(Register.getR(n), Register.getR(n)));
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDVal src = getOperand().irGenExpr(compiler, ir);

        IRType type = getIRType();
        if (type != IRType.INT && type != IRType.FLOAT) {
            throw new UnsupportedOperationException("UNARY_MINUS unsupported IRType: " + type);
        }

        IRScratch dst = ir.scratch(type);
        String name = (type == IRType.INT) ? "NEG_INT" : "NEG_FLOAT";
        ir.addInstruction(new IRUnaryExpr(IRLocation.from(getLocation()), name, dst, src));
        return dst;
    }    
}
