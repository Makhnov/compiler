package fr.ensimag.deca.codegen;

import java.util.ArrayDeque;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.ir.operands.IRLabel;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Manage unique labels.
 * 
 * @author gl25
 * @date 29/12/2026
 */
public class LabelManager {
    // On n'utilise pas de static ici pour ne pas interférer avec un éventuel parallélisme
    private int counter = 0;
    
    // Pile de labels de fin de méthodes (pour éviter les doublons dans les RTS)
    private final ArrayDeque<Label> methodsEnd = new ArrayDeque<>();

    public int getLabelID() {
        return counter++;
    }

    /* LABELS SANS OBJETS */

    public Label label(String prefix, int id) {
        return new Label(prefix + "." + id);
    }

    public Label labelFin(int id) {
        return new Label("E_Fin." + id);
    }

    public Label labelSinon(int id) {
        return new Label("E_Sinon." + id);
    }

    public Label labelDebut(int id) {
        return new Label("E_Debut." + id);
    }

    public Label labelCond(int id) {
        return new Label("E_Cond." + id);
    }

    public Label labelEchec(int id) {
        return new Label("E_Echec." + id);
    }

    /* LABELS OBJETS */

    public void pushLabelFin(Label l) {
        Validate.notNull(l);
        methodsEnd.push(l);
    }

    public Label popLabelFin() {
        Validate.isTrue(!methodsEnd.isEmpty(), "No label to pop (method end)");
        return methodsEnd.pop();
    }

    public Label currentLabelFin() {
        Validate.isTrue(!methodsEnd.isEmpty(), "No label to get (method end)");
        return methodsEnd.peek();
    }

    public Label labelInit(String className) {
        return new Label("init." + className);
    }

    public Label labelMethod(String className, String methodName) {
        return new Label("code." + className + "." + methodName);
    }

    public Label labelFin(String className, String methodName) {
        return new Label("fin." + className + "." + methodName);
    }    

    public Label labelCast(int id) {
        return new Label("E_Cast." + id);
    }

    /* IR */
    public IRLabel irLabel(String prefix, int id) {
        return new IRLabel(prefix + "." + id);
    }

    public IRLabel irLabelFin(int id)   {
        return new IRLabel("E_Fin." + id);
    }

    public IRLabel irLabelSinon(int id) {
        return new IRLabel("E_Sinon." + id);
    }

    public IRLabel irLabelDebut(int id) {
        return new IRLabel("E_Debut." + id);
    }

    public IRLabel irLabelCond(int id)  {
        return new IRLabel("E_Cond." + id);
    }


}

