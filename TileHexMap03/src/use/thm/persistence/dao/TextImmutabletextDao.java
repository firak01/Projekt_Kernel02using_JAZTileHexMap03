package use.thm.persistence.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;

import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TextImmutabletext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zBasic.util.datatype.calling.ReferenceZZZ;
import basic.zKernel.KernelZZZ;
public class TextImmutabletextDao<T> extends ImmutabletextDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
	public TextImmutabletextDao() throws ExceptionZZZ{
		super();
		this.installLoger(TextImmutabletext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TextImmutabletextDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(TextImmutabletext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TextImmutabletextDao(IHibernateContextProviderZZZ objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(TextImmutabletext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TextImmutabletextDao(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(TextImmutabletext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	public int createEntriesAll(){
		int iReturn = 0;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
								
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();
				Session session = this.getSessionOpen();
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				//###################
				//1. Speichere den Defaulttext
				//####################					
								
				//Alle Enumerations hier einlesen.
				//TODO 20171114 ...ohje das irgendwie generisch machen ... vgl. meine _fillValue(...) Lösung..
				Collection<String> colsEnumAlias = EnumZZZ.getNames(TextDefaulttext.getThiskeyEnumClassStatic());
				for(String sEnumAlias : colsEnumAlias){
					System.out.println("Starte Transaction:.... Gefundener Enum-Name: " + sEnumAlias);
					TextImmutabletext objValueTemp = new TextImmutabletext();

					//DAS GEHT NICHT, DA JAVA IMMER EIN PASS_BY_VALUE MACHT.
					//Long lngThisValue = new Long(0);
					//String sName = new String("");
					//String sShorttext = new String("");
					//String sLongtext = new String("");
					//String sDescription = new String("");
					//this._fillValueImmutable(objValueTemp, sEnumAlias, lngThisValue, sName, sShorttext, sLongtext, sDescription); 

					//Hier der Workaround mit Refenz-Objekten, aus denen dann der Wert geholt werden kann. Also PASS_BY_REFERENCE durch auslesen der Properties der Objekte.  
					ReferenceZZZ<Long> lngThisValue = new ReferenceZZZ(0);
					ReferenceZZZ<String> sName = new ReferenceZZZ("");
					ReferenceZZZ<String> sShorttext = new ReferenceZZZ("");
					ReferenceZZZ<String> sLongtext = new ReferenceZZZ("");
					ReferenceZZZ<String> sDescription = new ReferenceZZZ("");
					this._fillValueImmutable(objValueTemp, sEnumAlias, lngThisValue, sName, sShorttext, sLongtext, sDescription);
										
					TextImmutabletext objValueText = new TextImmutabletext(((int)lngThisValue.get().intValue()), sShorttext.get(), sLongtext.get(), sDescription.get());		//Bei jedem Schleifendurchlauf neu machen, sonst wird lediglich nur 1 Datensatz immer wieder verändert.
					
				//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
				session.save(objValueText); //Hibernate Interceptor wird aufgerufen																				
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
//				}else{
					iReturn++;
//				}
				}//end for
												

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				iReturn = 0;
			}			
		}//end main:
		return iReturn;		
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
	//....	Ohje dies generisch machen...
	public boolean deleteAll(){
	boolean bReturn = false;
	main:{
		//Nun alle holen
		ArrayList<TextImmutabletext> listaText = (ArrayList<TextImmutabletext>) this.findLazyAll();
		
		for(TextImmutabletext objText : listaText){
			System.out.println("Lösche: Immutabletext.toString(): " + objText.toString());
			String sDescriptionStored = objText.getDescription();
			System.out.println("Description (gespeichert): " + sDescriptionStored);	

			//!!! SICHERSTELLEN, DASS NICHT NOCH ANDERE GELOESCHT WERDEN !!!
			String sKeyType = objText.getKeyType();
			if(this.getKeyTypeUsed().equalsIgnoreCase(sKeyType)){
				this.delete(objText);
			}
		}//End for
		bReturn = true;
		
	}//End main
	return bReturn;				
}
	
	public boolean delete(TextImmutabletext objText) {
		boolean bReturn = false;
		main:{
			if(objText==null)break main;
			
			//!!! SICHERSTELLEN, DASS NICHT NOCH ANDERE GELOESCHT WERDEN !!!
			String sKeyType = objText.getKeyType();
			if(this.getKeyTypeUsed().equalsIgnoreCase(sKeyType)){
				bReturn = super.delete((T) objText);
			}
		}//end main
		return bReturn;
	}

	public String getKeyTypeUsed(){		        	
		return "IMMUTABLETEXTTEXT";
	}
	
	//####### EIGENE METHODEN ###########
	//....
//		public Key searchKey(String sKeyType, Long lngThiskey){
//			Key objReturn = null;
//			
////			select mate
////			from Cat as cat
////			    inner join cat.mate as mate
//			    
//			//1. Beispiel: wenn man aber die WHERE Parameter so als String reinprogrammiert, ist das anfällig für SQL injection.
//			//String sHql = "SELECT id from Tile as tableTile";								
//			//listReturn = this.findByHQL(sHql, 0, 0);//start ist indexwert also 0 = erster Wert, Danach folgt maximale Anzahl von Objekten.
//			
//			//2. Beispiel: Etwas sicherer ist es die Parameter mit Platzhaltern zu füllen
//			Session session = this.getSession();
//			//liefert die ID Spalte als Integer zurück, also nicht das TileId Objekt...  Query query = session.createQuery("SELECT id from Tile as tableTile");
//			//                                                       wird nicht gefunden Query query = session.createQuery("SELECT TileIdObject from Tile as tableTile");
//			
//			//Also über die HEXCELL gehen...
//			//JA, das liefert die HEXCELL-Objekte zurück
//			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile");
//							
//			//JA, das liefert die CellId-Objekte der Hexcell zurück
//			//Query query = session.createQuery("SELECT objHexCell.id from Tile as tableTile");
//			
//			//JA, das liefert die Alias Map-Werte zurück
//			//Query query = session.createQuery("SELECT objHexCell.id.mapAlias from Tile as tableTile");
//			
//			//DARAUS VERSUCHEN DIE ABFRAGE ZU BAUEN....
//			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile where tableTile.objHexCell.Id.MapAlias IN (:mapAlias)");
//			
//			
//			/* Weiteres Beispiel aus TroopDao...	
//			//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
//			Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
//			
//			query.setString("mapAlias", sMapAlias);
//			query.setString("mapX", sX);
//			query.setString("mapY", sY);
//			 */
//				
//			//JA, das funktioniert, andere Beispiele
//			//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias");
//			//Query query = session.createQuery("from Tile as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
//			//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
//			
//			//Query query = session.createQuery("from Tile as tableTile where tableTile.tileIdObject.uniquename = :uniqueName");//Merke: In TroopArmy ist der uniquename transient. Also kommt man über das Objekt daran.
//			//Query query = session.createQuery("from Key as tableKey where tableKey.keyType = :keyType and tableKey.thiskey = :thiskey");
//			
//			//Query query = session.createQuery("from TileDefaulttext as tableKey where tableKey.thiskey = :thiskey");			
//			Query query = session.createQuery("from TileDefaulttext as tableKey where tableKey.thiskey = :thiskey and tableKey.keyType = :keyType ");
//			query.setString("keyType", sKeyType);
//			query.setLong("thiskey", lngThiskey);
//
//			
//			Object objResult = query.uniqueResult();//für einen einzelwert, darum ist es wichtig, das der uniquename beim Einfügen eines Spielsteins auch wirklich unique ist... Bei 2 gefundenen Werten kammt es hier zum begründeten Fehler. 		
//			//listReturn = query.list(); //Für meherer Werte
//			
//			objReturn = (Key) objResult;
//			return objReturn;
//		}
			
}//end class
