package use.thm.persistence.dto;

import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.IDTOAttributeGroup;

public interface ITileDtoAttribute extends IDTOAttributeGroup {

    public final static DTOAttribute<ITileDtoAttribute, String> UNIQUENAME = DTOAttribute.getInstance("UNIQUENAME");
    
    //Werte aus TileImmutableText
    public final static DTOAttribute<ITileDtoAttribute, String> VARIANT_SHORTTEXT = DTOAttribute.getInstance("VARIANT_SHORTTEXT");
    //public final static Attribute<Integer> ID = Attribute.getInstance("CustomerAttributes.Id");
    
    //Wird per SQL ermittelt. Ist dann die höchste Zahl, der Spielsteinsorte um 1 jeweils erhöht
   public final static DTOAttribute<ITileDtoAttribute, Integer> INSTANCE_VARIANT_UNIQUENUMBER = DTOAttribute.getInstance("INSTANCE_VARIANT_UNIQUENUMBER");
}
