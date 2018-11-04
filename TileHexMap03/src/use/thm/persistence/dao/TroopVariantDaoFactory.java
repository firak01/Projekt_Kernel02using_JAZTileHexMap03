package use.thm.persistence.dao;

import use.thm.client.component.TileTHM;
import use.thm.persistence.hibernate.HibernateContextProviderJndiSingletonTHM;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Troop;
import basic.persistence.dao.AbstractDaoFactoryZZZ;
import basic.persistence.dao.GeneralDAO;
import basic.persistence.daoFacade.AbstractDaoFacadeFactoryZZZ;
import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.daoFacade.IDaoFacadeFactoryZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelZZZ;

public class TroopVariantDaoFactory extends AbstractDaoFactoryZZZ{
private static TroopVariantDaoFactory objFactory = null;  //muss static sein, wg. getInstance()!!!

	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @throws ExceptionZZZ
	 */
	private TroopVariantDaoFactory(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	private TroopVariantDaoFactory(){
		super();
	}
	
	public static TroopVariantDaoFactory getInstance() throws ExceptionZZZ{
		if(objFactory==null){
			KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
			objFactory = new TroopVariantDaoFactory(objKernel);
		}
		return objFactory;		
	}
	
	
	public static TroopVariantDaoFactory getInstance(KernelSingletonTHM objKernel) throws ExceptionZZZ{
		if(objKernel==null){
			String stemp = "Kein Kernel-Objekt übergeben.";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, TroopVariantDaoFactory.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;		
		}
		if(objFactory==null){
			objFactory = new TroopVariantDaoFactory(objKernel);
		}else{
			if(objFactory.getKernelObject().equals(objKernel)){
				//nichts neues erstellen.
			}else{
				objFactory = new TroopVariantDaoFactory(objKernel);
			}
		}
		return objFactory;		
	}
	
	public TroopVariantDao createDaoVariant(long lngThiskey) throws ExceptionZZZ {
		TroopVariantDao objReturn = null;
		
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
		
		//FALLUNTERSCHEIDUNG: Suche nach der Variante über den Thiskey. Danach je nach Typ eine andere Dao-Klasse zurückgeben	
				//Merke: Switch mit long ist nicht erlaubt.
				if(lngThiskey >=0 && lngThiskey <=19){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyVariantDao");
					TroopArmyVariantDao objReturnTemp = new TroopArmyVariantDao(objContextHibernate);
					objReturn = objReturnTemp;
				}else if(lngThiskey >=20 && lngThiskey <=29){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetVariantDao");
					TroopFleetVariantDao objReturnTemp = new TroopFleetVariantDao(objContextHibernate);			
					objReturn = objReturnTemp;
				}else{
					String stemp = "Keine Thiskey-ID für eine passenden DaoVariante übergeben ('" + lngThiskey + ")";
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
					ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TroopVariantDaoFactory.class,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
		objReturn.setThiskeyUsed(lngThiskey);
		return objReturn;
	}
	
	public TroopVariantDao createDaoVariantJndi(long lngThiskey) throws ExceptionZZZ {		
		TroopVariantDao objReturn = null;
		
		HibernateContextProviderJndiSingletonTHM objContextHibernate = HibernateContextProviderJndiSingletonTHM.getInstance();
		
		//FALLUNTERSCHEIDUNG: Suche nach der Variante über den Thiskey. Danach je nach Typ eine andere Dao-Klasse zurückgeben	
		//Merke: Switch mit long ist nicht erlaubt.
		if(lngThiskey >=0 && lngThiskey <=19){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopArmyVariantDao (JNDI)");
			TroopArmyVariantDao objReturnTemp = new TroopArmyVariantDao(objContextHibernate);
			objReturn = objReturnTemp;
		}else if(lngThiskey >=20 && lngThiskey <=29){
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, ReflectCodeZZZ.getMethodCurrentName() + ": Creating TroopFleetVariantDao (JNDI)");
			TroopFleetVariantDao objReturnTemp = new TroopFleetVariantDao(objContextHibernate);			
			objReturn = objReturnTemp;
		}else{
			String stemp = "Keine Thiskey-ID für eine passenden DaoVariante übergeben ('" + lngThiskey + ") (JNDI)";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_VALUE, TroopVariantDaoFactory.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		objReturn.setThiskeyUsed(lngThiskey);
		return objReturn;
	}
}
