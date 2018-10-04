package use.thm.persistence.daoFacade;

import use.thm.client.component.TileTHM;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.persistence.daoFacade.AbstractDaoFacadeFactoryZZZ;
import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.daoFacade.IDaoFacadeFactoryZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelZZZ;

public class TileDaoFacadeFactoryTHM extends AbstractDaoFacadeFactoryZZZ{
private static TileDaoFacadeFactoryTHM objFacadeFactory = null;  //muss static sein, wg. getInstance()!!!

	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @throws ExceptionZZZ
	 */
	private TileDaoFacadeFactoryTHM(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	private TileDaoFacadeFactoryTHM(){
		super();
	}
	
	public static TileDaoFacadeFactoryTHM getInstance() throws ExceptionZZZ{
		if(objFacadeFactory==null){
			KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
			objFacadeFactory = new TileDaoFacadeFactoryTHM(objKernel);
		}
		return objFacadeFactory;		
	}
	
	
	public static TileDaoFacadeFactoryTHM getInstance(KernelSingletonTHM objKernel) throws ExceptionZZZ{
		if(objKernel==null){
			String stemp = "Kein Kernel-Objekt übergeben.";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;		
		}
		if(objFacadeFactory==null){
			objFacadeFactory = new TileDaoFacadeFactoryTHM(objKernel);
		}else{
			if(objFacadeFactory.getKernelObject().equals(objKernel)){
				//nichts neues erstellen.
			}else{
				objFacadeFactory = new TileDaoFacadeFactoryTHM(objKernel);
			}
		}
		return objFacadeFactory;		
	}
	
	@Override
	public GeneralDaoFacadeZZZ createDaoFacade(Object objWithDto) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.
		TileTHM objTile = (TileTHM) objWithDto;
		String sSubtype = objTile.getSubtype();
		
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
		if(sSubtype.equalsIgnoreCase("ar")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
			TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
			return objTroopDaoFacade;
		}else if(sSubtype.equalsIgnoreCase("fl")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
			TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
			return objTroopDaoFacade;
		}
		return null;
	}
	
	
}
