#!/usr/bin/env python
import sys
import shutil

import common
common.PRE_ANALYSIS = 'ci'

from common import *

ALIAS_MAIN = 'ptatoolkit.alias.Main'
ALIAS_PTA = 'ptatoolkit.alias.DoopPointsToAnalysis'
ALIAS = True # whether compute aliases

def computeAlias(app, analysis, dump=False, redump=True):
    def getAliasCommand(app):
        cmd = 'java -Xmx%s -Xss%s ' % (TOOLKIT_HEAP_SIZE, TOOLKIT_STACK_SIZE)
        cmd += '-cp %s %s ' % (TOOLKIT_CP, ALIAS_MAIN)
        cmd += '-pta %s ' % ALIAS_PTA
        cmd += '-db %s ' % db
        cmd += '-cache %s -out %s ' % (TOOLKIT_CACHE, TOOLKIT_OUT)
        cmd += '-app %s ' % app
        if TOOLKIT_THREAD > 1:
            cmd += '-thread %d ' % TOOLKIT_THREAD
        if dump:
            cmd += '-dump-alias '
        return cmd

    if not ALIAS:
        return
    print 'Computing %sAliases%s of %s for %s ...' %\
      (Colors.YELLOW, Colors.ENDC,
       Colors.CYAN + app + Colors.ENDC,
       Colors.CYAN + analysis + Colors.ENDC)
    db = getPTADB(app, 'last-analysis')
    if redump:
        dumpRequiredDoopResults(app, 'alias', db,
         TOOLKIT_CACHE, overwrite=DUMP_OVERWRITE)
    cmd = getAliasCommand(app)
    #print cmd
    os.system(cmd)
    print

if __name__ == '__main__':
    analysis = sys.argv[1]
    for app in sys.argv[2:]:
        computeAlias(analysis, app)
