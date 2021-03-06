package use.thm.persistence.daoFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Session.LockRequest;
import org.hibernate.Transaction;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventType;

import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopFleetDao;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopType;
import use.thm.persistence.model.TroopVariant;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.model.TroopArmyRuleType;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopArmyDaoFacade extends TileDaoFacade{
	private TroopArmy objTroopArmy = null;
	
	public TroopArmyDaoFacade(IHibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	
	
	@Override
	public String insert(TroopVariant objTroopVariant, HexCell objCell) throws ExceptionZZZ {
		String sFacadeType = this.getFacadeType();
		String sUniquename= this.computeUniquename(sFacadeType);
		
		TroopArmyVariant objTroopArmyVariant = (TroopArmyVariant) objTroopVariant;
		AreaCell objArea = (AreaCell) objCell;
		boolean bReturn = this.insertTroopArmy(sUniquename, objTroopArmyVariant, objArea);
		if(bReturn){
			return sUniquename;
		}else{
			return null;
		}
	}
	
	public String insert(TroopArmyVariant objTroopArmyVariant, HexCell objCell) throws ExceptionZZZ {
		String sFacadeType = this.getFacadeType();
		String sUniquename= this.computeUniquename(sFacadeType);
		
		AreaCell objArea = (AreaCell) objCell;
		boolean bReturn = this.insertTroopArmy(sUniquename, objTroopArmyVariant, objArea);
		if(bReturn){
			return sUniquename;
		}else{
			return null;
		}
	}
	
	public boolean insertTroopArmy(String sUniqueName, TroopArmyVariant objTroopArmyVariant, AreaCell objArea) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			check:{
				//Fehler können z.B. auftreten, wenn die Datenbank komplett leer ist... Dies hier abfangen.
				if(objTroopArmyVariant==null){
					String stemp = "Keine TroopArmyVariant übergeben.";
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
					ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;		
				}
				if(objArea==null){
					String stemp = "Keine Area übergeben.";
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
					ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;		
				}			
			}//end check:
			Integer intSubtypeUniqueNumberUsed  = null;
			Integer intVariantUniqueNumberUsed  = null;			
			additionalData:{
				//Hole die bisher höchste Zahl der Varianten 			
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
				TroopArmyDao objTroopDao = new TroopArmyDao(objContextHibernate);
				
				///!!! Das reicht aus nicht aus... Intern wird wohl Es muss die ZhisId der Variante als WHERE Teil einbezogen werden.
				//Merke: .findColumnValueMax("instanceVariantUniquenumber"); macht nur Unterscheidung zwischen Army und Fleet. Feinere Variantenunterscheidung muss scheitern.
				//TODO GOON 20180322: Integer intVariantUniqueNumber = objTroopDao.findColumnValueMaxForVariant("instanceVariantUniquenumber", intThisIdOfVariantUsed); 
				Integer intSubtypeUniqueNumber = objTroopDao.findColumnValueMax("instanceSubtypeUniquenumber"); 
				if(intSubtypeUniqueNumber==null){
					intSubtypeUniqueNumberUsed = new Integer(1);
				}else{
					int itemp = intSubtypeUniqueNumber.intValue() + 1;
					intSubtypeUniqueNumberUsed = new Integer(itemp);
				}
				

//				try{
				Map<String, Object> whereBy = new HashMap<String, Object>();
				
				//########### FEHLER: could not resolve property: trooparmyvariant_thiskey_id of: use.thm.persistence.model.TroopArmy [select max(instanceVariantUniquenumber) from use.thm.persistence.model.TroopArmy g where g.trooparmyvariant_thiskey_id = :trooparmyvariant_thiskey_id]
				//whereBy.put("trooparmyvariant_thiskey_id",11);
				//Also: Mit demObjekt selbst arbeiten.
				
				TroopArmyVariant objTroopArmyVariantSearchedFor = objTroopArmyVariant;
				whereBy.put("troopArmyVariantObject",objTroopArmyVariantSearchedFor);
				Integer intCategoryUniqueNumber = objTroopDao.findColumnValueMax("instanceVariantUniquenumber",whereBy); 
				if(intCategoryUniqueNumber==null){ //Merke: Wenn es noch keine Variante der Truppe auf der KArte gibt, dann ist der ermittelte Wert 'null'
					intVariantUniqueNumberUsed = new Integer(1);
				}else{
					int itemp = intCategoryUniqueNumber.intValue() + 1;
					intVariantUniqueNumberUsed = new Integer(itemp);
				}
				System.out.println("############ Errechneter neuer max der übergebenen Troopvariant  ist: " + intVariantUniqueNumberUsed);
				
//				}catch(Exception e){
//					System.out.println("########### FEHLER: " + e.getMessage());
//				}
				
			}
		
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
						
			//###################
			//1. Speicher die TroopArmy neu, füge die Area der TroopArmy hinzu, damit sie weiss in welchem Feld sie steht.
			//####################		
			
			//+++ DAS ENTITY UND VERBUNDENE OBJEKTE  ++++++++++++++++++++
			Session session = this.getSessionOpen();
			if(session == null) break main;	
//			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Starte Transaction:....");
//			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
			TroopArmy objTroopTemp = new TroopArmy(new TileId("EINS", "1", sUniqueName));									
//			session.update(objArea);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
//			session.getTransaction().commit();					
			objTroopTemp.setHexCell(objArea); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
						
			//Füge Variante der Truppe hinzu wg n:1 Beziehung. ABER: Diese muss schon zuvor geholt worden sein, sonst überschneiden sich die Transaktionen.
			objTroopTemp.setTroopArmyVariantObject(objTroopArmyVariant);
			objTroopTemp.setInstanceVariantUniquenumber(intVariantUniqueNumberUsed); //die muss zuvor ausgerechnet worden sein.
			objTroopTemp.setInstanceSubtypeUniquenumber(intSubtypeUniqueNumberUsed); //die muss zuvor ausgerechnet worden sein.
			
			//Füge die Initialwerte der Variante hinzu.
			Float fltHealthInitial = objTroopArmyVariant.getHealthInitial();
			objTroopTemp.setHealth(fltHealthInitial.floatValue());
			
			//+++ DAS ENTITY SELBST  ++++++++++++++++++++
//			session = this.getSessionOpen();
//			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Starte Transaction:....");
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.

			//DAS ERSTELLDATUM ++++++++++++++++++++++++++++++++++++
			this.makeCreatedDates(objTroopTemp);
			
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() +": Versuche eine TroopArmy zu speichern.");
			session.save(objTroopTemp); //Hibernate Interceptor wird aufgerufen																				
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"save",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
		
			//###################
			//2. Aktualisiere die Area-Zelle, setze den "Besitzer" in das Gebiet.
			//####################			
			if(objArea instanceof AreaCell){
				session = this.getSessionCurrent();		//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.					 
				
				AreaCellLand objAreaTemp = (AreaCellLand) objArea;
				int iPlayer = objTroopTemp.getPlayer();
				objAreaTemp.setPlayerOwner(iPlayer);							

				//Die Area wurde ja schon persistiert, also hier update und nicht save.
				session.update(objAreaTemp); //SaveAndUpdate-Listener wird NICHT(!) ausgeführt, aber es gibt keine constraint verletzung ... update
				if (!session.getTransaction().wasCommitted()) {
					//session.flush();								
					session.getTransaction().commit();///SaveAndUpdate-Listener NICHT ausgeführt. //TODO GOON: Probiere ob 'AbstractFlushingEventListener' ausgeführt würde.
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
					if(objResult!=null){
						sMessage = objResult.getVetoMessage();
						bGoon = !objResult.isVeto();
					}else{
						//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
						bGoon = true;
					}
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			
			//###################
			//3. Aktualisiere die Hex-Zelle, füge die TroopArmy der Liste hinzu, damit die Hex-Zelle weiss welche TroopArmies in ihr stehen.
			//####################		
			//session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			session = this.getSessionOpen();
			if(session == null) break main;		
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();
			
			//Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session
			//http://stackoverflow.com/questions/18956825/hibernate-collection-is-not-associated-with-any-session
			//session.refresh(objCellTemp);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			session.refresh(objArea);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			
			//weil Lazy - Loading eingestellt ist an dieser Stelle erst mal sehn, die Collection zu bekommen
			//objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
			//Collection<Tile>colTile = objCellTemp.getTileBag();
			Collection<Tile>colTile = objArea.getTileBag();
						
			Hibernate.initialize(colTile);
			if(colTile!=null){
				colTile.add(objTroopTemp);
			}
	
			session.update(objArea); //SaveAndUpdate-Listener wird nicht ausgeführt
			if (!session.getTransaction().wasCommitted()) {
				//session.flush();								
				session.getTransaction().commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				
				//FGL 20180215: Fehlermeldung beim Versuch mit DateTimestamp und @Version
				//Exception in thread "main" org.hibernate.TransientObjectException: object references an unsaved transient instance - save the transient instance before flushing: use.thm.persistence.model.Tile
				//also rausnehmen des flushing.....  
				//session.flush(); //versuch folgendes zu
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}

			//Falls alles glatt durchgeht....
			this.setEntityUsed(objTroopTemp);
			bReturn = true;
		}//end validEntry:
					
		}//end main:
		return bReturn;
	}
	
	@Override
	public boolean move(Troop objTroop, HexCell objCell) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean updateTroopArmyPosition(String sUniqueName, AreaCell objAreaTarget) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### UPDATE TROOPARMYPOSITION ####################");
			
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################											
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopArmyDao objTroopArmyDao = new TroopArmyDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopArmy objTroopArmy = objTroopArmyDao.searchTroopArmyByUniquename(sUniqueName);

			//#############################
			//2. Mache die vorgeschaltete Validierung
			//############################# 
			//Wichtig: Vor dem ganzen Ablauf die Validierung durchführen, sonst muss man nach der Validierung wieder alles rückgängig machen.
			TroopArmyRuleFacade objRuleFacade = new TroopArmyRuleFacade(getHibernateContext(), objTroopArmy);
			bGoon = objRuleFacade.onUpdateTroopArmyPosition(objAreaTarget,"PREINSERT");//Die PREINSERT  Angaebe ist notwendig wg. anderen Stacking Limits. Mögliche Werte UPDATE / PREINSERT.
			if(!bGoon){	
				//NEGATIVES Ergebnis der vorgeschalteten Validierung
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vorgeschaltete Validierung NICHT erfolgreich");
				
				//Hole die Meldungen aus dem Regelwerk ab.			
				sMessage = objRuleFacade.getMessagesAsString();

				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}//Vorgeschaltetet Validierung
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vorgeschaltete Validierung erfolgreich");
			
			//#############################
			//3. Hole die Backendentsprechung der Ausgangszelle, daraus muss die TroopArmy entfernt werden.
			//############################# 
			AreaCellDao objAreaDaoSource = new AreaCellDao(objContextHibernate);
			int iXStarted = objTroopArmy.getMapX();
			int iYStarted = objTroopArmy.getMapY();
			CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
			AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			
			//#############################
			//4. Mache die Datenbankoperationen
			//############################# 
			
				//+++ 4.1 Datenbankoperationen: Entferne aus der Ausgangszelle
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
				Session session = this.getSessionOpen();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
					
				//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten/genutzt worden ist.
				//session.update(objCellStarted);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
				PersistentBag pbag = new PersistentBag((SessionImplementor) session, objCellStarted.getTileBag());
				boolean bSuccessfulRemoved = pbag.remove(objTroopArmy);//Aber damit das funktioniert war eigentlich das Überschreiben der equals() Methode in Troop - Entity wichtig und notwendig
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ergebnis des Entfernenversuchs: " + bSuccessfulRemoved);
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
				//session.saveOrUpdate(objCellStarted); //Hibernate Interceptor wird aufgerufen
				session.update(objCellStarted); //Hibernate Interceptor wird aufgerufen			
				session.flush();
				session.getTransaction().commit();
				if (!session.getTransaction().wasCommitted()) {
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					
					//VErsuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
					
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
					if(objResult!=null){
						sMessage = objResult.getVetoMessage();
						bGoon = !objResult.isVeto();
					}else{
						//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
						bGoon = true;
					}
				}			
				if(!bGoon){
					//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
					this.getFacadeResult().setMessage(sMessage);
					break validEntry;
				}
			
			
			//#############################
			//Hole die Backendentsprechung der Zielzelle
			//#############################
			/*nicht notwendig, wird von aussen übergeben, LASS DEN CODE ABER STEHEN, ZUR ANSCHAUUNG
			AreaCellDao objAreaDao = new AreaCellDao(objContextHibernate);
			int iXDropped = objArea.getMapX();
			int iYDropped = objArea.getMapY();
			CellId primaryKeyCell = new CellId("EINS", Integer.toString(iXDropped), Integer.toString(iYDropped));
			AreaCell objCellTarget = objAreaDao.findByKey(primaryKeyCell);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			*/ 
			   
			//+++ 4.2: Datenbankoperation: Aktualisiere die Troop mit der neuen Position/dem neuien HexFeld.		
			session = this.getSessionOpen();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
			session.update(objAreaTarget);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
			objTroopArmy.setHexCell(objAreaTarget); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
			session.flush();
			session.getTransaction().commit();
			
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			//wenn man .save() aufruft, wird immer eine neue Zeile mit dem Objekt erzeugt, auch wenn man ein bestehendes aktualisieren möchte session.save(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			//Alternative: Siehe https://stackoverflow.com/questions/30473707/hibernate-creates-new-row-on-save-and-not-updates
			//session.saveOrUpdate(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			session = this.getSessionOpen();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;		
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			session.update(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			session.flush();
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				
				//Versuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Aktuelle Position der Army (X/Y): " + objTroopArmy.getMapX() + "/" + objTroopArmy.getMapY());
			
			
			/* ########## TODO GOON: DAS ERST MAL NOCH AUSKLAMMERN. ERST MUSS DIE ARMY ECHT DIE NEUE POSITION HABEN 
			//###################
			//2. Aktualisiere die Area-AUSGANGS-Zelle, Besitzer bleibt, aber die TroopArmy muss aus der Collection rausgenommen werden.
			//###################
			//TODO GOON 20170713
			
			//###################
			//2. Aktualisiere die Area-ZIEL-Zelle, setze den "Besitzer" in das Gebiet.
			//####################			
			if(objArea instanceof AreaCell){
				session = this.getSession();		//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");				
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.					 
				
				AreaCellLand objAreaTemp = (AreaCellLand) objArea;
				int iPlayer = objTroopArmy.getPlayer();
				objAreaTemp.setPlayerOwner(iPlayer);							

				//Die Area wurde ja schon persistiert, also hier update und nicht save.
				session.update(objAreaTemp); //SaveAndUpdate-Listener wird NICHT(!) ausgeführt, aber es gibt keine constraint verletzung ... update
				if (!session.getTransaction().wasCommitted()) {
					//session.flush();								
					session.getTransaction().commit();///SaveAndUpdate-Listener NICHT ausgeführt. //TODO GOON: Probiere ob 'AbstractFlushingEventListener' ausgeführt würde.
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			*/
			
			//++++++++ 4.3 Datanbankoperationen Aktualisiere die Hex-Zelle, füge die TroopArmy der Liste hinzu, damit die Hex-Zelle weiss welche TroopArmies in ihr stehen.
			session = this.getSessionOpen();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;		
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();
			
			//Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session
			//http://stackoverflow.com/questions/18956825/hibernate-collection-is-not-associated-with-any-session
			//session.refresh(objCellTemp);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			session.refresh(objAreaTarget);///wg. Exception in thread "AWT-EventQueue-0" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session //liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			
			//weil Lazy - Loading eingestellt ist an dieser Stelle erst mal sehn, die Collection zu bekommen
			//objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
			//Collection<Tile>colTile = objCellTemp.getTileBag();
			Collection<Tile>colTile = objAreaTarget.getTileBag();				
			Hibernate.initialize(colTile);//Exception in thread "AWT-EventQueue-0" org.hibernate.HibernateException: collection is not associated with any session
			if(colTile!=null){
				colTile.add(objTroopArmy);
			}
	
			//session.saveOrUpdate(objAreaTarget); //SaveAndUpdate-Listener wird nicht ausgeführt
			session.update(objAreaTarget); //SaveAndUpdate-Listener wird nicht ausgeführt
			session.flush();
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				
				//session.getTransaction().commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				//session.flush(); //versuch folgendes zu 
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			
			
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}

			/*
			//###VERZEWEIFELUNG 
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");		
			session.getTransaction().begin();
			session.flush(); //versuch folgendes zu 
			if (!session.getTransaction().wasCommitted()) {
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			*/
			
			//Falls alles glatt durchgeht....
			bReturn = true;
		}//end validEntry:		
		}//end main:
		return bReturn;
	}
	
	public boolean delete(String sUniquename) throws ExceptionZZZ{
		return this.deleteTroopArmy(sUniquename);
	}
	
	@Override
	public boolean delete(Troop objTroop) throws ExceptionZZZ {
		TroopArmy objTroopArmy = (TroopArmy) objTroop;
		return this.deleteTroopArmy(objTroopArmy);
	}
	
	public boolean deleteTroopArmy(String sUniqueName) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### DELETE TROOP: Uniquename '" + sUniqueName + "' ####################");
			
		  //###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################										
		  	IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopArmyDao objTroopArmyDao = new TroopArmyDao(objContextHibernate);
			
			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopArmy objTroopArmy = objTroopArmyDao.searchTroopArmyByUniquename(sUniqueName);
		    bReturn = this.deleteTroopArmy(objTroopArmy);
		    if(bReturn){
		    	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Armee gelöscht '" + sUniqueName + "'");
		    }else{
		    	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Armee NICHT gelöscht '" + sUniqueName + "'");
		    }
		  }//end main:
		  return bReturn;
		}
		
		public boolean deleteTroopArmy(TroopArmy objTroopArmy) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				boolean bGoon = false;
				String sMessage = new String("");
			
				//#############################
				//2. Hole die Backendentsprechung der Ausgangszelle, daraus muss die TroopArmy entfernt werden.
				//############################# 
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
				Session session = null;
				AreaCellDao objAreaDaoSource = new AreaCellDao(objContextHibernate);
				int iXStarted = objTroopArmy.getMapX();
				int iYStarted = objTroopArmy.getMapY();
				CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
				AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
				
				System.out.println("Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
				if(objCellStarted.getTileBag().size()>=1){	
					//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
					//session = this.getSessionNew();//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
					session = this.getSessionOpen();
					if(session == null) break main;		
					
					//ALSO: Ich bin mir nicht sicher, ob objTroopArmy in der TileBag gefunden wird.
					//1. VErsuch: Darum nicht so einfach, statt dessen versuchen wieder ein Objekt im persistant state zu bekommen:
					//Merke: session.lock(object, lock_option) ist deprecated... 
					//LockRequest lockRequest = session.buildLockRequest(LockOptions.NONE);//versuch das hinzubekommen. Idee ist, das das Objetk jetzt keine PErsistant State mehr hat.   session2.lock(entity, LockMode.NONE);
					//lockRequest.lock(objTroopArmy);
					//boolean bSuccessfulRemoved = objCellStarted.getTileBag().remove(objTroopArmy);
					
					
					//2. Versuch: Aber damit das funktioniert war eigentlich das Überschreiben der equals() MEthode in Troop - Entity wichtig und notwendig
					PersistentBag pbag = new PersistentBag((SessionImplementor) session, objCellStarted.getTileBag());
					boolean bSuccessfulRemoved = pbag.remove(objTroopArmy);
					System.out.println("Ergebnis des Entfernenversuchs: " + bSuccessfulRemoved);
					
					//sondern als Notlösung: 
					//objCellStarted.getTileBag().clear();
		
					//Reicht es das aus dem PersistentBag zu entfernen. Muss dann nicht die Zelle noch aktualisiert werden?
					//Dies erst mal auskommentiert wg. Fehler: Caused by: org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
	//				session.saveOrUpdate(objCellStarted); //Hibernate Interceptor wird aufgerufen
	//				session.update(objCellStarted); //Hibernate Interceptor wird aufgerufen 
					System.out.println("Ausgangszelle. Anzahl Tiles nach UPDATE =" + objCellStarted.getTileBag().size());
					
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Starte Transaction:....");
					Transaction tx = session.getTransaction();
					if(tx.isActive()){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Nutze noch aktive Transaction.");
					}else{
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Transaction ist nicht aktiv.");
						tx = session.beginTransaction();
					}
					
					if (!tx.wasCommitted()) {
						tx.commit();
						
						//VErsuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
						//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
						//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
						
						//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
						VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
						if(objResult!=null){
							sMessage = objResult.getVetoMessage();
							bGoon = !objResult.isVeto();
						}else{
							//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
							bGoon = true;
						}
					}
					if(!bGoon){
						//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
						this.getFacadeResult().setMessage(sMessage);
						break main;
					}
					
					pbag.unsetSession((SessionImplementor) session); //WG. Fehler: Caused by: org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
				}//end if: (objCellStarted.getTileBag().size()>=1)
				
				//############################
				//3. Lösche die eigentliche TroopArmy
				//############################
				
				//+++ Datenbankoperationen		
				session = this.getSessionOpen();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				Transaction tx = session.getTransaction();
				if(tx.isActive()){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Nutze noch aktive Transaction.");
				}else{
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Transaction ist nicht aktiv.");
					tx = session.beginTransaction();
				}
				session.delete(objTroopArmy);
				
				//FLUSH IST HIER NOTWENDIG, UM DIE JOIN-Tabelle HEXCELL_TILE um den Eintrag zu reduzieren, ansonsten habe ich es nicht hinbekommen.
				session.flush(); //Datenbank ynchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.

				if (!tx.wasCommitted()) {
					tx.commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
					
					//Versuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
					
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
					if(objResult!=null){
						sMessage = objResult.getVetoMessage();
						bGoon = !objResult.isVeto();
					}else{
						//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
						bGoon = true;
					}
				}
				if(!bGoon){
					//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
					this.getFacadeResult().setMessage(sMessage);
					break main;
				}
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Armee gelöscht.");
				
				
				
				/*
				//ABER: ES BLEIBT IMMER ETWAS IN DER HEXCELL_TILE TABELLE ZURUECK...
				//###VERZEWEIFELUNG 
				session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;		
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");	
				session.getTransaction().begin();
				session.flush(); //versuch folgendes zu 
				if (!session.getTransaction().wasCommitted()) {
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}
				if(!bGoon){
					//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
					this.getFacadeResult().setMessage(sMessage);
					break validEntry;
				}
				*/
				
				//Falls alles glatt durchgeht....
				bReturn = true;	
			}//end main:
			return bReturn;
		}

		
		
		
			
	
	public boolean fillTroopArmyDto(String sUniqueName, GenericDTO<IDTOAttributeGroup> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopArmyDto(sUniqeName)  ####################");
						
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################								
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopArmyDao objTroopArmyDao = new TroopArmyDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopArmy objTroopArmy = objTroopArmyDao.searchTroopArmyByUniquename(sUniqueName);
			if(objTroopArmy == null) break main;
			
			bReturn = this.fillTroopArmyDto(objTroopArmy, dto);
			
		}//end main:
		return bReturn;
	}
	
	public boolean fillTroopArmyDto(TroopArmy objTroopArmy, GenericDTO dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopArmyDto(objTroopArmy)  ####################");			
			super.fillTroopDto(objTroopArmy, dto);
			
			if(objTroopArmy == null) break main;
						
			//FRAGE: FUNKTIONIERT HIERBEI CALL BY REFERENCE? JA. Es werden nämlich Werte in den Objekten gefüllt.	
			if(objTroopArmy.getTroopArmyVariantObject()!=null){
				dto.set(ITileDtoAttribute.VARIANT_IMAGE_URL_STRING,objTroopArmy.getTroopArmyVariantObject().getImageUrlString());
				
				//20180630: Nun das Bild ausch direkt als byte[] gespeichert aus der Datenbank holen.
				dto.set(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE,objTroopArmy.getTroopArmyVariantObject().getImage());
								
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01,objTroopArmy.getTroopArmyVariantObject().getImageDialog01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_02,objTroopArmy.getTroopArmyVariantObject().getImageDialog02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_03,objTroopArmy.getTroopArmyVariantObject().getImageDialog03());
								
				dto.set(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE_01,objTroopArmy.getTroopArmyVariantObject().getImageCatalog01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE_02,objTroopArmy.getTroopArmyVariantObject().getImageCatalog02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE_03,objTroopArmy.getTroopArmyVariantObject().getImageCatalog03());
				
				
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_01,objTroopArmy.getTroopArmyVariantObject().getImageHexmapDrag01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_02,objTroopArmy.getTroopArmyVariantObject().getImageHexmapDrag02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_03,objTroopArmy.getTroopArmyVariantObject().getImageHexmapDrag03());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_04,objTroopArmy.getTroopArmyVariantObject().getImageHexmapDrag04());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_05,objTroopArmy.getTroopArmyVariantObject().getImageHexmapDrag05());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_06,objTroopArmy.getTroopArmyVariantObject().getImageHexmapDrag06());
				
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_01,objTroopArmy.getTroopArmyVariantObject().getImageHexmap01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_02,objTroopArmy.getTroopArmyVariantObject().getImageHexmap02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_03,objTroopArmy.getTroopArmyVariantObject().getImageHexmap03());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_04,objTroopArmy.getTroopArmyVariantObject().getImageHexmap04());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_05,objTroopArmy.getTroopArmyVariantObject().getImageHexmap05());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_06,objTroopArmy.getTroopArmyVariantObject().getImageHexmap06());
											
				if(objTroopArmy.getTroopArmyVariantObject().getImmutabletextObject()!=null){
					dto.set(ITileDtoAttribute.VARIANT_SHORTTEXT, objTroopArmy.getTroopArmyVariantObject().getImmutabletextObject().getShorttext());	
				}
			}
			
			bReturn = true;
		}//end main:
		return bReturn;
	}

	
	
	@Override
	public String getFacadeType() {
		return TroopType.ARMY.name();
	}
	
	@Override
	public TroopArmy getEntityUsed() {
		return this.objTroopArmy;
	}
	
	@Override
	public void setEntityUsed(Troop objTroopArmy){
		this.objTroopArmy =  (TroopArmy) objTroopArmy;
	}	

}
