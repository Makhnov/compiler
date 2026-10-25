package fr.ensimag.deca.codegen;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.CompilerOptions;

/**
 * Contexte principal de la génération de code (Manager des managers)
 * 
 * @author Nico
 * @date 30/12/2025
 * 
 */

public class GenerationContext {
    // public final LabelManager labelTable = new LabelManager();
    private DecacCompiler compiler;
    private CompilerOptions options;
    private static final int MAX_REG = 16;

    private OptimizationsManager optis;
    private RegisterManager regs;
    private LabelManager labels;
    private ErrorManager errs;
    private VarManager vars;

    // MEMORY MANAGERS
    private StaticManager methods;
    private StackManager stack;

    public GenerationContext(DecacCompiler compiler, CompilerOptions options) {
        this.compiler = compiler;
        this.options = options;
        
        int rMax = (options != null) ? options.getRMax() : MAX_REG;
        
        this.optis = new OptimizationsManager(compiler);
        this.regs = new RegisterManager(compiler, rMax);
        this.methods = new StaticManager(compiler);
        this.stack = new StackManager(compiler);
        this.errs = new ErrorManager(compiler);
        this.vars = new VarManager(compiler);
        this.labels = new LabelManager();
    }
    
    public OptimizationsManager getOptis() { 
        return optis;
    }

    public RegisterManager getRegisters() { 
        return regs;
    }
    
    public CompilerOptions getOptions() { 
        return options;
    }
    
    public DecacCompiler getCompiler() { 
        return compiler;
    }
    
    public LabelManager getLabels() { 
        return labels;
    }

    public ErrorManager getErrors() { 
        return errs;
    }

    public StackManager getStack() { 
        return stack;
    }

    public StaticManager getMethods() { 
        return methods;
    }

    public VarManager getVars() { 
        return vars;
    }

}
