package debug.thm.persistence.keytable;

import java.util.Collection;
import java.util.Set;

import org.hibernate.Session;

import use.thm.persistence.dao.DefaulttextDao;
import use.thm.persistence.dao.ImmutabletextDao;
import use.thm.persistence.dao.TextDefaulttextDao;
import use.thm.persistence.dao.TextImmutabletextDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.dao.TileImmutabletextDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.Defaulttext.EnumDefaulttext;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.Immutabletext.EnumImmutabletext;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.KeyImmutable;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TextDefaulttext.EnumTextDefaulttext;
import use.thm.persistence.model.TextImmutabletext;
import use.thm.persistence.model.TextImmutabletext.EnumTextImmutabletext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import use.thm.persistence.model.TileDefaulttextType;
import use.thm.persistence.model.TileDefaulttextValue;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.TileImmutabletext.EnumTileImmutabletext;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;
import basic.zBasic.util.abstractEnum.EnumSetDefaulttextTestTypeTHM;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumZZZ;
import basic.zKernel.KernelZZZ;
/** In dieser Variante werden innere Klassen verwendet, welche die Enumation sind.
 *  Die hier angelegten Texte sind veränderbar, d.h. die Entities haben auch Setter-Methoden.
 *  Vergleiche immutable - Texte. Dort haben die Entities keine Setter-Methoden.
 *  
 * @author Fritz Lindhauer
 *
 */
public class DebugKeyTable_Version_TileImmutabletextTHM {

