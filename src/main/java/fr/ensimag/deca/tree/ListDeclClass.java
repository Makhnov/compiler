package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

import org.apache.log4j.Logger;

/**
 * AST node representing a list of class declarations.
 *
 * @author gl25
 * @date 01/01/2026
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        // Regle (1.2)
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : this.getList()) {
            c.verifyClass(compiler);
        }
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        // Regle (2.2)
        LOG.debug("verify listClassMembers: start");
        for (AbstractDeclClass c : this.getList()) {
            c.verifyClassMembers(compiler);
        }
        LOG.debug("verify listClassMembers: end");
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        // Regle (3.2)
        LOG.debug("verify listClassBody: start");
        for (AbstractDeclClass c : this.getList()) {
            c.verifyClassBody(compiler);
        }
        LOG.debug("verify listClassBody: end");
    }

    /**
     * Pass 1 of code generation for object version
     */
    public void assignTable(DecacCompiler compiler) throws GenerationError {
        for (AbstractDeclClass c : this.getList()) {
            c.assignTable(compiler);
        }
    }

    /**
     * Pass 2 of code generation for object version
     */
    public void generateTable(DecacCompiler compiler)  throws GenerationError{
        for (AbstractDeclClass c : this.getList()) {
            c.generateTable(compiler);
        }
    }

    public void codeGenClasses(DecacCompiler compiler) throws GenerationError {
        for (AbstractDeclClass c : this.getList()) {
            c.codeGenInit(compiler);
        }
    }

    public void codeGenMethods(DecacCompiler compiler) throws GenerationError {
        for (AbstractDeclClass c : this.getList()) {
            c.codeGenMethod(compiler);
        }
    }
}
