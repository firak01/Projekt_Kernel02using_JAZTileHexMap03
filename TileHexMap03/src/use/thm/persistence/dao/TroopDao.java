package use.thm.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
public class TroopDao<T> extends TileDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor  
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
	public TroopDao() throws ExceptionZZZ{
		super();
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	//public TroopDao(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
	public TroopDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{	
		super(objContextHibernate);		
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopDao(IHibernateContextProviderZZZ objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopDao(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(Troop.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	//Den Namen des Aktuellen Objekts kann ich nun auslesen.
	//Darum diese Methode generisch in die DaoZZZ - Klasse ausgelagert.
//    public List<T> findLazyAll(int first, int max){
//    	return this.findLazyAll("Troop", first, max);
//    }
//    
//	@Override
//	public int count(){
//		this.getLog().debug("counting Troops");
//		Query q = getSession().createQuery("select count(t) from Troop t");
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
//		return this.countByCriteria("Troop", whereBy, filter);
//	}


	/* (non-Javadoc)
	 * @see use.thm.persistence.dao.GeneralDAO#getID(tryout.hibernate.AreaCell)
	 */
//	@Override
//	public Map<String, Object> getID(T instance) {
//		Map<String, Object> id = new HashMap<String, Object>();
//		id.put("id", instance.);		
//		return id;
//	}
	
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
	public Troop searchTroopByUniquename(String sUniquename){
		Troop objReturn = null;
		try{
//		select mate
//		from Cat as cat
//		    inner join cat.mate as mate
		    
		//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
		//String sHql = "SELECT id from Tile as tableTile";								
		//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
		
		//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
		Session session = this.getSessionOpen();
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
		session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
	
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
		
		Query query = session.createQuery("from Troop as tableTile where tableTile.tileIdObject.uniquename = :uniqueName");//Merke: In TroopArmy ist der uniquename transient. Also kommt man über das Objekt daran.
		query.setString("uniqueName", sUniquename);

		
		Object objResult = query.uniqueResult();//für einen einzelwert		
		//listReturn = query.list(); //Für meherer Werte
		session.getTransaction().commit();
		
		objReturn = (Troop) objResult;
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objReturn;
	}
	
	public List<TroopArmy> searchTroopCollectionByHexCell(String sMapAlias, String sX, String sY){
		List<TroopArmy> listReturn = new ArrayList<TroopArmy>();
		main:{
			try{
//		select mate
//		from Cat as cat
//		    inner join cat.mate as mate
		    
		//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
		//String sHql = "SELECT id from Tile as tableTile";								
		//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
		
		//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
		Session session = this.getSessionOpen();
		if(session == null) break main;	
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
		session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.

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
		
			
		//JA, das funktioniert
		//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias");
		//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
		Query query = session.createQuery("from Troop as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
		
		query.setString("mapAlias", sMapAlias);
		query.setString("mapX", sX);
		query.setString("mapY", sY);
		
		//Object objResult = query.uniqueResult(); //Das sind aber ggfs. mehrere Werte		
		listReturn = query.list(); 
		session.getTransaction().commit();
		
		//3. Beispiel
		//TODO: Nicht den statischen HQL Ansatz, sondern über die Criteria API, d.h. die Where - Bedingung zur Laufzeit zusammensetzen
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return listReturn;
	}
}
