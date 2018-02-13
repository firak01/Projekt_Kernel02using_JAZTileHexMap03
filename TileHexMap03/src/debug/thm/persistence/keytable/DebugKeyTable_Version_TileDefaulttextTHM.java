package debug.thm.persistence.keytable;

import java.util.Collection;
import java.util.Set;

import org.hibernate.Session;

import use.thm.persistence.dao.DefaulttextDao;
import use.thm.persistence.dao.TextDefaulttextDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.Defaulttext.EnumDefaulttext;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TextDefaulttext.EnumTextDefaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import use.thm.persistence.model.TileDefaulttextType;
import use.thm.persistence.model.TileDefaulttextValue;
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
public class DebugKeyTable_Version_TileDefaulttextTHM {

	public static void main(String[] args) {
		try {
									
		DebugKeyTable_Version_TileDefaulttextTHM objDebug = new DebugKeyTable_Version_TileDefaulttextTHM();
		
		//Hole alle Einträge des Enums
		objDebug.getEntrySetDefaulttextValues();
		objDebug.getEntrySetTileDefaulttextValues();
		objDebug.getEntrySetTextDefaulttextValues();

		KernelZZZ objKernel = new KernelZZZ();			
		HibernateContextProviderSingletonTHM objContextHibernate;		
		objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
		objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.							

		int iFound = -1;
									
		TileDefaulttextDao daoTiletext = new TileDefaulttextDao(objContextHibernate);	
		TextDefaulttextDao daoTexttext = new TextDefaulttextDao(objContextHibernate);		
		DefaulttextDao daoDefaulttext = new DefaulttextDao(objContextHibernate);
		
		//LÖSCHE ALLE EINTRÄGE
		//Lösche alle erzeugten Einträge vor dem Test. D.h. es muss eine DAO-Klasse für die DefaultValues geben
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE VORAB");
		daoDefaulttext.deleteAll();//Das reicht "Normalerweise", wg. der "Hibernate-Vererbung" der Model Klasse ... Diese Vererbungsstruktur spiegelt sich dann auch in den Dao-Klassen wieder.
		//                           ABER: Wg. der KeyType Einschränkung, wird wirklich nur gelöscht, was für diese DAO-Klasse relevant ist.
				
		//Zähle die Anzahl der Einträge
		iFound = daoDefaulttext.count();//Der Count liefert alle Elemente auch der Kindklasse wieder. Also: Neues Entitiy TextDefaulttext, erbend aus Defaulttext.
		//								  Entsprechend auch TextDefaulttextDao als neue Klasse.
		//TODO GOON: Wie auch beim Löschen, so beim count() den KeyType einbeziehen.		
		System.out.println("Anzahl gefundener Defaulttext - Einträge nach Löschung aller Defaulttext - Einträge: " + iFound);
		
		iFound = daoTiletext.count();
		System.out.println("Anzahl gefundener TileDefaulttext - Einträge nach Löschung aller Defaulttext - Einträge: " + iFound);

		iFound = daoTexttext.count();
		System.out.println("Anzahl gefundener TextDefaulttext - Einträge nach Löschung aller Defaulttext - Einträge: " + iFound);
		
		//Erzeuge nun für die Tabellen jeweils einen Eintrag
		System.out.println("\n---------------------------------------\nErzeuge einen Eintrag in  Defaultttext");		
		objDebug.debugCreateEntry_Defaulttext(0);
				
		//Zähle die Anzahl der Einträge
		System.out.println("\n---------------------------------------\nZähle alle Einträge");		
		iFound = daoDefaulttext.count();
		System.out.println("Anzahl gefundener Defaulttext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Erzeugen eines Eintrags " + iFound);
		
		iFound = daoTiletext.count();
		System.out.println("Anzahl gefundener TileDefaulttext - Einträge nach Erzeugen eines Eintrags " + iFound);

		iFound = daoTexttext.count();
		System.out.println("Anzahl gefundener TextDefaulttext - Einträge nach Erzeugen eines Eintrags " + iFound);
	
		System.out.println("\n--------------------------------------------\nErzeuge jeweil weitere Einträge in TileDefaulttext / Defaultttext / Texttext");		
		objDebug.debugCreateEntry_Defaulttext(1);
		
		//LÖSCHE ALLE EINTRÄGE: TEXTTEXT 
		System.out.println("\n##########################\nLÖSCHE ALLE TEXT(!)TEXT EINTRÄGE");
		daoTexttext.deleteAll();
		
		//LÖSCHE ALLE EINTRÄGE: TILETEXT 
		System.out.println("\n##########################\nLÖSCHE ALLE TILE(!)TEXT EINTRÄGE");
		daoTiletext.deleteAll();			
		
		
		//Zähle die Anzahl der Einträge
		System.out.println("\n---------------------------------------\nZähle alle Einträge");	
		iFound = daoDefaulttext.count();
		System.out.println("Anzahl gefundener Defaulttext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Erzeugen eines Eintrags " + iFound);

		iFound = daoTiletext.count();
		System.out.println("Anzahl gefundener Tiletext - Einträge nach Erzeugen eines Eintrags " + iFound);

		iFound = daoTexttext.count();
		System.out.println("Anzahl gefundener Texttext - Einträge nach Erzeugen eines Eintrags " + iFound);

		
		objDebug.debugCreateEntry_TextDefaulttext(0);		
		objDebug.debugCreateEntry_TextDefaulttext(1);
		
		objDebug.debugCreateEntry_TileDefaulttext(0);
		objDebug.debugCreateEntry_TileDefaulttext(1);
		
		
		//Zähle die Anzahl der Einträge
		iFound = daoTiletext.count();
		System.out.println("Anzahl gefundener Tiletext - Einträge nach Erstellung von 2 Tiletext - Einträge: " + iFound);
				
		iFound = daoTexttext.count();
		System.out.println("Anzahl gefundener Texttext - Einträge nach Erstellung von 2 Textext - Einträge: " + iFound);
		
		iFound = daoDefaulttext.count();
		System.out.println("Anzahl gefundener Defaulttext - Einträge: " + iFound);
		
		 
		//LÖSCHE ALLE EINTRÄGE ALS VORBEREITUNG ÜR DEN NÄCHSTEN SCHRITT... alle generieren
		System.out.println("\n##########################\nLÖSCHE ALLE EINTRÄGE ALS VORBEREITUNG ZUM NEUEN GENERIEREN ALLER EINTRÄGE");
		daoDefaulttext.deleteAllInherited();
		
		//Zähle die Anzahl der Einträge
		iFound = daoDefaulttext.count();
		System.out.println("Anzahl gefundener Defaulttext - Einträge (UND DER DARAUS ERBENDEN ENTITIES) nach Löschung aller Einträge " + iFound);

		iFound = daoTiletext.count();
		System.out.println("Anzahl gefundener TileDefaulttext - Einträge nach Löschung aller Einträge " + iFound);

		iFound = daoTexttext.count();
		System.out.println("Anzahl gefundener TextDefaulttext - Einträge nach Löschung aller Einträge " + iFound);
			
		System.out.println("\n---------------------------------------\nErzeuge alle Einträge");
		objDebug.debugCreateEntriesAll();
		
		//Zähle die Anzahl der Einträge
		iFound = daoTiletext.count();
		System.out.println("Anzahl gefundener Tiletext - Einträge nach Erzeugung aller Einträge: " + iFound);
				
		iFound = daoTexttext.count();
		System.out.println("Anzahl gefundener Texttext - Einträge nach Erzeugung aller Einträge: " + iFound);
		
		iFound = daoDefaulttext.count();
		System.out.println("Anzahl gefundener Defaulttext - Einträge nach Erzeugung aller Einträge: " + iFound);
		
		//##############################################################################################################################
		System.out.println("\n##########################\nSUCHE EINTRAG NACH THISKEY");
		int iThiskey = 10;
		objDebug.debugTileDefaulttextDao_searchDefaulttextByThiskey(iThiskey);
		
		iThiskey = 20;
		objDebug.debugTileDefaulttextDao_searchDefaulttextByThiskey(iThiskey);
		
		//TODO GOON 20171109
		//objDebug.debugFindColumnValueMax();
		//objDebug.debugFindColumnMinValue();
		
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public boolean getEntrySetDefaulttextValues(){
		boolean bReturn = false;
		main:{
//			try {
				Defaulttext objValue = new Defaulttext();				
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
	
	public boolean getEntrySetTileDefaulttextValues(){
		boolean bReturn = false;
		main:{
//			try {
				TileDefaulttext objValue = new TileDefaulttext();				
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
	
	public boolean getEntrySetTextDefaulttextValues(){
		boolean bReturn = false;
		main:{
//			try {
				TextDefaulttext objValue = new TextDefaulttext();				
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
	
	public boolean debugCreateEntry_Defaulttext(int iIndex){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Speicher den Defaulttext
				//####################					
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				EnumDefaulttext[] objaType = Defaulttext.EnumDefaulttext.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				Defaulttext objValue = new Defaulttext();
				String sDescription = objaType[iIndex].getDescription();
				objValue.setDescription(sDescription);
				
				String sShorttext = objaType[iIndex].getShorttext();
				objValue.setShorttext(sShorttext);
				
				String sLongtext = objaType[iIndex].getLongtext();
				objValue.setLongtext(sLongtext);
				
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
				objValue.setThiskey(lngThiskey);
			    
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
	
	/** Lies aus der Enumeration den dort hinterlegten "Defaulttext" aus.
	 *   Anschliessend diesen über ein Entity in der Datenbank persistieren.
	 *   
	 * @param iIndex
	 * @return
	 */
	public boolean debugCreateEntry_TileDefaulttext(int iIndex){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Speicher den Defaulttext
				//####################					
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				EnumTileDefaulttext[] objaType = TileDefaulttext.EnumTileDefaulttext.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				TileDefaulttext objValue = new TileDefaulttext();
				String sDescription = objaType[iIndex].getDescription();
				objValue.setDescription(sDescription);
				
				String sShorttext = objaType[iIndex].getShorttext();
				objValue.setShorttext(sShorttext);
				
				String sLongtext = objaType[iIndex].getLongtext();
				objValue.setLongtext(sLongtext);
				
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
				objValue.setThiskey(lngThiskey);
			    
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
	
	/** Lies aus der Enumeration den dort hinterlegten "Defaulttext" aus.
	 *   Anschliessend diesen über ein Entity in der Datenbank persistieren.
	 *   
	 * @param iIndex
	 * @return
	 */
	public boolean debugCreateEntry_TextDefaulttext(int iIndex){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Speicher den Defaulttext
				//####################					
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				EnumTextDefaulttext[] objaType = TextDefaulttext.EnumTextDefaulttext.values();

				//String s = objaType[0].name(); //Prasenzstudium .... also entsprechend was als Eigenschaft vorgeschlagen wird von TileDefaulttextType.Praesenzstudium
				//String s = objaType[0].toString(); //dito
				//String s = objaType[0].description(); //gibt es nicht, das @description wohl nur etwas für Tool ist, welches diese Metasprachlichen Annotiations auswertet.
				String s = objaType[0].name();
				System.out.println("debugCreateEntry für ... " + s);
				
				TextDefaulttext objValue = new TextDefaulttext();
				String sDescription = objaType[iIndex].getDescription();
				objValue.setDescription(sDescription);
				
				String sShorttext = objaType[iIndex].getShorttext();
				objValue.setShorttext(sShorttext);
				
				String sLongtext = objaType[iIndex].getLongtext();
				objValue.setLongtext(sLongtext);
				
			    Long lngThiskey = objaType[iIndex].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
				objValue.setThiskey(lngThiskey);
			    
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
	
	public boolean debugCreateEntriesAll(){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Hole das jeweilige Dao - Objekt
				//####################				
				TileDefaulttextDao daoTileText = new TileDefaulttextDao(objContextHibernate);
				daoTileText.deleteAll();//Bevor man alles neu erstellt, erst einmal alles löschen, sonst bekommtm man Constraint Verletzungen, wg. doppelter Schlüssel.						
				int iTileTextCreated = daoTileText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TileTexte: " + iTileTextCreated);
				
				TextDefaulttextDao daoTextText = new TextDefaulttextDao(objContextHibernate);
				daoTextText.deleteAll();//Bevor man alles neu erstellt, erst einmal alles löschen, sonst bekommtm man Constraint Verletzungen, wg. doppelter Schlüssel.
				int iTextTextCreated = daoTextText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TextTexte: " + iTextTextCreated);
				
				DefaulttextDao daoText = new DefaulttextDao(objContextHibernate);
				daoText.deleteAll();//Bevor man alles neu erstellt, erst einmal alles löschen, sonst bekommtm man Constraint Verletzungen, wg. doppelter Schlüssel.
				int iTextCreated = daoText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte Texte: " + iTextCreated);
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
		}//end main:
		return bReturn;	
	}
	

	
	/** 
	 * 
	 * @param iIndex
	 * @return
	 */
	public boolean debugTileDefaulttextDao_searchDefaulttextByThiskey(int iThiskey){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Hole den Text
				//####################				
				TileDefaulttextDao daoText = new TileDefaulttextDao(objContextHibernate);
				
				Long lngThiskey = new Long(iThiskey);
				//IThiskeyValueZZZ objText = daoText.searchThiskey(lngThiskey);
				//Defaulttext objText = (Defaulttext) daoText.searchThiskey(lngThiskey);				
				//TileDefaulttext objValue = (TileDefaulttext) objText;
				Key objKey = daoText.searchThiskey(lngThiskey);
				if(objKey==null){
					System.out.println("Thiskey='"+iThiskey+"' NICHT gefunden.");
				}else{
					TileDefaulttext objValue = (TileDefaulttext) objKey;
					
					String sDescription = objValue.getDescription();
					String sShorttext = objValue.getShorttext();				
					String sLongtext = objValue.getLongtext();
					
					System.out.println("Thiskey='"+iThiskey+"' gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");
				}	
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
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
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
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
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
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
