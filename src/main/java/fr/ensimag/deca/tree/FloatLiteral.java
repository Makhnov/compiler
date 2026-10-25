package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;

import java.io.PrintStream;
import java.util.OptionalDouble;

import org.apache.commons.lang.Validate;

/**
 * Single precision, floating-point literal
 *
 * @author gl25
 * @date 01/01/2026
 */
public class FloatLiteral extends AbstractExpr {

    private float value;

    public float getValue() {
        return value;
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        return new ImmediateFloat(value);
    }

    @Override
    public IRDVal getIRDVal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        return new IRImmediateFloat(value);
    }

    @Override
    public OptionalDouble getFloatValue() {
        return OptionalDouble.of(value);
    }

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value), "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value), "literal values cannot be NaN");
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.38)
        // Regle (3.45)
        Type t = compiler.environmentType.FLOAT;
        this.setType(t);
        return t;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) throws GenerationError {
        // On charge la valeur entière avant de l'afficher au format attendu
        compiler.addInstruction(new LOAD(new ImmediateFloat(value), Register.R1));
        compiler.addInstruction((printHex ? new WFLOATX() : new WFLOAT()));
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) {
        return new IRImmediateFloat(value);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Float.toString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
