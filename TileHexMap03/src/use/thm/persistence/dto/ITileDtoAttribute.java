package use.thm.persistence.dto;

import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.IDTOAttributeGroup;

public interface ITileDtoAttribute extends IDTOAttributeGroup {

    public final static DTOAttribute<ITileDtoAttribute, String> UNIQUENAME = DTOAttribute.getInstance("UNIQUENAME");
    public final static DTOAttribute<ITileDtoAttribute, String> SUBTYPE = DTOAttribute.getInstance("SUBTYPE");
    
    public final static DTOAttribute<ITileDtoAttribute, String> VARIANT_IMAGE_URL_STRING = DTOAttribute.getInstance("VARIANT_IMAGE_URL_STRING");
    
    //20180630: Das Ziel ist es die Daten eines Bilds ebenfalls über das DTO-Objekt auszutauschen. Das byte[] kommt aus der in der Datenbank gespeicherten Variante.
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGE_IN_BYTE= DTOAttribute.getInstance("VARIANT_IMAGE_IN_BYTE");
  
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGECATALOG_IN_BYTE_01 = DTOAttribute.getInstance("VARIANT_IMAGECATALOG_IN_BYTE_01");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGECATALOG_IN_BYTE_02 = DTOAttribute.getInstance("VARIANT_IMAGECATALOG_IN_BYTE_02");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGECATALOG_IN_BYTE_03 = DTOAttribute.getInstance("VARIANT_IMAGECATALOG_IN_BYTE_03");
    
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDIALOG_IN_BYTE_01 = DTOAttribute.getInstance("VARIANT_IMAGEDIALOG_IN_BYTE_01");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDIALOG_IN_BYTE_02 = DTOAttribute.getInstance("VARIANT_IMAGEDIALOG_IN_BYTE_02");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDIALOG_IN_BYTE_03 = DTOAttribute.getInstance("VARIANT_IMAGEDIALOG_IN_BYTE_03");
       
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_01= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_01");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_02= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_02");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_03= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_03");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_04= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_04");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_05= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_05");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_06= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_06");
     
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEHEXMAP_IN_BYTE_01= DTOAttribute.getInstance("VARIANT_IMAGEHEXMAP_IN_BYTE_01");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEHEXMAP_IN_BYTE_02= DTOAttribute.getInstance("VARIANT_IMAGEHEXMAP_IN_BYTE_02");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEHEXMAP_IN_BYTE_03= DTOAttribute.getInstance("VARIANT_IMAGEHEXMAP_IN_BYTE_03");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEHEXMAP_IN_BYTE_04= DTOAttribute.getInstance("VARIANT_IMAGEHEXMAP_IN_BYTE_04");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEHEXMAP_IN_BYTE_05= DTOAttribute.getInstance("VARIANT_IMAGEHEXMAP_IN_BYTE_05");
    public final static DTOAttribute<ITileDtoAttribute, byte[]> VARIANT_IMAGEHEXMAP_IN_BYTE_06= DTOAttribute.getInstance("VARIANT_IMAGEHEXMAP_IN_BYTE_06");
    
    
    
    //Werte aus TileImmutableText
    public final static DTOAttribute<ITileDtoAttribute, String> VARIANT_SHORTTEXT = DTOAttribute.getInstance("VARIANT_SHORTTEXT");
    //public final static Attribute<Integer> ID = Attribute.getInstance("CustomerAttributes.Id");
    
    //Wird per SQL ermittelt. Ist dann die höchste Zahl, der Spielsteinsorte um 1 jeweils erhöht
   public final static DTOAttribute<ITileDtoAttribute, Integer> INSTANCE_VARIANT_UNIQUENUMBER = DTOAttribute.getInstance("INSTANCE_VARIANT_UNIQUENUMBER");
   public final static DTOAttribute<ITileDtoAttribute, Integer> INSTANCE_SUBTYPE_UNIQUENUMBER = DTOAttribute.getInstance("INSTANCE_SUBTYPE_UNIQUENUMBER");
   
   //Die aktuelle Health des Spielsteins
   public final static DTOAttribute<ITileDtoAttribute, Float> HEALTH = DTOAttribute.getInstance("HEALTH");
}
