package use.thm.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.KeyImmutable;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileType;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopType;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyUserDaoZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

/** DAS WÄRE DIE VARIANTE, WENN ES EINE TABELLE "KEY" GÄBE.
 *   ABER DIESE TABELLE WILL ICH NICHT.
 *   Außerdem würden dann alle Entities hier einen Eintrag bekommen. Das will ich auch nicht.
 *   
 * @author Fritz Lindhauer
 *
 * @param <T>
 */
public abstract class AbstractKeyImmutableDao<T> extends GeneralDaoZZZ<T>  implements IThiskeyUserDaoZZZ {
	private static final long serialVersionUID = 1L;

	/* Constructor 
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
	public AbstractKeyImmutableDao() throws ExceptionZZZ{
		super();
		this.installLoger(KeyImmutable.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public AbstractKeyImmutableDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(KeyImmutable.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public AbstractKeyImmutableDao(IHibernateContextProviderZZZ objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(KeyImmutable.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public AbstractKeyImmutableDao(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(KeyImmutable.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
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
	public KeyImmutable searchThiskey(Long lngThiskey){
		KeyImmutable objReturn = null;
		main:{
			Session session = this.getSession();	//Versuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session			
			if(session == null) break main;			
				
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.

//			select mate
//			from Cat as cat
//			    inner join cat.mate as mate
			    
			//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
			//String sHql = "SELECT id from Tile as tableTile";								
			//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
			
			//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
			//Session session = this.getSession();
			//liefert die ID Spalte als Integer zurück, also nicht das TileId Objekt...  Query query = session.createQuery("SELECT id from Tile as tableTile");
			//                                                       wird nicht gefunden Query query = session.createQuery("SELECT TileIdObject from Tile as tableTile");
						
			//Beispiele:
			//Das liefert die HEXCELL-Objekte zurück
			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile");
							
			//Liefert die CellId-Objekte der Hexcell zurück
			//Query query = session.createQuery("SELECT objHexCell.id from Tile as tableTile");
			
			//Liefert die Alias Map-Werte zurück
			//Query query = session.createQuery("SELECT objHexCell.id.mapAlias from Tile as tableTile");
			
			//Abfrage mit Parametern
			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile where tableTile.objHexCell.Id.MapAlias IN (:mapAlias)");	
			//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
//			query.setString("mapAlias", sMapAlias);
//			query.setString("mapX", sX);
//			query.setString("mapY", sY);
			 						
		String sTableNameHql = this.getDaoTableName();
		Query query = session.createQuery("from " + sTableNameHql + " as tableKey where tableKey.thiskey = :thiskey");
		query.setLong("thiskey", lngThiskey);

		
		Object objResult = query.uniqueResult();//für einen einzelwert, darum ist es wichtig, das der uniquename beim Einfügen eines Spielsteins auch wirklich unique ist... Bei 2 gefundenen Werten kammt es hier zum begründeten Fehler. 		
		//listReturn = query.list(); //Für meherer Werte
		
		objReturn = (KeyImmutable) objResult;
		
		if (!session.getTransaction().wasCommitted()) {
			//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
			session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
			
			//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
			VetoFlag4ListenerZZZ objResultVeto = HibernateUtil.getCommitResult(this.getHibernateContextProvider(),"save",session.getTransaction());
//			sMessage = objResultVeto.getVetoMessage();
//			bGoon = !objResultVeto.isVeto();
		}
//		if(!bGoon){
//			//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
//			this.getFacadeResult().setMessage(sMessage);
//			break validEntry;
//		}
		
		}//end main:
		return objReturn;
	}
	
	public KeyImmutable searchKey(String sKeyType, Long lngThiskey){
		KeyImmutable objReturn = null;
		main:{
		Session session = this.getSession();	//Versuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session			
		if(session == null) break main;			
			
		session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.

//		select mate
//		from Cat as cat
//		    inner join cat.mate as mate
		    
		//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
		//String sHql = "SELECT id from Tile as tableTile";								
		//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
		
		//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
		//Session session = this.getSession();
		//liefert die ID Spalte als Integer zurück, also nicht das TileId Objekt...  Query query = session.createQuery("SELECT id from Tile as tableTile");
		//                                                       wird nicht gefunden Query query = session.createQuery("SELECT TileIdObject from Tile as tableTile");
					
		//Beispiele:
		//Das liefert die HEXCELL-Objekte zurück
		//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile");
						
		//Liefert die CellId-Objekte der Hexcell zurück
		//Query query = session.createQuery("SELECT objHexCell.id from Tile as tableTile");
		
		//Liefert die Alias Map-Werte zurück
		//Query query = session.createQuery("SELECT objHexCell.id.mapAlias from Tile as tableTile");
		
		//Abfrage mit Parametern
		//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile where tableTile.objHexCell.Id.MapAlias IN (:mapAlias)");	
		//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
//		query.setString("mapAlias", sMapAlias);
//		query.setString("mapX", sX);
//		query.setString("mapY", sY);
		 						
		String sTableNameHql = this.getDaoTableName();
		Query query = session.createQuery("from " + sTableNameHql + " as tableKey where tableKey.thiskey = :thiskey and tableKey.keyType = :keyType ");
		query.setString("keyType", sKeyType);
		query.setLong("thiskey", lngThiskey);
		
		Object objResult = query.uniqueResult();//für einen einzelwert, darum ist es wichtig, das der uniquename beim Einfügen eines Spielsteins auch wirklich unique ist... Bei 2 gefundenen Werten kammt es hier zum begründeten Fehler. 		
		//listReturn = query.list(); //Für meherer Werte
		
		objReturn = (KeyImmutable) objResult;
		}//end main:
		return objReturn;
	}
			
	//Das kann dann z.B. zum gezielteren Löschen ausgeführt werden.
	public abstract String getKeyTypeUsed();	
	
	@Override
	public int count(){
		int iReturn = -1;
		try{
		String sTableName = this.getDaoTableName();
		
		this.getLog().debug(ReflectCodeZZZ.getPositionCurrent() + ": Counting '" + sTableName);
		
//		20171101: daoKey.count "locked" die Datenbank. 
//		Lösungsidee: Es wurde hier keine Transaktion gebraucht? Das Ziel muss aber sein 1 Session : 1 Transaktion.
//		                    Daher this.begin() und this.commit(), um die Transaktion wieder zu schliessen.
		
		this.begin();
			
		String sKeyType = this.getKeyTypeUsed();
		Query query = getSession().createQuery("select count(tableKey) from " + sTableName + " tableKey where tableKey.keyType = :keyType ");
		query.setString("keyType", sKeyType);
		
		iReturn = ((Long)query.uniqueResult()).intValue();
		
		this.commit();
		
//		catch (NonUniqueObjectException non) {
//			log.error("Method delete failed NonUniqueObjectException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , non);
//			System.out.println("NON UNIQUE!!!");
//			return this.helpCatchException(non, instance);			
//		}
//		catch(StaleObjectStateException er){
//			log.error("Method delete failed StaleObjectStateException +\n" + session.hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , er);
//			System.out.println("STALE!!!");
//			return this.staleObjectStateException(instance, er);
		}catch(HibernateException he){
			log.error("Method delete failed HibernateException +\n" + getSession().hashCode() + "\n ThreadID:" + Thread.currentThread().getId() +"\n" , he);
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HIBERNATE EXCEPTION!!!!");
			he.printStackTrace();
			 iReturn = -1;
		}finally {
			if (getSession().getTransaction().isActive()) {
				this.rollback();
				log.debug(ReflectCodeZZZ.getPositionCurrent() + ": rollback executed");
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HIBERNATE ROLLBACK EXECUTED!!!!");
				iReturn = -1;
			}
		}

		return iReturn;
	}
	
	public int countAllInherited(){
		return super.count();
	}
}
