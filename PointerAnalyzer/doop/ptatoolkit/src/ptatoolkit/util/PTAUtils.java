package ptatoolkit.util;

import ptatoolkit.Options;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PTAUtils {

    private PTAUtils() {
    }

    public static <T> T readPointsToAnalysis(Options opt) {
        try {
            Class<?> ptaClass = Class.forName(opt.getPTA());
            Constructor<?> constructor = ptaClass.getConstructor(Options.class);
            return (T) constructor.newInstance(opt);
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Reading points-to analysis results fails");
        }
    }
}
