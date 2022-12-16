package ptatoolkit;

public class Options {

    private String pta;
    private String dbPath;
    private String cachePath;
    private String app;
    private String outPath;

    private String analysis = "";
    private boolean isDebug = false;
    private String dbPath2;
    private String app2;

    public static Options parse(String[] args) {
        Options opt = new Options();
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-pta":
                    i = shift(args, i);
                    opt.setPTA(args[i]);
                    break;
                case "-db":
                    i = shift(args, i);
                    opt.setDbPath(args[i]);
                    break;
                case "-cache":
                    i = shift(args, i);
                    opt.setCachePath(args[i]);
                    break;
                case "-app":
                    i = shift(args, i);
                    opt.setApp(args[i]);
                    break;
                case "-out":
                    i = shift(args, i);
                    opt.setOutPath(args[i]);
                    break;
                case "-a":
                    i = shift(args, i);
                    opt.setAnalysis(args[i]);
                    break;
                case "-db2":
                    i = shift(args, i);
                    opt.setDbPath2(args[i]);
                    break;
                case "-app2":
                    i = shift(args, i);
                    opt.setApp2(args[i]);
                    break;
                case "-sep":
                    i = shift(args, i);
                    Global.setSep(args[i]);
                    break;
                case "-debug":
                    opt.setIsDebug(true);
                    Global.setDebug(true);
                    break;
                case "-flow":
                    i = shift(args, i);
                    Global.setFlow(args[i]);
                    switch (args[i]) {
                        case "Direct": {
                            Global.setEnableWrappedFlow(false);
                            Global.setEnableUnwrappedFlow(false);
                        }
                        break;
                        case "Direct+Wrapped": {
                            Global.setEnableUnwrappedFlow(false);
                        }
                        break;
                        case "Direct+Unwrapped": {
                            Global.setEnableWrappedFlow(false);
                        }
                        break;
                        case "Direct+Wrapped+Unwrapped": {
                            Global.setEnableWrappedFlow(true);
                            Global.setEnableUnwrappedFlow(true);
                        }
                        break;
                        default: {
                            throw new Error("Unknown -flow argument: " + args[i]);
                        }
                    }
                    break;
                case "-no-wrapped-flow":
                    Global.setEnableWrappedFlow(false);
                    break;
                case "-no-unwrapped-flow":
                    Global.setEnableUnwrappedFlow(false);
                    break;
                case "-express":
                    Global.setExpress(true);
                    if (i + 1 < args.length) {
                        try {
                            float threshold = Float.parseFloat(args[i + 1]);
                            // Float.parseFloat() succeeds (without exception),
                            // then the next argument is a float, and we take
                            // it as express threshold
                            i = shift(args, i);
                            Global.setExpressThreshold(threshold);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } // Not a float
                    }
                    break;
                case "-thread":
                    i = shift(args, i);
                    Global.setThread(Integer.parseInt(args[i]));
                    break;
                case "-only-jdk":
                    Global.setOnlyJDK(true);
                    break;
                case "-tst":
                    i = shift(args, i);
                    Global.setTST(Integer.parseInt(args[i]));
                    break;
                case "-list-context":
                    Global.setListContext(true);
                    break;
                case "-pcm-file":
                    i = shift(args, i);
                    Global.setPCMFile(args[i]);
                    break;
                case "-non-pcm-file":
                    i = shift(args, i);
                    Global.setNonPCMFile(args[i]);
                    break;
                case "-dump-alias":
                    Global.setDumpAlias(true);
                    break;
                case "-stage":
                    i = shift(args, i);
                    Global.addStage(args[i]);
                    break;
                default:
                    throw new RuntimeException("Unexpected options: " + args[i]);
            }
        }
        return opt;
    }

    private static int shift(String[] args, int index) {
        if (args.length == index + 1) {
            System.err.println("error: option " + args[index]
                    + " requires an argument");
            System.exit(1);
        }

        return index + 1;
    }

    public String getPTA() {
        return pta;
    }

    public void setPTA(String pta) {
        this.pta = pta;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getOutPath() {
        return outPath;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public void setIsDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public String getDbPath2() {
        return dbPath2;
    }

    public void setDbPath2(String dbPath2) {
        this.dbPath2 = dbPath2;
    }

    public String getApp2() {
        return app2;
    }

    public void setApp2(String app2) {
        this.app2 = app2;
    }
}
