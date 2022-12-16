package ptatoolkit.scaler.doop;

import ptatoolkit.Global;
import ptatoolkit.Options;
import ptatoolkit.doop.DataBase;
import ptatoolkit.doop.Query;
import ptatoolkit.doop.factory.TypeFactory;
import ptatoolkit.doop.factory.VariableFactory;
import ptatoolkit.pta.basic.InstanceMethod;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.pta.basic.Obj;
import ptatoolkit.pta.basic.Type;
import ptatoolkit.pta.basic.Variable;
import ptatoolkit.scaler.pta.PointsToAnalysis;
import ptatoolkit.util.MutableInteger;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ptatoolkit.scaler.doop.Attribute.ALLOCATED;
import static ptatoolkit.scaler.doop.Attribute.CALLEE;
import static ptatoolkit.scaler.doop.Attribute.DECLARING_ALLOC_TYPE;
import static ptatoolkit.scaler.doop.Attribute.DECLARING_TYPE;
import static ptatoolkit.scaler.doop.Attribute.METHODS_CALLED_ON;
import static ptatoolkit.scaler.doop.Attribute.POINTS_TO_SET;
import static ptatoolkit.scaler.doop.Attribute.POINTS_TO_SIZE;
import static ptatoolkit.scaler.doop.Attribute.RECEIVER;
import static ptatoolkit.scaler.doop.Attribute.VARS_IN;

public class DoopPointsToAnalysis implements PointsToAnalysis {

    private final DataBase db;
    private Set<Obj> allObjs;
    private Set<Method> reachableMethods;
    private Set<String> specialObjects;

    public DoopPointsToAnalysis(Options opt) {
        File dbDir = opt.getDbPath() != null ?
                new File(opt.getDbPath()) : null;
        File cacheDir = new File(opt.getCachePath());
        this.db = new DataBase(dbDir, cacheDir, opt.getApp());
        init();
    }

    @Override
    public Set<Obj> allObjects() {
        return allObjs;
    }

    @Override
    public Set<Method> reachableMethods() {
        return reachableMethods;
    }

    @Override
    public Set<Obj> pointsToSetOf(Variable var) {
        if (var.hasAttribute(POINTS_TO_SET)) {
            return (Set<Obj>) var.getAttribute(POINTS_TO_SET);
        } else { // in this case, the variable is a null pointer
            if (Global.isDebug()) {
                System.out.println(var + " is a null pointer.");
            }
            return Collections.emptySet();
        }
    }

    @Override
    public int pointsToSetSizeOf(Variable var) {
        if (var.hasAttribute(POINTS_TO_SIZE)) {
            MutableInteger size = (MutableInteger) var.getAttribute(POINTS_TO_SIZE);
            return size.intValue();
        } else {
            return 0;
        }
    }

    @Override
    public Set<Variable> variablesDeclaredIn(Method method) {
        return method.getAttributeSet(VARS_IN);
    }

    @Override
    public Set<Obj> objectsAllocatedIn(Method method) {
        return method.getAttributeSet(ALLOCATED);
    }

    @Override
    public Set<Method> calleesOf(Method method) {
        return method.getAttributeSet(CALLEE);
    }

    @Override
    public Set<Method> methodsInvokedOn(Obj obj) {
        return obj.getAttributeSet(METHODS_CALLED_ON);
    }

    @Override
    public Set<Obj> receiverObjectsOf(Method method) {
        return method.getAttributeSet(RECEIVER);
    }

    @Override
    public Type declaringAllocationTypeOf(Obj obj) {
        return (Type) obj.getAttribute(DECLARING_ALLOC_TYPE);
    }

    @Override
    public Type declaringTypeOf(Method method) {
        return (Type) method.getAttribute(DECLARING_TYPE);
    }

    private void init() {
        TypeFactory typeFactory = new TypeFactory();
        VariableFactory varFactory = new VariableFactory();
        ObjFactory objFactory = new ObjFactory();
        MethodFactory mtdFactory = new MethodFactory(db, varFactory);

        // Set of variable names whose points-to sets may be needed
        Set<String> interestingVarNames = new HashSet<>();

        // obtain all reachable instance methods
        db.query(Query.INST_METHODS).forEachRemaining(list -> {
            String mtdSig = list.get(0);
            InstanceMethod instMtd =
                    (InstanceMethod) mtdFactory.get(mtdSig);
            interestingVarNames.add(instMtd.getThis().toString());
        });

        buildPointsToSet(varFactory, objFactory, interestingVarNames);

        // compute the objects allocated in each method
        specialObjects = new HashSet<>();
        db.query(Query.SPECIAL_OBJECTS).forEachRemaining(list ->
                specialObjects.add(list.get(0)));

        computeAllocatedObjects(objFactory, mtdFactory);

        buildCallees(mtdFactory);

        buildMethodsInvokedOnObjects(mtdFactory);

        buildReceiverObjects();

        buildDeclaringAllocationType(objFactory, typeFactory);

        buildDeclaredVariables(mtdFactory, varFactory);

        buildDeclaringType(mtdFactory, typeFactory);
    }

