#!/usr/bin/env python
import sys

import common
common.PRE_ANALYSIS = 'ci'
Colors = common.Colors

import alias

def run(app):
    print 'Analyze %s%s%s by %sscaler%s ...' %\
     (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, Colors.ENDC)
    scaler_file = common.runScaler(app)
    common.runPTA(app, 'specified', ['-specifiedcs', scaler_file])
    alias.computeAlias(app, 'scaler')

if __name__ == '__main__':
    apps = common.processArgs(sys.argv[1:])
    common.setSuffix('scaler')
    for app in apps:
        run(app)
