package ptatoolkit.pta.basic;

import java.util.Collection;
import java.util.Map;

public abstract class InstanceMethod extends Method {

    private final Variable thisVar; // for static method, this field is null;

    protected InstanceMethod(Variable thisVar,
                             Collection<Variable> params,
                             Map<Integer, Variable> indexedParams,
                             Collection<Variable> retVars,
                             boolean isPrivate) {
        super(params, indexedParams, retVars, isPrivate);
        this.thisVar = thisVar;
    }

    public Variable getThis() {
        return thisVar;
    }

    @Override
    public boolean isInstance() {
        return true;
    }
}
