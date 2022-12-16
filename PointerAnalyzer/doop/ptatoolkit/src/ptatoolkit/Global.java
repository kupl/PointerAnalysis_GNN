package ptatoolkit;

import java.util.ArrayList;
import java.util.List;

public class Global {

    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        Global.debug = debug;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static final int UNDEFINED = -1;

    public static String sep = ", ";

    public static String getSep() {
        return sep;
    }

    public static void setSep(String sep) {
        Global.sep = sep;
    }

    // Zipper
    private static String flow = null;

    public static String getFlow() {
        return flow;
    }

    public static void setFlow(String flow) {
        Global.flow = flow;
    }

    private static boolean enableWrappedFlow = true;

    public static boolean isEnableWrappedFlow() {
        return enableWrappedFlow;
    }

    public static void setEnableWrappedFlow(boolean enableWrappedFlow) {
        Global.enableWrappedFlow = enableWrappedFlow;
    }

    private static boolean enableUnwrappedFlow = true;

    public static boolean isEnableUnwrappedFlow() {
        return enableUnwrappedFlow;
    }

    public static void setEnableUnwrappedFlow(boolean enableUnwrappedFlow) {
        Global.enableUnwrappedFlow = enableUnwrappedFlow;
    }

    private static boolean isExpress = false;

    public static boolean isExpress() {
        return isExpress;
    }

    public static void setExpress(boolean isExpress) {
        Global.isExpress = isExpress;
    }

    private static float expressThreshold = 0.05f;

    public static float getExpressThreshold() {
        return expressThreshold;
    }

    public static void setExpressThreshold(float expressThreshold) {
        Global.expressThreshold = expressThreshold;
    }

    /** Whether Zipper only outputs the precision-critical methods in JDK. */
    private static boolean isOnlyJDK = false;

    public static boolean isOnlyJDK() {
        return isOnlyJDK;
    }

    public static void setOnlyJDK(boolean isOnlyJDK) {
        Global.isOnlyJDK = isOnlyJDK;
    }

    private static int thread = UNDEFINED;

    public static int getThread() {
        return thread;
    }

    public static void setThread(int thread) {
        Global.thread = thread;
    }

    // Scaler
    private static int tst = UNDEFINED;

    public static int getTST() {
        return tst;
    }

    public static void setTST(int tst) {
        Global.tst = tst;
    }

    private static boolean listContext = false;

    public static boolean isListContext() {
        return listContext;
    }

    public static void setListContext(boolean listContext) {
        Global.listContext = listContext;
    }

    private static String pcmFile = null;

    public static String getPCMFile() {
        return pcmFile;
    }

    public static void setPCMFile(String pcmFile) {
        Global.pcmFile = pcmFile;
    }

    private static String nonPcmFile = null;

    public static String getNonPCMFile() {
        return nonPcmFile;
    }

    public static void setNonPCMFile(String nonPcmFile) {
        Global.nonPcmFile = nonPcmFile;
    }

    // Alias
    private static boolean dumpAlias = false;

    public static boolean isDumpAlias() {
        return dumpAlias;
    }

    public static void setDumpAlias(boolean dumpAlias) {
        Global.dumpAlias = dumpAlias;
    }

    // Intersection
    private static final List<String> stages = new ArrayList<>();

    public static void addStage(String stage) {
        stages.add(stage);
    }

    public static List<String> getStages() {
        return stages;
    }
}
