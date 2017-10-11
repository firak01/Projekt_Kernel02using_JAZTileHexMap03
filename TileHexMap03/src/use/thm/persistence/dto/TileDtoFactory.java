package use.thm.persistence.dto;

import basic.persistence.dto.AbstractDtoFactoryZZZ;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;

public class TileDtoFactory extends AbstractDtoFactoryZZZ {

	@Override
	public GenericDTO<IDTOAttributeGroup> createDTO() {		
		@SuppressWarnings("unchecked")
		GenericDTO<IDTOAttributeGroup> dto = (GenericDTO<IDTOAttributeGroup>) this.createDTO(ITileDtoAttribute.class);
		return dto;
	}

}
