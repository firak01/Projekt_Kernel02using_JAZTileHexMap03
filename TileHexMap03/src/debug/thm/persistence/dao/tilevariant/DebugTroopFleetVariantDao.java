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
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopFleetVariant.EnumTroopFleetVariant;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTroopFleetVariantDao {

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
			
			DebugTroopFleetVariantDao objDebug = new DebugTroopFleetVariantDao();
			//TODO: VORHER SICHERHEITSHALBER ALLE LÖSCHEN....
			
			objDebug.debugCreateEntry(1);//Dabei werden ggfs. benötigte Defaulttexte erzeugt.
			objDebug.debugCreateEntry(0);//Dabei werden ggfs. benötigte Defaulttexte erzeugt.
			
			//Suche mal nach einem der hoffentlich erzeugten Einträge, über den Thiskey.
			Long lngThiskey = new Long(1);
			objDebug.debugSearchKey(lngThiskey);	
		
	//		//TODO GOON 20180124
	//		objDebug.debugFindAll();
	//		
	//		objDebug.debugDeleteAll();
			
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
		} 
		
	}
	public DebugTroopFleetVariantDao(){		
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
				 TroopFleetVariantDao daoTroopFleetVariant = new TroopFleetVariantDao(objContextHibernate);
				daoTroopFleetVariant.createEntriesAll();
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
		}//end main:
		return bReturn;	
	}
	
	
	public boolean debugCreateEntry(int iIndex){
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
				EnumTroopFleetVariant[] objaType = TroopFleetVariant.EnumTroopFleetVariant.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				//Es gibt bei Immutable keine Setter. Daher alles nur im Konstruktor übergeben.														
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
			    String sUniquetext = objaType[iIndex].getUniquetext();
			    String sCategorytext = objaType[iIndex].getCategorytext();
			    int iMoveRange = objaType[iIndex].getMapMoveRange();
			    String sImageUrl = objaType[iIndex].getImageUrlString();
				String sDescription = null;
				String sShorttext = null;				
				String sLongtext = null;
			    
			    
			    TileDefaulttext objDefaulttext = null;			    			   
			    TileDefaulttextDao daoTileText = new TileDefaulttextDao(objContextHibernate);
			    Key objKey = daoTileText.searchThiskey(lngThiskey);
				if(objKey==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' NICHT gefunden.");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' erzeuge benötigten TileDefaulttext.");
					
					//TODO GOON 20180126: So schön so gut, aber untenstehendes sollte alles in einer Methode des DAO gekapselt werden
					//neu: daoTileText.createEntryForThiskey(lngThiskey);
					//......teste ssh keys in Eclipse hinterlegt
					
					EnumTileDefaulttext objEnumDefaulttext;
					try {
						Enum objEnum = EnumSetInnerUtilZZZ.getThiskeyEnum(TileDefaulttext.getThiskeyEnumClassStatic(),lngThiskey);
						if(objEnum!=null){
							objEnumDefaulttext = (EnumTileDefaulttext)objEnum;
							int iIndexFound = objEnumDefaulttext.getIndex();
							DebugKeyTable_Version_TileDefaulttextTHM objDefaulttextProvider = new DebugKeyTable_Version_TileDefaulttextTHM();
							objDefaulttextProvider.debugCreateEntry_TileDefaulttext(iIndexFound);
						}else{
							System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' nicht im Enum von  TileDefaulttext gefunden.");
						}
					} catch (ThiskeyEnumMappingExceptionZZZ e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					/* obige Zeilen sollen das hier ersetzten
					EnumTileDefaulttext[] objaTypeDefaulttext = TileDefaulttext.EnumTileDefaulttext.values();
					int iIndexFound = -1;
					for(EnumTileDefaulttext objEnum : objaTypeDefaulttext){
						iIndexFound++;
						if(objEnum.getThiskey().equals(lngThiskey)){
							DebugKeyTable_Version_TileDefaulttextTHM objDefaulttextProvider = new DebugKeyTable_Version_TileDefaulttextTHM();
							objDefaulttextProvider.debugCreateEntry_TileDefaulttext(iIndexFound);							
							break;
						}
					}*/
					
					objKey = daoTileText.searchThiskey(lngThiskey);
					if(objKey==null){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' kann benötigten TileDefaulttext nicht erzeugen.");
						return false;						
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' benötigten TileDefaulttext erzeugt.");						
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
					System.out.println("Thiskey='"+lngThiskey.toString()+"' NICHT gefunden.");
					System.out.println("Thiskey='"+lngThiskey.toString()+"' erzeuge benötigten TileImmutabletext.");
					
					EnumTileImmutabletext[] objaTypeImmutabletext = TileImmutabletext.EnumTileImmutabletext.values();
					int iIndexFound = -1;
					for(EnumTileImmutabletext objEnum : objaTypeImmutabletext){
						iIndexFound++;
						if(objEnum.getThiskey().equals(lngThiskey)){
							DebugKeyTable_Version_TileImmutabletextTHM objDefaulttextProvider = new DebugKeyTable_Version_TileImmutabletextTHM();
							objDefaulttextProvider.debugCreateEntry_TileImmutabletext(iIndexFound);							
							break;
						}
					}
					
					objKeyImmutable = daoTileImmutable.searchThiskey(lngThiskey);
					if(objKeyImmutable==null){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' kann benötigten TileImmutabletext nicht erzeugen.");
						return false;						
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThiskey.toString()+"' benötigten TileImmutabletext erzeugt.");						
					}
			    }
				objImmutableText = (TileImmutabletext) objKeyImmutable;				
				sDescription = objImmutableText.getDescription();
				sShorttext = objImmutableText.getShorttext();				
				sLongtext = objImmutableText.getLongtext();				
				System.out.println("Thiskey='"+lngThiskey+"' gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
					
			    
				//###################
				//1. Speichere die FLEETVARIANT		
				//
				//1.2. Nun das eigentliche Speichern
			    //#############
			    //Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
			    
			    // public TroopFleetVariant(int iKey, int intMapMoveRange, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
				TroopFleetVariant objValue = new TroopFleetVariant(lngThiskey.intValue(),sUniquetext, sCategorytext, iMoveRange, sImageUrl, objDefaulttext, objImmutableText);
				
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
	
	public boolean debugSearchKey(Long lngThiskey){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
				TileDefaulttextDao daoKey = new TileDefaulttextDao(objContextHibernate);
				String sKeytype = new String("");
				
				
				//TODO GOON 2017-06-14: Hier CellId-Schlüsselwerte vorbereiten und an die daoTroop.readByCellId(...) übergeben...
//				sKeytype = "NIXVALUE";
//				Key objKey = daoKey.searchKey(sKeytype, lngThiskey);
//				if(objKey==null){
//					System.out.println("1. Abfrage: Erwartetes Ergebnis. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
//				}else{
//					System.out.println("1. Abfrage: UNERWARTETES ERGEBNIS. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThiskey.toString() + "' gefunden.");
//				}
				
				sKeytype = "TILE";
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
