package debug.thm.persistence.dao.tilevariant;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import debug.thm.persistence.keytable.DebugKeyTable_Version_TileDefaulttextTHM;
import debug.thm.persistence.keytable.DebugKeyTable_Version_TileImmutabletextTHM;
import use.thm.persistence.dao.ImmutabletextDao;
import use.thm.persistence.dao.KeyDao;
import use.thm.persistence.dao.TextImmutabletextDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.dao.TileImmutabletextDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.KeyImmutable;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TextImmutabletext;
import use.thm.persistence.model.TextImmutabletext.EnumTextImmutabletext;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TextDefaulttext.EnumTextDefaulttext;
import use.thm.persistence.model.TileImmutabletext.EnumTileImmutabletext;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopArmyVariant.EnumTroopArmyVariant;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopFleetVariant.EnumTroopFleetVariant;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTroopArmyVariantDao {

	public static void main(String[] args) {
		
		
		try {
			KernelZZZ objKernel = new KernelZZZ();		
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
			objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
			//DAS ERSTELLEN DER DEFAULTTEXTE
//			DebugKeyTable_Version_TileDefaulttextTHM objDefaulttextProvider = new DebugKeyTable_Version_TileDefaulttextTHM();			
//			objDefaulttextProvider.debugCreateEntriesAll();
//			
//			DebugKeyTable_Version_TileImmutabletextTHM objImmutabletextProvider = new DebugKeyTable_Version_TileImmutabletextTHM();		
//			objImmutabletextProvider.debugCreateEntriesAll();
//			
			//##################################
			
			DebugTroopArmyVariantDao objDebug = new DebugTroopArmyVariantDao();
		
			//### VARIANTE 1: VORHER SICHERHEITSHALBER ALLE LÖSCHEN....
			objDebug.debugDeleteAll();
			objDebug.debugCreateEntryByEnumSetIndex(1);//Dabei werden ggfs. benötigte Defaulttexte erzeugt.
			objDebug.debugCreateEntryByEnumSetIndex(0);//Dabei werden ggfs. benötigte Defaulttexte erzeugt.
			
			//Suche mal nach einem der hoffentlich erzeugten Einträge, über den Thiskey.
			Long lngThiskey = new Long(110);
			objDebug.debugSearchKey(lngThiskey);
		

			//### VARIANTE 2: VORHER SICHERHEITSHALBER ALLE LÖSCHEN....
			objDebug.debugDeleteAll();			
			objDebug.debugCreateEntriesAll();
			
			//		//TODO GOON 20180124
			//		objDebug.debugFindAll();
			//		
			
			
			
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
		} 
		
	}
	public DebugTroopArmyVariantDao(){		
	}
	
	public boolean debugCreateEntriesAll(){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);													
			
				//###################
				//1. Hole das jeweilige Dao - Objekt
				//####################	
				TroopArmyVariantDao daoTroopArmyVariant = new TroopArmyVariantDao(objContextHibernate);
				daoTroopArmyVariant.createEntriesAll();
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
		}//end main:
		return bReturn;	
	}
	
	
	public boolean debugCreateEntryByEnumSetIndex(int iIndex){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
						
				//###################
				//1. Speichere die ARMYVARIANT		
				//
				//1.1. Vorbereitung: Hole die anderen Objekte..
				//####################
				EnumTroopArmyVariant[] objaType = TroopArmyVariant.EnumTroopArmyVariant.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[iIndex].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				//Es gibt bei Immutable keine Setter. Daher alles nur im Konstruktor übergeben.														
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
			    String sUniquetext = objaType[iIndex].getUniquetext();
			    String sCategorytext = objaType[iIndex].getCategorytext();
			    int iMoveRange = objaType[iIndex].getMapMoveRange();
			    
			    int iDefaulttextThiskey = objaType[iIndex].getDefaulttextThisid();
			    Long lngDefaulttextThiskey = new Long(iDefaulttextThiskey);
			    
			    int iImmutabletextThiskey = objaType[iIndex].getImmutabletextThisid();			    
			    Long lngImmutabletextThiskey = new Long(iImmutabletextThiskey);
			    
			    int iDegreeOfCoverMax = objaType[iIndex].getDegreeOfCoverMax();
			    
			    String sImageUrl = objaType[iIndex].getImageUrlString();
				String sDescription = null;
				String sShorttext = null;				
				String sLongtext = null;
			    
			    
			    TileDefaulttext objDefaulttext = null;			    			   
			    TileDefaulttextDao daoTileText = new TileDefaulttextDao(objContextHibernate);
			    Key objKey = daoTileText.searchThiskey(lngDefaulttextThiskey);
				if(objKey==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' TileDefaulttext ('" + lngDefaulttextThiskey + "') NOCH NICHT gefunden.");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' erzeuge benötigten TileDefaulttext ('" + lngDefaulttextThiskey + "') .");
					
					//TODO GOON 20180130: Hier das erstellte Objekt zurückgeben, dann braucht man auch nicht mehr danach zu suchen.
					daoTileText.createEntryForThiskey(lngDefaulttextThiskey);					
					objKey = daoTileText.searchThiskey(lngDefaulttextThiskey);
					if(objKey==null){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' kann benötigten TileDefaulttext ('" + lngDefaulttextThiskey + "') nicht erzeugen.");
						return false;						
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' benötigten TileDefaulttext ('" + lngDefaulttextThiskey + "')  erzeugt.");						
					}				
				}
				objDefaulttext = (TileDefaulttext) objKey;				
				sDescription = objDefaulttext.getDescription();
				sShorttext = objDefaulttext.getShorttext();				
				sLongtext = objDefaulttext.getLongtext();				
				System.out.println("Thiskey='"+lngThiskey+"' dazugehörender TileDefaulttext gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			    
				TileImmutabletext objImmutableText = null;
				TileImmutabletextDao daoTileImmutable = new TileImmutabletextDao(objContextHibernate);
			    KeyImmutable objKeyImmutable = daoTileImmutable.searchThiskey(lngImmutabletextThiskey);
			    if(objKeyImmutable==null){
					System.out.println("Thiskey='"+lngThiskey.toString()+"' TileImmutabletext ('" + lngImmutabletextThiskey + "') NOCH NICHT gefunden.");
					System.out.println("Thiskey='"+lngThiskey.toString()+"' erzeuge benötigten TileImmutabletext ('" + lngImmutabletextThiskey + "') .");
					
					//TODO GOON 20180130: Hier das erzeugte Objekt direkt zurückgeben lassen, dann braucht man es nicht zu suchen.
					daoTileImmutable.createEntryForThiskey(lngImmutabletextThiskey);
					
					objKeyImmutable = daoTileImmutable.searchThiskey(lngImmutabletextThiskey);
					if(objKeyImmutable==null){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' kann benötigten TileImmutabletext ('"+lngImmutabletextThiskey+"') nicht erzeugen.");
						return false;						
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' benötigten TileImmutabletext ('"+lngImmutabletextThiskey+"') erzeugt.");						
					}
			    }
				objImmutableText = (TileImmutabletext) objKeyImmutable;				
				sDescription = objImmutableText.getDescription();
				sShorttext = objImmutableText.getShorttext();				
				sLongtext = objImmutableText.getLongtext();				
				System.out.println("Thiskey='"+lngThiskey+"' dazugehörender TileImmutabletext gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			    
				//###################
				//1. Speichere die TROOPVARIANT		
				//
				//1.2. Nun das eigentliche Speichern
			    //#############
			    //Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
							    			   
				TroopArmyVariant objValue = new TroopArmyVariant(lngThiskey.intValue(),sUniquetext, sCategorytext, iMoveRange, sImageUrl, objDefaulttext, objImmutableText, iDegreeOfCoverMax);
				
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
	
	public boolean debugCreateEntryForThiskey(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
						
				//###################
				//1. Speichere die FLEETVARIANT		
				//
				//1.1. Vorbereitung: Hole die anderen Objekte..
				//####################
				EnumTroopArmyVariant[] objaType = TroopArmyVariant.EnumTroopArmyVariant.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				//Es gibt bei Immutable keine Setter. Daher alles nur im Konstruktor übergeben.	
				int iIndex = 0;
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
			   	
			    this.debugCreateEntryForThiskey(lngThiskey);
			    
			    
			} catch (ExceptionZZZ e) {
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
		}//end main:
		return bReturn;											
	}
	
	public boolean debugCreateEntryForThiskey(long lThiskey){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
						
				//###################
				//1. Speichere die ARMYVARIANT		
				//
				//1.1. Vorbereitung: Hole die anderen Objekte..
				//####################
				EnumTroopArmyVariant[] objaType = TroopArmyVariant.EnumTroopArmyVariant.values();
				EnumTroopArmyVariant objType = EnumSetInnerUtilZZZ.getThiskeyEnum(TroopArmyVariant.getThiskeyEnumClassStatic(), lThiskey);
				Long lngThiskey = new Long(lThiskey);				
				
				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				//Es gibt bei Immutable keine Setter. Daher alles nur im Konstruktor übergeben.																	    
			    String sUniquetext = objType.getUniquetext();
			    String sCategorytext = objType.getCategorytext();
			    int iMoveRange = objType.getMapMoveRange();
			    String sImageUrl = objType.getImageUrlString();
				int iDefaulttextThiskey = objType.getDefaulttextThisid();
				Long lngThiskeyDefaulttext = new Long(iDefaulttextThiskey);
										
				int iImmutabletextThiskey = objType.getImmutabletextThisid();
				Long lngThiskeyImmutabletext = new Long(iImmutabletextThiskey);
			    
				//Speziell für TROOP
				int iDegreeOfCoverMax = objType.getDegreeOfCoverMax();
				
				//Variablen für die Ausgabe der Werte der Texte
				String sDescription = null;
				String sShorttext = null;
				String sLongtext = null;
			    
			    TileDefaulttext objDefaulttext = null;			    			   
			    TileDefaulttextDao daoTileText = new TileDefaulttextDao(objContextHibernate);
			    Key objKey = daoTileText.searchThiskey(lngThiskeyDefaulttext);
				if(objKey==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskeyDefaulttext.toString()+"' NICHT gefunden.");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskeyDefaulttext.toString()+"' erzeuge benötigten TileDefaulttext.");
						
					//TODO GOON 20180130: Rückgabewert, dann braucht man anschliessend auch nicht zu suchen.
					daoTileText.createEntryForThiskey(lngThiskeyDefaulttext);
					
					objKey = daoTileText.searchThiskey(lngThiskeyDefaulttext);
					if(objKey==null){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskeyDefaulttext.toString()+"' kann benötigten TileDefaulttext nicht erzeugen.");
						return false;						
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskeyDefaulttext.toString()+"' benötigten TileDefaulttext erzeugt.");						
					}				
				}
				objDefaulttext = (TileDefaulttext) objKey;				
				sDescription = objDefaulttext.getDescription();
				sShorttext = objDefaulttext.getShorttext();				
				sLongtext = objDefaulttext.getLongtext();				
				System.out.println("Thiskey='"+lngThiskey+"' gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
				
			    
				TileImmutabletext objImmutableText = null;
				TileImmutabletextDao daoTileImmutable = new TileImmutabletextDao(objContextHibernate);
			    KeyImmutable objKeyImmutable = daoTileImmutable.searchThiskey(lngThiskey);
			    if(objKeyImmutable==null){
					System.out.println("Thiskey='"+lngThiskeyImmutabletext.toString()+"' NICHT gefunden.");
					System.out.println("Thiskey='"+lngThiskeyImmutabletext.toString()+"' erzeuge benötigten TileImmutabletext.");
										
					//TODO GOON 20180130: Rückgabewert, dann braucht man anschliessend auch nicht zu suchen.
					daoTileImmutable.createEntryForThiskey(lngThiskey);
					
					objKeyImmutable = daoTileImmutable.searchThiskey(lngThiskey);
					if(objKeyImmutable==null){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskeyImmutabletext.toString()+"' kann benötigten TileImmutabletext nicht erzeugen.");
						return false;						
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskeyImmutabletext.toString()+"' benötigten TileImmutabletext erzeugt.");						
					}
			    }
				objImmutableText = (TileImmutabletext) objKeyImmutable;				
				sDescription = objImmutableText.getDescription();
				sShorttext = objImmutableText.getShorttext();				
				sLongtext = objImmutableText.getLongtext();				
				System.out.println("Thiskey='"+lngThiskey+"' gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
					
			    
				//###################
				//1. Speichere die ARMYVARIANT		
				//
				//1.2. Nun das eigentliche Speichern
			    //#############
			    //Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
			    
			    // public TroopFleetVariant(int iKey, int intMapMoveRange, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
				TroopArmyVariant objValue = new TroopArmyVariant(lngThiskey.intValue(),sUniquetext, sCategorytext, iMoveRange, sImageUrl, objDefaulttext, objImmutableText, iDegreeOfCoverMax);
				
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
				e.printStackTrace();
			} catch (ThiskeyEnumMappingExceptionZZZ e1) {			
				e1.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
		}//end main:
		return bReturn;											
	}
	
	public boolean debugSearchKey(Long lngThiskey){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
				TileDefaulttextDao daoKey = new TileDefaulttextDao(objContextHibernate);
				String sKeytype = new String("");
				
				//TODO GOON: FEHLER HIER WIRD nach der VARIANTE gesucht und nicht nach dem Defaulttext....
				sKeytype = "DEFAULTTILETEXT";
				TileDefaulttext objKey02 = (TileDefaulttext) daoKey.searchKey(sKeytype, lngThiskey );
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
				boolean bSuccess = false;
				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//diese Methode hat darüber nicht zu entscheiden:  objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
				TroopArmyVariantDao daoKey = new TroopArmyVariantDao(objContextHibernate);
				bSuccess = daoKey.deleteAll();
								
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
}