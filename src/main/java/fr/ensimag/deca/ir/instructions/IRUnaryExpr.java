package fr.ensimag.deca.ir.instructions;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.AbstractIRVisitor;
import fr.ensimag.deca.ir.display.IRPrinter;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRScratch;

/**
 * IR instruction representing a unary expression.
 * Computes an operation on a single operand and stores the result in a scratch register.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class IRUnaryExpr extends IRAbstractInst {
    private final String name;
    private final IRScratch dst;
    private final IRDVal src;

    public IRUnaryExpr(IRLocation loc, String name, IRScratch dst, IRDVal src) {
        super(loc);
        Validate.notEmpty(name);
        Validate.notNull(dst);
        Validate.notNull(src);
        this.name = name;
        this.dst = dst;
        this.src = src;
    }
    
    public String name() { 
        return name; 
    }

    public IRScratch dest() { 
        return dst; 
    }

    public IRDVal src() { 
        return src; 
    }
    
    @Override
    public void accept(AbstractIRVisitor visitor) throws IRError {
        visitor.visit(this);
    }

    @Override
    public void prettyPrint(IRPrinter p) {
        p.inst(name, dst.toString(), src.toString());
    }
}

