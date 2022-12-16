#!/usr/bin/env python
import os
import sys
import time
import multiprocessing
import shutil

QUERY={
    # points-to set
    'REACHABLE_VAR':"Stats:Simple:InsensReachableVar",
    'VPT':"Stats:Simple:InsensVarPointsTo",

    # field points-to graph
    'OBJ':"_(obj)<-Stats:Simple:InsensVarPointsTo(obj,_).",
    'OBJ_TYPE':"_[obj]=type<-" +
                "HeapAllocation:Type[obj]=type," +
                "Stats:Simple:InsensVarPointsTo(obj,_).",
    'IFPT':"_(baseobj,field,obj)<-" +
                "InstanceFieldPointsTo(_,obj,field,_,baseobj).",
    'APT':"_(array,obj)<-ArrayIndexPointsTo(_,obj,_,array).",

    # object
    'SPECIAL_OBJECTS':"HeapAllocation:Special",
    'MERGE_OBJECTS':"HeapAllocation:Merge",
    'STRING_CONSTANTS':"StringConstant",
    'REF_STRING_CONSTANTS':"ReflectionStringConstant",

    'OBJECT_IN':"_(obj,inmethod)<-" +
            "AssignHeapAllocation(obj,_,inmethod)," +
            "Reachable(inmethod).",
    'OBJECT_ASSIGN':"_(obj,var)<-" +
            "AssignHeapAllocation(obj,var,inmethod)," +
            "Reachable(inmethod).",
    'REF_OBJECT':"_(obj,callsite)<-" +
                "ReflectiveHeapAllocation[callsite,_]=obj.",

    'STRING_CONSTANT':"<<string-constant>>",

    # flow graph
    'LOCAL_ASSIGN':"_(to,from)<-OptAssignLocal(to,from);" +
            "Assign(_,_,to,_,from).",
    'INTERPROCEDURAL_ASSIGN':"_(to,from)<-" +
            "OptInterproceduralAssign(_,to,_,from).",
    'THIS_ASSIGN':"_(this,base)<-" +
            "Stats:Simple:InsensCallGraphEdge(invo,callee)," +
            "(VirtualMethodInvocation:Base[invo]=base;" +
            "SpecialMethodInvocation:Base[invo]=base)," +
            "ThisVar[callee]=this.",
    'INSTANCE_LOAD':"_(to,base,field)<-" +
            "LoadHeapInstanceField(_,to,field,_,base).",
    'ARRAY_LOAD':"_(to,array)<-LoadHeapArrayIndex(_,to,_,array).",
    'INSTANCE_STORE':"_(base,field,from)<-" +
            "StoreHeapInstanceField(field,_,base,_,from).",
    'ARRAY_STORE':"_(array,from)<-StoreHeapArrayIndex(_,array,_,from).",
    'INSTANCE_LOAD_FROM_TO':"_(from,to)<-" +
            "ReachableLoadInstanceFieldBase(from)," +
            "OptLoadInstanceField(to,_,from).",
    'ARRAY_LOAD_FROM_TO':"_(from,to)<-" +
            "ReachableLoadArrayIndexBase(from)," +
            "OptLoadArrayIndex(to,from).",
    # currently not considering special calls
    'CALL_RETURN_TO':"_(recv,to)<-" +
            "VirtualMethodInvocation:Base[invo]=recv," +
            "VirtualMethodInvocation:In(invo,inmethod)," +
            "AssignReturnValue[invo]=to," +
            "Reachable(inmethod).",


    # call graph edges
    'REGULARCALL':"Stats:Simple:InsensCallGraphEdge",
    'REFCALL':"_(from,to)<-ReflectiveCallGraphEdge(_,from,_,to).",
    'NATIVECALL':"_(from,to)<-NativeCallGraphEdge(_,from,_,to).",
    'CALL_EDGE':"Stats:Simple:WholeInsensCallGraphEdge",
    'CALLER_CALLEE':"_(caller,callee)<-" +
            "(Stats:Simple:InsensCallGraphEdge(callsite,callee);" +
            "ReflectiveCallGraphEdge(_,callsite,_,callee))," +
            "(SpecialMethodInvocation:In(callsite,caller);" +
            "VirtualMethodInvocation:In(callsite,caller);" +
            "StaticMethodInvocation:In(callsite,caller)).",
    'MAINMETHOD':"MainMethodDeclaration",
    'REACHABLE':"Reachable",
    'IMPLICITREACHABLE':"ImplicitReachable",

    # instance field store
    'INSTANCE_STORE_IN':"_(obj,inmethod)<-" +
            "ReachableStoreInstanceFieldBase(base)," +
            "VarPointsTo(_,obj,_,base)," +
            "Var:DeclaringMethod(base,inmethod).",
    'ARRAY_STORE_IN':"_(array,inmethod)<-" +
            "ReachableStoreArrayIndexBase(base)," +
            "VarPointsTo(_,array,_,base)" +
            "Var:DeclaringMethod(base,inmethod).",

    # call site
    'INST_CALL_RECV':"_(callsite,recv)<-" +
            "Stats:Simple:InsensCallGraphEdge(callsite,_)," +
            "(VirtualMethodInvocation:Base[callsite]=recv;" +
            "SpecialMethodInvocation:Base[callsite]=recv).",
    'CALLSITE_ARGS':"_(callsite,i,arg)<-" +
            "Stats:Simple:InsensCallGraphEdge(callsite,_)," +
            "ActualParam[i,callsite]=arg.",
    'CALLSITE_RETURN':"_(callsite,to)<-" +
            "Stats:Simple:WholeInsensCallGraphEdge(callsite,_)," +
            "AssignReturnValue[callsite]=to.",
    'CALLSITE_IN':"MethodInvocation:In",

    # method
    'THIS_VAR':"_(mtd,this)<-Reachable(mtd),ThisVar[mtd]=this.",
    'PARAMS':"_(mtd,param)<-Reachable(mtd),FormalParam[_,mtd]=param.",
    'INDEXED_PARAMS':"_(mtd,i,param)<-" +
            "Reachable(mtd),FormalParam[i,mtd]=param.",
    'RET_VARS':"_(mtd,ret)<-Reachable(mtd),ReturnVar(ret,mtd).",
    'OBJFINAL':"ObjectSupportsFinalize",
    'VAR_IN':"_(var,inmethod)<-Var:DeclaringMethod(var,inmethod)," +
            "Reachable(inmethod).",
    'METHOD_MODIFIER':"_(mtd,mod)<-" +
            "Reachable(mtd),MethodModifier(mod,mtd).",
    'METHOD_INSTRUCTION_NUMBER':"Method:InstructionNumber",
    # only instance methods have this variables
    'INST_METHODS':"_(mtd)<-Reachable(mtd),ThisVar[mtd]=_.",
    'APP_METHODS':"Stats:Simple:InsensReachableApplicationMethod",
    'CS_METHODS':"ContextSensitiveMethod",
    'COLLECTION_METHODS':"CollectionMethod",

    # type
    'APPLICATION_CLASS':"ApplicationClass",
    'DIRECT_SUPER_TYPE':"DirectSuperclass",
    'DECLARING_CLASS_ALLOCATION':"DeclaringClassAllocation",
    'VAR_TYPE':"_(var,type)<-" +
            "Var:Type[var]=type," +
            "Var:DeclaringMethod(var,inmethod)," +
            "Reachable(inmethod).",
    'ASSIGN_COMPATIBLE':"AssignCompatible",

    # client
    'MAY_FAIL_CAST':"Stats:Simple:PotentiallyFailingCast",
    'POLY_CALL':"Stats:Simple:PolymorphicCallSite",
    'INSENS_CALL_EDGE':"Stats:Simple:InsensCallGraphEdge",
}

