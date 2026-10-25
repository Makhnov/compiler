package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRLoad;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRVariable;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * Deca Identifier
*
* @author gl25
* @date 01/01/2026
*/
public class Identifier extends AbstractIdentifier {
    private Definition definition;
    
    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError("Identifier " + getName() + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError("Identifier " + getName() + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }
    
    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a field definition.
    */
   @Override
   public FieldDefinition getFieldDefinition() {
       try {
           return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError("Identifier " + getName() + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }
    
    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a field definition.
    */
   @Override
   public VariableDefinition getVariableDefinition() {
       try {
           return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError("Identifier " + getName() + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }
    
    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws fr.ensimag.deca.tools.DecacInternalError
     *             if the definition is not a field definition.
    */
   @Override
   public ExpDefinition getExpDefinition() {
       try {
           return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError("Identifier " + getName() + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }
    
    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public DAddr getDAddr(DecacCompiler compiler) throws GenerationError {
        DAddr addr = getExpDefinition().getDAddr(compiler);
        if (addr == null) {
            throw new UnsupportedOperationException("Identifier " + getName() + " has no attached DAddr");
        }
        return addr;
    }

    @Override
    public DVal getDVal(DecacCompiler compiler) throws GenerationError {
        // Un champ ne peut pas être lu directement, il dépend de "this"
        if (getDefinition() != null && getDefinition().isField()) {
            return null;
        }
        
        // Si la variable est dans un registre, on renvoie ce registre, sinon on renvoie son adresse
        GPRegister GPReg = getExpDefinition().getRegister(compiler);
        return GPReg != null ? GPReg : getDAddr(compiler);
    }

    @Override
    public IRDVal getIRDVal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        return null;
    }
    
    @Override
    protected GPRegister getRegister(DecacCompiler compiler) throws GenerationError {
        return getExpDefinition().getRegister(compiler);
    }

    @Override
    protected void modified(DecacCompiler compiler) throws GenerationError {
        getExpDefinition().modified(compiler);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (0.1)
        // // Regle (3.64)
        // // Regle (3.67)
        // // Regle (3.68)
        // // Regle (3.69)

        ExpDefinition expDef = localEnv.get(getName());
        if (expDef == null) {
            throw new ContextualError("Identifier '" + getName() + "' not declared", getLocation());
        }

        if (!expDef.isExpression()) {
            throw new ContextualError("Identifier '" + getName() + "' cannot be used as Expr", getLocation());
        }

        Type edt = expDef.getType();
        setDefinition(expDef);
        setType(edt);

        return edt;
    }

    @Override
    public Type verifyMethodType(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Regle (0.1)
        // // Regle (3.64)
        // // Regle (3.67)
        // // Regle (3.68)
        // // Regle (3.69)

        ExpDefinition expDef = localEnv.get(getName());
        if (expDef == null) {
            throw new ContextualError("Identifier '" + getName() + "' not declared", getLocation());
        }

        if (!expDef.isMethod()) {
            throw new ContextualError("Identifier '" + getName() + "' cannot be used as Method", getLocation());
        }

        Type edt = expDef.getType();
        setDefinition(expDef);
        setType(edt);

        return edt;
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        // Regle (0.2)

        TypeDefinition typeDef = compiler.environmentType.defOfType(getName());
        if (typeDef == null) {
            throw new ContextualError("Unknown type '" + getName() + "'", getLocation());
        }

        setDefinition(typeDef);
        Type tdt = typeDef.getType();
        setType(tdt);
        return tdt;
    }

@Override
    protected void codeGenExprInternal(DecacCompiler compiler, int n) throws GenerationError {
        // Champ implicite (on doit utiliser this)
        if (getDefinition() != null && getDefinition().isField()) {
            int idx = getFieldDefinition().getIndex();
            
            // On charge l'objet courant depuis la pile
            compiler.registers().load(new RegisterOffset(-2, Register.LB), Register.R1);
            
            // On récupère la valeur du champ à l'offset indiqué
            compiler.registers().load(new RegisterOffset(idx, Register.R1), Register.getR(n));
            return;
        }

        // Cas standard, On charge simplement la variable locale ou le paramètre
        DVal v = getDVal(compiler);
        if (v == null) {           
            throw new GenerationError("Identifier " + getName() + " cannot be evaluated (no DVal)", getLocation());
        }
        compiler.registers().load(v, Register.getR(n));
    }

    @Override
    protected void codeGenStore(DecacCompiler compiler, GPRegister GPReg) throws GenerationError {
        if (getDefinition() != null && getDefinition().isField()) {
            int idx = getFieldDefinition().getIndex();
            compiler.registers().load(new RegisterOffset(-2, Register.LB), Register.R1); // this
            compiler.addInstruction(new STORE(GPReg, new RegisterOffset(idx, Register.R1)));
            return;
        }
    
        ExpDefinition def = getExpDefinition();
        GPRegister reserved = def.getRegister(compiler);

        if (reserved != null) {
            if (reserved.equals(GPReg)) {
                // Rien à faire, la valeur est déjà dans le bon registre on se contente de marquer le fait qu'elle a été modifiée
                def.modified(compiler);
                return;
            }
            // On charge la valeur dans le registre prévu à cet effet
            compiler.registers().load(GPReg, reserved);

            // On indique que la variable a été modifiée récemment (cf. VarManager)
            def.modified(compiler);
        } else {
            // On renvoie vers AbstractLvalue
            super.codeGenStore(compiler, GPReg);
        }
    }

    @Override
    protected void codeGenBranch(DecacCompiler compiler, boolean b, Label label) throws GenerationError {
        if (getDefinition() != null && getDefinition().isField()) {
            int idx = getFieldDefinition().getIndex();
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1)); // this
            compiler.addInstruction(new LOAD(new RegisterOffset(idx, Register.R1), Register.R1)); // value
        } else {
            DVal val = getDVal(compiler);
            compiler.addInstruction(new LOAD(val, Register.R1));
        }
        
        if (!compiler.optis().medium()) {
            compiler.addInstruction(new CMP(new ImmediateInteger(0), Register.R1));
        }

        compiler.addInstruction(b ? new BNE(label) : new BEQ(label));
    }

    @Override
    protected IRDVal irGenExprInternal(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        IRDAddr src = irGenAddr(compiler, ir);
        IRScratch dest = ir.scratch(getIRType());
        ir.addInstruction(new IRLoad(IRLocation.from(getLocation()), getIRType(), dest, src));
        return dest;
    }
    
    @Override
    public IRDAddr irGenAddr(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        return ir.addStack(getName().getName(), getIRType());
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.getName());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }
}
