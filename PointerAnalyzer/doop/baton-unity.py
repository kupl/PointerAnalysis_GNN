#!/usr/bin/env python
import sys

import common
common.PRE_ANALYSIS = 'ci'

from common import *
import alias

BATON_CS = 'specified3'

def runBaton(app):
    print 'Analyze %s%s%s by %sbaton-unity%s ...' %\
     (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, Colors.ENDC)
    print 'Obtaining results of selective approaches ...'
    collection_file = getCollection(app)
    zipper_file = runZipper(app, True)
    scaler_file = runScaler(app)
    cs_file = postProcess(app, collection_file, zipper_file, scaler_file)
    runPTA(app, BATON_CS, ['-specifiedcs', cs_file])
    alias.computeAlias(app, 'baton-unity')

def postProcess(app, collection_file, zipper_file, scaler_file):
    collection = set()
    with open(collection_file) as f:
        for line in f:
            collection.add(line.strip())
    zippercs = set()
    with open(zipper_file) as f:
        for line in f:
            zippercs.add(line.strip())
    scalercs = {}
    SPLIT = '\t'
    with open(scaler_file) as f:
        for line in f:
            [m, cs] = line.strip().split(SPLIT)
            scalercs[m] = cs
    resultcs = dict(scalercs)
    for m in zippercs:
        resultcs[m] = '2-object'
    for m in collection:
        resultcs[m] = '3-object'
    tmp = os.path.join(DOOP_HOME, 'tmp', app + '.SPECIFIEDCS')
    with open(tmp, 'w') as f:
        for (m, cs) in sorted(resultcs.items(), key=lambda x: (x[1], x[0])):
            f.write('%s%s%s\n' % (m, SPLIT, cs))
    return tmp

if __name__ == '__main__':
    apps = common.processArgs(sys.argv[1:])
    common.setSuffix('baton-unity')
    for app in apps:
        runBaton(app)
