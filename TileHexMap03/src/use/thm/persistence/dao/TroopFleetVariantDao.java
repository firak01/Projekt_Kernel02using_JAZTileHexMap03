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
import use.thm.persistence.interfaces.ITroopFleetVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopVariantTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.KeyImmutable;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.util.datatype.enums.EnumSetTroopArmyVariantUtilTHM;
import use.thm.util.datatype.enums.EnumSetTroopFleetVariantUtilTHM;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.dao.GeneralDaoZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zBasic.util.dataype.calling.ReferenceZZZ;
import basic.zKernel.KernelZZZ;

public class TroopFleetVariantDao<T> extends TroopVariantDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
	public TroopFleetVariantDao() throws ExceptionZZZ{
		super();
		this.installLoger(TroopFleetVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopFleetVariantDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(TroopFleetVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopFleetVariantDao(IHibernateContextProviderZZZ objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(TroopFleetVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopFleetVariantDao(IHibernateContextProviderZZZ objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(TroopFleetVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	public int createEntriesAll(){
		int iReturn = 0;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
			
			try {				
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();
				Session session = objContextHibernate.getSession(); //kürzer: session=this.getSession()
				if(session == null) break main;	
				
				//###################
				//1. Speichere die TroopFleetVarianten
				//####################					
											
				//Alle Enumerations hier einlesen.
				//Anders als bei der _fillValue(...) Lösung können hier nur die Variablen gefüllt werden. Die Zuweisung muss im Konstruktor des immutable Entity-Objekts passieren, das dies keine Setter-Methodne hat.				
				Collection<String> colsEnumAlias = EnumZZZ.getNames(TroopFleetVariant.getThiskeyEnumClassStatic());
				for(String sEnumAlias : colsEnumAlias){
					boolean bErg = this.createEntryByEnumSetAlias(sEnumAlias);
					if(bErg = true){					
						iReturn++;
					}else{						
					}
				}//end for
												
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
						
		}//end main:
		return iReturn;		
	}
	
	boolean createEntryByEnumSetAlias(String sEnumAlias){
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START: .... Gefundener Enum-Name: " + sEnumAlias);
			
			try {	
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();
				Session session = objContextHibernate.getSession(); //kürzer: session=this.getSession()
				if(session == null) break main;	
			
			//Alle Enumerations hier einlesen.
			TroopFleetVariant objValueTemp = new TroopFleetVariant();//Quasi als Dummy, aus dem die Enumeration (angelegt als innere Klasse) ausgelesen werden kann.

			//DAS GEHT NICHT, DA JAVA IMMER EIN PASS_BY_VALUE MACHT.
			//Long lngThisValue = new Long(0);
			//String sName = new String("");
			//String sShorttext = new String("");
			//String sLongtext = new String("");
			//String sDescription = new String("");
			//this._fillValueImmutable(objValueTemp, sEnumAlias, lngThisValue, sName, sShorttext, sLongtext, sDescription); 

			//Hier der Workaround mit Referenz-Objekten, aus denen dann der Wert geholt werden kann. Also PASS_BY_REFERENCE durch auslesen der Properties der Objekte.  
			ReferenceZZZ<Long> lngThisValue = new ReferenceZZZ(4);
			ReferenceZZZ<String> sName = new ReferenceZZZ("");
			ReferenceZZZ<String> sUniquetext = new ReferenceZZZ("");
			ReferenceZZZ<String> sCategorytext = new ReferenceZZZ("");
			ReferenceZZZ<Integer> iMoveRange = new ReferenceZZZ("");
			ReferenceZZZ<Float> fHealthInitial = new ReferenceZZZ("");			
			ReferenceZZZ<String> sImageUrl = new ReferenceZZZ("");
			ReferenceZZZ<Long> lngThisidDefaulttext = new ReferenceZZZ("");
			ReferenceZZZ<Long> lngThisidImmutabletext = new ReferenceZZZ("");
			
			//Speziell für FLEET:
			ReferenceZZZ<Integer> iNumberOfTurret = new ReferenceZZZ("");
			this._fillValueImmutableByEnumAlias(objValueTemp, sEnumAlias, lngThisValue, sName, sUniquetext, sCategorytext, iMoveRange, fHealthInitial, sImageUrl, lngThisidDefaulttext, lngThisidImmutabletext, iNumberOfTurret);
			
			//TODO .... nicht vergessen nun basierend auf den Thiskey-Einträgen für den Defaulttext und Immutabletext das jeweilige Objekt zu suchen.
			//          Falls das Objekt nicht gefunden wird, muss es per TileDefaulttextDAO oder TileImmutabletextDAO erzeugt werden.

			//TODO .... Mit den TEXTOBJEKTEN kann man dann über den Konstruktor das Objekt füllen. Merke: Auch die TroopFleetVarianten sind IMMUTABLE.
			
			
			
			
			
			//Für die Ausgabe im Log: Hilfsvariablen
			String sDescription = new String("");
			String sShorttext = new String("");
			String sLongtext = new String("");
			
			
			//####################################################
			//### Suchen und ggfs. Erzeugen des TileDefaulttext
			//#####################################################	
			TileDefaulttextDao daoTileText = new TileDefaulttextDao(objContextHibernate);
		    Key objKey = daoTileText.searchThiskey(lngThisidDefaulttext.get());
			if(objKey==null){
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThisidDefaulttext.get() +"' TileDefaulttext ('" + lngThisidDefaulttext.get() + "') NOCH NICHT gefunden.");
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThisidDefaulttext.get() +"' erzeuge benötigten TileDefaulttext ('" + lngThisidDefaulttext.get() + "') .");
				
				//TODO GOON 20180130: Hier das erstellte Objekt zurückgeben, dann braucht man auch nicht mehr danach zu suchen.
				daoTileText.createEntryForThiskey(lngThisidDefaulttext.get());					
				objKey = daoTileText.searchThiskey(lngThisidDefaulttext.get());
				if(objKey==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThisidDefaulttext.get()+"' kann benötigten TileDefaulttext ('" + lngThisidDefaulttext.get() + "') nicht erzeugen.");
					break main;					
				}else{
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThisidDefaulttext.get()+"' benötigten TileDefaulttext ('" + lngThisidDefaulttext.get() + "')  erzeugt.");						
				}				
			}
			TileDefaulttext objDefaulttext = (TileDefaulttext) objKey;				
			sDescription = objDefaulttext.getDescription();
			sShorttext = objDefaulttext.getShorttext();				
			sLongtext = objDefaulttext.getLongtext();				
			System.out.println("Thiskey='"+lngThisidDefaulttext.get()+"' dazugehörender TileDefaulttext gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		    
			
			//####################################################
			//### Suchen und ggfs. Erzeugen des TileImmutabletext
			//#####################################################
			TileImmutabletextDao daoTileImmutable = new TileImmutabletextDao(objContextHibernate);			
		    KeyImmutable objKeyImmutable = daoTileImmutable.searchThiskey(lngThisidImmutabletext.get());
		    if(objKeyImmutable==null){
				System.out.println("Thiskey='"+lngThisidImmutabletext.get()+"' TileImmutabletext ('" + lngThisidImmutabletext.get() + "') NOCH NICHT gefunden.");
				System.out.println("Thiskey='"+lngThisidImmutabletext.get()+"' erzeuge benötigten TileImmutabletext ('" + lngThisidImmutabletext.get() + "') .");
				
				//TODO GOON 20180130: Hier das erzeugte Objekt direkt zurückgeben lassen, dann braucht man es nicht zu suchen.
				daoTileImmutable.createEntryForThiskey(lngThisidImmutabletext.get());
				
				objKeyImmutable = daoTileImmutable.searchThiskey(lngThisidImmutabletext.get());
				if(objKeyImmutable==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThisidImmutabletext.get()+"' kann benötigten TileImmutabletext ('"+lngThisidImmutabletext.get()+"') nicht erzeugen.");
					break main;						
				}else{
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Thiskey='"+lngThisidImmutabletext.get()+"' benötigten TileImmutabletext ('"+lngThisidImmutabletext.get()+"') erzeugt.");						
				}
		    }
		    TileImmutabletext objImmutableText = (TileImmutabletext) objKeyImmutable;				
			sDescription = objImmutableText.getDescription();
			sShorttext = objImmutableText.getShorttext();				
			sLongtext = objImmutableText.getLongtext();				
			System.out.println("Thiskey='"+lngThisidImmutabletext.get()+"' dazugehörender TileImmutabletext gefunden. ("+sShorttext+"|"+sLongtext+"|"+sDescription+")");									
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
		    
			
			//####################################################################################################
			//### Erzeugen der Variante. Merke: Sie ist immutable, also alles nur über den Konstruktor erzeugen.
			//####################################################################################################		
			session = this.getSession(); //Die Session am Anfang ist durch die vielen anderen DaoObjekte und deren Aktionen bestimmt schon geschlossen.			
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			//public TroopFleetVariant(int iKey, String sUniquetext, String sCategorytext, int intMapMoveRange, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
			TroopFleetVariant objValueVariant = new TroopFleetVariant(lngThisValue.get().intValue(), sUniquetext.get(), sCategorytext.get(), iMoveRange.get().intValue(), fHealthInitial.get().floatValue(), sImageUrl.get(), objDefaulttext, objImmutableText, iNumberOfTurret.get().intValue());		//Bei jedem Schleifendurchlauf neu machen, sonst wird lediglich nur 1 Datensatz immer wieder verändert.
			
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			session.save(objValueVariant); //Hibernate Interceptor wird aufgerufen																				
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
					bReturn = true;
//				}
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE");
			
		}
		return bReturn;
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
		ArrayList<TroopFleetVariant> listaObject = (ArrayList<TroopFleetVariant>) this.findLazyAll();
		
		for(TroopFleetVariant objTroopFleetVariant : listaObject){
			System.out.println("Lösche: TroopFleetVariant.toString(): " + objTroopFleetVariant.toString());
			String sUniquetextStored = objTroopFleetVariant.getUniquetext();
			System.out.println("Uniquetext (gespeichert): " + sUniquetextStored);	

			this.delete(objTroopFleetVariant);
		}//End for
		bReturn = true;
		
	}//End main
	return bReturn;				
}

public boolean delete(TroopFleetVariant objFleetVariant) {
	boolean bReturn = false;
	main:{
		if(objFleetVariant==null)break main;
		bReturn = super.delete((T) objFleetVariant);
	}//end main
	return bReturn;
}
	
	//####### EIGENE METHODEN ###########
/* Das ist die Variante für Entities, die nicht mit der Annotation "Immutable" versehen sind.
 * Die Entities mit der Annotation "Immutable" haben nämlich keine setter-Methoden.
 */
//Da Java nur ein CALL_BY_VALUE machen kann, weden hier für die eingefüllten Werte Referenz-Objekte verwendet.
//Erst die normalen Enum-Werte, dann ... sUniquetext / sCategorytext / iMoveRange / sImageUrl / iThisKeyDefaulttext / iThiskeyImmutabletext;
//
//Merke objValue ist ein Dummy, damit man an die Enumeration, die als innere Klasse realisiert ist, kommt.
protected <T> void _fillValueImmutableByEnumAlias(ITroopFleetVariantTHM objValue,String sEnumAlias, ReferenceZZZ<Long> objlngThiskey, 
		ReferenceZZZ<String> objsName, ReferenceZZZ<String> objsUniquetext, ReferenceZZZ<String> objsCategorytext, 
		ReferenceZZZ<Integer> objintMoveRange, ReferenceZZZ<Float> objfltHealthInitial, ReferenceZZZ<String> objsImageUrl,
		ReferenceZZZ<Long> objlngThisidTextDefault, ReferenceZZZ<Long> objlngThisidTextImmutable,
		ReferenceZZZ<Integer> objintNumberOfTurret){
	
	//Merke: Direktes Reinschreiben geht wieder nicht wg. "bound exception"
	//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, sEnumAlias);
	
	//Einlesen der Werte, die über alle Entities gleich sind:
	super._fillValueImmutableByEnumAlias(objValue, sEnumAlias, objlngThiskey, objsName, objsUniquetext, objsCategorytext, objintMoveRange, objfltHealthInitial, objsImageUrl, objlngThisidTextDefault, objlngThisidTextImmutable);
	
	//Also: Klasse holen und danach CASTEN.
	Class<?> objClass = ((KeyImmutable) objValue).getThiskeyEnumClass();

	//Einlesen Spezieller Werte für FLEET: 
	Integer intNumberOfTurret = EnumSetTroopFleetVariantUtilTHM.readEnumConstant_NumberOfTurretValue((Class<IEnumSetTroopFleetVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundene NumberOfTurret: " + intNumberOfTurret);	
	objintNumberOfTurret.set(intNumberOfTurret); //Damit wird CALL_BY_VALUE quasi gemacht....
}

@Override
public String getKeyTypeUsed() {
	return "TROOPFLEETVARIANT";
}
@Override
public boolean isVariantValid(long lngThisIdKey) {
	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
	boolean bReturn = false;
	main:{

	String sKeytype = "TROOPFLEETVARIANT";
	TroopFleetVariant objKey02 = (TroopFleetVariant)this.searchKey(sKeytype, lngThisIdKey );
	if(objKey02==null){
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Abfrage: UNERWARTETES ERGEBNIS. Kein Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThisIdKey + "' gefunden.");
		bReturn=false;
	}else{
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Abfrage: Erwartetes Ergebnis. Key mit dem KeyType '" + sKeytype + "' und dem Thiskey '" + lngThisIdKey + "' gefunden.");	
		bReturn=true;
	}			

	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
				
}//end main:
return bReturn;
}
@Override
public boolean isVariantStandard(long lngThisIdKey) {
	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
	boolean bReturn = false;
	main:{			
			Session session = this.getSession();
			if(session == null) break main;	
			
			//###################
			//1. Ermittle Daten der TroopFleetVarianten
			//####################					
					
			//Alle Enumerations hier einlesen.
			TroopFleetVariant objValueTemp = new TroopFleetVariant();//Quasi als Dummy, aus dem die Enumeration (angelegt als innere Klasse) ausgelesen werden kann.
						
			//Anders als bei der _fillValue(...) Lösung können hier nur die Variablen gefüllt werden. Die Zuweisung muss im Konstruktor des immutable Entity-Objekts passieren, das dies keine Setter-Methodne hat.				
			Collection<String> colsEnumAlias = EnumZZZ.getNames(TroopFleetVariant.getThiskeyEnumClassStatic());
			for(String sEnumAlias : colsEnumAlias){
				
				//DAS GEHT NICHT, DA JAVA IMMER EIN PASS_BY_VALUE MACHT.
				//Long lngThisValue = new Long(0);
				//String sName = new String("");
				//String sShorttext = new String("");
				//String sLongtext = new String("");
				//String sDescription = new String("");
				//this._fillValueImmutable(objValueTemp, sEnumAlias, lngThisValue, sName, sShorttext, sLongtext, sDescription); 

				//Hier der Workaround mit Referenz-Objekten, aus denen dann der Wert geholt werden kann. Also PASS_BY_REFERENCE durch auslesen der Properties der Objekte.  
				ReferenceZZZ<Long> lngThisValue = new ReferenceZZZ(4);
				ReferenceZZZ<String> sName = new ReferenceZZZ("");
				ReferenceZZZ<String> sUniquetext = new ReferenceZZZ("");
				ReferenceZZZ<String> sCategorytext = new ReferenceZZZ("");
				ReferenceZZZ<Integer> iMoveRange = new ReferenceZZZ("");
				ReferenceZZZ<Float> fHealthInitial = new ReferenceZZZ("");
				ReferenceZZZ<String> sImageUrl = new ReferenceZZZ("");
				ReferenceZZZ<Long> lngThisidDefaulttext = new ReferenceZZZ("");
				ReferenceZZZ<Long> lngThisidImmutabletext = new ReferenceZZZ("");
				
				//Speziell für FLEET:
				ReferenceZZZ<Integer> iNumberOfTurret = new ReferenceZZZ("");
				this._fillValueImmutableByEnumAlias(objValueTemp, sEnumAlias, lngThisValue, sName, sUniquetext, sCategorytext, iMoveRange, fHealthInitial, sImageUrl, lngThisidDefaulttext, lngThisidImmutabletext, iNumberOfTurret);
				
				if(lngThisValue.get().longValue() == lngThisIdKey ){
					bReturn = true;
					break main;
				}						
			}//end for

		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
					
	}//end main:
	return bReturn;
}
			
public List<TroopFleetVariant> searchTroopFleetVariantsAll() throws ExceptionZZZ{ //TODO GOON: Sortierung... , int iSortedDirection, boolean bAscending){
	List<TroopFleetVariant> listReturn = new ArrayList<TroopFleetVariant>();
	
	Session session = this.getSession();
	
	String sKeyType = this.getKeyTypeUsed(); //z.B. "TROOPARMYVARIANT" , "TROOPFLEETVARIANT"
	String sTable = this.getDaoTableName();  //z.B. TroopArmyVariant
	String sQuery = "from " + sTable + " as tableVariant where tableVariant.keyType = :keyType";
	Query query = session.createQuery(sQuery); // AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
	
	query.setString("keyType", sKeyType);
	//query.setString("mapX", sX);
	//query.setString("mapY", sY);
	
	//Object objResult = query.uniqueResult(); //Das sind aber ggfs. mehrere Werte		
	listReturn = query.list(); 
	System.out.println("Ergebnis der Query. Es wurden " + listReturn.size() + " Datensätze gefunden.");
	
	//3. Beispiel
	//TODO: Nicht den statischen HQL Ansatz, sondern über die Criteria API, d.h. die Where - Bedingung zur Laufzeit zusammensetzen
			
	//TODO GOON 20171127: Nach dem Update soll mit dem UI weitergearbeitet werden können			
	this.getHibernateContextProvider().closeAll();
	System.out.println("SessionFactory über den HibernateContextProvider geschlossen.... Nun wieder bearbeitbar im Java Swing Client?");
	return listReturn;
}
}//end class
