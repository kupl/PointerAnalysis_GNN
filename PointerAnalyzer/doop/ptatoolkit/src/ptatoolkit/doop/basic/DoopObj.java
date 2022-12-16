package ptatoolkit.doop.basic;

import ptatoolkit.pta.basic.Obj;
import ptatoolkit.pta.basic.Type;

public class DoopObj extends Obj {

    private final int id;
    private final String rep;

    public DoopObj(String rep, Type type, int id) {
        super(type);
        this.id = id;
        //this.rep = String.format("%s(%d)", rep, id);
        this.rep = rep;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return rep;
    }
}
