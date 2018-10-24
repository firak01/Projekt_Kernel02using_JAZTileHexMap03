package basic.persistence.daoFacade;

import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Troop;
import basic.persistence.dto.IFacadeDtoZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public abstract class AbstractDaoFacadeFactoryZZZ extends KernelUseObjectZZZ implements IDaoFacadeFactoryZZZ {
	
	/**Konstruktor ist in den hieraus erbenden Klassen private, wg. Singleton. Daher muss er in der Abstrakten Klasse protected sein. 
	 * @param objKernel
	 * @throws ExceptionZZZ
	 */
	protected AbstractDaoFacadeFactoryZZZ(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	protected AbstractDaoFacadeFactoryZZZ(){
		super();
	}
	
	@Override
	public abstract GeneralDaoFacadeZZZ createDaoFacade(Object objectWithDto) throws ExceptionZZZ;
	
	@Override
	public abstract GeneralDaoFacadeZZZ createDaoFacade(Troop objectTroopEntity) throws ExceptionZZZ;

	@Override
	public abstract GeneralDaoFacadeZZZ createDaoFacadeJndi(Object objectWithDto) throws ExceptionZZZ;
	
	@Override
	public abstract GeneralDaoFacadeZZZ createDaoFacadeJndi(Troop objectTroopEntity) throws ExceptionZZZ;

}
