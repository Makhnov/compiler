package fr.ensimag.deca;

import fr.ensimag.deca.backend.ima.IMAGenerator;
import fr.ensimag.deca.backend.qbe.QBEGenerator;
import fr.ensimag.deca.backend.riscv.RVGenerator;
import fr.ensimag.deca.codegen.ErrorManager;
import fr.ensimag.deca.codegen.GenerationContext;
import fr.ensimag.deca.codegen.LabelManager;
import fr.ensimag.deca.codegen.OptimizationsManager;
import fr.ensimag.deca.codegen.RegisterManager;
import fr.ensimag.deca.codegen.StackManager;
import fr.ensimag.deca.codegen.StaticManager;
import fr.ensimag.deca.codegen.VarManager;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.ima.pseudocode.AbstractLine;
import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.Instruction;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Line;
import fr.ensimag.riscv.pseudocode.RVProgram;
import fr.ensimag.deca.ir.control.IRError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Decac compiler instance.
 *
 * This class is to be instantiated once per source file to be compiled. It
 * contains the meta-data used for compiling (source file name, compilation
 * options) and the necessary utilities for compilation (symbol tables, abstract
 * representation of target file, ...).
 *
 * It contains several objects specialized for different tasks. Delegate methods
 * are used to simplify the code of the caller (e.g. call
 * compiler.addInstruction() instead of compiler.getProgram().addInstruction()).
 *
 * @author gl25
 * @date 01/01/2026
 */
public final class DecacCompiler {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    /**
     * Portable newline character.
     * NOTE : On accepte getProperty vu que les enseignants l'utilisent, mais on note que, l'état est partagé entre tous les threads. 
     * Il faut donc éviter de modifier les otpions systèmes dans le cas d'une compilation parallèle. // A FAIRE ?
     */
    private static final String nl = System.getProperty("line.separator", "\n");

    public DecacCompiler(CompilerOptions compilerOptions, File source) {
        super();
        this.compilerOptions = compilerOptions;
        this.source = source;
        this.generationContext = new GenerationContext(this, compilerOptions);
    }

    /**
     * Source file associated with this compiler instance.
     */
    public File getSource() {
        return source;
    }

