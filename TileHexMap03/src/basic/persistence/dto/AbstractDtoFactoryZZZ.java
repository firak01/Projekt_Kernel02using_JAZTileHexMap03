package basic.persistence.dto;

import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;

public abstract class AbstractDtoFactoryZZZ implements IDtoFactoryZZZ{
	
	@SuppressWarnings("unchecked")
	@Override
	public GenericDTO<?> createDTO(Class classObjectType) {
		GenericDTO<?> dto = GenericDTO.getInstance(classObjectType);
		return dto;
	}
	
}
