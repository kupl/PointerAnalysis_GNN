#!/usr/bin/env python
import sys

from common import Colors, runPTA
import alias

def run(app, analysis):
    print 'Analyze %s%s%s by %s%s%s ...' %\
     (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, analysis, Colors.ENDC)
    runPTA(app, analysis)
    #alias.computeAlias(app, analysis)

if __name__ == '__main__':
    analysis = sys.argv[1]
    for app in sys.argv[2:]:
        run(app, analysis)
