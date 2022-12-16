package ptatoolkit.doop.factory;

import ptatoolkit.doop.DataBase;
import ptatoolkit.doop.Query;
import ptatoolkit.doop.basic.DoopInstanceCallSite;
import ptatoolkit.doop.basic.DoopStaticCallSite;
import ptatoolkit.pta.basic.CallSite;
import ptatoolkit.pta.basic.Variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CallSiteFactory extends ElementFactory<CallSite> {

    private final Map<String, Variable> call2recv = new HashMap<>();
    private final Map<String, Map<Integer, Variable>> call2args = new HashMap<>();

    public CallSiteFactory(DataBase db, VariableFactory varFactory) {
        db.query(Query.INST_CALL_RECV).forEachRemaining(list -> {
            String call = list.get(0);
            Variable thisVar = varFactory.get(list.get(1));
            call2recv.put(call, thisVar);
        });

        db.query(Query.CALLSITE_ARGS).forEachRemaining(list -> {
            String call = list.get(0);
            int i = Integer.parseInt(list.get(1));
            Variable arg = varFactory.get(list.get(2));
            if (!call2args.containsKey(call)) {
                call2args.put(call, new HashMap<>(4));
            }
            call2args.get(call).put(i, arg);
        });
    }

    @Override
    protected CallSite createElement(String callSite) {
        Variable recv = call2recv.get(callSite);
        Map<Integer, Variable> args = call2args.get(callSite);
        if (args == null) {
            args = Collections.emptyMap();
        }
        // TODO: check completeness here, null must be static?
        if (recv != null) {
            return new DoopInstanceCallSite(callSite, recv, args, ++count);
        } else {
            return new DoopStaticCallSite(callSite, args, ++count);
        }
    }
}
