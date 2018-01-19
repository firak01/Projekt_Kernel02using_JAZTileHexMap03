package debug.thm.persistence.dao.tilevariant;

import java.util.ArrayList;
import java.util.List;

import debug.thm.persistence.keytable.DebugKeyTable_Version_TileDefaulttextTHM;
import debug.thm.persistence.keytable.DebugKeyTable_Version_TileImmutabletextTHM;
import use.thm.persistence.dao.KeyDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTroopFleetVariantDao {

	public static void main(String[] args) {
		//Voraussetzung: Alle Defaulttexte, Immutabletexte sind da.
		DebugKeyTable_Version_TileDefaulttextTHM objDefaulttextProvider = new DebugKeyTable_Version_TileDefaulttextTHM();
		objDefaulttextProvider.debugCreateEntriesAll();
		
		DebugKeyTable_Version_TileImmutabletextTHM objImmutabletextProvider = new DebugKeyTable_Version_TileImmutabletextTHM();
		objImmutabletextProvider.debugCreateEntriesAll();
		
		//##################################
		
		DebugTroopFleetVariantDao objDebug = new DebugTroopFleetVariantDao();
		objDebug.debugCreateEntry();
		
		//TODO GOON 20180119
//		objDebug.debugSearchKey();	
//		
//		objDebug.debugFindAll();
//		
//		objDebug.debugDeleteAll();
	}
	public DebugTroopFleetVariantDao(){		
	}
	
	
	public boolean debugCreateEntry(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.
				
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;	
	}
	
	
	//##### TODO GOON 2018019 ALT ÜBERABIETEN ###############
	public boolean debugSearchKey(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
				TileDefaulttextDao daoKey = new TileDefaulttextDao(objContextHibernate);
				String sKeytype = new String("");
				Long lngThiskey = new Long(1);
				
				//TODO GOON 2017-06-14: Hier CellId-Schlüsselwerte vorbereiten und an die daoTroop.readByCellId(...) übergeben...
//				sKeytype = "NIXVALUE";
//				Key objKey = daoKey.searchKey(sKeytype, lngThiskey);
//				if(objKey==null){
//					System.out.println("1. Abfrage: Erwartetes Ergebnis. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
//				}else{
//					System.out.println("1. Abfrage: UNERWARTETES ERGEBNIS. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
//				}
				
				sKeytype = "TILE";
				Defaulttext objKey02 = (Defaulttext) daoKey.searchKey(sKeytype, lngThiskey );
				if(objKey02==null){
					System.out.println("2. Abfrage: UNERWARTETES ERGEBNIS. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
				}else{
					System.out.println("2. Abfrage: Erwartetes Ergebnis. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");					
				}			
				
				String sLongtext = objKey02.getLongtext();
				System.out.println("Longtext = " + sLongtext);
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
	
	public boolean debugFindAll(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
				TileDefaulttextDao daoKey = new TileDefaulttextDao(objContextHibernate);
				String sKeytype = new String("DEFAULTTEXT");		
				Long lngThiskey = new Long(1);

				Defaulttext objKey02 = (Defaulttext) daoKey.searchKey(sKeytype, lngThiskey );
				if(objKey02==null){
					System.out.println("2. Abfrage: UNERWARTETES ERGEBNIS. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
				}else{
					System.out.println("2. Abfrage: Erwartetes Ergebnis. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");			
					
					//Nun alle holen
					ArrayList<Defaulttext> listaTileDefaulttext = (ArrayList<Defaulttext>) daoKey.findLazyAll();
					for(Defaulttext text : listaTileDefaulttext){
						System.out.println("TileDefaulttext.toString(): " + text.toString());
//						String sTYPE = "ARMY";
//						String sValue = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue(text.getThiskeyEnumClass(), sTYPE);
						Long lngThiskeyTemp = text.getThiskey();
						String sDescriptionStored = text.getDescription();
						System.out.println("Description (gespeichert): " + sDescriptionStored);
						
						String sDescriptionDefault = null; 
						try {
							String sType = EnumSetInnerUtilZZZ.getThiskeyEnum(text.getThiskeyEnumClass(), lngThiskeyTemp).name();							
							System.out.println("Typ: " + sType);
							
							sDescriptionDefault = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue(text.getThiskeyEnumClass(), sType);
							System.out.println("Description (Default): " + sDescriptionDefault);
						} catch (ThiskeyEnumMappingExceptionZZZ e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(sDescriptionStored.equals(sDescriptionDefault)){
							System.out.println("Wert ist unverändert");							
						}else{
							System.out.println("WERT WURDE VERÄNDERT");
						}
						
						
					}
					
				}		
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
	
	public boolean debugDeleteAll(){
		boolean bReturn = false;
		main:{
			try {				
				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
				TileDefaulttextDao daoKey = new TileDefaulttextDao(objContextHibernate);
				boolean bSuccess = daoKey.deleteAll();

			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
}