    /**
     * Compilation options (e.g. when to stop compilation, number of registers
     * to use, ...).
     */
    public CompilerOptions options() {
        return compilerOptions;
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#add(fr.ensimag.ima.pseudocode.AbstractLine)
     */
    public void add(AbstractLine line) {
        block.add(line);
    }

    /**
     * @see fr.ensimag.ima.pseudocode.IMAProgram#addComment(java.lang.String)
     */
    public void addComment(String comment) {
        block.addComment(comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addLabel(fr.ensimag.ima.pseudocode.Label)
     */
    public void addLabel(Label label) {
        block.addLabel(label);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction)
     */
    public void addInstruction(Instruction instruction) {
        block.addInstruction(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addInstruction(fr.ensimag.ima.pseudocode.Instruction,
     * java.lang.String)
     */
    public void addInstruction(Instruction instruction, String comment) {
        block.addInstruction(instruction, comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Line)
     */
    public void addFirstInstruction(Instruction instruction) {
        block.addFirst(instruction);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Line)
     */
    public void addFirstInstruction(Instruction instruction, String comment) {
        block.addFirst(instruction, comment);
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Line)
     */
    public void addFirstComment(String comment) {
        block.addFirst(new Line(comment));
    }

    /**
     * @see
     * fr.ensimag.ima.pseudocode.IMAProgram#addFirst(fr.ensimag.ima.pseudocode.Line)
     */
    public void addFirstLabel(Label label) {
        block.addFirst(new Line(label));
    }

    /** 
    * @see 
     * fr.ensimag.ima.pseudocode.IMAProgram#display()
     */
    public String displayIMAProgram() {
        return program.display();
    }    

    private final CompilerOptions compilerOptions;
    private final File source;
    /**
     * The deque repreensting the real program (blocks + main program).     * 
     */
    private final ArrayDeque<IMAProgram> programs = new ArrayDeque<>();

    /**
     * The main program. Every instruction generated will eventually end up here.
    */
    private final IMAProgram program = new IMAProgram();
    
    /**
     * The current block. (for method stack management)
     */
    private IMAProgram block = program;

    public IMAProgram pushNewBlock() {
        // On sauvegarde le block courant
        programs.push(block);

        // On crée un nouveau block courant        
        block = new IMAProgram();

        // On retourne le block créé
        return block;
    }

    public void popBlock(IMAProgram b) {
        Validate.notNull(b, "Can't delete a null block");
        Validate.isTrue(b == block, "Can only delete the current block");
        Validate.isTrue(!programs.isEmpty(), "No parent block left");
        
        // On sauvegarde le bloc que l'on vient de remplir
        IMAProgram current = block;

        // On restaure le block parent
        block = programs.pop();

        // On ajoute le block sauvegardé au block en cours (ex parent)
        block.append(current);
    }


    /** The global environment for symbols (phase B) */
    public final SymbolTable symbolTable = new SymbolTable();

    /** The global environment for types (phase B) */
    public final EnvironmentType environmentType = new EnvironmentType(this);

    /** The global environment for generation of code (phase C) */
    private final GenerationContext generationContext;

    public GenerationContext getGenerationContext() {
        return generationContext;
    }

    public OptimizationsManager optis() {
        return generationContext.getOptis();
    }

    public RegisterManager registers() {
        return generationContext.getRegisters();
    }

    public ErrorManager errors() {
        return generationContext.getErrors();
    }

    public LabelManager labels() {
        return generationContext.getLabels();
    }
    
    public StackManager stack() {
        return generationContext.getStack();
    }
    
    public StaticManager methods() {
        return generationContext.getMethods();
    }
    
    public VarManager vars() {
        return generationContext.getVars();
    }
    
    public Symbol createSymbol(String name) {
        return symbolTable.create(name);
    }

    /**
     * Run the compiler (parse source file, generate code)
     *
     * @return true on error
     */
    public boolean compile() {
        String sourceFile = source.getAbsolutePath();
        String extension = ".ass";

        // AJOUT RISCV
        int irMode = options().ir();
        if (irMode == CompilerOptions.RARS) {
            extension = ".s";
        } 
        if (irMode == CompilerOptions.IMA) {
            extension = ".ass";
        }
        if (irMode == CompilerOptions.QBE) {
            extension = ".qbe";
        }        
        String destFile = sourceFile.substring(0, sourceFile.lastIndexOf('.')) + extension;
        // FIN AJOUT RISCV

        PrintStream err = System.err;
        PrintStream out = System.out;
        // LOG.debug("Compiling file " + sourceFile + " to assembly file " + destFile);
        try {
            return doCompile(sourceFile, destFile, out, err);
        } catch (LocationException e) {
            LOG.debug("Location exception", e);
            e.display(err);
            return true;
        } catch (DecacFatalError e) {
            err.println(e.getMessage());
            return true;
        } catch (StackOverflowError e) {
            LOG.debug("stack overflow", e);
            err.println("Stack overflow while compiling file " + sourceFile + ".");
            return true;
        } catch (Exception e) {
            LOG.fatal("Exception raised while compiling file " + sourceFile + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        } catch (AssertionError e) {
            LOG.fatal("Assertion failed while compiling file " + sourceFile + ":", e);
            err.println("Internal compiler error while compiling file " + sourceFile + ", sorry.");
            return true;
        }
    }

    /**
     * Internal function that does the job of compiling (i.e. calling lexer,
     * verification and code generation).
     *
     * @param sourceName name of the source (deca) file
     * @param destName name of the destination (assembly) file
     * @param out stream to use for standard output (output of decac -p)
     * @param err stream to use to display compilation errors
     *
     * @return true on error
     */
    private boolean doCompile(String sourceName, String destName, PrintStream out, PrintStream err) throws DecacFatalError, LocationException {
        AbstractProgram prog = doLexingAndParsing(sourceName, err);
        // LOG.debug("doCompile: start");
        if (prog == null) {
            LOG.error("Parsing failed");
            return true;
        } else {
            LOG.info("Parsing successful");
        }

        assert (prog.checkAllLocations());
        
        if (options().parsing()) {
            out.print(prog.decompile());
            LOG.info("Deca program printed successfully.");
            return false;
        }

        prog.verifyProgram(this);
        assert (prog.checkAllDecorations());

        int irmode = options().ir();

        if (options().verification() && irmode == CompilerOptions.NONE) {
            LOG.info("Deca program verified successfully.");
            // page 103, aucun affichage n'est demandé.
            return false;
        }

        addComment("start main program");
        LOG.debug("doCompile: before codeGenProgram");

        // AJOUT RISCV
        if (irmode != CompilerOptions.NONE) {
            // On transforme notre AST décoré en IR
            IRProgram ir = new IRProgram(options());
            prog.irGenProgram(this, ir);
            if (options().verification()) {
                LOG.info("IR program generated successfully.");
                ir.display(out);
                return false;
            }

            // On branche notre IR vers le backend choisi
            try (PrintStream ps = new PrintStream(new FileOutputStream(destName))) {
                // OPTION IR 1
                if (irmode == CompilerOptions.RARS) {
                    RVProgram rv = new RVProgram();
                    RVGenerator gen = new RVGenerator();                    
                    gen.generate(ir, rv, options().nocheck());               
                    rv.assembly(ps);
                    LOG.debug("Generated RISC-V assembly code:" + nl + rv.display());
                } 

                // OPTION IR 2
                else if (irmode == CompilerOptions.IMA) {
                    IMAGenerator gen = new IMAGenerator();
                    gen.generate(this, ir);
                    program.display(ps);
                    LOG.debug("Generated IMA assembly code:" + nl + displayIMAProgram());
                } 
                // OPTION IR 3
                else if (irmode == CompilerOptions.QBE) {
                    QBEGenerator gen = new QBEGenerator();
                    gen.generate(ir, ps);
                    LOG.debug("Generated QBE code:" + nl + gen.display());
                } 
                else {
                    throw new UnsupportedOperationException("Unknown -ir mode: " + irmode);
                }
            } catch (FileNotFoundException e) {
                throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
            } catch (IRError e) { // <--- AJOUT DU BLOC CATCH ICI
                throw new DecacFatalError("Error during IR generation: " + e.getMessage());
            }

            LOG.info("Compilation (IR) of " + sourceName + " successful.");
            LOG.info("Output file is: " + destName);
            return false;
        }
        // FIN AJOUT RISCV
        
        // Aucun retour ici, seulement des exceptions en cas de problème.
        prog.codeGenProgram(this);

        addComment("end main program");        
        LOG.debug("Generated assembly code:" + nl + program.display());
        
        FileOutputStream fstream = null;
        try {
            fstream = new FileOutputStream(destName);
        } catch (FileNotFoundException e) {
            throw new DecacFatalError("Failed to open output file: " + e.getLocalizedMessage());
        }
        
        // LOG.info("Writing assembler file ...");
        
        program.display(new PrintStream(fstream));
        LOG.info("Compilation of " + sourceName + " successful.");
        LOG.info("Output file assembly file is: " + destName);
        return false;
    }

    /**
     * Build and call the lexer and parser to build the primitive abstract
     * syntax tree.
     *
     * @param sourceName Name of the file to parse
     * @param err Stream to send error messages to
     * @return the abstract syntax tree
     * @throws DecacFatalError When an error prevented opening the source file
     * @throws fr.ensimag.deca.tools.DecacInternalError When an inconsistency was detected in the
     * compiler.
     * throws fr.ensimag.deca.tree.LocationException When a compilation error (incorrect program)
     * occurs.
     */
    protected AbstractProgram doLexingAndParsing(String sourceName, PrintStream err) throws DecacFatalError, DecacInternalError {
        DecaLexer lex;
        try {
            lex = new DecaLexer(CharStreams.fromFileName(sourceName));
        } catch (IOException ex) {
            throw new DecacFatalError("Failed to open input file: " + ex.getLocalizedMessage());
        }
        lex.setDecacCompiler(this);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        parser.setDecacCompiler(this);
        return parser.parseProgramAndManageErrors(err);        
    }
}
