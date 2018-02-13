package debug.thm.persistence.dao.key;

import java.util.List;

import use.thm.persistence.dao.AbstractKeyDao;
import use.thm.persistence.dao.DefaulttextDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DebugDefaulttextDao {

	public static void main(String[] args) {
		try {
		KernelZZZ objKernel = new KernelZZZ();
		
		//Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
		objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.
		
		DebugDefaulttextDao objDebug = new DebugDefaulttextDao();	
		objDebug.debugSearchKey();
		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public DebugDefaulttextDao(){		
	}
	public boolean debugSearchKey(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
				DefaulttextDao daoKey = new DefaulttextDao(objContextHibernate);
				String sKeytype = new String("");

				sKeytype = "NIXVALUE";
				Long lngThiskey = new Long(1);
				Key objKey = daoKey.searchKey(sKeytype, lngThiskey);
				if(objKey==null){
					System.out.println("1. Abfrage: Erwartetes Ergebnis. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
				}else{
					System.out.println("1. Abfrage: UNERWARTETES ERGEBNIS. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
				}
				
				sKeytype = "DEFAULTTEXTVALUE";
				Key objKey02 = daoKey.searchKey(sKeytype, lngThiskey );
				if(objKey02==null){
					System.out.println("2. Abfrage: UNERWARTETES ERGEBNIS. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
				}else{
					System.out.println("2. Abfrage: Erwartetes Ergebnis. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");					
				}				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
}
