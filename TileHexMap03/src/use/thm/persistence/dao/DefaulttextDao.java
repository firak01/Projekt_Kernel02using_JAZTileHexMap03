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
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TextDefaulttext;
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
import basic.zKernel.KernelZZZ;
public class DefaulttextDao<T> extends AbstractKeyDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. 
	 *                
	 * Merke: //!!! FGL 20171108: Das Problem ist, dass in HQL bei einem count() der Superklasse auch die Elemente der Kindklasse gezählt zu werden scheinen!!!	 
	 *              Darum nicht die Werte in dem Entity der Superklasse speichern, sondern in jeder Kindklasse selbst.               
	 */
	public DefaulttextDao() throws ExceptionZZZ{
		super();
		this.installLoger(Defaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public DefaulttextDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(Defaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public DefaulttextDao(IHibernateContextProviderZZZ objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(Defaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public DefaulttextDao(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(Defaulttext.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	public int createEntriesAll(){
		int iReturn = 0;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				//Darüber hat diese Methode nicht zu befinden... objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
				//###################
				//1. Speichere den Defaulttext
				//####################					
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = objContextHibernate.getSession();
				if(session == null) break main;			
								
				//Alle Enumerations hier einlesen.
				//TODO 20171114 ...ohje das irgendwie generisch machen ... vgl. meine _fillValue(...) Lösung..
				Collection<String> colsEnumAlias = EnumZZZ.getNames(Defaulttext.getThiskeyEnumClassStatic());
				for(String sEnumAlias : colsEnumAlias){
					System.out.println("Starte Transaction:.... Gefundener Enum-Name: " + sEnumAlias);
					Defaulttext objValue = new Defaulttext();		//Bei jedem Schleifendurchlauf neu machen, sonst wird lediglich nur 1 Datensatz immer wieder verändert.
					session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
					this._fillValue(objValue, sEnumAlias);//FGL 20171114: Mein generischer Lösungsversuch.

				//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
				session.save(objValue); //Hibernate Interceptor wird aufgerufen																				
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
//				}
//				}else{
					iReturn++;
//				}
				}//end for
				
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
			
			
		}//end main:
		return iReturn;		
	}
	
	/* Das ist die Variante für Entities, die nicht mit der Annotation "Immutable" versehen sind.
	 * Die Entities mit der Annotation "Immutable" haben nämlich keine setter. Darum findet hier keine Zuweisung an ein Entity-Objekt statt.
	 * Es werden nur Variablen gefüllt, die dann im Konstruktor an das Entity-Objekt übergeben werden. 
	 */
	protected <T> void _fillValue(Defaulttext objValueTile, String sEnumAlias){
		
		//Merke: Direktes Reinschreiben geht wieder nicht wg. "bound exception"
		//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, sEnumAlias);
				
		//Also: Klasse holen und danach CASTEN.
		Class<?> objClass = ((Key) objValueTile).getThiskeyEnumClass();
		String sName = EnumSetDefaulttextUtilZZZ.readEnumConstant_NameValue((Class<IEnumSetTextTHM>) objClass, sEnumAlias);
		System.out.println("Gefundener Typname: " + sName);
		
		String sShorttext = EnumSetDefaulttextUtilZZZ.readEnumConstant_ShorttextValue((Class<IEnumSetTextTHM>) objClass, sEnumAlias);
		System.out.println("Gefundener Typkurztext: " + sShorttext);
		((Defaulttext) objValueTile).setShorttext(sShorttext);
		
		String sLongtext = EnumSetDefaulttextUtilZZZ.readEnumConstant_LongtextValue((Class<IEnumSetTextTHM>) objClass, sEnumAlias);
		System.out.println("Gefundener Typlangtext: " + sLongtext);
		((Defaulttext) objValueTile).setLongtext(sLongtext);
				
		String sDescription = EnumSetDefaulttextUtilZZZ.readEnumConstant_DescriptionValue((Class<IEnumSetTextTHM>) objClass, sEnumAlias);
		System.out.println("Gefundene Description: " + sDescription);			
		((Defaulttext) objValueTile).setDescription(sDescription);
		
	    Long lngThiskey = EnumSetDefaulttextUtilZZZ.readEnumConstant_ThiskeyValue((Class<IEnumSetTextTHM>) objClass, sEnumAlias);//Das darf nicht NULL sein, sonst Fehler. Über diesen Schlüssel wird der Wert dann gefunden.
	    System.out.println("Gefundener Thiskey: " + lngThiskey.toString());	
		((Key) objValueTile).setThiskey(lngThiskey);
		
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
	//....	Ohje dies generisch machen...
	public boolean deleteAll(){
	boolean bReturn = false;
	main:{
		//Nun alle holen
		ArrayList<Defaulttext> listaText = (ArrayList<Defaulttext>) this.findLazyAll();
		
		for(Defaulttext objText : listaText){
			
			//!!! SICHERSTELLEN, DASS NICHT NOCH ANDERE GELOESCHT WERDEN !!!
			String sKeyType = objText.getKeyType();
			if(this.getKeyTypeUsed().equalsIgnoreCase(sKeyType)){
				System.out.println("Lösche: Texttext.toString(): " + objText.toString());
				String sDescriptionStored = objText.getDescription();
				System.out.println("Description (gespeichert): " + sDescriptionStored);	
				
				this.delete(objText);
			}
			
		}//End for
		bReturn = true;
		
	}//End main
	return bReturn;				
}
	
	/** Bei dieser Löschvariante wird nicht auf einen KeyType Rücksicht genommen.
	 *  Daher werden alle Objekte, die aus dieser Klasse erben auch gelöscht.
	 *  
	 * @return
	 */
	public boolean deleteAllInherited(){
		boolean bReturn = false;
		main:{
			//Nun alle holen
			ArrayList<Defaulttext> listaText = (ArrayList<Defaulttext>) this.findLazyAll();
			
			for(Defaulttext objText : listaText){
				
				String sKeyType = objText.getKeyType();
				if(this.getKeyTypeUsed().equalsIgnoreCase(sKeyType)){					
					this.delete(objText);
				}
			}//End for
			bReturn = true;
			
		}//End main
		return bReturn;				
	}
	
	public int countAllInherited(){
		return super.count();		
	}

public boolean delete(Defaulttext objText) {
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

@Override
public String getKeyTypeUsed() {	
	return "DEFAULTTEXT";
}
	
	//####### EIGENE METHODEN ###########
	//....
//		public Key searchKey(String sKeyType, Long lngThiskey){
//			Key objReturn = null;
//			main:{
//			Session session = this.getSession();	//Versuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session			
//			if(session == null) break main;			
//				
//			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
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
//			//Session session = this.getSession();
//			//liefert die ID Spalte als Integer zurück, also nicht das TileId Objekt...  Query query = session.createQuery("SELECT id from Tile as tableTile");
//			//                                                       wird nicht gefunden Query query = session.createQuery("SELECT TileIdObject from Tile as tableTile");
//						
//			//Beispiele:
//			//Das liefert die HEXCELL-Objekte zurück
//			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile");
//							
//			//Liefert die CellId-Objekte der Hexcell zurück
//			//Query query = session.createQuery("SELECT objHexCell.id from Tile as tableTile");
//			
//			//Liefert die Alias Map-Werte zurück
//			//Query query = session.createQuery("SELECT objHexCell.id.mapAlias from Tile as tableTile");
//			
//			//Abfrage mit Parametern
//			//Query query = session.createQuery("SELECT objHexCell from Tile as tableTile where tableTile.objHexCell.Id.MapAlias IN (:mapAlias)");	
//			//Query query = session.createQuery("from TroopArmy as tableTile where tableTile.objHexCell.id.mapAlias = :mapAlias AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
////			query.setString("mapAlias", sMapAlias);
////			query.setString("mapX", sX);
////			query.setString("mapY", sY);
//			 						
//			String sTableNameHql = this.getDaoTableName();
//			Query query = session.createQuery("from " + sTableNameHql + " as tableKey where tableKey.thiskey = :thiskey and tableKey.keyType = :keyType ");
//			query.setString("keyType", sKeyType);
//			query.setLong("thiskey", lngThiskey);
//			
//			Object objResult = query.uniqueResult();//für einen einzelwert, darum ist es wichtig, das der uniquename beim Einfügen eines Spielsteins auch wirklich unique ist... Bei 2 gefundenen Werten kammt es hier zum begründeten Fehler. 		
//			//listReturn = query.list(); //Für meherer Werte
//			
//			objReturn = (Key) objResult;
//			}//end main:
//			return objReturn;
//		}
			
}//end class
