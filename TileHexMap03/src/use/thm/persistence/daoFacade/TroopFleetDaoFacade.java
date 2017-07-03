package use.thm.persistence.daoFacade;

import java.util.Collection;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
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
public class TroopFleetDaoFacade {
	private HibernateContextProviderSingletonTHM objContextHibernate = null;
	public TroopFleetDaoFacade(){		
	}
	public TroopFleetDaoFacade(HibernateContextProviderSingletonTHM objContextHibernate){
		this.objContextHibernate = objContextHibernate;
	}
	private HibernateContextProviderSingletonTHM getHibernateContext(){
		return this.objContextHibernate;
	}
	private Session getSession() throws ExceptionZZZ{
		Session objReturn = null;		
		HibernateContextProviderSingletonTHM objHibernateContext = this.getHibernateContext();
		if(objHibernateContext!=null){
			objReturn = objHibernateContext.getSession();
		}
		return objReturn;
	}
	
	public boolean insertTroopFleet(String sUniqueName, AreaCell objArea) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		
			validEntry:{
			boolean bGoon = false;
			
			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
//			if(session!=null){
//				if(session.isOpen()) session.close();
//				session = null;
//			}
//			session = objContextHibernate.getSession();
			Session session = this.getSession();
			if(session == null) break main;
			
			session.getTransaction().begin();
			//session.beginTransaction(); //Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.					
	
			//+++ Datenbankoperationen
			TroopFleet objTroopTemp = new TroopFleet(new TileId("EINS", "1", sUniqueName));//TODO GOON : sY als Uniquename zu verwenden ist nur heuristisch und nicht wirklich UNIQUE
			
			//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
			session.update(objArea);			
			objTroopTemp.setHexCell(objArea); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
			
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			session.save(objTroopTemp); //Hibernate Interceptor wird aufgerufen																				
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO GOON: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());
			}
			if(!bGoon)break validEntry;
		
			//if(objCellTemp instanceof AreaCell){
			if(objArea instanceof AreaCell){
				//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session!=null){
					if(session.isOpen()) session.close();
					session = null;
				}
				session = objContextHibernate.getSession();
				
				session.getTransaction().begin();					
				//session.beginTransaction(); //Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				//Das aktiviert nicht den PreUpdateListener und zu diesem Zeitpunkt auch nicht mehr den PreInsertListener 
				
				AreaCellOcean objAreaTemp = (AreaCellOcean) objArea;
				
				//!!! WASSERFELDER GEHOEREN KEINEM SPIELER darum hier nicht relevant
//				int iPlayer = objTroopTemp.getPlayer();
//				bjAreaTemp.setPlayerOwner(iPlayer);							

				//session.save(objAreaTemp); //constraint violation, so als ob ein neues Objekt gepseichert werden würde .... insert ...
				session.update(objAreaTemp); //SaveAndUpdate-Listener wird NICHT(!) ausgeführt, aber es gibt keine constraint verletzung ... update
				if (!session.getTransaction().wasCommitted()) {
					//session.flush();								
					session.getTransaction().commit();///SaveAndUpdate-Listener NICHT ausgeführt. //TODO GOON: Probiere ob 'AbstractFlushingEventListener' ausgeführt würde.
					bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
				}
			}
			if(!bGoon)break validEntry;
			
			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session!=null){
				if(session.isOpen()) session.close();
				session = null;
			}
			session = objContextHibernate.getSession();
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
			
			//session.update(objCellTemp); //SaveAndUpdate-Listener wird nicht ausgeführt
			session.update(objArea); //SaveAndUpdate-Listener wird nicht ausgeführt
			if (!session.getTransaction().wasCommitted()) {
				//session.flush();								
				session.getTransaction().commit();///SaveAndUpdate-Listener wir ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
			}
			if(!bGoon)break validEntry;
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
