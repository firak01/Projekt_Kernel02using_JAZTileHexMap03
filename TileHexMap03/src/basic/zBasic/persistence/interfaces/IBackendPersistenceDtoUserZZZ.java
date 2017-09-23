package basic.zBasic.persistence.interfaces;

import use.thm.persistence.dto.ITileDtoAttribute;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;

public interface IBackendPersistenceDtoUserZZZ<G extends IDTOAttributeGroup> {
	public GenericDTO<G>getDto();
	public void setDto(GenericDTO<?>objDto);
}
