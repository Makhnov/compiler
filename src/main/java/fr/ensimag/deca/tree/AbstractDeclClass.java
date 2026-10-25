package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ContextualError;

/**
 * Class declaration.
 *
 * @author gl25
 * @date 01/01/2026
 */
public abstract class AbstractDeclClass extends Tree {

        AbstractIdentifier name;
        AbstractIdentifier superClass;
        ListDeclField fields;
        ListDeclMethod methods;

        /**
         * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
         * without looking at its content.
         *
         * @param compiler the DECA compiler
         * @throws ContextualError if a contextual error is detected
         */
        protected abstract void verifyClass(DecacCompiler compiler) throws ContextualError;

        /**
         * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
         * methods) are OK, without looking at method body and field initialization.
         *
         * @param compiler the DECA compiler
         * @throws ContextualError if a contextual error is detected
         */
        protected abstract void verifyClassMembers(DecacCompiler compiler) throws ContextualError;

        /**
         * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
         * contained in the class are OK.
         *
         * @param compiler the DECA compiler
         * @throws ContextualError if a contextual error is detected
         */
        protected abstract void verifyClassBody(DecacCompiler compiler) throws ContextualError;

        /**
         * Pass 1 of [CodeGen]. Generate code to declare class methods.
         *
         * @param compiler the DECA compiler
         * @throws GenerationError if code generation fails
         */
        protected abstract void assignTable(DecacCompiler compiler) throws GenerationError;

        /**
         * Pass 2 of [CodeGen]. Generate code to build class method tables.
         *
         * @param compiler the DECA compiler
         * @throws GenerationError if code generation fails
         */
        protected abstract void generateTable(DecacCompiler compiler) throws GenerationError;

        /**
         * Generate initialization code for this class.
         *
         * @param compiler the DECA compiler
         * @throws GenerationError if code generation fails
         */
        protected abstract void codeGenInit(DecacCompiler compiler) throws GenerationError;

        /**
         * Generate code for this class's methods.
         *
         * @param compiler the DECA compiler
         * @throws GenerationError if code generation fails
         */
        protected abstract void codeGenMethod(DecacCompiler compiler) throws GenerationError;
        
        @Override
        protected void iterChildren(TreeFunction f) {
                name.iter(f);
                superClass.iter(f);
                fields.iter(f);
                methods.iter(f);
        }

}
