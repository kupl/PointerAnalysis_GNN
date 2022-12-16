package ptatoolkit.scaler.analysis;

import ptatoolkit.pta.basic.IAttribute;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.pta.basic.Obj;
import ptatoolkit.scaler.pta.PointsToAnalysis;
import ptatoolkit.util.graph.DirectedGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ObjectAllocationGraph implements DirectedGraph<Obj> {

    private final PointsToAnalysis pta;

    ObjectAllocationGraph(PointsToAnalysis pta) {
        this.pta = pta;
        init();
    }

    @Override
    public Set<Obj> allNodes() {
        return pta.allObjects();
    }

    @Override
    public Set<Obj> predsOf(Obj obj) {
        return obj.getAttributeSet(Attr.PREDS);
    }

    @Override
    public Set<Obj> succsOf(Obj obj) {
        return obj.getAttributeSet(Attr.SUCCS);
    }

    private void init() {
        Map<Obj, Set<Method>> invokedMethods = computeInvokedMethods();
        invokedMethods.forEach((obj, methods) -> methods.stream()
                .map(pta::objectsAllocatedIn)
                .flatMap(Collection::stream)
                .forEach(o -> {
                    obj.addToAttributeSet(Attr.SUCCS, o);
                    o.addToAttributeSet(Attr.PREDS, obj);
                }));
    }

    private Map<Obj, Set<Method>> computeInvokedMethods() {
        Map<Obj, Set<Method>> invokedMethods = new HashMap<>();
        pta.allObjects().forEach(obj -> {
            Set<Method> methods = new HashSet<>();
            Queue<Method> queue = new LinkedList<>(pta.methodsInvokedOn(obj));
            while (!queue.isEmpty()) {
                Method method = queue.poll();
                methods.add(method);
                pta.calleesOf(method).stream()
                        .filter(m -> m.isStatic() && !methods.contains(m))
                        .forEach(queue::offer);
            }
            invokedMethods.put(obj, methods);
        });
        return invokedMethods;
    }

    private enum Attr implements IAttribute {
        PREDS, SUCCS
    }
}
