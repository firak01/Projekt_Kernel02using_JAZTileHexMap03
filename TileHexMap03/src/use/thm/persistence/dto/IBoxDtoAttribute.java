package use.thm.persistence.dto;

import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.IDTOAttributeGroup;

public interface IBoxDtoAttribute extends IDTOAttributeGroup {

    public final static DTOAttribute<IBoxDtoAttribute, String> UNIQUENAME = DTOAttribute.getInstance("UNIQUENAME");
    public final static DTOAttribute<IBoxDtoAttribute, String> SUBTYPE = DTOAttribute.getInstance("SUBTYPE");
    
    public final static DTOAttribute<IBoxDtoAttribute, String> VARIANT_IMAGE_URL_STRING = DTOAttribute.getInstance("VARIANT_IMAGE_URL_STRING");
    
    //20180630: Das Ziel ist es die Daten eines Bilds ebenfalls über das DTO-Objekt auszutauschen. Das byte[] kommt aus der in der Datenbank gespeicherten Variante.
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGE_IN_BYTE= DTOAttribute.getInstance("VARIANT_IMAGE_IN_BYTE");
  
    //20180726: Damit schnell die GUIgröße "gezoomt" werden kann ist es das Ziel die verschiedenen Bilder für die unterschiedlichen Zoomstufen in der Datenbank zu speichern
    //               und natürlich dann per DTO zu übergeben.
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGE_IN_BYTE_01 = DTOAttribute.getInstance("VARIANT_IMAGECATALOG_IN_BYTE_01");
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGE_IN_BYTE_02 = DTOAttribute.getInstance("VARIANT_IMAGECATALOG_IN_BYTE_02");
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGE_IN_BYTE_03 = DTOAttribute.getInstance("VARIANT_IMAGECATALOG_IN_BYTE_03");
    
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGEDIALOG_IN_BYTE_01 = DTOAttribute.getInstance("VARIANT_IMAGEDIALOG_IN_BYTE_01");
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGEDIALOG_IN_BYTE_02 = DTOAttribute.getInstance("VARIANT_IMAGEDIALOG_IN_BYTE_02");
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGEDIALOG_IN_BYTE_03 = DTOAttribute.getInstance("VARIANT_IMAGEDIALOG_IN_BYTE_03");
       
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_01= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_01");
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_02= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_02");
    public final static DTOAttribute<IBoxDtoAttribute, byte[]> VARIANT_IMAGEDRAG_IN_BYTE_03= DTOAttribute.getInstance("VARIANT_IMAGEDRAG_IN_BYTE_03");
     
    //Werte aus TileImmutableText
    public final static DTOAttribute<IBoxDtoAttribute, String> VARIANT_SHORTTEXT = DTOAttribute.getInstance("VARIANT_SHORTTEXT");
    //public final static Attribute<Integer> ID = Attribute.getInstance("CustomerAttributes.Id");
    
}
