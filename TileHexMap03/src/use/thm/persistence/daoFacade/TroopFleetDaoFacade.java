package use.thm.persistence.daoFacade;

import java.util.Collection;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;

import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.HibernateContextProviderZZZ;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopFleetDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import use.thm.persistence.model.TroopType;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.facade.TroopFleetRuleFacade;
import use.thm.rule.model.TroopArmyRuleType;
import use.thm.rule.model.TroopFleetRuleType;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopFleetDaoFacade extends TileDaoFacade{
	//private HibernateContextProviderSingletonTHM objContextHibernate = null;

	public TroopFleetDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public boolean deleteTroopFleet(String sUniqueName) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### DELETE TROOPFLEET ####################");
			
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################								
			HibernateContextProviderSingletonTHM objContextHibernate = (HibernateContextProviderSingletonTHM) this.getHibernateContext();
			TroopFleetDao objTroopFleetDao = new TroopFleetDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopFleet objTroopFleet = objTroopFleetDao.searchTroopFleetByUniquename(sUniqueName);
			
			
			//#############################
			//2. Hole die Backendentsprechung der Ausgangszelle, daraus muss die TroopArmy entfernt werden.
			//############################# 
			AreaCellDao objAreaDaoSource = new AreaCellDao(objContextHibernate);
			int iXStarted = objTroopFleet.getMapX();
			int iYStarted = objTroopFleet.getMapY();
			CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
			AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			
			System.out.println("Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
			Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			System.out.println("Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
			
			//ALSO: Ich bin mir nicht sicher, ob objTroopArmy in der TileBag gefunden wird.
			//1. VErsuch: Darum nicht so einfach, statt dessen versuchen wieder ein Objekt im persistant state zu bekommen:
			//Merke: session.lock(object, lock_option) ist deprecated... 
			//LockRequest lockRequest = session.buildLockRequest(LockOptions.NONE);//versuch das hinzubekommen. Idee ist, das das Objetk jetzt keine PErsistant State mehr hat.   session2.lock(entity, LockMode.NONE);
			//lockRequest.lock(objTroopArmy);
			//boolean bSuccessfulRemoved = objCellStarted.getTileBag().remove(objTroopArmy);
			
			
			//2. Versuch: Aber damit das funktioniert war eigentlich das Überschreiben der equals() MEthode in Troop - Entity wichtig und notwendig
			PersistentBag pbag = new PersistentBag((SessionImplementor) session, objCellStarted.getTileBag());
			boolean bSuccessfulRemoved = pbag.remove(objTroopFleet);
			System.out.println("Ergebnis des Entfernenversuchs: " + bSuccessfulRemoved);
			
			//sondern als Notlösung: 
			//objCellStarted.getTileBag().clear();
			
//			session.saveOrUpdate(objCellStarted); //Hibernate Interceptor wird aufgerufen
			session.update(objCellStarted); //Hibernate Interceptor wird aufgerufen
			System.out.println("Ausgangszelle. Anzahl Tiles nach UPDATE =" + objCellStarted.getTileBag().size());
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				
				//VErsuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			
			//############################
			//3. Lösche die eigentliche TroopArmy
			//############################
			
			//+++ Datenbankoperationen		
			session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			session.delete(objTroopFleet);
			
			//FLUSH IST HIER NOTWENDIG, UM DIE JOIN-Tabelle HEXCELL_TILE um den Eintrag zu reduzieren, ansonsten habe ich es nicht hinbekommen.
			session.flush(); //Datenbank ynchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
		
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				
				//Versuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Flotte gelöscht '" + sUniqueName + "'");
			
			
			
			/*
			//ABER: ES BLEIBT IMMER ETWAS IN DER HEXCELL_TILE TABELLE ZURUECK...
			//###VERZEWEIFELUNG 
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
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
		
	public boolean insertTroopFleet(String sUniqueName, AreaCell objArea) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Speicher die TroopFleet neu, füge die Area der TroopFleet hinzu, damit sie weiss in welchem Feld sie steht.
			//####################
			Session session = this.getSession();//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			//+++ Datenbankoperationen
			TroopFleet objTroopTemp = new TroopFleet(new TileId("EINS", "1", sUniqueName));//TODO GOON : sY als Uniquename zu verwenden ist nur heuristisch und nicht wirklich UNIQUE						
			session.update(objArea);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.			
			objTroopTemp.setHexCell(objArea); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
			
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			session.save(objTroopTemp); //Hibernate Interceptor wird aufgerufen																				
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(this.getHibernateContext(),"save",session.getTransaction());
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"save",session.getTransaction());
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung.
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}

			/* WASSERFELDER GEHOEREN KEINEM SPIELER darum hier nicht relevant
			if(objArea instanceof AreaCell){				
				session = this.getSession();//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session				
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.					
			
				AreaCellOcean objAreaTemp = (AreaCellOcean) objArea;
				
				//!!! WASSERFELDER GEHOEREN KEINEM SPIELER darum hier nicht relevant
//				int iPlayer = objTroopTemp.getPlayer();
//				bjAreaTemp.setPlayerOwner(iPlayer);							

				//session.save(objAreaTemp); //constraint violation, so als ob ein neues Objekt gepseichert werden würde .... insert ...
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
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung.
				this.getFacadeResult().setMessage(sMessage);			
				break validEntry;
			}
			*/
			
			//###################
			//2. Aktualisiere die Hex-Zelle, füge die TroopFleet der Liste hinzu, damit die Hex-Zelle weiss welche TroopFleets in ihr stehen.
			//####################		
			session = this.getSession();//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;	
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
				session.getTransaction().commit();///SaveAndUpdate-Listener wir ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());				
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"save",session.getTransaction());
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			
			//Falls alles glatt durchgeht....
			bReturn = true;
		}//end validEntry:
								
		}//end main:
		return bReturn;
		
		
		//#### Aus Dokumentationsgründen mal den ursprungsCode hier erhalten. Das ist dann alles schicker gemacht worden.
		//TODO GOON 20170419: 
