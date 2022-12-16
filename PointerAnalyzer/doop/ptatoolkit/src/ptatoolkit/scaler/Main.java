package ptatoolkit.scaler;

import ptatoolkit.Global;
import ptatoolkit.Options;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.pta.basic.Type;
import ptatoolkit.scaler.analysis.ContextComputer;
import ptatoolkit.scaler.analysis.Scaler;
import ptatoolkit.scaler.pta.PointsToAnalysis;
import ptatoolkit.util.PTAUtils;
import ptatoolkit.util.Timer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ptatoolkit.util.ANSIColor.BOLD_GREEN;
import static ptatoolkit.util.ANSIColor.BOLD_YELLOW;
import static ptatoolkit.util.ANSIColor.colored;
import static ptatoolkit.util.ANSIColor.printColored;

public class Main {

    private static final char SEP = '\t';
    private static final char EOL = '\n';

    public static void main(String[] args) throws FileNotFoundException {
        Options opt = Options.parse(args);
        run(opt);
    }

    public static void run(Options opt) throws FileNotFoundException {
        System.out.printf("Analyze %s ...\n", opt.getApp());
        System.out.print("Reading points-to analysis results ... ");
        PointsToAnalysis pta = Timer.executeAndCount(
                "", () -> PTAUtils.readPointsToAnalysis(opt));
        if (Global.isDebug()) {
            System.out.printf("%d objects in (pre) points-to analysis.\n",
                    pta.allObjects().size());
        }

        Timer scalerTimer = new Timer("Scaler Timer");
        printColored(BOLD_YELLOW, "Scaler starts ...");
        scalerTimer.start();
        Scaler scaler = new Scaler(pta);
        if (Global.getTST() != Global.UNDEFINED) {
            scaler.setTST(Global.getTST());
        }
        String pcmFile = Global.getPCMFile();
        if (pcmFile != null) {
            System.out.println("Reading precision-critical methods from "
                    + pcmFile);
            scaler.setPrecisionCriticalMethods(pcmFile);
        }
        String nonPcmFile = Global.getNonPCMFile();
        if (nonPcmFile != null) {
            System.out.println("Reading non-precision-critical methods from "
                    + nonPcmFile);
            scaler.setNonPrecisionCriticalMethods(nonPcmFile);
        }
        Map<Method, String> scalerResults = scaler.selectContext();
        scalerTimer.stop();
        printColored(BOLD_GREEN, colored(BOLD_YELLOW, "Scaler finishes, analysis time: "),
                String.format("%.2fs", scalerTimer.inSecond()));
        if (Global.isDebug()) {
            for (ContextComputer cc : scaler.getContextComputers()) {
                outputMethodContext(pta, cc, scaler);
                outputContextByType(pta, cc);
            }
        }

        File scalerOutput = new File(opt.getOutPath(),
                String.format("%s-ScalerMethodContext-TST%d%s.facts",
                        opt.getApp(), scaler.getTST(),
                        opt.getAnalysis().isEmpty() ? "" : "-" + opt.getAnalysis()));
        System.out.printf("Writing Scaler method context sensitivities to %s...\n",
                scalerOutput.getPath());
        writeScalerResults(scalerResults, scalerOutput);
    }

    public static void outputMethodContext(PointsToAnalysis pta,
                                           ContextComputer cc,
                                           Scaler scaler) {
        System.out.println("Method context, analysis: " + cc.getAnalysisName());
        pta.reachableMethods().stream()
                .filter(Method::isInstance)
                .sorted((m1, m2) -> cc.contextNumberOf(m2) - cc.contextNumberOf(m1))
                .forEach(m -> {
                    System.out.printf("%s\t%d\tcontexts\t%d ",
                            m.toString(), cc.contextNumberOf(m),
                            ((long) cc.contextNumberOf(m))
                                    * ((long) scaler.getAccumulativePTSSizeOf(m)));
                    if (Global.isListContext()) {
                        System.out.print(cc.contextNumberOf(m));
                    }
                    System.out.println();
                });
    }

    public static void outputContextByType(PointsToAnalysis pta,
                                           ContextComputer cc) {
        System.out.println("Type context, analysis: " + cc.getAnalysisName());
        Map<Type, List<Method>> group = pta.reachableMethods().stream()
                .filter(Method::isInstance)
                .collect(Collectors.groupingBy(pta::declaringTypeOf));
        Map<Type, Integer> typeContext = new HashMap<>();
        group.forEach((type, methods) -> {
            int contextSum = methods.stream()
                    .mapToInt(cc::contextNumberOf)
                    .sum();
            typeContext.put(type, contextSum);
        });
        typeContext.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEach(e -> System.out.printf("%s: %d contexts\n",
                        e.getKey(), e.getValue()));
    }

    private static void writeScalerResults(
            Map<Method, String> results, File outputFile)
            throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputFile);
        String[] CS = {"context-insensitive", "1-type", "2-type", "2-object"};
        Map<String, Set<Method>> contextMethods = new HashMap<>();
        for (String cs : CS) {
            contextMethods.put(cs, new HashSet<>());
        }
        results.forEach((m, cs) -> contextMethods.get(cs).add(m));
        for (String cs : CS) {
            contextMethods.get(cs)
                    .stream()
                    .sorted(Comparator.comparing(Method::toString))
                    .forEach(method -> {
                        writer.write(method.toString());
                        writer.write(SEP);
                        writer.write(cs);
                        writer.write(EOL);
                    });
        }
        writer.close();
    }
}
