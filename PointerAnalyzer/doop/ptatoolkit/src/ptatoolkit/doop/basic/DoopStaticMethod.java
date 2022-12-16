package ptatoolkit.doop.basic;

import ptatoolkit.pta.basic.StaticMethod;
import ptatoolkit.pta.basic.Variable;

import java.util.Collection;
import java.util.Map;

public class DoopStaticMethod extends StaticMethod {

    private final String sig;
    private final int id;

    public DoopStaticMethod(String sig,
                            Collection<Variable> params,
                            Collection<Variable> retVars,
                            boolean isPrivate,
                            int id) {
        this(sig, params, null, retVars, isPrivate, id);
    }

    public DoopStaticMethod(String sig,
                            Map<Integer, Variable> indexedParams,
                            Collection<Variable> retVars,
                            boolean isPrivate,
                            int id) {
        this(sig, indexedParams.values(), indexedParams,
                retVars, isPrivate, id);
    }

    private DoopStaticMethod(String sig,
                             Collection<Variable> params,
                             Map<Integer, Variable> indexedParams,
                             Collection<Variable> retVars,
                             boolean isPrivate,
                             int id) {
        super(params, indexedParams, retVars, isPrivate);
        this.sig = sig;
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return sig;
    }

}