//		if(objCellTemp instanceof AreaCell){
//			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
//			session = objContextHibernate.getSession();
//			session.getTransaction().begin();					
//			//session.beginTransaction(); //Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
//			//Das aktiviert nicht den PreUpdateListener und zu diesem Zeitpunkt auch nicht mehr den PreInsertListener 
//			
//			AreaCellLand objAreaTemp = (AreaCellLand) objCellTemp;
//			int iPlayer = objTroopTemp.getPlayer();
//			objAreaTemp.setPlayerOwner(iPlayer);
//			
//			session.update(objCellTemp); //SaveAndUpdate-Listener wird nicht ausgeführt
//			if (!session.getTransaction().wasCommitted()) {
//				//session.flush();								
//				session.getTransaction().commit();///SaveAndUpdate-Listener wir ausgeführt, aber FÜR EIN TROOPARMY OBJEKT!!!
//			}
//		}
//		
//		//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
//		session = objContextHibernate.getSession();
//		session.getTransaction().begin();	
//		
//		//Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session
//		//http://stackoverflow.com/questions/18956825/hibernate-collection-is-not-associated-with-any-session
//		session.refresh(objCellTemp);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
//											
//		//weil Lazy - Loading eingestellt ist an dieser Stelle erst mal sehn, die Collection zu bekommen
//		//objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
//		Collection<Tile>colTile = objCellTemp.getTileBag();
//		
//		
//		Hibernate.initialize(colTile);
//		if(colTile!=null){
//			colTile.add(objTroopTemp);
//		}
//		session.update(objCellTemp);
//		if (!session.getTransaction().wasCommitted()) {
//			//session.flush();
//			session.getTransaction().commit();//SaveAndUpdate-Listener wir NICHT ausgeführt, aber PreInsert-Listener wird ausgeführt.
//		}
	}
	
	public boolean updateTroopFleetPosition(String sUniqueName, AreaCell objAreaTarget) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### UPDATE TROOPFLEETPOSITION ####################");
			
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################								
			HibernateContextProviderSingletonTHM objContextHibernate = (HibernateContextProviderSingletonTHM) this.getHibernateContext();
			TroopFleetDao objTroopFleetDao = new TroopFleetDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopFleet objTroopFleet = objTroopFleetDao.searchTroopFleetByUniquename(sUniqueName);

			//#############################
			//2. Mache die vorgeschaltete Validierung
			//############################# 
			//Wichtig: Vor dem ganzen Ablauf die Validierung durchführen, sonst muss man nach der Validierung wieder alles rückgängig machen.
			TroopFleetRuleFacade objRuleFacade = new TroopFleetRuleFacade(getHibernateContext(), objTroopFleet);
			bGoon = objRuleFacade.onUpdateTroopFleetPosition(objAreaTarget,"PREINSERT");//Die PREINSERT  Angaebe ist notwendig wg. anderen Stacking Limits. Mögliche Werte UPDATE / PREINSERT.
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
			int iXStarted = objTroopFleet.getMapX();
			int iYStarted = objTroopFleet.getMapY();
			CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
			AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			
			//#############################
			//4. Mache die Datenbankoperationen
			//############################# 
			
				//+++ 4.1 Datenbankoperationen: Entferne aus der Ausgangszelle
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
				Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
					
				//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten/genutzt worden ist.
				//session.update(objCellStarted);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
				PersistentBag pbag = new PersistentBag((SessionImplementor) session, objCellStarted.getTileBag());
				boolean bSuccessfulRemoved = pbag.remove(objTroopFleet);//Aber damit das funktioniert war eigentlich das Überschreiben der equals() Methode in Troop - Entity wichtig und notwendig
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
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
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
			session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
			session.update(objAreaTarget);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
			objTroopFleet.setHexCell(objAreaTarget); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
		
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			//wenn man .save() aufruft, wird immer eine neue Zeile mit dem Objekt erzeugt, auch wenn man ein bestehendes aktualisieren möchte session.save(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			//Alternative: Siehe https://stackoverflow.com/questions/30473707/hibernate-creates-new-row-on-save-and-not-updates
			//session.saveOrUpdate(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			session.update(objTroopFleet); //Hibernate Interceptor wird aufgerufen
			session.flush();
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				
				//Versuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Aktuelle Position der Army (X/Y): " + objTroopFleet.getMapX() + "/" + objTroopFleet.getMapY());
			
			
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
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
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
				colTile.add(objTroopFleet);
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
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
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

	@Override
	public String getFacadeType() {
		return TroopType.FLEET.name();
	}
}
