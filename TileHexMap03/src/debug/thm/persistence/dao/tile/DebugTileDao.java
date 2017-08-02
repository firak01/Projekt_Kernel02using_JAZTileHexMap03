package debug.thm.persistence.dao.tile;

import java.util.List;

import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTileDao {

	public static void main(String[] args) {
		DebugTileDao objDebug = new DebugTileDao();
		objDebug.debugReadByCellId();	
		objDebug.debugSearchTileByUniquename();		
	}
	public DebugTileDao(){		
	}
	public boolean debugReadByCellId(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TileDao daoTile = new TileDao(objContextHibernate);
				
				//TODO GOON 2017-06-14: Hier CellId-Schlüsselwerte vorbereiten und an die daoTroop.readByCellId(...) übergeben...
				List<Tile> list = daoTile.searchTileCollectionByHexCell("EINS", "5", "5");
				System.out.println("1. Abfrage: Anzahl gefundener Spielsteine = " + list.size());
				
				List<Tile> list02 = daoTile.searchTileCollectionByHexCell("EINS", "1", "3");
				System.out.println("2. Abfrage: Anzahl gefundener Spielsteine = " + list02.size());
				
				for(Tile objTile : list02){
					System.out.println("Gefundene uniquenames: " + objTile.getUniquename());
				}
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
	public boolean debugSearchTileByUniquename(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TileDao daoTile = new TileDao(objContextHibernate);
				String sUniquename = new String("");
				
				//TODO GOON 2017-06-14: Hier CellId-Schlüsselwerte vorbereiten und an die daoTroop.readByCellId(...) übergeben...
				sUniquename = "nix";
				Tile objTile = daoTile.searchTileByUniquename(sUniquename);
				if(objTile==null){
					System.out.println("1. Abfrage: ERWARTETES ERGEBNIS. Kein Spielstein mit dem Uniquename '" + sUniquename + "' gefunden.");
				}else{
					System.out.println("1. Abfrage: UNERWARTETETS ERGEBNIS: Spielstein mit dem Uniquename '" + sUniquename + "' gefunden.");
				}
				
				sUniquename = "ARMY UNIQUE 3";
				Tile objTile02 = daoTile.searchTileByUniquename(sUniquename);
				if(objTile02==null){
					System.out.println("2. Abfrage: UNERWARTETETS ERGEBNIS: Kein Spielstein mit dem Uniquename '" + sUniquename + "' gefunden.");
				}else{
					System.out.println("2. Abfrage: ERWARTETES ERGEBNIS. Spielstein mit dem Uniquename '" + sUniquename + "' gefunden.");
					System.out.println("Es ist ein Spielstein vom Typ: '" + objTile02.getTileType() + "'");
				}				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
}
