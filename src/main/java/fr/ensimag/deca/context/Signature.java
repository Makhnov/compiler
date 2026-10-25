package fr.ensimag.deca.context;

import java.util.ArrayList;
import java.util.List;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl25
 * @date 01/01/2026
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();

    public void add(Type t) {
        args.add(t);
    }

    public Type paramNumber(int n) {
        return args.get(n);
    }

    public int size() {
        return args.size();
    }

    public boolean sameSignature(Signature sig) {
        if (this.size() != sig.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (!this.paramNumber(i).sameType(sig.paramNumber(i))) {
                return false;
            }
        }
        return true;
    }

}
