package debug.thm.persistence.dao.troop;

import java.util.List;

import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTroopDao {

	public static void main(String[] args) {
		DebugTroopDao objDebug = new DebugTroopDao();
		objDebug.debugReadByCellId();
		objDebug.debugFndColumnMaxValue();
	}
	public DebugTroopDao(){		
	}
	public boolean debugReadByCellId(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TroopArmyDao daoTroop = new TroopArmyDao(objContextHibernate);
				
				//TODO GOON 2017-06-14: Hier CellId-Schlüsselwerte vorbereiten und an die daoTroop.readByCellId(...) übergeben...
				List<TroopArmy> list = daoTroop.searchTroopArmyCollectionByHexCell("EINS", "5", "5");
				System.out.println("1. Abfrage: Anzahl gefundener Armeen = " + list.size());
				if(list.size()>=1) System.out.println("Da wurde keine Armee, sondern eine Flotte erwartet");
				
				List<TroopArmy> list02 = daoTroop.searchTroopArmyCollectionByHexCell("EINS", "1", "3");
				System.out.println("2. Abfrage: Anzahl gefundener Armeen = " + list02.size());
				
				for(TroopArmy objArmy : list02){
					System.out.println("Gefundene uniquenames: " + objArmy.getUniquename());
				}
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
	
	public boolean debugFndColumnMaxValue(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TroopArmyDao daoTroop = new TroopArmyDao(objContextHibernate);
				
				//Also nicht der Tabellenname, sondern das gemappte Objekt: TroopArmy für die Tabelle ARMY, auch nicht den Spaltennamen TILE_ID_INCREMENTED , sondern Id
				Integer intValue = daoTroop.findColumnValueMax("id");
				System.out.println("Maximalwert der id - Spalte: " + intValue.toString());
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
	
	public boolean debugFindColumnSortedByColumn(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TroopArmyDao daoTroop = new TroopArmyDao(objContextHibernate);
				
				//TODO GOON 20170808 Diese Methode in die daoZZZ - Klasse einbauen
				//.findColumnSortedByColumn(...)
				
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}

}
