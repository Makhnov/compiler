package fr.ensimag.deca.backend.ima;

import java.util.IdentityHashMap;
import java.util.Map;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.ir.IRProgram;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRConvFloat;
import fr.ensimag.deca.ir.instructions.IRExit;
import fr.ensimag.deca.ir.instructions.IRLoad;
import fr.ensimag.deca.ir.instructions.IRPrintChar;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.instructions.IRUnaryExpr;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRType;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;

import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.QUO;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;

public final class IMAGenerator {

    private DecacCompiler compiler;
    private IRProgram ir;

    // Slots mémoire (GB) pour variables et scratchs.
    private final Map<Object, DAddr> slots = new IdentityHashMap<>();
    private int nextGbOffset = 1;

    // Registres de travail
    private static final GPRegister OUT = Register.R1; // registre d'affichage
    private static final GPRegister TMP = Register.R0; // registre de travail

    public void generate(DecacCompiler compiler, IRProgram ir) throws IRError {
        this.compiler = compiler;
        this.ir = ir;

        for (IRAbstractInst inst : ir.text()) {

            // --- I/O ---
            if (inst instanceof IRPrintString ps) {
                String value = ir.getString(ps.label());
                compiler.addInstruction(new WSTR(new ImmediateString(value)));
                continue;
            }

            if (inst instanceof IRPrintChar pc) {
                if (pc.c() == '\n') {
                    compiler.addInstruction(new WNL());
                } else {
                    throw new UnsupportedOperationException("IMA(IR): IRPrintChar only supports \\n for now, got '" + pc.c() + "' at " + inst.getLocation());
                }
                continue;
            }

            if (inst instanceof IRPrintValue pv) {
                // value() est un IRDVal (immédiat ou scratch), jamais une IRDAddr (chargée avant côté IR).
                loadVal(OUT, pv.value(), inst);
                if (pv.type() == IRType.INT) {
                    compiler.addInstruction(new WINT());
                } else if (pv.type() == IRType.FLOAT) {
                    compiler.addInstruction(pv.hex() ? new WFLOATX() : new WFLOAT());
                } else {
                    throw new UnsupportedOperationException("IMA(IR): IRPrintValue unsupported type " + pv.type() + " at " + inst.getLocation());
                }
                continue;
            }

            // --- Mémoire IR ---
            if (inst instanceof IRLoad ld) {
                // LOAD_<type> dstScratch, srcAddr  =>  LOAD [srcAddrSlot] -> OUT ; STORE OUT -> [dstScratchSlot]
                compiler.addInstruction(new LOAD(addrOf(ld.src()), OUT));
                compiler.addInstruction(new STORE(OUT, scratchOf(ld.dst())));
                continue;
            }

            if (inst instanceof IRAssign as) {
                // STORE srcVal, dstAddr
                loadVal(OUT, as.src(), inst);
                compiler.addInstruction(new STORE(OUT, addrOf(as.dst())));
                continue;
            }

            // --- Conversions ---
            if (inst instanceof IRConvFloat cf) {
                // CONV_FLOAT dst, src : dstScratch = float(src)
                loadVal(OUT, cf.src(), inst);
                compiler.addInstruction(new FLOAT(OUT, OUT));
                compiler.addInstruction(new STORE(OUT, scratchOf(cf.dst())));
                continue;
            }

            // --- Unaires ---
            if (inst instanceof IRUnaryExpr un) {
                emitUnary(un, inst);
                continue;
            }

            // --- Binaires ---
            if (inst instanceof IRBinaryExpr bin) {
                emitBinary(bin, inst);
                continue;
            }

            // --- Fin ---
            if (inst instanceof IRExit) {
                compiler.addInstruction(new HALT());
                continue;
            }

            throw new UnsupportedOperationException("IMA(IR): unsupported IR instruction " + inst.getClass().getSimpleName() + " at " + inst.getLocation());
        }
    }

    // ===================== Helpers =====================

    private DAddr slotOf(Object key) {
        return slots.computeIfAbsent(key, k -> new RegisterOffset(nextGbOffset++, Register.GB));
    }

    private DAddr addrOf(IRDAddr a) {
        return slotOf(a);
    }

    private DAddr scratchOf(IRScratch s) {
        return slotOf(s);
    }

    private void loadVal(GPRegister reg, IRDVal v, IRAbstractInst inst) throws IRError {
        if (v instanceof IRImmediateInt ii) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(ii.value()), reg));
            return;
        }
        if (v instanceof IRImmediateFloat ff) {
            compiler.addInstruction(new LOAD(new ImmediateFloat(ff.value()), reg));
            return;
        }
        if (v instanceof IRScratch s) {
            compiler.addInstruction(new LOAD(scratchOf(s), reg));
            return;
        }
        throw new IRError("IMA(IR): unsupported IRDVal " + v.getClass().getSimpleName(), inst.getLocation());
    }

    private void emitUnary(IRUnaryExpr un, IRAbstractInst inst) throws IRError {
        String op = un.name();

        if (!op.startsWith("NEG_")) {
            throw new UnsupportedOperationException("IMA(IR): IRUnaryExpr unsupported op '" + op + "' at " + inst.getLocation());
        }

        // src -> OUT
        loadVal(OUT, un.src(), inst);

        // tmp = 0 (int/float), puis tmp = tmp - out
        if (op.endsWith("_INT")) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), TMP));
        } else if (op.endsWith("_FLOAT")) {
            compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), TMP));
        } else {
            throw new UnsupportedOperationException("IMA(IR): NEG_* unknown type for op '" + op + "' at " + inst.getLocation());
        }

        compiler.addInstruction(new SUB(OUT, TMP)); // TMP = TMP - OUT = -src
        compiler.addInstruction(new STORE(TMP, scratchOf(un.dest())));
    }

    private void emitBinary(IRBinaryExpr bin, IRAbstractInst inst) throws IRError {
        String op = bin.name();

        // left -> OUT, right -> TMP
        loadVal(OUT, bin.left(), inst);
        loadVal(TMP, bin.right(), inst);

        // OUT = OUT (op) TMP   (attention: signature IMA = OP src, dst)
        if (op.startsWith("ADD_")) {
            compiler.addInstruction(new ADD(TMP, OUT));
        } else if (op.startsWith("SUB_")) {
            compiler.addInstruction(new SUB(TMP, OUT));
        } else if (op.startsWith("MUL_")) {
            compiler.addInstruction(new MUL(TMP, OUT));
        } else if (op.equals("DIV_INT")) {
            compiler.addInstruction(new QUO(TMP, OUT));
        } else if (op.equals("MOD_INT")) {
            compiler.addInstruction(new REM(TMP, OUT));
        } else if (op.equals("DIV_FLOAT")) {
            compiler.addInstruction(new DIV(TMP, OUT));
        } else {
            throw new UnsupportedOperationException("IMA(IR): IRBinaryExpr unsupported op '" + op + "' at " + inst.getLocation());
        }

        compiler.addInstruction(new STORE(OUT, scratchOf(bin.dst())));
    }
}