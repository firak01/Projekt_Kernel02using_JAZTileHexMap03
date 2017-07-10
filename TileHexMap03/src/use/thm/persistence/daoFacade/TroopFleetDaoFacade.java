package use.thm.persistence.daoFacade;

import java.util.Collection;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.HibernateContextProviderZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopFleetDaoFacade extends GeneralDaoFacadeZZZ{
	//private HibernateContextProviderSingletonTHM objContextHibernate = null;

	public TroopFleetDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
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
}
