package fr.ensimag.deca.ir;

import java.util.HashMap;
import java.util.Map;

import fr.ensimag.deca.ir.instructions.IRAssign;
import fr.ensimag.deca.ir.instructions.IRBinaryExpr;
import fr.ensimag.deca.ir.instructions.IRConvFloat;
import fr.ensimag.deca.ir.instructions.IRExit;
import fr.ensimag.deca.ir.instructions.IRLoad;
import fr.ensimag.deca.ir.instructions.IROpCmp;
import fr.ensimag.deca.ir.instructions.IRPrintChar;
import fr.ensimag.deca.ir.instructions.IRPrintString;
import fr.ensimag.deca.ir.instructions.IRPrintValue;
import fr.ensimag.deca.ir.instructions.IRUnaryExpr;
import fr.ensimag.deca.ir.operands.IRDAddr;
import fr.ensimag.deca.ir.operands.IRDVal;
import fr.ensimag.deca.ir.operands.IRImmediateFloat;
import fr.ensimag.deca.ir.operands.IRImmediateInt;
import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.deca.ir.operands.IRScratch;
import fr.ensimag.deca.ir.operands.IRVariable;
import fr.ensimag.deca.ir.control.IRError;
import fr.ensimag.deca.ir.instructions.IRAbstractInst;
import fr.ensimag.deca.ir.instructions.IRJump;
import fr.ensimag.deca.ir.instructions.IRLabelDef;

/**
 * Un visiteur qui simule l'exécution de l'IR (Stack + Registres)
 */
public class IRSimulator implements AbstractIRVisitor {

    private final IRProgram program;

    // Mémoire symbolique (Nom variable -> Valeur)
    private final Map<String, Object> memory = new HashMap<>();

    // Registres virtuels (ID du scratch -> Valeur)
    private final Map<Integer, Object> registers = new HashMap<>();

    // Registre virtuel pour stocker le résultat des comparaisons
    private static final int CMP_FLAG_REG = -1;

    // Buffer de sortie (console)
    private final StringBuilder output = new StringBuilder();

    // Label courante (pour les sauts)
    private IRLabel currentLabel = null;

    public IRSimulator(IRProgram program) {
        this.program = program;
    }

    public String getOutput() {
        return output.toString();
    }

    public IRLabel getCurrentLabel() {
        return currentLabel;
    }

    public Object getRegisterValue(int regId) {
        return registers.get(regId);
    }

    public void run() throws IRError {
        for (var inst : program.text()) {
            inst.accept(this);
        }
    }
    // Méthodes pour évaluer les opérandes

    /**
     * Evalue une valeur (IRDVal).
     */
    private Object eval(IRDVal val) {
        if (val instanceof IRImmediateInt immInt) {
            return immInt.value();
        } else if (val instanceof IRImmediateFloat immFloat) {
            return immFloat.value();
        } else if (val instanceof IRScratch scratch) {
            int id = scratch.id();
            if (!registers.containsKey(id)) {
                throw new RuntimeException("Runtime Error: Registre s" + id + " vide (non initialisé)");
            }
            return registers.get(id);
        }
        throw new UnsupportedOperationException("Type d'opérande non supporté dans eval: " + (val != null ? val.getClass().getSimpleName() : "null"));
    }

    /**
     * Evalue une adresse (IRDAddr), lit depuis la mémoire ou un registre.
     */
    private Object evalAddr(IRDAddr addr) {
        if (addr instanceof IRVariable var) {
            if (!memory.containsKey(var.name())) {
                throw new RuntimeException("Runtime Error: Variable '" + var.name() + "' non initialisée");
            }
            return memory.get(var.name());
        }
        throw new UnsupportedOperationException("Type d'adresse non supporté dans evalAddr: " + (addr != null ? addr.getClass().getSimpleName() : "null"));
    }

    // Visiteurs pour les instructions IR

    @Override
    public void visit(IRAssign inst) {
        // Assignation : Variable <--- Valeur
        Object val = eval(inst.src());

        // La destination est une IRDAddr
        if (inst.dst() instanceof IRVariable var) {
            memory.put(var.name(), val);
        } else {
            throw new UnsupportedOperationException("Assignation vers " + inst.dst().getClass().getSimpleName() + " non gérée");
        }
    }

    @Override
    public void visit(IRLoad inst) {
        // Chargement : Registre <--- Mémoire/Valeur
        Object val = evalAddr(inst.src());
        registers.put(inst.dst().id(), val);
    }

    @Override
    public void visit(IRPrintValue inst) {
        Object val = eval(inst.value());
        output.append(val.toString());
    }

