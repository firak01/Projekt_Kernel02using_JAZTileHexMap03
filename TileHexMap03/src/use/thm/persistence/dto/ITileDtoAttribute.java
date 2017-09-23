package use.thm.persistence.dto;

import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.IDTOAttributeGroup;

public interface ITileDtoAttribute extends IDTOAttributeGroup {

    public final static DTOAttribute<ITileDtoAttribute, String> UNIQUENAME = DTOAttribute.getInstance("UNIQUENAME");

    //public final static Attribute<Integer> ID = Attribute.getInstance("CustomerAttributes.Id");

}
