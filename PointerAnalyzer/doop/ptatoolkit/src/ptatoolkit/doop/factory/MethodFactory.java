package ptatoolkit.doop.factory;

import ptatoolkit.doop.DataBase;
import ptatoolkit.doop.Query;
import ptatoolkit.doop.basic.DoopInstanceMethod;
import ptatoolkit.doop.basic.DoopStaticMethod;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.pta.basic.Variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodFactory extends ElementFactory<Method> {

    protected final Map<String, Variable> sig2this = new HashMap<>();
    protected final Map<String, Set<Variable>> sig2ret = new HashMap<>();
    protected final Set<String> privateMethods = new HashSet<>();
    private final Map<String, Set<Variable>> sig2params = new HashMap<>();

    public MethodFactory(DataBase db, VariableFactory varFactory) {
        init(db, varFactory);
    }

    /**
     * This constructor is used by subclass, to avoid the null pointer
     * due to the order of initialization.
     */
    protected MethodFactory() {
    }

    protected void init(DataBase db, VariableFactory varFactory) {
        buildThis(db, varFactory);
        buildParams(db, varFactory);
        buildReturn(db, varFactory);
        buildModifier(db);
    }

    private void buildThis(DataBase db, VariableFactory varFactory) {
        db.query(Query.THIS_VAR).forEachRemaining(list -> {
            String sig = list.get(0);
            Variable thisVar = varFactory.get(list.get(1));
            sig2this.put(sig, thisVar);
        });
    }

    protected void buildParams(DataBase db, VariableFactory varFactory) {
        db.query(Query.PARAMS).forEachRemaining(list -> {
            String sig = list.get(0);
            Variable param = varFactory.get(list.get(1));
            if (!sig2params.containsKey(sig)) {
                sig2params.put(sig, new HashSet<>(4));
            }
            sig2params.get(sig).add(param);
        });
    }

    private void buildReturn(DataBase db, VariableFactory varFactory) {
        db.query(Query.RET_VARS).forEachRemaining(list -> {
            String sig = list.get(0);
            Variable ret = varFactory.get(list.get(1));
            if (!sig2ret.containsKey(sig)) {
                sig2ret.put(sig, new HashSet<>(4));
            }
            sig2ret.get(sig).add(ret);
        });
    }

    private void buildModifier(DataBase db) {
        db.query(Query.METHOD_MODIFIER).forEachRemaining(list -> {
            String sig = list.get(0);
            String mod = list.get(1);
            if (mod.equals("private")) {
                privateMethods.add(sig);
            }
        });
    }

    @Override
    protected Method createElement(String sig) {
        Variable thisVar = sig2this.get(sig);
        Set<Variable> params = sig2params.get(sig);
        if (params == null) {
            params = Collections.emptySet();
        }
        Set<Variable> retVars = sig2ret.get(sig);
        if (retVars == null) {
            retVars = Collections.emptySet();
        }
        boolean isPrivate = privateMethods.contains(sig);
        if (thisVar != null) { // sig represents an instance method
            return new DoopInstanceMethod(sig, thisVar, params, retVars,
                    isPrivate, ++count);
        } else { // sig represents a static method
            return new DoopStaticMethod(sig, params, retVars,
                    isPrivate, ++count);
        }
    }
}
