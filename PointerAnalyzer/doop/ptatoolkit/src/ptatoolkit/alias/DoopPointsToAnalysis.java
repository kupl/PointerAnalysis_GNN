package ptatoolkit.alias;

import ptatoolkit.Options;
import ptatoolkit.doop.DataBase;
import ptatoolkit.doop.Query;
import ptatoolkit.doop.factory.ObjFactory;
import ptatoolkit.doop.factory.TypeFactory;
import ptatoolkit.doop.factory.VariableFactory;
import ptatoolkit.pta.basic.IAttribute;
import ptatoolkit.pta.basic.Obj;
import ptatoolkit.pta.basic.Type;
import ptatoolkit.pta.basic.Variable;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DoopPointsToAnalysis implements PointsToAnalysis {

    private final DataBase db;
    private Set<Obj> allObjs;
    private Set<Obj> allMergedObjs;
    private Set<Variable> allVars;
    private Type string;

    public DoopPointsToAnalysis(Options options) {
        File dbDir = options.getDbPath() != null ?
                new File(options.getDbPath()) : null;
        File cacheDir = new File(options.getCachePath());
        this.db = new DataBase(dbDir, cacheDir, options.getApp());
        init();
    }

    @Override
    public Set<Obj> allObjects() {
        return allObjs;
    }

    @Override
    public Set<Obj> allMergedObjects() {
        return allMergedObjs;
    }

    @Override
    public Set<Variable> pointersOf(Obj obj) {
        return obj.getAttributeSet(Attribute.POINTERS);
    }

    @Override
    public Set<Variable> allVariables() {
        return allVars;
    }

    @Override
    public PointsToSet pointsToSetOf(Variable var) {
        return (PointsToSet) var.getAttribute(Attribute.POINTS_TO);
    }

    @Override
    public Set<Variable> assignedTo(Variable var) {
        return var.getAttributeSet(Attribute.ASSIGNED_TO);
    }

    @Override
    public boolean isConcerned(Obj obj) {
        return !allMergedObjs.contains(obj) && !obj.getType().equals(string);
    }

    private void init() {
        TypeFactory typeFactory = new TypeFactory();
        VariableFactory varFactory = new VariableFactory();
        ObjFactory objFactory = new ObjFactory(db, typeFactory);
        allObjs = new HashSet<>();
        allMergedObjs = new HashSet<>();
        allVars = new HashSet<>();
        string = typeFactory.get("java.lang.String");
        db.query(Query.MERGE_OBJECTS).forEachRemaining(list -> {
            allMergedObjs.add(objFactory.get(list.get(1)));
        });
        processPointsToSet(varFactory, objFactory);
        db.query(Query.LOCAL_ASSIGN).forEachRemaining(list -> {
            Variable to = varFactory.get(list.get(0));
            Variable from = varFactory.get(list.get(1));
            from.addToAttributeSet(Attribute.ASSIGNED_TO, to);
        });
        db.query(Query.INTERPROCEDURAL_ASSIGN).forEachRemaining(list -> {
            Variable to = varFactory.get(list.get(0));
            Variable from = varFactory.get(list.get(1));
            from.addToAttributeSet(Attribute.ASSIGNED_TO, to);
        });
        db.query(Query.THIS_ASSIGN).forEachRemaining(list -> {
            Variable thisVar = varFactory.get(list.get(0));
            Variable base = varFactory.get(list.get(1));
            base.addToAttributeSet(Attribute.ASSIGNED_TO, thisVar);
        });
    }

    private void processPointsToSet(
            VariableFactory varFactory, ObjFactory objFactory) {
        db.query(Query.VPT).forEachRemaining(list -> {
            Obj obj = objFactory.get(list.get(0));
            if (!isConcerned(obj)) {
                return;
            }
            Variable var = varFactory.get(list.get(1));
            obj.addToAttributeSet(Attribute.POINTERS, var);
            var.addToAttributeSet(Attribute.POINTS_TO, obj);
            allObjs.add(obj);
            allVars.add(var);
        });
        ConcurrentMap<Set<Obj>, PointsToSet> ptss = new ConcurrentHashMap<>();
        allVars.parallelStream().forEach(var -> {
            PointsToSet pts = ptss.computeIfAbsent(
                    var.getAttributeSet(Attribute.POINTS_TO),
                    PointsToSet::new);
            var.setAttribute(Attribute.POINTS_TO, pts);
        });
        ptss.values().parallelStream().forEach(pts ->
                pts.objects()
                        .map(o -> o.getAttributeSet(Attribute.POINTERS))
                        .flatMap(Set::stream)
                        .map(p -> (Variable) p)
                        .forEach(pts::addPointer));
    }

    private enum Attribute implements IAttribute {
        POINTERS,
        POINTS_TO,
        ASSIGNED_TO,
    }
}
