#!/usr/bin/env python
import sys

import common
common.PRE_ANALYSIS = 'ci'
Colors = common.Colors

import alias

def run(app):
    print 'Analyze %s%s%s by %szipper-e%s ...' %\
     (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, Colors.ENDC)
    zipper_file = common.runZipper(app, express=True)
    common.runPTA(app, '2obj1h', ['-csmethod', zipper_file])
    alias.computeAlias(app, 'zipper-e')

if __name__ == '__main__':
    apps = common.processArgs(sys.argv[1:])
    common.setSuffix('zipper-e')
    for app in apps:
        run(app)
