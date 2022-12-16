#!/usr/bin/env python
import sys

import common
common.PRE_ANALYSIS = 'ci'

from common import *
import alias
# alias.ALIAS = False

CONFIG = [
    ('collection', 'o1'),
    ('zipper', 'o1'),
    ('scaler', 'o1'),
]

def isConfig(s):
    return ',' in s and '-' in s

def parseConfig(s):
    config = []
    for p in s.split(','):
        [a, o] = p.split('-')
        config.append((a, o))
    return config

def runBaton(app, config):
    def analyze(a, p, o):
        if a == 'collection':
            analyzeCollection(p, o)
        elif a == 'zipper':
            analyzeZipper(p, o)
        elif a == 'scaler':
            analyzeScaler(p, o)

    def analyzeCollection(p, o):
        print 'Analyzing %s%s%s, %sbaton-relay pass %d%s (colletion) ...' %\
         (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, p, Colors.ENDC)
        args = ['-csmethod', collection_file]
        processPTAArgs(p, args)
        runPTA(app, '3obj2h', args)
        alias.computeAlias(app, 'baton-relay%s-p%d' % (o, p))
    
    def analyzeZipper(p, o):
        print 'Analyzing %s%s%s, %sbaton-relay pass %d%s (zipper-e) ...' %\
         (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, p, Colors.ENDC)
        if o == 'o1':
            cs_file = processZipper(app, p, zipper_file, cs_map)
            args = ['-specifiedcs', cs_file]
            analysis = 'specified3'
        else:
            cs_file = zipper_file
            args = ['-csmethod', cs_file]
            analysis = '2obj1h'
        processPTAArgs(p, args)
        runPTA(app, analysis, args)
        alias.computeAlias(app, 'baton-relay%s-p%d' % (o, p))
    
    def analyzeScaler(p, o):
        print 'Analyzing %s%s%s, %sbaton-relay pass %d%s (scaler) ...' %\
         (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, p, Colors.ENDC)
        if o == 'o1':
            cs_file = processScaler(app, p, scaler_file, cs_map)
        else:
            cs_file = scaler_file
        args = ['-specifiedcs', cs_file]
        processPTAArgs(p, args)
        runPTA(app, 'specified3', args)
        alias.computeAlias(app, 'baton-relay%s-p%d' % (o, p))

    def processPTAArgs(p, args):
        if p > 1:
            dumpRequiredDoopResults(app, 'baton', getPTADB(app, 'last-analysis'),
             TOOLKIT_CACHE, True, True)
            vpt = os.path.join(TOOLKIT_CACHE, app + '.VPT')
            args += ['-precisevpt', vpt]

    print 'Analyze %s%s%s by %sbaton-relay%s ...' %\
     (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, Colors.ENDC)
    print 'Obtaining results of selective approaches ...'
    collection_file = getCollection(app)
    zipper_file = runZipper(app, True)
    scaler_file = runScaler(app)
    cs_map = getMostPreciseCS(app, collection_file, zipper_file, scaler_file)
    
    p = 1
    for (a, o) in config:
        analyze(a, p, o)
        p += 1

def processZipper(app, n, zipper_file, cs_map):
    zippercs = set()
    with open(zipper_file) as f:
        for line in f:
            zippercs.add(line.strip())
    resultcs = {}
    for m, cs in cs_map.items():
        if m in zippercs:
            resultcs[m] = cs
        else:
            resultcs[m] = 'context-insensitive'
    tmp = os.path.join(DOOP_HOME, 'tmp', app + '-p%d.SPECIFIEDCS' % n)
    writeCSFile(tmp, resultcs)
    return tmp

def processScaler(app, n, scaler_file, cs_map):
    scalercs = {}
    with open(scaler_file) as f:
        for line in f:
            [m, cs] = line.strip().split(SPLIT)
            scalercs[m] = cs
    resultcs = {}
    for m, cs in scalercs.items():
        if cs == 'context-insensitive':
            resultcs[m] = cs
        else:
            resultcs[m] = cs_map[m]
    tmp = os.path.join(DOOP_HOME, 'tmp', app + '-p%d.SPECIFIEDCS' % n)
    writeCSFile(tmp, resultcs)
    return tmp

SPLIT = '\t'
def getMostPreciseCS(app, collection_file, zipper_file, scaler_file):
    collection = set()
    with open(collection_file) as f:
        for line in f:
            collection.add(line.strip())
    zippercs = set()
    with open(zipper_file) as f:
        for line in f:
            zippercs.add(line.strip())
    scalercs = {}
    with open(scaler_file) as f:
        for line in f:
            [m, cs] = line.strip().split(SPLIT)
            scalercs[m] = cs
    resultcs = dict(scalercs)
    for m in zippercs:
        resultcs[m] = '2-object'
    for m in collection:
        resultcs[m] = '3-object'
    return resultcs

def writeCSFile(file_path, cs_map):
    with open(file_path, 'w') as f:
        for (m, cs) in sorted(cs_map.items(), key=lambda x: (x[1], x[0])):
            f.write('%s%s%s\n' % (m, SPLIT, cs))

if __name__ == '__main__':
    apps = common.processArgs(sys.argv[1:])
    if isConfig(apps[0]):
        config = parseConfig(apps[0])
        apps = apps[1:]
    else:
        config = CONFIG
    common.setSuffix('baton-relay')
    for app in apps:
        runBaton(app, config)
