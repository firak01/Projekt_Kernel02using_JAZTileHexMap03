package use.thm.persistence.daoFacade;

import java.util.Collection;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.event.spi.EventType;

import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.HibernateContextProviderZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.TroopArmy;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopArmyDaoFacade extends GeneralDaoFacadeZZZ{
		
	public TroopArmyDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	public boolean insertTroopArmy(String sUniqueName, AreaCell objArea) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Speicher die TroopArmy neu, füge die Area der TroopArmy hinzu, damit sie weiss in welchem Feld sie steht.
			//####################					
			Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
	
			//+++ Datenbankoperationen
			TroopArmy objTroopTemp = new TroopArmy(new TileId("EINS", "1", sUniqueName));									
			session.update(objArea);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
			objTroopTemp.setHexCell(objArea); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
			
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			session.save(objTroopTemp); //Hibernate Interceptor wird aufgerufen																				
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"save",session.getTransaction());
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
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
				session = this.getSession();		//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;				
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
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
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
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
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
				session.getTransaction().commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				session.flush(); //versuch folgendes zu 
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

			//Falls alles glatt durchgeht....
			bReturn = true;
		}//end validEntry:
					
		}//end main:
		return bReturn;
	}
	public boolean updateTroopArmyPosition(String sUniqueName, AreaCell objArea) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################								
			HibernateContextProviderSingletonTHM objContextHibernate = (HibernateContextProviderSingletonTHM) this.getHibernateContext();
			TroopArmyDao objTroopArmyDao = new TroopArmyDao(objContextHibernate);

			//Das HQL, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopArmy objTroopArmy = objTroopArmyDao.searchTroopArmyByUniquename(sUniqueName);

			
			//TODO GOON: 20170713 -WIE UPDATE DER ZELLENEINTRÄGE HINBEKOMMEN !!!
			
			
			
			//#############################
			//3. Hole die Backendentsprechung der Ausgangszelle, daraus muss die TropArmy entfernt werden.
			//#############################
			//TODO GOON 20170713    
			
			//#############################
			//4. Hole die Backendentsprechung der Zielzelle
			//#############################
			AreaCellDao objAreaDao = new AreaCellDao(objContextHibernate);
			int iXDropped = objArea.getMapX();
			int iYDropped = objArea.getMapY();
			CellId primaryKeyCell = new CellId("EINS", Integer.toString(iXDropped), Integer.toString(iYDropped));
			AreaCell objCellTemp = objAreaDao.findByKey(primaryKeyCell);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			    
			
			//+++ Datenbankoperationen
			//TroopArmy objTroopTemp = new TroopArmy(new TileId("EINS", "1", sUniqueName));
			
			Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			//FEHLER: Momentan werden die Koordinaten direkt aus der zugeordneten HEXCell geholt. Darum macht das keine Änderung...
			//objTroopArmy.setMapX(iXDropped);
			//objTroopArmy.setMapY(iYDropped);
			
			//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
			session.update(objCellTemp);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
			objTroopArmy.setHexCell(objCellTemp); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
		
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			//wenn man .save() aufruft, wird immer eine neue Zeile mit dem Objekt erzeugt, auch wenn man ein bestehendes aktualisieren möchte session.save(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			//Alternative: Siehe https://stackoverflow.com/questions/30473707/hibernate-creates-new-row-on-save-and-not-updates
			session.saveOrUpdate(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				
				//VErsuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
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
			
			//###################
			//3. Aktualisiere die Hex-Zelle, füge die TroopArmy der Liste hinzu, damit die Hex-Zelle weiss welche TroopArmies in ihr stehen.
			//####################		
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
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
				colTile.add(objTroopArmy);
			}
	
			session.update(objArea); //SaveAndUpdate-Listener wird nicht ausgeführt
			if (!session.getTransaction().wasCommitted()) {
				//session.flush();								
				session.getTransaction().commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				session.flush(); //versuch folgendes zu 
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			*/
			
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
	}
}
