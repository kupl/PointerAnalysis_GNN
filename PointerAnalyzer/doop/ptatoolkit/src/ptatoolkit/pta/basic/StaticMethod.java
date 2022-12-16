package ptatoolkit.pta.basic;

import java.util.Collection;
import java.util.Map;

public abstract class StaticMethod extends Method {

    protected StaticMethod(Collection<Variable> params,
                           Map<Integer, Variable> indexedParams,
                           Collection<Variable> retVars,
                           boolean isPrivate) {
        super(params, indexedParams, retVars, isPrivate);
    }

    @Override
    public Collection<Variable> getAllParameters() {
        return getParameters();
    }

    @Override
    public boolean isInstance() {
        return false;
    }

}