REQUIRED_QUERIES={
    'collection': [ 'COLLECTION_METHODS' ],
    'scaler': [
        'CALL_EDGE', 'CALLSITE_IN', 'DECLARING_CLASS_ALLOCATION',
        'OBJECT_IN', 'SPECIAL_OBJECTS', 'THIS_VAR',  'VAR_IN', 'VPT',
        'INST_METHODS', 'CS_METHODS',
    ],
    'zipper': [
        'LOCAL_ASSIGN', 'INTERPROCEDURAL_ASSIGN',
        'INSTANCE_LOAD', 'ARRAY_LOAD', 'INSTANCE_STORE', 'ARRAY_STORE',
        'INST_METHODS', 'METHOD_MODIFIER', 'APP_METHODS', 'CS_METHODS',
        'CALLSITE_IN', 'CALL_RETURN_TO', 'CALL_EDGE',
        'VPT', 'VAR_IN', 'THIS_VAR', 'PARAMS', 'RET_VARS', 'INST_CALL_RECV',
        'SPECIAL_OBJECTS', 'OBJECT_ASSIGN', 'OBJ_TYPE', 'OBJECT_IN',
        'DIRECT_SUPER_TYPE', 'VAR_TYPE', 'ASSIGN_COMPATIBLE',
    ],
    'baton': [ 'VPT', ],
    'baton-all-cs': [ 'VPT', 'REACHABLE', 'CS_METHODS' ],
    'alias': [
        'VPT', 'OBJ_TYPE', 'MERGE_OBJECTS',
        'LOCAL_ASSIGN', 'INTERPROCEDURAL_ASSIGN', 'THIS_ASSIGN',
    ],
    'intersect': [
        'VPT', 'REACHABLE_VAR',
        'MAY_FAIL_CAST', 'POLY_CALL',
        'REACHABLE', 'INSENS_CALL_EDGE',
     ],
}