    @Override
    public void visit(IRBinaryExpr inst) {
        // Opérations binaires : dst = left op right
        Object leftVal = eval(inst.left());
        Object rightVal = eval(inst.right());
        Object result;

        String op = inst.name();

        // Opérations arithmétiques
        if (leftVal instanceof Integer leftInt && rightVal instanceof Integer rightInt) {
            result = switch (op) {
            case "ADD" -> leftInt + rightInt;
            case "SUB" -> leftInt - rightInt;
            case "MUL" -> leftInt * rightInt;
            case "DIV" -> leftInt / rightInt;
            case "MOD" -> leftInt % rightInt;
            default -> throw new UnsupportedOperationException("Opération " + op + " non supportée pour int");
            };
        } else if (leftVal instanceof Float leftFloat && rightVal instanceof Float rightFloat) {
            result = switch (op) {
            case "ADD" -> leftFloat + rightFloat;
            case "SUB" -> leftFloat - rightFloat;
            case "MUL" -> leftFloat * rightFloat;
            case "DIV" -> leftFloat / rightFloat;
            default -> throw new UnsupportedOperationException("Opération " + op + " non supportée pour float");
            };
        } else if (leftVal instanceof Integer leftInt && rightVal instanceof Float rightFloat) {
            float leftAsFloat = leftInt.floatValue();
            result = switch (op) {
            case "ADD" -> leftAsFloat + rightFloat;
            case "SUB" -> leftAsFloat - rightFloat;
            case "MUL" -> leftAsFloat * rightFloat;
            case "DIV" -> leftAsFloat / rightFloat;
            default -> throw new UnsupportedOperationException("Opération " + op + " non supportée pour int/float");
            };
        } else if (leftVal instanceof Float leftFloat && rightVal instanceof Integer rightInt) {
            float rightAsFloat = rightInt.floatValue();
            result = switch (op) {
            case "ADD" -> leftFloat + rightAsFloat;
            case "SUB" -> leftFloat - rightAsFloat;
            case "MUL" -> leftFloat * rightAsFloat;
            case "DIV" -> leftFloat / rightAsFloat;
            default -> throw new UnsupportedOperationException("Opération " + op + " non supportée pour float/int");
            };
        } else {
            throw new UnsupportedOperationException("Types incompatibles pour " + op + ": " + (leftVal != null ? leftVal.getClass() : "null") + " et " + (rightVal != null ? rightVal.getClass() : "null"));
        }

        registers.put(inst.dst().id(), result);
    }

    @Override
    public void visit(IRUnaryExpr inst) {
        // Opérations unaires : dst = op src
        Object srcVal = eval(inst.src());
        Object result;
        String op = inst.name();
        if (srcVal instanceof Integer srcInt) {
            result = switch (op) {
            case "NEG" -> -srcInt;
            case "NOT" -> (srcInt == 0) ? 1 : 0;
            default -> throw new UnsupportedOperationException("Opération " + op + " non supportée pour int");
            };
        } else if (srcVal instanceof Float srcFloat) {
            result = switch (op) {
            case "NEG" -> -srcFloat;
            case "NOT" -> (srcFloat == 0.0f) ? 1 : 0;
            default -> throw new UnsupportedOperationException("Opération " + op + " non supportée pour float");
            };
        } else {
            throw new UnsupportedOperationException("Type incompatible pour " + op + ": " + (srcVal != null ? srcVal.getClass() : "null"));
        }

        registers.put(inst.dest().id(), result);
    }

    @Override
    public void visit(IRConvFloat inst) {
        // Conversion int -> float
        Object srcVal = eval(inst.src());
        if (srcVal instanceof Integer srcInt) {
            float result = srcInt.floatValue();
            registers.put(inst.dst().id(), result);
        } else {
            throw new UnsupportedOperationException("Conversion non supportée pour le type: " + (srcVal != null ? srcVal.getClass().getSimpleName() : "null"));
        }
    }

    @Override
    public void visit(IRPrintChar inst) {
        char c = inst.c();
        output.append(c);
    }

    @Override
    public void visit(IROpCmp inst) {
        Object leftVal = eval(inst.left());
        Object rightVal = eval(inst.right());

        if (!(leftVal instanceof Number) || !(rightVal instanceof Number)) {
            throw new UnsupportedOperationException("Types incompatibles pour " + inst.name());
        }

        double l = ((Number) leftVal).doubleValue();
        double r = ((Number) rightVal).doubleValue();

        boolean cmpResult = switch (inst.name()) {
        case "CMP_EQ", "beq" -> l == r;
        case "CMP_NEQ", "bne" -> l != r;
        case "CMP_LT", "blt" -> l < r;
        case "CMP_LE", "ble" -> l <= r;
        case "CMP_GT", "bgt" -> l > r;
        case "CMP_GE", "bge" -> l >= r;
        default -> throw new UnsupportedOperationException("Comparaison " + inst.name() + " non supportée");
        };

        int result = cmpResult ? 1 : 0;
        registers.put(CMP_FLAG_REG, result);
        output.append(result);
    }

    @Override
    public void visit(IRPrintString inst) {
        String str = program.getString(inst.label());
        if (str == null) {
            throw new RuntimeException("Runtime Error: Label '" + inst.label().name() + "' non trouvé dans les données");
        }
        output.append(str);
    }

    @Override
    public void visit(IRExit inst) {
        // Arrêt de l'exécution simulée
        throw new RuntimeException("Simulation terminée avec IRExit");
    }

    @Override
    public void visit(IRLabelDef inst) {
        // Mise à jour du label courant
        currentLabel = inst.label();
    }

    @Override
    public void visit(IRJump inst) {
        // Saut inconditionnel
        IRLabel target = inst.target();
        boolean found = false;
        for (var instruction : program.text()) {
            if (instruction instanceof IRLabelDef labelDef && labelDef.label().equals(target)) {

                break;
            }
        }
        if (!found) {
            throw new RuntimeException("Runtime Error: Label de saut '" + target.name() + "' non trouvé");
        }
    }

    @Override
    public void visit(IRAbstractInst inst) {
        throw new UnsupportedOperationException("IRSimulator: pas de gestionnaire pour " + inst.getClass().getName());
    }

}