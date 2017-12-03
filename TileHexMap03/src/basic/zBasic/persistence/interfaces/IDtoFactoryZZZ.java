package basic.zBasic.persistence.interfaces;

import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;

public interface IDtoFactoryZZZ {
	public GenericDTO<IDTOAttributeGroup> createDTO();
	abstract GenericDTO<?> createDTO(Class classObjectType); //class ist von ... IDTOAttributeGroup objType
}
