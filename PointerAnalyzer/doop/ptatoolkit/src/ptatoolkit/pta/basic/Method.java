package ptatoolkit.pta.basic;

import java.util.Collection;
import java.util.Map;

/**
 * The abstraction of method.
 */
public abstract class Method extends AttributeElement {

    private final Collection<Variable> params;
    private final Collection<Variable> retVars;
    private final boolean isPrivate;
    private final Map<Integer, Variable> indexedParams;

    protected Method(Collection<Variable> params,
                     Map<Integer, Variable> indexedParams,
                     Collection<Variable> retVars,
                     boolean isPrivate) {
        this.params = params;
        this.indexedParams = indexedParams;
        this.retVars = retVars;
        this.isPrivate = isPrivate;
    }

    public Collection<Variable> getParameters() {
        return params;
    }

    /**
     * @return map from index to corresponding parameter
     */
    public Map<Integer, Variable> getIndexedParameters() {
        return indexedParams;
    }

    /**
     * @return all parameters. For instance methods, this variable
     * will also be returned.
     */
    public abstract Collection<Variable> getAllParameters();

    public Collection<Variable> getRetVars() {
        return retVars;
    }

    public abstract boolean isInstance();

    /**
     * Returns the signature of this method. The format of signature is:
     * <[class-name]: [return-type] [method-name]([parameter-list])>
     *
     * @return signature of the method.
     */
    public abstract String toString();

    public boolean isStatic() {
        return !isInstance();
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    /**
     * Return the name of class where this method is declared.
     * <p>
     * This method depends on {@link ptatoolkit.pta.basic.Method#toString()}.
     * It assumes that {@link ptatoolkit.pta.basic.Method#toString()} returns
     * the signature of this method with following format:
     * <[class-name]: [return-type] [method-name]([parameter-list])>
     *
     * @return the class name
     */
    public String getClassName() {
        String sig = toString();
        int end = sig.indexOf(':');
        return sig.substring(1, end);
    }
}
