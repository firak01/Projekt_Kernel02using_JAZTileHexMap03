package basic.persistence.daoFacade;

import use.thm.persistence.daoFacade.TileDaoFacade;
import basic.persistence.dto.IFacadeDtoZZZ;
import basic.zBasic.ExceptionZZZ;

public interface IDaoFacadeFactoryZZZ {
	public GeneralDaoFacadeZZZ createDaoFacade(Object objectWithDto) throws ExceptionZZZ;
}
