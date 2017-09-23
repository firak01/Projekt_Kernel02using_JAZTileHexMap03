package basic.persistence.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GenericDTO<G extends IDTOAttributeGroup> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Class<G> attributeGroup;

    private Map<DTOAttribute<G, ?>, Object> attributes = new LinkedHashMap<DTOAttribute<G, ?>, Object>();

    private GenericDTO(Class<G> attributeGroup) {
        this.attributeGroup = attributeGroup;
    }

    public static <B extends IDTOAttributeGroup> GenericDTO<B> getInstance(
            Class<B> clazz) {
        return new GenericDTO<B>(clazz);
    }

    public <T> GenericDTO<G> set(DTOAttribute<G, T> identifier, T value) {

        attributes.put(identifier, value);

        return this;
    }

    public <T> T get(DTOAttribute<G, T> identifier) {

        @SuppressWarnings("unchecked")
        T theValue = (T) attributes.get(identifier);

        return theValue;
    }

    public <T> T remove(DTOAttribute<G, T> identifier) {

        @SuppressWarnings("unchecked")
        T theValue = (T) attributes.remove(identifier);

        return theValue;
    }

    public void clear() {
        attributes.clear();
    }

    public int size() {
        return attributes.size();
    }

    public Set<DTOAttribute<G, ?>> getAttributes() {
        return attributes.keySet();
    }

    public boolean contains(DTOAttribute<G, ?> identifier) {

        return attributes.containsKey(identifier);
    }

    @Override
    public String toString() {
        return attributeGroup.getSimpleName() + " [" + attributes + "]";
    }

    // equals(), hashCode() ...
}