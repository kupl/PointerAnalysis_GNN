package ptatoolkit.pta.basic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An instance of this class is a element in points-to analysis
 * that carries some attribute values.
 */
public abstract class AttributeElement extends BasicElement {

    private final Map<IAttribute, Object> attributes = new HashMap<>(4);

    public boolean hasAttribute(IAttribute attr) {
        return attributes.containsKey(attr);
    }

    public Object getAttribute(IAttribute attr) {
        return attributes.get(attr);
    }

    public void setAttribute(IAttribute attr, Object value) {
        attributes.put(attr, value);
    }

    /**
     * For the case where attr represents an attribute which
     * is a set of elements. This API directly adds the elem
     * into the attribute set (corresponding to attr).
     */
    @SuppressWarnings("unchecked")
    public <T> void addToAttributeSet(IAttribute attr, T elem) {
        attributes.putIfAbsent(attr, new HashSet<>());
        ((Set<T>) attributes.get(attr)).add(elem);
    }

    /**
     * @return the attribute set corresponding to attr.
     * If the set does not exist, then return an empty set.
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getAttributeSet(IAttribute attr) {
        return (Set<T>) attributes.getOrDefault(attr, Collections.emptySet());
    }
}
