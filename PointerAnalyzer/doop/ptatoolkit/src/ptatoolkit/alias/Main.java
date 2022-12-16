package ptatoolkit.alias;

import ptatoolkit.Global;
import ptatoolkit.Options;
import ptatoolkit.util.PTAUtils;
import ptatoolkit.util.Timer;

import java.text.DecimalFormat;

import static ptatoolkit.util.ANSIColor.BOLD_GREEN;
import static ptatoolkit.util.ANSIColor.BOLD_YELLOW;
import static ptatoolkit.util.ANSIColor.colored;
import static ptatoolkit.util.ANSIColor.printColored;

public class Main {

    private static final DecimalFormat formatter = new DecimalFormat("#,###");

    public static void main(String[] args) {
        Options opt = Options.parse(args);
        run(opt);
    }

    public static void run(Options opt) {
        // System.out.printf("Computing #aliases of %s ...\n", opt.getApp());
        System.out.print("Reading points-to analysis results ... ");
        PointsToAnalysis pta = Timer.executeAndCount(
                "", () -> PTAUtils.readPointsToAnalysis(opt));
        System.out.print("Computing alias pairs ... ");
        if (Global.isDumpAlias()) {
            Timer.executeAndCount("",
                    () -> new AliasComputer(pta, opt).dumpAliases());
        } else {
            int nrAliases = Timer.executeAndCount("",
                    () -> new AliasComputer(pta, opt).computeAliasNumberME());
            printColored(BOLD_GREEN, colored(BOLD_YELLOW,
                    String.format("%-45s", "Aliases (may-alias variable pairs)")),
                    formatter.format(nrAliases));
        }
    }
}
