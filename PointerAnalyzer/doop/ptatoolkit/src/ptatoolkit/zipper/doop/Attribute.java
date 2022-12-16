package ptatoolkit.zipper.doop;

import ptatoolkit.pta.basic.IAttribute;

public enum Attribute implements IAttribute {

    POINTS_TO_SET,
    POINTS_TO_SET_SIZE,
    FIELD_POINTS_TO_SET,
    METHODS_CALLED_ON,
    ALLOCATED,
    VARS_IN,
    DECLARING,
    CALL_SITES,
    CALL_SITES_ON,
    CALL_SITE_RETURN,
    CALLEE,
    OBJECT_ASSIGNED,
    DECLARING_TYPE,
    RECEIVER,
    RETURN_TO,
    TYPE,

}