    /**
     * Build points-to sets of interesting variables. This method also computes
     * the size of points-to set for each variable (in instance method).
     */
    private void buildPointsToSet(VariableFactory varFactory, ObjFactory objFactory,
                                  Set<String> interestingVarNames) {
        allObjs = new HashSet<>();
        db.query(Query.VPT).forEachRemaining(list -> {
            String objName = list.get(0);
            String varName = list.get(1);
            Obj obj = objFactory.get(objName);
            Variable var = varFactory.get(varName);
            if (interestingVarNames.contains(varName)) {
                // add points-to set to var as its attribute
                var.addToAttributeSet(POINTS_TO_SET, obj);
            }
            increasePointsToSetSizeOf(var);
            allObjs.add(obj);
        });
    }

    private void increasePointsToSetSizeOf(Variable var) {
        if (var.hasAttribute(POINTS_TO_SIZE)) {
            MutableInteger size = (MutableInteger) var.getAttribute(POINTS_TO_SIZE);
            size.increase();
        } else {
            var.setAttribute(POINTS_TO_SIZE, new MutableInteger(1));
        }
    }

    private void computeAllocatedObjects(ObjFactory objFactory,
                                         MethodFactory mtdFactory) {
        db.query(Query.OBJECT_IN).forEachRemaining(list -> {
            String objName = list.get(0);
            if (isNormalObject(objName)) {
                Obj obj = objFactory.get(objName);
                Method method = mtdFactory.get(list.get(1));
                method.addToAttributeSet(ALLOCATED, obj);
                obj.setAttribute(ALLOCATED, method);
            }
        });
    }

    private boolean isNormalObject(String objName) {
        return !specialObjects.contains(objName) &&
                !objName.startsWith("<class "); // class constant
    }

    /**
     * Build caller-callee relations.
     */
    private void buildCallees(MethodFactory mtdFactory) {
        reachableMethods = new HashSet<>();
        Map<String, String> callIn = new HashMap<>();
        db.query(Query.CALLSITE_IN).forEachRemaining(list -> {
            String call = list.get(0);
            String methodSig = list.get(1);
            callIn.put(call, methodSig);
        });
        db.query(Query.CALL_EDGE).forEachRemaining(list -> {
            String callerSig = callIn.get(list.get(0));
            if (callerSig != null) {
                Method caller = mtdFactory.get(callerSig);
                Method callee = mtdFactory.get(list.get(1));
                caller.addToAttributeSet(CALLEE, callee);
                reachableMethods.add(caller);
                reachableMethods.add(callee);
            } else if (Global.isDebug()) {
                System.out.println("Null caller of: " + list.get(0));
            }
        });
    }

    /**
     * Map each object to the methods invoked on it.
     */
    private void buildMethodsInvokedOnObjects(MethodFactory mtdFactory) {
        mtdFactory.getAllElements()
                .stream()
                .filter(Method::isInstance)
                .map(m -> (InstanceMethod) m)
                .forEach(instMtd -> {
                    Variable thisVar = instMtd.getThis();
                    pointsToSetOf(thisVar).forEach(obj ->
                            obj.addToAttributeSet(METHODS_CALLED_ON, instMtd));
                });
    }

    /**
     * Map each object to the type which contains its allocation site.
     */
    private void buildDeclaringAllocationType(ObjFactory objFactory,
                                              TypeFactory typeFactory) {
        db.query(Query.DECLARING_CLASS_ALLOCATION).forEachRemaining(list -> {
            Obj obj = objFactory.get(list.get(0));
            Type type = typeFactory.get(list.get(1));
            obj.setAttribute(DECLARING_ALLOC_TYPE, type);
        });
    }


    /**
     * Map each instance method to their receiver objects.
     */
    private void buildReceiverObjects() {
        for (Obj obj : allObjects()) {
            for (Method method : methodsInvokedOn(obj)) {
                method.addToAttributeSet(RECEIVER, obj);
            }
        }
    }

    /**
     * Map each method to the variables declared in the method.
     */
    private void buildDeclaredVariables(MethodFactory mtdFactory,
                                        VariableFactory varFactory) {
        db.query(Query.VAR_IN).forEachRemaining(list -> {
            Variable var = varFactory.get(list.get(0));
            Method inMethod = mtdFactory.get(list.get(1));
            if (inMethod.isInstance()) { // ignore static methods
                inMethod.addToAttributeSet(VARS_IN, var);
            }
        });
    }

    /**
     * Map each method to the type which declares it.
     */
    private void buildDeclaringType(MethodFactory mtdFactory,
                                    TypeFactory typeFactory) {
        mtdFactory.getAllElements().forEach(m -> {
            String sig = m.toString();
            String typeName = sig.substring(1, sig.indexOf(':'));
            Type type = typeFactory.get(typeName);
            m.setAttribute(DECLARING_TYPE, type);
        });
    }
}
