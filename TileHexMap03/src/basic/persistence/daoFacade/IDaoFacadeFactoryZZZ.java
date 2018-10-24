package basic.persistence.daoFacade;

import use.thm.persistence.daoFacade.TileDaoFacade;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Troop;
import basic.persistence.dto.IFacadeDtoZZZ;
import basic.zBasic.ExceptionZZZ;

public interface IDaoFacadeFactoryZZZ {
	public GeneralDaoFacadeZZZ createDaoFacade(Object objectWithDto) throws ExceptionZZZ;
	public GeneralDaoFacadeZZZ createDaoFacade(Troop objectTroopEntity) throws ExceptionZZZ;
	public GeneralDaoFacadeZZZ createDaoFacadeJndi(Object objectWithDto) throws ExceptionZZZ;
	public GeneralDaoFacadeZZZ createDaoFacadeJndi(Troop objectTroopEntity) throws ExceptionZZZ;
}
