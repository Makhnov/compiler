package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.GenerationError;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.RTS;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

/**
 * Declaration of a class (<code>class name extends superClass {members}</code>).
 * 
 * @author gl25
 * @date 01/01/2026
 */
public class DeclClass extends AbstractDeclClass {

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superClass, ListDeclField fields, ListDeclMethod methods) {
        Validate.notNull(name);
        Validate.notNull(superClass);
        Validate.notNull(fields);
        Validate.notNull(methods);
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        s.print(" extends ");
        superClass.decompile(s);
        s.println(" {");

        s.indent();
        fields.decompile(s);
        methods.decompile(s);
        s.unindent();

        s.println("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        // Regle (1.3)

        // Verification de l'existence et du type de la superClasse
        Definition superDef = compiler.environmentType.defOfType(superClass.getName());
        if (superDef == null) {
            throw new ContextualError("Unrecognised superClass " + superClass.getName().getName() + " during declaration", getLocation());
        }
        if (!superDef.isClass()) {
            throw new ContextualError("Class " + superClass.getName().getName() + " is not of type 'class'", getLocation());
        }

        // Ajout nico, getters de class, field, etc.
        ClassDefinition superClassDef = superDef.getClassDef();
        ClassType classType = new ClassType(name.getName(), getLocation(), superClassDef);

        ClassDefinition classDef = classType.getDefinition();

        // Insertion dans l'environnement des types
        try {
            compiler.environmentType.declareClass(name.getName(), classDef);
        } catch (DoubleDefException e) {
            throw new ContextualError("Class " + name.getName().getName() + " already declared", getLocation());
        }

        // Mise a jour des identificateurs
        name.setType(classType);
        name.setDefinition(classDef);

        superClass.setType(superDef.getType());
        superClass.setDefinition(superDef);
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        // Regle (2.3)
        ClassDefinition classDef = name.getClassDefinition();

        ClassDefinition superDef = classDef.getSuperClass();
        classDef.setNumberOfFields(superDef.getNumberOfFields());
        classDef.setNumberOfMethods(superDef.getNumberOfMethods());

        fields.verifyListDeclField(compiler, classDef);
        methods.verifyListDeclMethod(compiler, classDef);
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        // Regle (3.5)
        ClassDefinition currentClass = name.getClassDefinition();
        EnvironmentExp classEnv = currentClass.getMembers();
        fields.verifyListDeclFieldInit(compiler, classEnv, currentClass);
        methods.verifyListDeclMethodBody(compiler, currentClass, classEnv);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        superClass.prettyPrint(s, prefix, false);
        fields.prettyPrint(s, prefix, false);
        methods.prettyPrint(s, prefix, true);
    }

    @Override
    protected void assignTable(DecacCompiler compiler) throws GenerationError {
        ClassDefinition cDef = name.getClassDefinition();
        ClassDefinition superDef = cDef.getSuperClass();

        if (superDef != null) {
            compiler.methods().assignClass(superDef);
        }
        compiler.methods().assignClass(cDef);

        methods.assignMethodLabels(compiler, cDef);
    }

    @Override
    protected void generateTable(DecacCompiler compiler) throws GenerationError {
        ClassDefinition cDef = name.getClassDefinition();
        ClassDefinition superDef = cDef.getSuperClass();

        compiler.methods().beginClassTable(cDef, superDef);

        methods.generateMethodEntries(compiler, cDef);
    }

    @Override
    protected void codeGenInit(DecacCompiler compiler) throws GenerationError {
        ClassDefinition cDef = name.getClassDefinition();
        String cName = cDef.getType().getName().getName();

        IMAProgram block = compiler.pushNewBlock();
        compiler.stack().reset();

        // init super
        ClassDefinition superDef = cDef.getSuperClass();
        if (superDef != null) {
            String sName = superDef.getType().getName().getName();

            // Charger this
            compiler.registers().load(new RegisterOffset(-2, Register.LB), Register.R0);

            // Empiler this pour l'appel init.super
            compiler.stack().push(Register.R0);

            // Comptage BSR (cf. stack manager)
            compiler.stack().bsr();

            compiler.addInstruction(new BSR(compiler.labels().labelInit(sName)));

            // On dépile l’argument
            compiler.stack().subsp(1);
        }

        // Nouveaux champs à initialiser

        // Initialisations par défaut
        fields.codeGenFieldsInit(compiler);

        // Initialisations à la requête de l'utilisateur
        fields.codeGenFields(compiler);
        compiler.addInstruction(new RTS());

        int blockSize = compiler.stack().size();
        compiler.errors().stack(blockSize);
        compiler.addFirstLabel(compiler.labels().labelInit(cName));

        compiler.popBlock(block);
    }

    @Override
    protected void codeGenMethod(DecacCompiler compiler) throws GenerationError {
        ClassDefinition cDef = name.getClassDefinition();
        methods.codeGenMethods(compiler, cDef);
    }

}
