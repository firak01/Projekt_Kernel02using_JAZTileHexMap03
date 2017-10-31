package debug.thm.persistence.keytable;

import java.util.Collection;
import java.util.Set;

import org.hibernate.Session;

import use.thm.persistence.dao.TileDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetDefaulttextTHM;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import use.thm.persistence.model.TileDefaulttextType;
import use.thm.persistence.model.TileDefaulttextValue;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.abstractEnum.EnumSetDefaulttextTestTypeTHM;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumZZZ;
import basic.zKernel.KernelZZZ;
/** In dieser Variante werden innere Klassen verwendet, welche die Enumation sind.
 *  
 * @author Fritz Lindhauer
 *
 */
public class DebugKeyTable_Version_TileDefaulttextTHM {

	public static void main(String[] args) {
		DebugKeyTable_Version_TileDefaulttextTHM objDebug = new DebugKeyTable_Version_TileDefaulttextTHM();
		
		//Hole alle Einträge des Enums
		objDebug.getEntrySetDefaultValues();
		
		//TODO GOON: Lösche alle erzeugten Einträge vor dem Test. D.h. es muss eine DAO-Klasse für die DefaultValues geben	
		//objDebug.debugCreateEntry();
	
		
		//TODO GOON: Lösche alle erzeugten Einträge vor dem Test.  D.h. es muss eine DAO-Klasse für die DefaultValues geben		
		objDebug.debugCreateEntriesAll();
		
	
		
		//####################
		//objDebug.debugFindColumnValueMax();
		//objDebug.debugFindColumnMinValue();
	}
	
	public boolean getEntrySetDefaultValues(){
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
					String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintypname: " + sName);
					
					String sShorttext = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintypkurztext: " + sShorttext);
					
					String sLongtext = EnumSetDefaulttextUtilZZZ.readEnumConstant_LongtextValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintyplangtext: " + sLongtext);
					
					String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundene Description: " + sDescription);			

				}
//			} catch (ExceptionZZZ e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}//end main:
		return bReturn;	
	}
	
	public boolean debugCreateEntry(){
		boolean bReturn = false;
		main:{
			System.out.println("debugCreateEntry: START ##############");			
			
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
				String sDescription = objaType[0].getDescription();
				objValue.setDescription(sDescription);
				
				String sShorttext = objaType[0].getShorttext();
				objValue.setShorttext(sShorttext);
				
				String sLongtext = objaType[0].getLongtext();
				objValue.setLongtext(sLongtext);
				
			    Long lngThiskey = objaType[0].getThiskey(); //Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
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
			System.out.println("debugCreateEntry: ENDE ##############");			
			
			
		}//end main:
		return bReturn;	
	}
	
	
	public boolean debugCreateEntriesAll(){
		boolean bReturn = false;
		main:{
			System.out.println("debugCreateEntriesAll: START ##############");			
			
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
									
				Collection<String> colsName = EnumZZZ.getNames(TileDefaulttext.getThiskeyEnumClassStatic());
				for(String s : colsName){
					System.out.println("Starte Transaction:.... Gefundener Enum-Name: " + s);
					TileDefaulttext objValue = new TileDefaulttext();		//Bei jedem Schleifendurchlauf neu machen, sonst wird lediglich nur 1 Datensatz immer wieder verändert.
					session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
					
					//Direktes Reinschreiben geht wieder nicht wg. "bound exception"
					//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, s);
					
					//Also: Klasse holen und danach CASTEN.
					Class<?> objClass = objValue.getThiskeyEnumClass();
					String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintypname: " + sName);
					
					String sShorttext = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintypkurztext: " + sShorttext);
					objValue.setShorttext(sShorttext);
					
					String sLongtext = EnumSetDefaulttextUtilZZZ.readEnumConstant_LongtextValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundener Spielsteintyplangtext: " + sLongtext);
					objValue.setLongtext(sLongtext);
					
					
					String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetDefaulttextTHM>) objClass, s);
					System.out.println("Gefundene Description: " + sDescription);			
					objValue.setDescription(sDescription);
					
				    Long lngThiskey = EnumSetDefaulttextUtilZZZ.readEnumConstant_ThiskeyValue((Class<IEnumSetDefaulttextTHM>) objClass, s);//Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
				    System.out.println("Gefundener Thiskey: " + lngThiskey.toString());	
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
				}//end for
				
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("debugCreateEntriesAll: ENDE ##############");			
			
			
		}//end main:
		return bReturn;	
	}
	
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
