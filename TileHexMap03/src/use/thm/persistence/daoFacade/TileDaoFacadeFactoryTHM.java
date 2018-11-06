package use.thm.persistence.daoFacade;

import use.thm.client.component.TileTHM;
import use.thm.persistence.hibernate.HibernateContextProviderJndiSingletonTHM;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Troop;
import basic.persistence.daoFacade.AbstractDaoFacadeFactoryZZZ;
import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.daoFacade.IDaoFacadeFactoryZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
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
	public GeneralDaoFacadeZZZ createDaoFacade(String sTroopType) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.		
		if(StringZZZ.isEmpty(sTroopType )){
			String stemp = "Kein TroopType - Key übergeben.";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
		if(sTroopType.equalsIgnoreCase("ar")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
			TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
			return objTroopDaoFacade;
		}else if(sTroopType.equalsIgnoreCase("fl")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
			TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
			return objTroopDaoFacade;
		}else{
			String stemp = "Kein TroopType - Key mit einer passenden DaoFacade übergeben ('" + sTroopType + ")";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	@Override
	public GeneralDaoFacadeZZZ createDaoFacadeJndi(String sTroopType) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.		
		if(StringZZZ.isEmpty(sTroopType )){
			String stemp = "Kein TroopType - Key übergeben.";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		HibernateContextProviderJndiSingletonTHM objContextHibernate = HibernateContextProviderJndiSingletonTHM.getInstance();
				
		if(sTroopType.equalsIgnoreCase("ar")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
			TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
			return objTroopDaoFacade;
		}else if(sTroopType.equalsIgnoreCase("fl")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
			TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
			return objTroopDaoFacade;
		}else{
			String stemp = "Kein TroopType - Key mit einer passenden DaoFacade übergeben ('" + sTroopType + ")";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	@Override
	public GeneralDaoFacadeZZZ createDaoFacade(Troop objTroopEntity) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.		
		String sTroopType = objTroopEntity.getTroopType();
		
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
		if(sTroopType.equalsIgnoreCase("ar")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
			TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
			return objTroopDaoFacade;
		}else if(sTroopType.equalsIgnoreCase("fl")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
			TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
			return objTroopDaoFacade;
		}else{
			String stemp = "Kein TroopType - Key mit einer passenden DaoFacade übergeben ('" + sTroopType + ")";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	@Override
	public GeneralDaoFacadeZZZ createDaoFacadeJndi(Troop objTroopEntity) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.		
		String sTroopType = objTroopEntity.getTroopType();
		
		HibernateContextProviderJndiSingletonTHM objContextHibernate = HibernateContextProviderJndiSingletonTHM.getInstance();
				
		if(sTroopType.equalsIgnoreCase("ar")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
			TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
			return objTroopDaoFacade;
		}else if(sTroopType.equalsIgnoreCase("fl")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
			TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
			return objTroopDaoFacade;
		}else{
			String stemp = "Kein TroopType - Key mit einer passenden DaoFacade übergeben ('" + sTroopType + ")";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	@Override
	public GeneralDaoFacadeZZZ createDaoFacade(Object objWithDto) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.
		TileTHM objTile = (TileTHM) objWithDto;
		String sTroopType = objTile.getSubtype();
		
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
		if(sTroopType.equalsIgnoreCase("ar")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
			TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
			return objTroopDaoFacade;
		}else if(sTroopType.equalsIgnoreCase("fl")){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
			TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
			return objTroopDaoFacade;
		}else{
			String stemp = "Kein TroopType - Key mit einer passenden DaoFacade übergeben ('" + sTroopType + ")";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	@Override
	public GeneralDaoFacadeZZZ createDaoFacadeJndi(Object objectWithDto) throws ExceptionZZZ {
		  //FALLUNTERSCHEIDUNG: Je nach Truppentyp eine andere DAOFACADE wählen.
				TileTHM objTile = (TileTHM) objectWithDto;
				String sTroopType = objTile.getSubtype();
				
				HibernateContextProviderJndiSingletonTHM objContextHibernate = HibernateContextProviderJndiSingletonTHM.getInstance();
						
				if(sTroopType.equalsIgnoreCase("ar")){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyDaoFacade");
					TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);		
					return objTroopDaoFacade;
				}else if(sTroopType.equalsIgnoreCase("fl")){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetDaoFacade");
					TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
					return objTroopDaoFacade;
				}else{
					String stemp = "Kein TroopType - Key mit einer passenden DaoFacade übergeben ('" + sTroopType + ")";
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
					ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TileDaoFacadeFactoryTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
	}
}
