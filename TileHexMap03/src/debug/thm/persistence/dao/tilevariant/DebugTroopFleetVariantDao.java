package debug.thm.persistence.dao.tilevariant;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import debug.thm.persistence.keytable.DebugKeyTable_Version_TileDefaulttextTHM;
import debug.thm.persistence.keytable.DebugKeyTable_Version_TileImmutabletextTHM;
import use.thm.persistence.dao.KeyDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TileImmutabletext.EnumTileImmutabletext;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
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
		objDebug.debugCreateEntry(0);
		
		//TODO GOON 20180119
//		objDebug.debugSearchKey();	
//		
//		objDebug.debugFindAll();
//		
//		objDebug.debugDeleteAll();
	}
	public DebugTroopFleetVariantDao(){		
	}
	
	
	public boolean debugCreateEntry(int iIndex){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.
				
				//#### TODO GOON 20180121
				//###################
				//1. Speichere die FLEETVARIANT
				//####################					
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				EnumTileImmutabletext[] objaType = TileImmutabletext.EnumTileImmutabletext.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				//Es gibt bei Immutable keine Setter. Daher alles nur im Konstruktor übergeben.
				
				String sDescription = objaType[iIndex].getDescription();
				String sShorttext = objaType[iIndex].getShorttext();
				String sLongtext = objaType[iIndex].getLongtext();
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.

				TileImmutabletext objValue = new TileImmutabletext(lngThiskey,sShorttext,sLongtext, sDescription);
				
				//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
				session.save(objValue); //Hibernate Interceptor wird aufgerufen																				
				if (!session.getTransaction().wasCommitted()) {
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
					
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(objContextHibernate,"save",session.getTransaction());
//					sMessage = objResult.getVetoMessage();
//					bGoon = !objResult.isVeto();
				}
//				if(!bGoon){
//					//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
//					this.getFacadeResult().setMessage(sMessage);
//					break validEntry;
//				}
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
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
