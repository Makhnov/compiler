package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WINT;

import java.io.PrintStream;
import java.util.OptionalInt;

/**
 * Integer literal
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IntLiteral extends AbstractExpr {
    private int value;

    public int getValue() {
        return value;
    }

    @Override
    public OptionalInt getIntegerValue() {
        return OptionalInt.of(value);
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        return new ImmediateInteger(value);
    }

    @Override
    public IRDVal getIRDVal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        return new IRImmediateInt(value);
    }
    
    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.38)
        // Regle (3.44)
        Type t = compiler.environmentType.INT;
        this.setType(t);
        return t;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) throws GenerationError {
        // On charge la valeur entière avant de l'afficher (booléen inutile ici)
        compiler.addInstruction(new LOAD(new ImmediateInteger(value), Register.R1));
        compiler.addInstruction(new WINT());
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) {
        return new IRImmediateInt(value);
    }
    
    @Override
    String prettyPrintNode() {
        return "Int (" + getValue() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Integer.toString(value));
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
