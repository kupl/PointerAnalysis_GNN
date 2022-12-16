#!/usr/bin/env python
import sys

import common
common.PRE_ANALYSIS = 'ci'
Colors = common.Colors

import alias

def run(app):
    print 'Analyze %s%s%s by %scollection%s ...' %\
     (Colors.CYAN, app, Colors.ENDC, Colors.CYAN, Colors.ENDC)
    collection_file = common.getCollection(app)
    common.runPTA(app, '3obj2h', ['-csmethod', collection_file])
    alias.computeAlias(app, 'collection')

if __name__ == '__main__':
    common.setSuffix('collection')
    for app in sys.argv[1:]:
        run(app)
