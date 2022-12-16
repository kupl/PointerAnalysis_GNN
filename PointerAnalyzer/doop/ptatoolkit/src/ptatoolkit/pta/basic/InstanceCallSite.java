package ptatoolkit.pta.basic;

import java.util.Map;

public abstract class InstanceCallSite extends CallSite {

    private final Variable receiver; // for static call, this field is null

    protected InstanceCallSite(Variable receiver, Map<Integer, Variable> args) {
        super(args);
        this.receiver = receiver;
    }

    public Variable getReceiver() {
        return receiver;
    }

    @Override
    public boolean isInstance() {
        return true;
    }
}
