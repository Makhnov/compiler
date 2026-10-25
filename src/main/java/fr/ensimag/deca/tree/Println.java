package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.ir.IRLocation;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.instructions.IRPrintChar;
import fr.ensimag.ima.pseudocode.instructions.WNL;

/**
 * AST node representing the println statement (with newline).
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) throws GenerationError{
        super.codeGenInst(compiler);
        compiler.addInstruction(new WNL());
    }

    @Override
    protected void irGenInst(DecacCompiler compiler, IRProgram ir) throws GenerationError {
        super.irGenInst(compiler, ir);
        ir.addInstruction(new IRPrintChar(IRLocation.from(getLocation()), '\n'));
    }


    @Override
    String getSuffix() {
        return "ln";
    }
}
