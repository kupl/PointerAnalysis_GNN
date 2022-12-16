package ptatoolkit.alias;

import ptatoolkit.pta.basic.Obj;
import ptatoolkit.pta.basic.Variable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

class PointsToSet {

    private final Set<Obj> objects;

    private final Set<Variable> pointers = new HashSet<>();

    private final int hashCode;

    PointsToSet(Set<Obj> objects) {
        this.objects = objects;
        this.hashCode = objects.hashCode();
    }

    Stream<Obj> objects() {
        return objects.stream();
    }

    void addPointer(Variable var) {
        pointers.add(var);
    }

    Stream<Variable> pointers() {
        return pointers.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointsToSet that = (PointsToSet) o;
        return hashCode == that.hashCode && objects.equals(that.objects);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return objects.toString();
    }
}
