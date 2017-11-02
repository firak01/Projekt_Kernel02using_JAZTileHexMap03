package use.thm.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileType;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopType;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
public class KeyDao<T> extends GeneralDaoZZZ<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor */
	public KeyDao() throws ExceptionZZZ{
		super();
		this.installLoger(Key.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public KeyDao(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(Key.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public KeyDao(HibernateContextProviderSingletonTHM objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(Key.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public KeyDao(HibernateContextProviderSingletonTHM objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(Key.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	//Den Namen des Aktuellen Objekts kann ich nun auslesen.
	//Darum diese Methode generisch in die DaoZZZ - Klasse ausgelagert.
//    public List<T> findLazyAll(int first, int max){
//    	return this.findLazyAll("Tile", first, max);
//    }
//    
//	@Override
//	public int count(){
//		this.getLog().debug("counting Tiles");
//		Query q = getSession().createQuery("select count(t) from Tile t");
//		int count = ((Long)q.uniqueResult()).intValue();
//		return count;
//	}
//	
//	
//	/* (non-Javadoc)
//	 * @see use.thm.persistence.dao.GeneralDAO#countByCriteria(java.util.Map, java.util.Map)
//	 */
//	@Override
//	public int countByCriteria(Map<String, Object> whereBy, 	Map<String, String> filter) {
//		return this.countByCriteria("Tile", whereBy, filter);
//	}


	/* (non-Javadoc)
	 * @see use.thm.persistence.dao.GeneralDAO#getID(tryout.hibernate.AreaCell)
	 */
	@Override
	public Map<String, Object> getID(T instance) {
		Map<String, Object> id = new HashMap<String, Object>();
		id.put("key", instance);		
		return id;
	}
	
	public List<T> findByHQL(String hql, int first, int max) {
		return this.findByHQLGeneral(hql, first, max);
	}

	public List<T> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findByCriteria(Map<String, Object> whereBy,
			List<String> orderByList, Map<String, String> filter, int first,
			int max) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	//####### EIGENE METHODEN ###########
	//....
		public Key searchKey(String sKeyType, Long lngThiskey){
			Key objReturn = null;
			
//			select mate
//			from Cat as cat
//			    inner join cat.mate as mate
			    
			//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
			//String sHql = "SELECT id from Tile as tableTile";								
			//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
			
			//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
			Session session = this.getSession();
			//liefert die ID Spalte als Integer zurück, also nicht das TileId Objekt...  Query query = session.createQuery("SELECT id from Tile as tableTile");
			//                                                       wird nicht gefunden Query query = session.createQuery("SELECT TileIdObject from Tile as tableTile");
			
			//Also über die HEXCELL gehen...
			//JA, das liefert die HEXCELL-Objekte zurück
			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile");
							
			//JA, das liefert die CellId-Objekte der Hexcell zurück
			//Query query = session.createQuery("SELECT objHexCell.id from Tile as tableTile");
			
			//JA, das liefert die Alias Map-Werte zurück
			//Query query = session.createQuery("SELECT objHexCell.id.mapAlias from Tile as tableTile");
			
			//DARAUS VERSUCHEN DIE ABFRAGE ZU BAUEN....
			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile where tableTile.objHexCell.Id.MapAlias IN (:mapAlias)");
			
			
			/* Weiteres Beispiel aus TroopDao...	
			//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
			Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
			
			query.setString("mapAlias", sMapAlias);
			query.setString("mapX", sX);
			query.setString("mapY", sY);
			 */
				
			//JA, das funktioniert, andere Beispiele
			//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias");
			//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
			//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
			
			//Query query = session.createQuery("from Tile as tableTile where tableTile.tileIdObject.uniquename = :uniqueName");//Merke: In TroopArmy ist der uniquename transient. Also kommt man über das Objekt daran.
			Query query = session.createQuery("from Key as tableKey where tableKey.keyType = :keyType and tableKey.thiskey = :thiskey");
			query.setString("keyType", sKeyType);
			query.setLong("thiskey", lngThiskey);

			
			Object objResult = query.uniqueResult();//für einen einzelwert, darum ist es wichtig, das der uniquename beim Einfügen eines Spielsteins auch wirklich unique ist... Bei 2 gefundenen Werten kammt es hier zum begründeten Fehler. 		
			//listReturn = query.list(); //Für meherer Werte
			
			objReturn = (Key) objResult;
			return objReturn;
		}
			
		
}
