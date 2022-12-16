package ptatoolkit.zipper.analysis;

import ptatoolkit.pta.basic.Method;

/**
 * Check whether a method or a class is declared in JDK.
 */
public class JDKChecker {

    private static final String[] JDKClassPrefix = {
            "com.oracle.", "com.sun.", "java.", "javax.",
            "org.ietf.jgss.", "org.jcp.xml.dsig.internal.", "org.omg.",
            "org.w3c.dom.", "org.xml.sax.",
            "sun.", "sunw."
    };

    public static boolean isJDKMethod(Method method) {
        return isJDKClass(method.getClassName());
    }

    public static boolean isJDKClass(String className) {
        for (String prefix : JDKClassPrefix) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
