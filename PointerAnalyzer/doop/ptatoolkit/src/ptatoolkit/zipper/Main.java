package ptatoolkit.zipper;

import ptatoolkit.Global;
import ptatoolkit.Options;
import ptatoolkit.pta.basic.BasicElement;
import ptatoolkit.pta.basic.Method;
import ptatoolkit.util.PTAUtils;
import ptatoolkit.util.Timer;
import ptatoolkit.zipper.analysis.Zipper;
import ptatoolkit.zipper.pta.PointsToAnalysis;
import ptatoolkit.zipper.pta.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Set;

import static ptatoolkit.util.ANSIColor.BOLD_GREEN;
import static ptatoolkit.util.ANSIColor.BOLD_YELLOW;
import static ptatoolkit.util.ANSIColor.colored;
import static ptatoolkit.util.ANSIColor.printColored;

public class Main {

    private static final char EOL = '\n';

    public static void main(String[] args) throws IOException {
        Options opt = Options.parse(args);
        run(opt);
    }

    public static void run(Options opt) throws IOException {
        System.out.printf("Analyze %s ...\n", opt.getApp());
        System.out.print("Reading points-to analysis results ... ");
        PointsToAnalysis pta = Timer.executeAndCount(
                "", () -> PTAUtils.readPointsToAnalysis(opt));
        Timer zipperTimer = new Timer("Zipper Timer");
        printColored(BOLD_YELLOW, "Zipper starts ...");
        String flows = Global.getFlow() != null ? Global.getFlow() : "Direct+Wrapped+Unwrapped";
        printColored(BOLD_GREEN, "Precision loss patterns: ", flows);
        Utils.outputNumberOfClasses(pta);
        Utils.outputNumberOfMethods(pta);
        System.out.println();

        zipperTimer.start();
        Zipper zipper = new Zipper(pta);
        Set<Method> pcm = zipper.analyze();
        zipperTimer.stop();
        printColored(BOLD_GREEN, colored(BOLD_YELLOW, "Zipper finishes, analysis time: "),
                String.format("%.2fs", zipperTimer.inSecond()));

//        if (Global.isDebug()) {
//            ObjectFlowGraph ofg = Zipper.buildObjectFlowGraph(pta);
//            System.out.println("Dumping object flow graph ...");
//            String output = Paths.get(opt.getOutPath(), opt.getApp() + "-FG.dot")
//                    .toString();
//            Dumper.dumpObjectFlowGraph(ofg, output);
//        }

        File outDir = new File(opt.getOutPath());
        if (!outDir.exists()) {
            Files.createDirectories(outDir.toPath());
        }
        File zipperPCMOutput = new File(opt.getOutPath(),
                String.format("%s-ZipperPrecisionCriticalMethod%s%s.facts",
                        opt.getApp(), opt.getAnalysis(),
                        Global.getFlow() == null ? "" : "-" + Global.getFlow()));
        System.out.printf("Writing Zipper precision-critical methods to %s ...\n",
                zipperPCMOutput.getPath());
        System.out.println();
        writeZipperResults(pcm, zipperPCMOutput);

    }

    public static void writeZipperResults(
            Set<? extends BasicElement> results, File outputFile)
            throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputFile);
        results.stream()
                .sorted(Comparator.comparing(BasicElement::toString))
                .forEach(method -> {
                    writer.write(method.toString());
                    writer.write(EOL);
                });
        writer.close();
    }
}
