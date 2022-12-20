#!/usr/bin/env python
import sys
import os
import shutil
from collections import OrderedDict

ARTIFACT_ROOT = sys.path[0] # sys.path[0] is the directory containing this script
DOOP_HOME = os.path.join(ARTIFACT_ROOT, 'doop')
PTATOOLKIT_HOME = os.path.join(DOOP_HOME, 'ptatoolkit')

RUN_TABLE2 = False
TABLE2 = OrderedDict()
TABLE2['eclipse'] = { 't': 0.05, 'tst': 60000000 }
TABLE2['briss'] = { 't': 0.12, 'tst': 60000000 }
TABLE2['pmd'] = { 't': 0.05, 'tst': 60000000 }
TABLE2['jedit'] = { 't': 0.2, 'tst': 60000000 }
TABLE2['h2'] = { 't': 0.05, 'tst': 30000000, 'relay': ['o1', 'o1', 'o2'] }
TABLE2['gruntspud'] = { 't': 0.16, 'tst': 60000000, 'relay': ['o1', 'o2', 'o2'] }

FEATURELEN = 20



def runAnalysis(script, cmd):
    cwd = os.getcwd()
    os.chdir(DOOP_HOME)
    os.system('python %s %s' % (script, cmd))
    os.chdir(cwd)

def runPTA(analysis, app):
    runAnalysis('pta.py', '%s %s' % (analysis, app))

def runCollection(app):
    runAnalysis('collection.py', app)

def runZipper(app):
    cmd = ''
    if RUN_TABLE2 and app in TABLE2:
        cmd += ' -t ' + str(TABLE2[app]['t'])
    cmd += ' %s' % app
    runAnalysis('zipper.py', cmd)

def runScaler(app):
    cmd = ''
    if RUN_TABLE2 and app in TABLE2:
        cmd += ' -tst ' + str(TABLE2[app]['tst'])
    cmd += ' %s' % app
    runAnalysis('scaler.py', cmd)

def runBatonUnity(app):
    runAnalysis('baton-unity.py', app)

def runBatonRelay(app):
    cmd = ''
    if app in TABLE2:
        cmd += '-t %s' % str(TABLE2[app]['t'])
        cmd += ' -tst %s' % str(TABLE2[app]['tst'])
        if 'relay' in TABLE2[app]:
            relay = TABLE2[app]['relay']
            cmd += ' collection-%s,zipper-%s,scaler-%s' %\
             (relay[0], relay[1], relay[2])
    cmd += ' %s' % app
    runAnalysis('baton-relay.py', cmd)

def runTable1(app):
    runPTA('ci', app)
    #runCollection(app)
    #runZipper(app)
    #runScaler(app)
    #runBatonUnity(app)

def runTable2(app):
    global RUN_TABLE2
    RUN_TABLE2 = True
    runPTA('ci', app)
    runZipper(app)
    runScaler(app)
    runBatonRelay(app)

TABLE1 = [
    'hsqldb', 'galleon', 'jedit', 'soot', 'gruntspud', 'heritrix',
    'pmd', 'jython', 'jasperreports', 'eclipse', 'briss', 'columba','luindex',
    'lusearch','antlr','pmdm','eclipsem','fop','xalan','hsqldb'
]
def runAll():
    for app in TABLE1:
        runTable1(app)
    for app in TABLE2:
        runTable2(app)

PTA = [ 'ci', '2obj1h', '3obj2h', '1callT', '1callTS','GNN']

RUNNERS = {
    'collection': runCollection,
    'zipper-e': runZipper,
    'scaler': runScaler,
    'baton-unity': runBatonUnity,
    'baton-relay': runBatonRelay,
    '-table1': runTable1,
    '-table2': runTable2,
}

def clean():
    def remove(path):
        if os.path.islink(path):
            os.unlink(path)
        else:
            shutil.rmtree(path, ignore_errors=True)
    def clearDir(path):
        os.system('rm -rf %s/*' % path)
    
    # remove cache
    remove(os.path.join(DOOP_HOME, 'cache/analysis'))
    remove(os.path.join(PTATOOLKIT_HOME, 'cache'))
    # remove analysis results
    for d in ['tmp', 'results']:
        clearDir(os.path.join(DOOP_HOME, d))
    remove(os.path.join(DOOP_HOME, 'last-analysis'))
    remove(os.path.join(PTATOOLKIT_HOME, 'out'))


def extract_graph(pgm):
    path = "{}/{}".format(ARTIFACT_ROOT, pgm)
    if not os.path.exists(path):
      cmd = "mkdir {}".format(pgm)
      os.system(cmd)
    cmd = "bloxbatch -db doop/last-analysis -query Node | sort > {}/Nodes.facts".format(pgm) 
    os.system(cmd)
    cmd = "bloxbatch -db doop/last-analysis -query Edge | sort > {}/Edges.facts".format(pgm) 
    os.system(cmd)
    for i in range(FEATURELEN):
      cmd = "bloxbatch -db doop/last-analysis -query RMFeature{} | sort > {}/MFeature{}.facts".format(i,pgm,i) 
      os.system(cmd)

def input_classification():
    my_list = []
    with open("classification.facts") as file:
        for line in file.readlines():
            method = line.strip(' ')
            my_list.append(method) 
    f = open("{}/MyMethod.facts".format(DOOP_HOME),'w')
    for _, method in enumerate(my_list):
        f.write(method)
    f.close()

def run(args):
    if args[0] == 'GNN':
        input_classification()

    global RUN_TABLE2
    if args[0] == '-clean':
        clean()
    elif args[0] == '-all':
        runAll()
    elif args[0] in PTA:
        runPTA(args[0], args[1])
    elif args[0] in RUNNERS:
        runner = RUNNERS[args[0]]
        if args[0] in ['zipper-e', 'scaler'] and args[1] == '-table2':
            RUN_TABLE2 = True
            runner(args[2])
        else:
            runner(args[1])
    if args[0] == 'ci':
        extract_graph(args[1])
    

if __name__ == '__main__':
    run(sys.argv[1:])



