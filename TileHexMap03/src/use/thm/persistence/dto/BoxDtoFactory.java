package use.thm.persistence.dto;

import basic.persistence.dto.AbstractDtoFactoryZZZ;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;

public class BoxDtoFactory extends AbstractDtoFactoryZZZ {
		
	//TODO: Erlaube das setzen von Flags im Konstruktor.... 
	//dazu muss die Klasse aber auch die getFlag / set Flag Methodik verwenden, die in anderen Kernel-Objekten genutzt wird.
	//Diese Flags müssen vererbbar sein....
	
//	public static  TileDtoFactory getInstance(String sFlagControl) throws ExceptionZZZ{
//		if(objFactorySingelton==null){
//			objFactorySingelton = new TileDtoFactory(sFlagControl);
//		}
//		return objFactorySingelton;	
//	}
	
	//###########################################

	public GenericDTO<IDTOAttributeGroup> createDTO() {		
		@SuppressWarnings("unchecked")
		GenericDTO<IDTOAttributeGroup> dto = (GenericDTO<IDTOAttributeGroup>) this.createDTO(IBoxDtoAttribute.class);
		return dto;
	}

}
