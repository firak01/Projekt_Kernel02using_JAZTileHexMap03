package basic.persistence.dto;

import java.io.Serializable;

public class DTOAttribute<G extends IDTOAttributeGroup, T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;

    private DTOAttribute(String name) {

        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public static <F extends IDTOAttributeGroup, S> DTOAttribute<F, S> getInstance(String name) {
        return new DTOAttribute<F, S>(name);
    }

    @Override
    public String toString() {
        return name;
    }

    //equals(), hashCode() ...

}