	public static void main(String[] args) {
		try {
									
		DebugKeyTable_Version_TileImmutabletextTHM objDebug = new DebugKeyTable_Version_TileImmutabletextTHM();
		
		//Hole alle Einträge des Enums
		objDebug.getEntrySetImmutabletextValues();
		objDebug.getEntrySetTileImmutabletextValues();
		objDebug.getEntrySetTextImmutabletextValues();

		KernelZZZ objKernel = new KernelZZZ();			
		HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
		objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.							

		int iFound = -1;
									
		TileImmutabletextDao daoTileImmutabletext = new TileImmutabletextDao(objContextHibernate);	
		TextImmutabletextDao daoTextImmutabletext = new TextImmutabletextDao(objContextHibernate);		
		ImmutabletextDao daoImmutabletext = new ImmutabletextDao(objContextHibernate);
		
		//LÖSCHE ALLE EINTRÄGE
		//Lösche alle erzeugten Einträge vor dem Test. D.h. es muss eine DAO-Klasse für die DefaultValues geben
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE (IMMUTABLETEXT) VORAB");
		daoImmutabletext.deleteAll();//Das reicht, wg. der "Hibernate-Vererbung" der Model Klasse ... Diese Vererbungsstruktur spiegelt sich dann auch in den Dao-Klassen wieder.
				
		//Zähle die Anzahl der Einträge
		iFound = daoImmutabletext.count();//Der Count liefert alle Elemente auch der Kindklasse wieder. Also: Neues Entitiy TextDefaulttext, erbend aus Defaulttext.
		//									Entsprechend auch TextImmutabletextDao als neue Klasse.
		System.out.println("Anzahl gefundener Immutabletext - Einträge nach Löschung aller Immutabletext - Einträge: " + iFound);
		
		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Löschung aller Immutabletext - Einträge: " + iFound);

		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Löschung aller Immutabletext - Einträge: " + iFound);
		
		//Erzeuge nun für die Tabellen jeweils einen Eintrag
		System.out.println("\n---------------------------------------\nErzeuge einen Eintrag in  Immutabletext");		
		objDebug.debugCreateEntry_Immutabletext(0);
				
		//Zähle die Anzahl der Einträge
		System.out.println("\n---------------------------------------\nZähle alle Einträge");		
		iFound = daoImmutabletext.count();
		System.out.println("Anzahl gefundener Immutabletext - Einträge nach Erzeugen eines Eintrags " + iFound);
		
		iFound = daoImmutabletext.countAllInherited();
		System.out.println("Anzahl gefundener Immutabletext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Erzeugen eines Eintrags " + iFound);
		
		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Erzeugen eines Eintrags " + iFound);

		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Erzeugen eines Eintrags " + iFound);
	
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE (TILEIMMUTABLETEXT) VORAB");
		daoTileImmutabletext.deleteAll();
		
		//Zähle die Anzahl der Einträge
		iFound = daoImmutabletext.count();//Der Count liefert alle Elemente auch der Kindklasse wieder. Also: Neues Entitiy TextDefaulttext, erbend aus Defaulttext.
		//									Entsprechend auch TextImmutabletextDao als neue Klasse.
		System.out.println("Anzahl gefundener Immutabletext - Einträge nach Löschung aller TILEImmutabletext - Einträge: " + iFound);
		
		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Löschung aller TILEImmutabletext - Einträge: " + iFound);

		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Löschung aller TILEImmutabletext - Einträge: " + iFound);
		
		
		System.out.println("\n---------------------------------------\nErzeuge einen Eintrag in  TileImmutabletext");		
		objDebug.debugCreateEntry_TileImmutabletext(0);
		
		
		System.out.println("\n--------------------------------------------\nErzeuge jeweil weitere Einträge in TileImmutabletext / Immutabletext / TextImmutabletext");		
		objDebug.debugCreateEntry_TileImmutabletext(1);
		objDebug.debugCreateEntry_Immutabletext(1);
				
		//Zähle die Anzahl der Einträge
		System.out.println("\n---------------------------------------\nZähle alle Einträge");	
		iFound = daoImmutabletext.count();
		System.out.println("Anzahl gefundener Immutabletext - Einträge nach Erzeugen eines Eintrags " + iFound);
		
		iFound = daoImmutabletext.countAllInherited();
		System.out.println("Anzahl gefundener Immutabletext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Erzeugen eines Eintrags " + iFound);

		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Erzeugen eines Eintrags " + iFound);

		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Erzeugen eines Eintrags " + iFound);

		//LÖSCHE ALLE EINTRÄGE: TILEIMMUTABLETEXT 
		System.out.println("\n##########################\nLÖSCHE ALLE TILE(!)TEXT EINTRÄGE");
		daoTileImmutabletext.deleteAll();
		
		//Zähle die Anzahl der Einträge
		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Löschung aller TileImmutabletext - Einträge: " + iFound);
				
		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Löschung aller TileImmutabletext - Einträge: " + iFound);
		
		iFound = daoImmutabletext.count();
		System.out.println("Anzahl gefundener Immutabletext - Einträge nach Löschung aller TileImmutabletext - Einträge: " + iFound);
		
		 
//		//LÖSCHE ALLE EINTRÄGE ALS VORBEREITUNG ÜR DEN NÄCHSTEN SCHRITT... alle generieren
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE ALS VORBEREITUNG ZUM NEUEN GENERIEREN ALLER EINTRÄGE");
		daoImmutabletext.deleteAll();
		
//		//Zähle die Anzahl der Einträge
		iFound = daoImmutabletext.count();
		System.out.println("Anzahl gefundener Immutabletext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Löschung aller Einträge " + iFound);

		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Löschung aller Einträge " + iFound);

		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Löschung aller Einträge " + iFound);
			
		
//		//LÖSCHE ALLE EINTRÄGE ALS VORBEREITUNG ÜR DEN NÄCHSTEN SCHRITT... alle generieren
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE ALS VORBEREITUNG ZUM NEUEN GENERIEREN ALLER EINTRÄGE");
		daoTextImmutabletext.deleteAll();
		
//		//Zähle die Anzahl der Einträge
		iFound = daoImmutabletext.count();
		System.out.println("Anzahl gefundener Immutabletext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Löschung aller Einträge " + iFound);

		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Löschung aller Einträge " + iFound);

		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Löschung aller Einträge " + iFound);
		
		System.out.println("\n################################################################################");
		System.out.println("\n---------------------------------------\nErzeuge alle Einträge");
		objDebug.debugCreateEntriesAll();
		
		//Zähle die Anzahl der Einträge
		iFound = daoTileImmutabletext.count();
		System.out.println("Anzahl gefundener TileImmutabletext - Einträge nach Erzeugung aller Einträge: " + iFound);
				
		iFound = daoTextImmutabletext.count();
		System.out.println("Anzahl gefundener TextImmutabletext - Einträge nach Erzeugung aller Einträge: " + iFound);
		
		iFound = daoImmutabletext.count();
		System.out.println("Anzahl gefundener Immutabletext - Einträge nach Erzeugung aller Einträge: " + iFound);
		
		
		//#############################################################################################################################
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE (TEXTIMMUTABLETEXT) VORAB");
		daoTextImmutabletext.deleteAll();
		objDebug.debugCreateEntry_TextImmutabletext(0);
		objDebug.debugCreateEntry_TextImmutabletext(1);
				
		//##############################################################################################################################
		System.out.println("\n##########################\nSUCHE EINTRAG NACH THISKEY");
		int iThiskey = 10;
		objDebug.debugTileImmutabletextDao_searchImmutabletextByThiskey(iThiskey);
		
		iThiskey = 20;
		objDebug.debugTileImmutabletextDao_searchImmutabletextByThiskey(iThiskey);
		
		//TODO GOON 20171109
		//objDebug.debugFindColumnValueMax();
		//objDebug.debugFindColumnMinValue();
		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public boolean getEntrySetImmutabletextValues(){
		boolean bReturn = false;
		main:{
//			try {
				Immutabletext objValue = new Immutabletext();				
				Long lngObj = new Long(1);
				Collection<String> colsName = EnumZZZ.getNames(objValue.getThiskeyEnumClass());
				for(String s : colsName){
					System.out.println("Gefundener Enum-Name: " + s);			
					
					//Direktes Reinschreiben geht wieder nicht wg. "bound exception"
					//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, s);
					
					//Also: Klasse holen und danach CASTEN.
					Class<?> objClass = objValue.getThiskeyEnumClass();
					String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Typname: " + sName);
					
					String sShorttext = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Typkurztext: " + sShorttext);
					
					String sLongtext = EnumSetDefaulttextUtilZZZ.readEnumConstant_LongtextValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Typlangtext: " + sLongtext);
					
					String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundene Typdescription: " + sDescription);			

				}
//			} catch (ExceptionZZZ e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}//end main:
		return bReturn;	
	}
	
	public boolean getEntrySetTileImmutabletextValues(){
		boolean bReturn = false;
		main:{
//			try {
				TileImmutabletext objValue = new TileImmutabletext();				
				Long lngObj = new Long(1);
				Collection<String> colsName = EnumZZZ.getNames(objValue.getThiskeyEnumClass());
				for(String s : colsName){
					System.out.println("Gefundener Enum-Name: " + s);			
					
					//Direktes Reinschreiben geht wieder nicht wg. "bound exception"
					//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, s);
					
					//Also: Klasse holen und danach CASTEN.
					Class<?> objClass = objValue.getThiskeyEnumClass();
					String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintypname: " + sName);
					
					String sShorttext = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintypkurztext: " + sShorttext);
					
					String sLongtext = EnumSetDefaulttextUtilZZZ.readEnumConstant_LongtextValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintyplangtext: " + sLongtext);
					
					String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundene Description: " + sDescription);			

				}
//			} catch (ExceptionZZZ e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}//end main:
		return bReturn;	
	}
	
	public boolean getEntrySetTextImmutabletextValues(){
		boolean bReturn = false;
		main:{
//			try {
				TextImmutabletext objValue = new TextImmutabletext();				
				Long lngObj = new Long(1);
				Collection<String> colsName = EnumZZZ.getNames(objValue.getThiskeyEnumClass());
				for(String s : colsName){
					System.out.println("Gefundener Enum-Name: " + s);			
					
					//Direktes Reinschreiben geht wieder nicht wg. "bound exception"
					//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, s);
					
					//Also: Klasse holen und danach CASTEN.
					Class<?> objClass = objValue.getThiskeyEnumClass();
					String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Texttypname: " + sName);
					
					String sShorttext = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Texttypkurztext: " + sShorttext);
					
					String sLongtext = EnumSetDefaulttextUtilZZZ.readEnumConstant_LongtextValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundener Texttyplangtext: " + sLongtext);
					
					String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetTextTHM>) objClass, s);
					System.out.println("Gefundene Description: " + sDescription);			

				}
//			} catch (ExceptionZZZ e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}//end main:
		return bReturn;	
	}
	
	public boolean debugCreateEntry_Immutabletext(int iIndex){
		boolean bReturn = false;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");						
							
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				
				//###################
				//1. Speicher den Immtabletext
				//####################									
				Session session = objContextHibernate.getSessionOpen();
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				EnumImmutabletext[] objaType = Immutabletext.EnumImmutabletext.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				
				//####################################################################################################
				//Achtung: Hier unterscheidet sich der Immutabletext. 
				//Er hat keine Setter-Methoden und muss daher komplett im Konsturktor erzeugt werden.
				String sDescription = objaType[iIndex].getDescription();
				String sLongtext = objaType[iIndex].getLongtext();
				String sShorttext = objaType[iIndex].getShorttext();
				Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
				
				Immutabletext objValue = new Immutabletext(lngThiskey, sShorttext, sLongtext, sDescription);

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
				

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				bReturn = false;
			}	
			
		}//end main:
		return bReturn;	
	}
	
	/** Lies aus der Enumeration den dort hinterlegten "Defaulttext" aus.
	 *   Anschliessend diesen über ein Entity in der Datenbank persistieren.
	 *   
	 * @param iIndex
	 * @return
	 */
	public boolean debugCreateEntry_TileImmutabletext(int iIndex){
		boolean bReturn = false;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Diese MEthode hat darüber nciht zu entscheiden. objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Speicher den Immutablettext
				//####################									
				Session session = objContextHibernate.getSessionOpen();
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
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

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				bReturn = false;
			}						
		}//end main:
		return bReturn;	
	}
	
	/** Lies aus der Enumeration den dort hinterlegten "Defaulttext" aus.
	 *   Anschliessend diesen über ein Entity in der Datenbank persistieren.
	 *   
	 * @param iIndex
	 * @return
	 */
	public boolean debugCreateEntry_TextImmutabletext(int iIndex){
		boolean bReturn = false;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
									
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Diese MEthode hat darüber nciht zu entscheiden. objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Speicher den Defaulttext
				//####################								
				Session session = objContextHibernate.getSessionOpen();
				if(session == null) break main;
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				EnumTextImmutabletext[] objaType = TextImmutabletext.EnumTextImmutabletext.values();

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

				TextImmutabletext objValue = new TextImmutabletext(lngThiskey,sShorttext,sLongtext, sDescription);
				
			    
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
			
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				bReturn = false;
			}
			
		}//end main:
		return bReturn;	
	}
	
	public boolean debugCreateEntriesAll(){
		boolean bReturn = false;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Diese MEthode hat darüber nciht zu entscheiden. objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Hole das jeweilige Dao - Objekt
				//####################				
				TileImmutabletextDao daoTileText = new TileImmutabletextDao(objContextHibernate);
				int iTileTextCreated = daoTileText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TileTexte: " + iTileTextCreated);
				
				TextImmutabletextDao daoTextText = new TextImmutabletextDao(objContextHibernate);
				int iTextTextCreated = daoTextText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TextTexte: " + iTextTextCreated);
				
				ImmutabletextDao daoText = new ImmutabletextDao(objContextHibernate);
				int iTextCreated = daoText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte Texte: " + iTextCreated);
							
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				bReturn = false;
			}			
		}//end main:
		return bReturn;	
	}
	

	
	/** 
	 * 
	 * @param iIndex
	 * @return
	 */
	public boolean debugTileImmutabletextDao_searchImmutabletextByThiskey(int iThiskey){
		boolean bReturn = false;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Diese Methode hat darüber nciht zu entscheiden. objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Hole den Text
				//####################				
				TileImmutabletextDao daoText = new TileImmutabletextDao(objContextHibernate);
				
				Long lngThiskey = new Long(iThiskey);
				//IThiskeyValueZZZ objText = daoText.searchThiskey(lngThiskey);
				//Defaulttext objText = (Defaulttext) daoText.searchThiskey(lngThiskey);				
				//TileDefaulttext objValue = (TileDefaulttext) objText;
				KeyImmutable objKey = daoText.searchThiskey(lngThiskey);
				if(objKey==null){
					System.out.println("Thiskey='"+iThiskey+"' NICHT gefunden.");
				}else{
					TileImmutabletext objValue = (TileImmutabletext) objKey;
					
					String sDescription = objValue.getDescription();
					String sShorttext = objValue.getShorttext();				
					String sLongtext = objValue.getLongtext();
					
					System.out.println("Thiskey='"+iThiskey+"' gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");
				}	
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				bReturn = false;
			}			
		}//end main:
		return bReturn;	
	}
	
	/** TODO GOON 20171114, das muss für die Defaultexte angepasst werden
	 * 
	 * @return
	 */
	public boolean debugFindColumnValueMax(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Diese MEthode hat darüber nciht zu entscheiden. objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TileDao daoTile = new TileDao(objContextHibernate);
				
				//Also nicht der Tabellenname, sondern das gemappte Objekt: TroopArmy für die Tabelle ARMY, auch nicht den Spaltennamen TILE_ID_INCREMENTED , sondern Id
				Integer intValue = daoTile.findColumnValueMax("TroopArmy", "id");
				System.out.println("Maximale Id: " + intValue.toString());
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
	
	/** TODO GOON 20171114, das muss für die Defaultexte angepasst werden
	 * 
	 * @return
	 */
	public boolean debugFindColumnMinValue(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Diese MEthode hat darüber nciht zu entscheiden. objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				TileDao daoTile = new TileDao(objContextHibernate);
				
				//Also nicht der Tabellenname, sondern das gemappte Objekt: TroopArmy für die Tabelle ARMY, auch nicht den Spaltennamen TILE_ID_INCREMENTED , sondern Id
				Integer intValue = daoTile.findColumnValueMin("TroopArmy", "id");
				System.out.println("Minimale Id: " + intValue.toString());
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}

}
