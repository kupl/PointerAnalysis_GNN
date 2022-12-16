import os
import sys
import multiprocessing
import json

DOOP_HOME = os.getcwd()
sys.path.append(os.path.join(DOOP_HOME, 'scripts'))
from dump_doop import dumpRequiredDoopResults
DUMP_OVERWRITE=True

TOOLKIT_HOME = os.path.join(DOOP_HOME, 'ptatoolkit')
TOOLKIT_CP = ':'.join([
    os.path.join(TOOLKIT_HOME, 'build',),
    os.path.join(TOOLKIT_HOME, 'lib', 'guava-23.0.jar'),
    os.path.join(TOOLKIT_HOME, 'lib', 'sootclasses-2.5.0.jar'),
])
TOOLKIT_HEAP_SIZE = '96g'
TOOLKIT_STACK_SIZE = '4m'
TOOLKIT_CACHE = os.path.join(TOOLKIT_HOME, 'cache')
TOOLKIT_OUT = os.path.join(TOOLKIT_HOME, 'out')
TOOLKIT_THREAD = multiprocessing.cpu_count()

ZIPPER_MAIN = 'ptatoolkit.zipper.Main'
ZIPPER_PTA = 'ptatoolkit.zipper.doop.DoopPointsToAnalysis'
ZIPPER_RERUN = True
ZIPPER_EXPRESS_THRESHOLD = 0.05
APP_THRESHOLD = {
    'soot': 0.005,
}
SCALER_MAIN = 'ptatoolkit.scaler.Main'
SCALER_PTA = 'ptatoolkit.scaler.doop.DoopPointsToAnalysis'
SCALER_TST = 30000000
SCALER_RERUN = True

# to be set
PRE_ANALYSIS = None
ANALYSIS_SUFFIX = ''

ANALYSIS = {
    'ci':'context-insensitive',
    '1callT':'1-call-site-sensitive+heap+SL',
    '1callTS':'1-tunneled-call-site-sensitive+heap+selective',
    'GNN':'1-tunneled-call-site-sensitive+heap+selective+GNN',
    '1type':'1-type-sensitive',
    '1type1h':'1-type-sensitive+heap',
    'introA-2obj1h':'refA-2-object-sensitive+heap',
    'introB-2obj1h':'refB-2-object-sensitive+heap',
    '2obj1h':'2-object-sensitive+heap',
    '3obj2h':'3-object-sensitive+2-heap',
    'introA-2type1h':'refA-2-type-sensitive+heap',
    'introB-2type1h':'refB-2-type-sensitive+heap',
    '2type1h':'2-type-sensitive+heap',
    '2cs1h':'2-call-site-sensitive+heap',
    'introA-2cs1h':'refA-2-call-site-sensitive+heap',
    'introB-2cs1h':'refB-2-call-site-sensitive+heap',
    's2obj1h':'selective-2-object-sensitive+heap',
    'specified':'specified-sensitive+heap',
    'specified3': 'specified-3-sensitive+2-heap',
}

class Colors:
    CYAN = '\033[96m'
    HEADER = '\033[95m'
    BLUE = '\033[94m'
    YELLOW = '\033[93m'
    GREEN = '\033[92m'
    RED = '\033[91m'
    ENDC = '\033[0m' # end color
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

DACAPO = ['luindex', 'hsqldb', 'jython', 'xalan', 'bloat', 'chart', 'fop', 'antlr', 'eclipsem', 'lusearch', 'pmdm']

with open('app-info.json') as f:
    APP_INFO = json.load(f)

def getAppInfo(app, key):
    if APP_INFO.has_key(app):
        info = APP_INFO[app]
        if info.has_key(key):
            return info[key]
    return None

def getCP(app):
    cp = getAppInfo(app, 'cp')
    if cp:
        return cp
    elif app in DACAPO:
        return 'jars/dacapo/%s.jar' % app
    else:
        raise Exception('Unknown application: %s' % app)

def getPTAOutput(app, analysis, opts):
    baton = ''
    for i in range(len(opts)):
        if '-batonround' in opts[i]:
            baton = '-r' + opts[i+1]
    return '%s-%s%s%s.output' % (app, analysis, baton, ANALYSIS_SUFFIX)

def getPTADB(app, analysis):
    if analysis == 'last-analysis':
        db = os.path.join(DOOP_HOME, analysis)
    elif app in DACAPO:
        db = os.path.join(DOOP_HOME, 'results/%s/jre1.6/%s.jar'
         % (ANALYSIS[analysis], app))
    else:
        jar = os.path.basename(APP_INFO[app]['cp'])
        db = os.path.join(DOOP_HOME, 'results/%s/jre1.6-phantom/%s'
         % (ANALYSIS[analysis], jar))
    return db

def runPTA(app, analysis, opts=[]):
    print("AA")
    cmd = './run -jre1.6 --cache --color '
    if app not in DACAPO:
        cmd += '--allow-phantom '
    # setup application-specific config
    tamiflex = getAppInfo(app, 'tamiflex')
    if tamiflex:
        cmd += '-tamiflex %s ' % tamiflex
    main = getAppInfo(app, 'main')
    if main:
        cmd += '-main %s ' % main
    inputjars = getAppInfo(app, 'input')
    if inputjars:
        for jar in inputjars:
            cmd += '-input %s ' % jar
    libjars = getAppInfo(app, 'lib')
    if libjars:
        for jar in libjars:
            cmd += '-l %s ' % jar
    for opt in opts:
        cmd += opt + ' '
    
    cmd += '%s %s ' % (ANALYSIS[analysis], getCP(app))
    cmd += '| tee results/%s' % getPTAOutput(app, analysis, opts)
    print cmd
    os.system(cmd)

