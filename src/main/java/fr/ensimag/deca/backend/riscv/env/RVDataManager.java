package fr.ensimag.deca.backend.riscv.env;

import fr.ensimag.deca.backend.riscv.RVGenerator;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.operands.*;

/**
 * Manager responsable des mouvements de données (Load/Store).
 * Fait le pont entre les opérandes abstraits (IR) et les registres physiques (RISC-V).
 */
public class RVDataManager {

    private final RVGenerator gen;

    public RVDataManager(RVGenerator generator) {
        this.gen = generator;
    }

    private RVFormatter format() { 
        return gen.format(); 
    }
    
    private String offset(IRVariable v) { 
        return gen.stack().offset(v) + "(sp)"; 
    }

    private String offset(IRScratch s)  { 
        return gen.stack().offset(s) + "(sp)"; 
    }           

    public void loadInt(IRDVal v, String physReg) throws IRError {        
        v.accept(new DValVisitor() {
            @Override
            public void visit(IRImmediateInt imm) {
                format().instruction("li", physReg, String.valueOf(imm.value()));
            }

            @Override
            public void visit(IRScratch scratch) {
                format().instruction("lw", physReg, offset(scratch));
            }
        });
    }

    public void loadFloat(IRDVal v, String physFReg) throws IRError {
        // On créé un visiteur annonyme
        DValVisitor visitor = new DValVisitor() {
            @Override
            public void visit(IRImmediateFloat imm) {
                int bits = Float.floatToIntBits(imm.value());
                format().instruction("li", "t0", "0x" + String.format("%08x", bits));
                format().instruction("fmv.w.x", physFReg, "t0");
            }

            @Override
            public void visit(IRScratch scratch) {
                format().instruction("flw", physFReg, offset(scratch));
            }
        };

        // On visite l'opérande
        v.accept(visitor);
    }

    public void store(IRDVal src, IRDAddr dst) throws IRError {
        // Même principe ici
        DAddrVisitor dstVisitor = new DAddrVisitor() {
            @Override
            public void visit(IRVariable var) throws IRError {
                if (var.segment() != IRSegment.STACK) {
                    throw new IRError("STORE only supports STACK variables", null);
                }

                src.accept(new DValVisitor() {
                    @Override
                    public void visit(IRImmediateInt imm) {
                        format().instruction("li", "t0", String.valueOf(imm.value()));
                        format().instruction("sw", "t0", offset(var));
                    }

                    @Override
                    public void visit(IRImmediateFloat imm) {
                        int bits = Float.floatToIntBits(imm.value());
                        format().instruction("li", "t0", "0x" + String.format("%08x", bits));
                        format().instruction("sw", "t0", offset(var));
                    }

                    @Override
                    public void visit(IRScratch scratch) throws IRError {
                        format().instruction("lw", "t0", offset(scratch));
                        format().instruction("sw", "t0", offset(var));
                    }
                });
            }
        };
        // On laisse le comportement par défaut lever une erreur si la destination n'est pas une variable cohérente
        dst.accept(dstVisitor);
    }

    public void loadToScratch(IRDAddr src, IRScratch dst, IRType type) throws IRError {
        src.accept(new DAddrVisitor() {
            @Override
            public void visit(IRVariable var) throws IRError {
                switch (type) {
                    case INT, BOOLEAN -> {
                        format().instruction("lw", "t0", offset(var));
                        format().instruction("sw", "t0", offset(dst));
                    }
                    case FLOAT -> {
                        format().instruction("flw", "ft0", offset(var));
                        format().instruction("fsw", "ft0", offset(dst));
                    }
                    default -> throw new IRError("LOAD unsupported type: " + type, null);
                }
            }
        });
    }
}