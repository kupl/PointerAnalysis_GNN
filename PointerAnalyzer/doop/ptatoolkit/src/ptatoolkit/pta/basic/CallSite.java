package ptatoolkit.pta.basic;

import java.util.Map;

public abstract class CallSite extends AttributeElement {

    private final Map<Integer, Variable> args;

    protected CallSite(Map<Integer, Variable> args) {
        this.args = args;
    }

    public Variable getArg(int i) {
        return args.get(i);
    }

    public abstract boolean isInstance();
}
