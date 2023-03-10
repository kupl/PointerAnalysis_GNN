package ptatoolkit.doop;

/**
 * Queries for accessing Doop database
 */
public enum Query {

    // points-to set
    VPT("Stats:Simple:InsensVarPointsTo"),
    REACHABLE_VAR("Stats:Simple:InsensReachableVar"),
    ALIAS("<DUMMY-EXPR>"),

    // field points-to graph
    OBJ("_(obj)<-Stats:Simple:InsensVarPointsTo(obj,_)."),
    OBJ_TYPE("_[obj]=type<-" +
            "HeapAllocation:Type[obj]=type," +
            "Stats:Simple:InsensVarPointsTo(obj,_)."),
    IFPT("_(baseobj,field,obj)<-" +
            "InstanceFieldPointsTo(_,obj,field,_,baseobj)."),
    APT("_(array,obj)<-ArrayIndexPointsTo(_,obj,_,array)."),

    // object
    SPECIAL_OBJECTS("HeapAllocation:Special"),
    MERGE_OBJECTS("HeapAllocation:Merge"),
    STRING_CONSTANTS("StringConstant"),
    REF_STRING_CONSTANTS("ReflectionStringConstant"),

    OBJECT_IN("_(obj,inmethod)<-" +
            "AssignHeapAllocation(obj,_,inmethod)," +
            "Reachable(inmethod)."),
    OBJECT_ASSIGN("_(obj,var)<-" +
            "AssignHeapAllocation(obj,var,inmethod)," +
            "Reachable(inmethod)."),
    REF_OBJECT("_(obj,callsite)<-" +
            "ReflectiveHeapAllocation[callsite,_]=obj."),

    STRING_CONSTANT("<<string-constant>>"),

    // flow graph
    LOCAL_ASSIGN("_(to,from)<-OptAssignLocal(to,from);" +
            "Assign(_,_,to,_,from)."),
    INTERPROCEDURAL_ASSIGN("_(to,from)<-" +
            "OptInterproceduralAssign(_,to,_,from)."),
    THIS_ASSIGN("_(this,base)<-" +
            "Stats:Simple:InsensCallGraphEdge(invo,callee)," +
            "(VirtualMethodInvocation:Base[invo]=base;" +
            "SpecialMethodInvocation:Base[invo]=base)," +
            "ThisVar[callee]=this."),
    INSTANCE_LOAD("_(to,base,field)<-" +
            "LoadHeapInstanceField(_,to,field,_,base)."),
    ARRAY_LOAD("_(to,array)<-LoadHeapArrayIndex(_,to,_,array)."),
    INSTANCE_STORE("_(base,field,from)<-" +
            "StoreHeapInstanceField(field,_,base,_,from)."),
    ARRAY_STORE("_(array,from)<-StoreHeapArrayIndex(_,array,_,from)."),
    INSTANCE_LOAD_FROM_TO("_(from,to)<-" +
            "ReachableLoadInstanceFieldBase(from)," +
            "OptLoadInstanceField(to,_,from)."),
    ARRAY_LOAD_FROM_TO("_(from,to)<-" +
            "ReachableLoadArrayIndexBase(from)," +
            "OptLoadArrayIndex(to,from)."),
    // currently not considering special calls
    CALL_RETURN_TO("_(recv,to)<-" +
            "VirtualMethodInvocation:Base[invo]=recv," +
            "VirtualMethodInvocation:In(invo,inmethod)," +
            "AssignReturnValue[invo]=to," +
            "Reachable(inmethod)."),


