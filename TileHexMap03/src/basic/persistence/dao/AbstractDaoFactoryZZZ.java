package basic.persistence.dao;

import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Troop;
import basic.persistence.daoFacade.IDaoFactoryZZZ;
import basic.persistence.dto.IFacadeDtoZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

/** !!! Wird noch nicht benutzt
 * @author lindhaueradmin
 *
 */
public abstract class AbstractDaoFactoryZZZ extends KernelUseObjectZZZ implements IDaoFactoryZZZ {
	
	/**Konstruktor ist in den hieraus erbenden Klassen private, wg. Singleton. Daher muss er in der Abstrakten Klasse protected sein. 
	 * @param objKernel
	 * @throws ExceptionZZZ
	 */
	protected AbstractDaoFactoryZZZ(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	protected AbstractDaoFactoryZZZ(){
		super();
	}
	
	//### Diese Methoden aus dem Inteface werden noch nicht benutzt!!!!
//	@Override
//	public abstract GeneralDAO createDAO(String sTroopType) throws ExceptionZZZ;
//			
//	@Override
//	public abstract GeneralDAO createDAOJndi(String sTroopType) throws ExceptionZZZ;	
}
