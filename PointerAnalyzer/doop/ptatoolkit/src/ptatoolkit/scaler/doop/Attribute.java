package ptatoolkit.scaler.doop;

import ptatoolkit.pta.basic.IAttribute;

public enum Attribute implements IAttribute {

    POINTS_TO_SET,
    POINTS_TO_SIZE,
    FIELD_POINTS_TO_SET,
    CALL_SITES_ON,
    METHODS_CALLED_ON,
    ALLOCATED,
    VARS_IN,
    CALL_SITE,
    CALLEE,
    OBJECT_ASSIGNED,
    DECLARING_TYPE,
    RETURN_TO,
    RECEIVER,
    DECLARING_ALLOC_TYPE,

}
