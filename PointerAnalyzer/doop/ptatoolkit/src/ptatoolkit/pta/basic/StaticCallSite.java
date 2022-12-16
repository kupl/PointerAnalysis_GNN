package ptatoolkit.pta.basic;

import java.util.Map;

public abstract class StaticCallSite extends CallSite {

    protected StaticCallSite(Map<Integer, Variable> args) {
        super(args);
    }

    @Override
    public boolean isInstance() {
        return false;
    }
}