def isValid(file_path):
    # heuristic: if the #line of the fact file is less than 5,
    # then it is probably invalid and requires to be regenerated
    f = open(file_path)
    i = 0
    for line in f:
        i += 1
        if i >= 5:
            valid = True
            break
    else:
        valid = False
    f.close()
    return valid

APP = None
DB_DIR = None
DUMP_DIR = None
POST_PROCESS = False
OVERWRITE = False

def dumpQuery(query):
    output = os.path.join(DUMP_DIR, '%s.%s' % (APP, query))
    if not os.path.exists(output) or not isValid(output) or OVERWRITE:
        cmd = "bloxbatch -db %s -query '%s' > %s" % (DB_DIR, QUERY[query], output)
        # print cmd
        os.system(cmd)
        if POST_PROCESS:
            postProcess(output)

def postProcess(output):
    temp = output + '.tmp'
    with open(output) as f, open(temp, 'w') as temp_file:
        for line in f:
            temp_file.write(line.replace('  ', '').replace(', ', '\t'))
    shutil.move(temp, output)

def dumpRequiredDoopResults(app, analysis, db_dir, dump_dir,
 post_process=False, overwrite=False):
    # set up global variables for dumpQuery()
    global APP, DB_DIR, DUMP_DIR, POST_PROCESS, OVERWRITE
    APP, DB_DIR, DUMP_DIR, POST_PROCESS, OVERWRITE\
     = app, db_dir, dump_dir, post_process, overwrite
    start_time = time.time()
    if not os.path.isdir(dump_dir):
        os.makedirs(dump_dir)
    print 'Dumping doop analysis results for %s ...' % app
    queries = REQUIRED_QUERIES[analysis]
    n = min(len(queries), multiprocessing.cpu_count())
    pool = multiprocessing.Pool(n)
    # dump queries with multiprocesses
    pool.map(dumpQuery, queries)
    pool.close()
    pool.join()
    end_time = time.time()
    #print 'Elapsed time: %ds' % (end_time - start_time)


if __name__ == '__main__':
    dumpRequiredDoopResults('luindex', 'baton', 'results/context-insensitive/jre1.6/luindex.jar', 'results')
