package fr.ensimag.deca.tree;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRConvFloat;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl25
 * @date 01/01/2026
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        if (!compiler.optis().medium()) {
            return null;
        }
        OptionalInt oi = getOperand().getIntegerValue(compiler);
        if (oi.isPresent()) {
            return new ImmediateFloat((float) oi.getAsInt());
        }

        return null;
    }

    @Override
    public IRDVal getIRDVal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        if (!compiler.optis().medium()) {
            return null;
        }
        OptionalInt oi = getOperand().getIntegerValue(compiler);
        if (oi.isPresent()) {
            return new fr.ensimag.deca.ir.operands.IRImmediateFloat((float) oi.getAsInt());
        }
        return null;
    }

    @Override
    public OptionalDouble getFloatValue(DecacCompiler compiler) {
        if (!compiler.optis().medium()) {
            return OptionalDouble.empty();
        }
        OptionalInt oi = getOperand().getIntegerValue(compiler);
        if (oi.isPresent()) {
            return OptionalDouble.of((double) (float) oi.getAsInt());
        }

        return OptionalDouble.empty();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type operandType = getOperand().verifyExpr(compiler, localEnv, currentClass);

        // Au cas où
        if (!operandType.isInt()) {
            throw new ContextualError("Cannot convert " + operandType, getLocation());
        }
        setType(compiler.environmentType.FLOAT);
        return getType();
    }

    @Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        getOperand().codeGenExpr(compiler, n);
        compiler.addInstruction(new FLOAT(Register.getR(n), Register.getR(n)), "Implicit conversion int -> float");
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDVal src = getOperand().irGenExpr(compiler, ir);
        IRScratch dst = ir.scratch(IRType.FLOAT);
        ir.addInstruction(new IRConvFloat(IRLocation.from(getLocation()), dst, src));
        return dst;
    }

    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    public static ConvFloat implicitConvFloat(DecacCompiler compiler, AbstractExpr operand) throws ContextualError {
        ConvFloat converted = new ConvFloat(operand);
        converted.setLocation(operand.getLocation());
        converted.setType(compiler.environmentType.FLOAT);
        return converted;
    }

    // @Override
    // public void decompile(IndentPrintStream s) {
    //     // s.print("(float) (");
    //     getOperand().decompile(s);
    //     // s.print(")");
    // }
}
