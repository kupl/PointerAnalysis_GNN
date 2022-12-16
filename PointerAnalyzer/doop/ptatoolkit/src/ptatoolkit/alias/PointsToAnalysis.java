package ptatoolkit.alias;

import ptatoolkit.pta.basic.Obj;
import ptatoolkit.pta.basic.Variable;

import java.util.Set;

interface PointsToAnalysis {

    /**
     * @return all objects in the points-to analysis
     */
    Set<Obj> allObjects();

    /**
     * @return all merged objects in the points-to analysis
     */
    Set<Obj> allMergedObjects();

    /**
     * @return if given obj is concerned in alias computation
     */
    boolean isConcerned(Obj obj);

    /**
     * @return all variables pointing to the given object
     */
    Set<Variable> pointersOf(Obj obj);

    /**
     * @return all variables in the points-to analysis
     */
    Set<Variable> allVariables();

    /**
     * @return points-to set of the given variable
     */
    PointsToSet pointsToSetOf(Variable var);

    /**
     * @return all variables that the given variable is assigned to
     */
    Set<Variable> assignedTo(Variable var);
}
