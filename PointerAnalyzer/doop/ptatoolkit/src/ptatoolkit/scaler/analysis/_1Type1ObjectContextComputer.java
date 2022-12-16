package ptatoolkit.scaler.analysis;

import ptatoolkit.Global;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.pta.basic.Obj;
import ptatoolkit.scaler.pta.PointsToAnalysis;

import java.util.Set;

/**
 * Context elements of 1-type-1-object analysis contain both
 * Obj and Type, so we use BasicElement as the element type, which is
 * the super type of both Obj and Type.
 */
public class _1Type1ObjectContextComputer extends ContextComputer {

    public _1Type1ObjectContextComputer(PointsToAnalysis pta,
                                        ObjectAllocationGraph oag) {
        super(pta, oag);
    }

    @Override
    public String getAnalysisName() {
        return "1-type-1-object";
    }

    @Override
    protected int computeContextNumberOf(Method method) {
        if (pta.receiverObjectsOf(method).isEmpty()) {
            if (Global.isDebug()) {
                System.out.printf("Empty receiver: %s\n", method.toString());
            }
            return 1;
        }
        int count = 0;
        for (Obj recv : pta.receiverObjectsOf(method)) {
            Set<Obj> preds = oag.predsOf(recv);
            if (!preds.isEmpty()) {
                count += preds.stream()
                        .map(pta::declaringAllocationTypeOf)
                        .distinct()
                        .count();
            } else {
                // without allocator, back to 1-object
                ++count;
            }
        }
        return count;
    }
}
