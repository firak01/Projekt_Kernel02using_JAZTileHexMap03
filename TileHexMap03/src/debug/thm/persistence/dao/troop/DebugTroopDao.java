package debug.thm.persistence.dao.troop;

import java.util.List;

import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.IConstantHibernateZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTroopDao {

	public static void main(String[] args) {
		DebugTroopDao objDebug = new DebugTroopDao();
		objDebug.debugReadByCellId();
		objDebug.debugFndColumnMaxValue();
		objDebug.debugFindColumnSortedByColumn();
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
				
				//Merke: uniquename ist @transient, darum muss man tileIdObject verwenden.
				List<?> listResultUnsorted = daoTroop.findColumnValueSortedByColumn("tileIdObject.uniquename");//Merke Uniquename kommt aus der Objektklasse: TileId. Diese wird in Tile per "Embedding" eingebunden. //Aus searchTroopArmyByUniquename: "from TroopArmy as tableTile where tableTile.tileIdObject.uniquename = :uniqueName"
				System.out.println("Ergebnis enthält '" + listResultUnsorted.size() + "' Werte. Spaltenwerte unsortiert:");
				for(Object o : listResultUnsorted){
					System.out.println(o.toString());
				}
				
				//Merke: uniquename ist @transient, darum muss man tileIdObject verwenden.
				List<?> listResultDesc = daoTroop.findColumnValueSortedByColumn("tileIdObject.uniquename", IConstantHibernateZZZ.iSORT_DESCENDING);//Merke Uniquename kommt aus der Objektklasse: TileId. Diese wird in Tile per "Embedding" eingebunden. //Aus searchTroopArmyByUniquename: "from TroopArmy as tableTile where tableTile.tileIdObject.uniquename = :uniqueName"
				System.out.println("Ergebnis enthält '" + listResultDesc.size() + "' Werte. Spaltenwerte absteigend sortiert:");
				for(Object o : listResultDesc){
					System.out.println(o.toString());
				}
				
				//Merke: uniquename ist @transient, darum muss man tileIdObject verwenden.
				List<?> listResultAsc = daoTroop.findColumnValueSortedByColumn("tileIdObject.uniquename", IConstantHibernateZZZ.iSORT_ASCENDING);//Merke Uniquename kommt aus der Objektklasse: TileId. Diese wird in Tile per "Embedding" eingebunden. //Aus searchTroopArmyByUniquename: "from TroopArmy as tableTile where tableTile.tileIdObject.uniquename = :uniqueName"
				System.out.println("Ergebnis enthält '" + listResultAsc.size() + "' Werte. Spaltenwerte aufsteigend sortiert:");
				for(Object o : listResultAsc){
					System.out.println(o.toString());
				}
				
				//#############################
				//Merke: mapX ist nicht @transient, darum darf man tileIdObject nicht zu verwenden.
				List<?> listResult02 = daoTroop.findColumnValueSortedByColumn("tileIdObject.uniquename", IConstantHibernateZZZ.iSORT_ASCENDING, "mapX");//Merke Uniquename kommt aus der Objektklasse: TileId. Diese wird in Tile per "Embedding" eingebunden. //Aus searchTroopArmyByUniquename: "from TroopArmy as tableTile where tableTile.tileIdObject.uniquename = :uniqueName"
				System.out.println("Ergebnis enthält '" + listResult02.size() + "' Werte. Spaltenwerte X aufsteigend sortiert:");
				for(Object o : listResult02){
					System.out.println(o.toString());
				}
				
				//Merke: mapX ist nicht @transient, darum darf man tileIdObject nicht zu verwenden.
				List<?> listResult03 = daoTroop.findColumnValueSortedByColumn("tileIdObject.uniquename", IConstantHibernateZZZ.iSORT_DESCENDING, "mapX");//Merke Uniquename kommt aus der Objektklasse: TileId. Diese wird in Tile per "Embedding" eingebunden. //Aus searchTroopArmyByUniquename: "from TroopArmy as tableTile where tableTile.tileIdObject.uniquename = :uniqueName"
				System.out.println("Ergebnis enthält '" + listResult03.size() + "' Werte. Spaltenwerte X absteigend sortiert:");
				for(Object o : listResult03){
					System.out.println(o.toString());
				}
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}

}