def getCollection(app):
    db = getPTADB(app, PRE_ANALYSIS)
    if not os.path.isdir(db):
        runPTA(app, PRE_ANALYSIS)
    dumpRequiredDoopResults(app, 'collection', db,
     TOOLKIT_CACHE, post_process=True, overwrite=DUMP_OVERWRITE)
    return os.path.join(TOOLKIT_CACHE, app + '.COLLECTION_METHODS')

def runZipper(app, express):
    def getZipperCommand(app, db, threshold):
        cmd = 'java -Xmx%s -Xss%s ' % (TOOLKIT_HEAP_SIZE, TOOLKIT_STACK_SIZE)
        cmd += '-cp %s %s ' % (TOOLKIT_CP, ZIPPER_MAIN)
        cmd += '-pta %s ' % ZIPPER_PTA
        cmd += '-db %s ' % db
        cmd += '-cache %s -out %s ' % (TOOLKIT_CACHE, TOOLKIT_OUT)
        suffix = '-zipper'
        if express:
            cmd += '-express %s ' % str(threshold)
            suffix += '-e-%s' % str(threshold)
        if TOOLKIT_THREAD > 1:
            cmd += '-thread %d ' % TOOLKIT_THREAD
        cmd += '-app %s -a %s ' % (app, suffix)
        cmd += '| tee %s/%s%s.output' % (TOOLKIT_OUT, app, suffix)
        zipper_file = os.path.join(TOOLKIT_OUT,
            '%s-ZipperPrecisionCriticalMethod%s.facts' % (app, suffix))
        return cmd, zipper_file

    threshold = ZIPPER_EXPRESS_THRESHOLD
    if app in APP_THRESHOLD:
        threshold = APP_THRESHOLD[app]
    db = getPTADB(app, PRE_ANALYSIS)
    if not os.path.isdir(db):
        runPTA(app, PRE_ANALYSIS)
    dumpRequiredDoopResults(app, 'zipper', db,
     TOOLKIT_CACHE, overwrite=DUMP_OVERWRITE)
    cwd = os.getcwd()
    os.chdir(TOOLKIT_HOME)
    zipper_cmd, zipper_file = getZipperCommand(app, db, threshold)
    if not os.path.isdir(TOOLKIT_OUT):
        os.makedirs(TOOLKIT_OUT)
    if not os.path.exists(zipper_file) or ZIPPER_RERUN:
        #print zipper_cmd
        os.system(zipper_cmd)
    os.chdir(cwd)
    return zipper_file

def runScaler(app):
    def getScalerCommand(app, db):
        cmd = 'java -Xmx%s -Xss%s ' % (TOOLKIT_HEAP_SIZE, TOOLKIT_STACK_SIZE)
        cmd += '-cp %s %s ' % (TOOLKIT_CP, SCALER_MAIN)
        cmd += '-pta %s ' % SCALER_PTA
        cmd += '-db %s ' % db
        cmd += '-cache %s -out %s ' % (TOOLKIT_CACHE, TOOLKIT_OUT)
        cmd += '-tst %d ' % SCALER_TST
        cmd += '-app %s ' % app
        suffix = '-TST%d' % SCALER_TST
        cmd += '| tee %s/%s%s.output' % (TOOLKIT_OUT, app, suffix)
        scaler_file = os.path.join(TOOLKIT_OUT,
        '%s-ScalerMethodContext%s.facts' % (app, suffix))
        return cmd, scaler_file
    
    db = getPTADB(app, PRE_ANALYSIS)
    if not os.path.isdir(db):
        runPTA(app, PRE_ANALYSIS)
    dumpRequiredDoopResults(app, 'scaler', db,
     TOOLKIT_CACHE, overwrite=DUMP_OVERWRITE)
    cwd = os.getcwd()
    os.chdir(TOOLKIT_HOME)
    scaler_cmd, scaler_file = getScalerCommand(app, db)
    if not os.path.isdir(TOOLKIT_OUT):
        os.makedirs(TOOLKIT_OUT)
    if not os.path.exists(scaler_file) or SCALER_RERUN:
        #print scaler_cmd
        os.system(scaler_cmd)
    os.chdir(cwd)
    return scaler_file

def processArgs(args):
    apps = []
    i = 0
    while i < len(args):
        if args[i] == '-t':
            i += 1
            global ZIPPER_EXPRESS_THRESHOLD
            ZIPPER_EXPRESS_THRESHOLD = float(args[i])
        elif args[i] == '-tst':
            i += 1
            global SCALER_TST
            SCALER_TST = int(args[i])
        else:
            apps.append(args[i])
        i += 1
    return apps

def setSuffix(suffix):
    global ANALYSIS_SUFFIX
    if 'baton' in suffix:
        zippere = format(ZIPPER_EXPRESS_THRESHOLD, '.1%').replace('.0', '')
        scaler = '%dM' % (SCALER_TST / 1000000)
        ANALYSIS_SUFFIX = '-%s-%s-%s' % (suffix, zippere, scaler)
    else:
        ANALYSIS_SUFFIX = '-%s' % suffix
