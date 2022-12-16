package ptatoolkit.scaler.analysis;

import ptatoolkit.Global;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.scaler.pta.PointsToAnalysis;

public class _1ObjectContextComputer extends ContextComputer {

    public _1ObjectContextComputer(PointsToAnalysis pta, ObjectAllocationGraph oag) {
        super(pta, oag);
    }

    @Override
    public String getAnalysisName() {
        return "1-object";
    }

    @Override
    protected int computeContextNumberOf(Method method) {
        if (pta.receiverObjectsOf(method).isEmpty()) {
            if (Global.isDebug()) {
                System.out.printf("Empty receiver: %s\n", method.toString());
            }
            return 1;
        }
        return pta.receiverObjectsOf(method).size();
    }
}