    // call graph edges
    REGULARCALL("Stats:Simple:InsensCallGraphEdge"),
    REFCALL("_(from,to)<-ReflectiveCallGraphEdge(_,from,_,to)."),
    NATIVECALL("_(from,to)<-NativeCallGraphEdge(_,from,_,to)."),
    CALL_EDGE("Stats:Simple:WholeInsensCallGraphEdge"),
    CALLER_CALLEE("_(caller,callee)<-" +
            "(Stats:Simple:InsensCallGraphEdge(callsite,callee);" +
            "ReflectiveCallGraphEdge(_,callsite,_,callee))," +
            "(SpecialMethodInvocation:In(callsite,caller);" +
            "VirtualMethodInvocation:In(callsite,caller);" +
            "StaticMethodInvocation:In(callsite,caller))."),
    MAINMETHOD("MainMethodDeclaration"),
    REACHABLE("Reachable"),
    IMPLICITREACHABLE("ImplicitReachable"),

    // instance field store
    INSTANCE_STORE_IN("_(obj,inmethod)<-" +
            "ReachableStoreInstanceFieldBase(base)," +
            "VarPointsTo(_,obj,_,base)," +
            "Var:DeclaringMethod(base,inmethod)."),
    ARRAY_STORE_IN("_(array,inmethod)<-" +
            "ReachableStoreArrayIndexBase(base)," +
            "VarPointsTo(_,array,_,base)" +
            "Var:DeclaringMethod(base,inmethod)."),

    // call site
    CALLSITE("_(callsite)<-" +
            "Stats:Simple:WholeInsensCallGraphEdge(callsite,_)."),
    INST_CALL_RECV("_(callsite,recv)<-" +
            "Stats:Simple:InsensCallGraphEdge(callsite,_)," +
            "(VirtualMethodInvocation:Base[callsite]=recv;" +
            "SpecialMethodInvocation:Base[callsite]=recv)."),
    CALLSITE_ARGS("_(callsite,i,arg)<-" +
            "Stats:Simple:InsensCallGraphEdge(callsite,_)," +
            "ActualParam[i,callsite]=arg."),
    CALLSITE_RETURN("_(callsite,to)<-" +
            "Stats:Simple:WholeInsensCallGraphEdge(callsite,_)," +
            "AssignReturnValue[callsite]=to"),
    CALLSITE_IN("MethodInvocation:In"),

    // method
    THIS_VAR("_(mtd,this)<-Reachable(mtd),ThisVar[mtd]=this."),
    PARAMS("_(mtd,param)<-Reachable(mtd),FormalParam[_,mtd]=param."),
    INDEXED_PARAMS("_(mtd,i,param)<-" +
            "Reachable(mtd),FormalParam[i,mtd]=param."),
    RET_VARS("_(mtd,ret)<-Reachable(mtd),ReturnVar(ret,mtd)."),
    OBJFINAL("ObjectSupportsFinalize"),
    VAR_IN("_(var,inmethod)<-Var:DeclaringMethod(var,inmethod)," +
            "Reachable(inmethod)."),
    METHOD_MODIFIER("_(mtd,mod)<-MethodModifier(mod,mtd)."),
    METHOD_INST_NUMBER("Method:InstructionNumber"),
    // only instance methods have this variables
    INST_METHODS("_(mtd)<-Reachable(mtd),ThisVar[mtd]=_."),
    APP_METHODS("Stats:Simple:InsensReachableApplicationMethod"),
    CS_METHODS("ContextSensitiveMethod"),

    // type
    APPLICATION_CLASS("ApplicationClass"),
    DIRECT_SUPER_TYPE("DirectSuperclass"),
    DECLARING_CLASS_ALLOCATION("DeclaringClassAllocation"),
    VAR_TYPE("_(var,type)<-" +
            "Var:Type[var]=type," +
            "Var:DeclaringMethod(var,inmethod)," +
            "Reachable(inmethod)."),
    ASSIGN_COMPATIBLE("AssignCompatible"),

    // clients
    MAY_FAIL_CAST("Stats:Simple:PotentiallyFailingCast"),
    POLY_CALL("Stats:Simple:PolymorphicCallSite"),
    INSENS_CALL_EDGE("Stats:Simple:InsensCallGraphEdge"),
    ;


    private final String expr;

    Query(String expr) {
        this.expr = expr;
    }

    @Override
    public String toString() {
        return expr;
    }
}
