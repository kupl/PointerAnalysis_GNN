package ptatoolkit.zipper.pta;

import ptatoolkit.pta.basic.Obj;

import static ptatoolkit.util.ANSIColor.BOLD_GREEN;
import static ptatoolkit.util.ANSIColor.printColoredNumber;

public class Utils {

    public static void outputNumberOfClasses(PointsToAnalysis pta) {
        int nrClasses = (int) pta.allObjects().stream()
                .map(Obj::getType)
                .distinct()
                .count();
        printColoredNumber(BOLD_GREEN, "#Classes: ", nrClasses);
    }

    public static void outputNumberOfMethods(PointsToAnalysis pta) {
        int nrMethods = pta.reachableMethods().size();
        printColoredNumber(BOLD_GREEN, "#Methods: ", nrMethods);
    }
}
