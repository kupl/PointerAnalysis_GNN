package ptatoolkit.doop.basic;

import ptatoolkit.pta.basic.StaticCallSite;
import ptatoolkit.pta.basic.Variable;

import java.util.Map;

public class DoopStaticCallSite extends StaticCallSite {

    private final String callSite;
    private final int id;

    public DoopStaticCallSite(String callSite,
                              Map<Integer, Variable> args, int id) {
        super(args);
        this.callSite = callSite;
        this.id = id;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return callSite;
    }
}
