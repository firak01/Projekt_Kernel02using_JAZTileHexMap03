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
public class TroopArmyDaoFacade {
	private HibernateContextProviderSingletonTHM objContextHibernate = null;
	public TroopArmyDaoFacade(){		
	}
	public TroopArmyDaoFacade(HibernateContextProviderSingletonTHM objContextHibernate){
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
	
	public boolean insertTroopArmy(String sUniqueName, AreaCell objArea) throws ExceptionZZZ{
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
			TroopArmy objTroopTemp = new TroopArmy(new TileId("EINS", "1", sUniqueName));
						
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
				
				AreaCellLand objAreaTemp = (AreaCellLand) objArea;
				int iPlayer = objTroopTemp.getPlayer();
				objAreaTemp.setPlayerOwner(iPlayer);							

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
				session.getTransaction().commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				session.flush(); //versuch folgendes zu 
				bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
			}
			if(!bGoon)break validEntry;
			bReturn = true;
		}//end validEntry:
		
			
			
		}//end main:
		return bReturn;
	}
}
