package ptatoolkit.zipper;

import ptatoolkit.Options;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.util.PTAUtils;
import ptatoolkit.zipper.analysis.JDKChecker;
import ptatoolkit.zipper.pta.PointsToAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.google.common.base.Predicates.not;

public class AllJDK {

    public static void main(String[] args) throws IOException {
        Options opt = Options.parse(args);
        run(opt);
    }

    public static void run(Options opt) throws IOException {
        PointsToAnalysis pta = PTAUtils.readPointsToAnalysis(opt);
        Set<Method> methods = pta.reachableMethods();
        methods.removeIf(not(JDKChecker::isJDKMethod));
        System.out.printf("%d JDK methods in %s ...\n",
                methods.size(), opt.getApp());
        File allLibOutput = new File(opt.getOutPath(),
                String.format("%s-AllJDKMethod.facts", opt.getApp()));
        System.out.printf("Writing all JDK methods to %s ...\n",
                allLibOutput.getPath());
        System.out.println();
        Main.writeZipperResults(methods, allLibOutput);
    }
}
