package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IROpCmp;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRType;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean implicit() {
        return false;
    }

    /**
     * @return true if the expression is predictable, i.e., has no side effects (readint, assignments, etc.)
     * Used for optimizations (commutative operations, etc.)
     * @see fr.ensimag.deca.codegen.OptimizationsManager#commute
     * 
     */
    public boolean predictable() {
        return true;
    }

    /**
     * Get the address where the value of this expression is stored.
     * @param compiler the DECA compiler
     * @return the memory address of this expression
     * @throws GenerationError if the address cannot be computed
     */
    public DAddr getDAddr(DecacCompiler compiler) throws GenerationError {
        throw new UnsupportedOperationException("getAddress not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Get the DVal of the expression (immediate or register value).
     * @param compiler the DECA compiler
     * @return the DVal representation, or null if not directly available
     * @throws GenerationError if value retrieval fails
     */
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        // throw new UnsupportedOperationException("getValue not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));                
        return null;
    }

    /**
     * Get the IR DVal representation of this expression.
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @return the IR value, or null if not directly available
     * @throws GenerationError if code generation fails
     */
    public IRDVal getIRDVal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        return null;
    }

    /**
     * Get the compile-time integer value of this expression, if known.
     * @return an optional containing the integer value, or empty if not a constant
     */
    public OptionalInt getIntegerValue() {
        return OptionalInt.empty();
    }

    /**
     * Get the compile-time float value of this expression, if known.
     * @return an optional containing the float value, or empty if not a constant
     */
    public OptionalDouble getFloatValue() {
        return OptionalDouble.empty();
    }

    /**
     * Get the compile-time integer value with constant folding.
     * @param compiler the DECA compiler
     * @return an optional containing the integer value, or empty if not computable
     */
    public OptionalInt getIntegerValue(DecacCompiler compiler) {
        return getIntegerValue();
    }

    /**
     * Get the compile-time float value with constant folding.
     * @param compiler the DECA compiler
     * @return an optional containing the float value, or empty if not computable
     */
    public OptionalDouble getFloatValue(DecacCompiler compiler) {
        return getFloatValue();
    }

    /**
     * Get the register allocated for this expression, if any.
     * @param compiler the DECA compiler
     * @return the allocated register, or null
     * @throws GenerationError if an error occurs
     */
    protected GPRegister getRegister(DecacCompiler compiler) throws GenerationError {
        return null;
    }

    /**
     * Notify that this expression has been modified (for optimization purposes).
     * @param compiler the DECA compiler
     * @throws GenerationError if an error occurs
     */
    protected void modified(DecacCompiler compiler) throws GenerationError {
        // nothing by default
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     * @return the type of this expression
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the IR type of this expression.
     * @return the IR type
     */
    public IRType getIRType() {
        return irType;
    }

    /**
     * Set the type decoration for this expression.
     * @param type the type to set
     */
    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;

        if (type.isInt()) {
            this.irType = IRType.INT;
        } else if (type.isFloat()) {
            this.irType = IRType.FLOAT;
        } else if (type.isBoolean()) {
            this.irType = IRType.BOOLEAN;
        } else if (type.isString()) {
            this.irType = IRType.STRING;
        } else {
            this.irType = null;
        }
    }

    private Type type;
    private IRType irType;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     * @throws ContextualError if a contextual error is detected
     */
    public abstract Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     * @throws ContextualError if a contextual error is detected
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type expectedType) throws ContextualError {
        // Regle (3.28)

        Type rType = this.verifyExpr(compiler, localEnv, currentClass);

        // Convertion du right int en float
        if (rType.isInt() && expectedType.isFloat()) {
            ConvFloat converted = ConvFloat.implicitConvFloat(compiler, this);
            return converted;
        }

        // Si Cast implicite ? TODO

        // Si compatible
        if (expectedType.assignCompatible(rType)) {
            return this;
        }

        throw new ContextualError("Type " + rType + " incompatible with expected type " + expectedType + " for " + this.getClass().getName(), getLocation());
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass, Type returnType) throws ContextualError {
        // Regle (3.20)
        // Dans le cas ou notre instruction est une expression, on verifie le type de l'expression
        verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (3.29)
        Type t = verifyExpr(compiler, localEnv, currentClass);
        if (!t.isBoolean()) {
            throw new ContextualError("Condition must be a boolean. Got: " + t, getLocation());
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError {
        // throw new UnsupportedOperationException("codeGenInst not yet implemented for " + this.getClass().getName() + " @ " + String.valueOf(getLocation()));
        int n = compiler.registers().getScatchMin();
        codeGenExpr(compiler, n);
    }

    @Override
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        irGenExpr(compiler, ir);
        // ir.addInstruction(new IRDrop());
    }

    @Override
    protected final void codeGenExpr(DecacCompiler compiler, int n) throws GenerationError {
        // On vérifie qu'on a assez de registres
        compiler.registers().check(n);

        // On vérifie si notre expression est une valeur directe
        DVal c = getDVal(compiler);
        if (c != null) {
            compiler.registers().load(c, Register.getR(n));
            return;
        }

        // Dans le cas contraire, chaque sous-classe doit implémenter sa propre génération de code
        codeGenExprInternal(compiler, n);
    }

    protected IRDVal irGenExpr(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDVal v = getIRDVal(compiler, ir);
        if (v != null) {
            return v;
        }

        return irGenExprInternal(compiler, ir);
    }

    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        throw new UnsupportedOperationException("irGenExprInternal not yet implemented for " + this.getClass().getSimpleName() + " @ " + String.valueOf(getLocation()));
    }

    /**
     * Generate code to print the expression.
     *
     * @param compiler the DECA compiler
     * @param printHex true to print floats in hexadecimal format
     * @throws GenerationError if code generation fails
     */
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) throws GenerationError {
        int n = compiler.registers().getScatchMin();
        codeGenExpr(compiler, n);

        // IMA
        compiler.registers().load(Register.getR(n), Register.R1);

        // On affiche le résultat en fonction du type
        if (getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else if (getType().isFloat()) {
            compiler.addInstruction(printHex ? new WFLOATX() : new WFLOAT());
        } else {
            throw new UnsupportedOperationException("codeGenPrint not yet implemented for " + this.getClass().getName() + " (" + getType() + ") @ " + String.valueOf(getLocation()));
        }
    }

    /**
     * Generate IR to print the expression.
     *
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @param printHex true to print floats in hexadecimal format
     * @throws GenerationError if code generation fails
     */
    protected void irGenPrint(DecacCompiler compiler, IRProgram ir, boolean printHex) throws GenerationError {
        if (getIRType() == IRType.BOOLEAN) {
            throw new UnsupportedOperationException("BOOLEAN is not printable in Deca IR");
        }
        if (getIRType() == IRType.STRING) {
            throw new UnsupportedOperationException("STRING print must use PRINT_STRING (label), not PRINT_VALUE");
        }

        IRDVal v = irGenExpr(compiler, ir);
        boolean hex = (printHex && (getIRType() == IRType.FLOAT));
        ir.addInstruction(new IRPrintValue(IRLocation.from(getLocation()), getIRType(), v, hex));
    }

    /**
     * Generate code to branch based on the expression value.
     *
     * @param compiler the DECA compiler
     * @param b branch condition (true=branch if non-zero, false=branch if zero)
     * @param label target label for the branch
     * @throws GenerationError if code generation fails
     */
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        // Fallback générique
        int n = compiler.registers().getScatchMin();
        GPRegister GPReg = Register.getR(n);

        // Évalue l'expression dans rn
        codeGenExpr(compiler, n);

        // Branchement basé sur rn
        if (!compiler.optis().medium()) {
            compiler.addInstruction(new CMP(new ImmediateInteger(0), GPReg));
        }
        compiler.addInstruction(b ? new BNE(label) : new BEQ(label));
    }

    /**
     * Generate IR to branch based on the expression value.
     *
     * @param compiler the DECA compiler
     * @param ir the IR program being built
     * @param b branch condition (true=branch if non-zero, false=branch if zero)
     * @param label target IR label for the branch
     * @throws GenerationError if code generation fails
     */
    @Override
    protected void irGenBranch(DecacCompiler compiler, IRProgram ir, boolean b, IRLabel label) throws GenerationError {
        if (getIRType() != IRType.BOOLEAN) {
            throw new UnsupportedOperationException("irGenBranch expects BOOLEAN, got " + getIRType() + " for " + getClass().getSimpleName());
        }
        IRDVal v = irGenExpr(compiler, ir);
        IRDVal zero = new IRImmediateInt(0);

        String name = b ? "BNE_INT" : "BEQ_INT";
        ir.addInstruction(new IROpCmp(IRLocation.from(getLocation()), name, v, zero, label));
    }

    /**
     * Check if expression can be computed in-place to avoid unnecessary Load/Store.
     * For example, in "a = a + 1", we can compute "a + 1" directly in the target register.
     *
     * @param compiler the DECA compiler
     * @param target the target l-value for the assignment
     * @param n the register number to use
     * @return true if in-place computation was performed
     * @throws GenerationError if code generation fails
     */
    protected boolean codeGenInPlace(DecacCompiler compiler, AbstractLValue target, int n) throws GenerationError {
        if (!compiler.optis().basic()) {
            return false;
        }
        return codeGenInPlace(compiler, target);
    }

    /**
     * Check if expression can be computed in-place (simplified version).
     *
     * @param compiler the DECA compiler
     * @param target the target l-value for the assignment
     * @return true if in-place computation was performed
     * @throws GenerationError if code generation fails
     */
    protected boolean codeGenInPlace(DecacCompiler compiler, AbstractLValue target) throws GenerationError {
        return false;
    }

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.println(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
