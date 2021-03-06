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
import use.thm.persistence.interfaces.ITroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopArmyVariantTHM;
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
import use.thm.persistence.util.HibernateUtilTHM;
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
import basic.zBasic.util.datatype.calling.ReferenceZZZ;
import basic.zKernel.KernelZZZ;

public class TroopArmyVariantDao<T> extends TroopVariantDao<T> {
	private static final long serialVersionUID = 1L;

	/* Constructor
	 * WICHTIG: Der hier angegebenen Name der Entity-Klasse wird von den GeneralDAO - Klassen verwendet.
	 *                Daher unbedingt beim Einsatz von Vererbung korrekt anpassen.
	 *                Z.B. Will man mit dem Dao eigentlicht TileDefaulttexte behandel und gibt hier Defaulttext an, werden sowohl die TileDefaulttexte als auch die Defaulttexte mit .findLazyAll() gefunden. */
	public TroopArmyVariantDao() throws ExceptionZZZ{
		super();
		this.installLoger(TroopArmyVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopArmyVariantDao(IHibernateContextProviderZZZ objContextHibernate) throws ExceptionZZZ{
		super(objContextHibernate);		
		this.installLoger(TroopArmyVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopArmyVariantDao(IHibernateContextProviderZZZ objContextHibernate, String sFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, sFlagControl);
		this.installLoger(TroopArmyVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	public TroopArmyVariantDao(HibernateContextProviderSingletonTHM objContextHibernate, String[] saFlagControl) throws ExceptionZZZ{
		super(objContextHibernate, saFlagControl);
		this.installLoger(TroopArmyVariant.class);//Durch das Installieren des Loggers mit der korrekten Klasse wird GeneralDao.getT() erst korrekt ermöglicht.
	}
	
	public int createEntriesAll(){
		int iReturn = 0;
		main:{
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
													
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();				
				Session session = this.getSessionOpen();
				if(session == null) break main;	
				
				//###################
				//1. Speichere die TroopFleetVarianten
				//####################					
												
				//Alle Enumerations hier einlesen.
				//Anders als bei der _fillValue(...) Lösung können hier nur die Variablen gefüllt werden. Die Zuweisung muss im Konstruktor des immutable Entity-Objekts passieren, das dies keine Setter-Methodne hat.				
				Collection<String> colsEnumAlias = EnumZZZ.getNames(TroopArmyVariant.getThiskeyEnumClassStatic());
				for(String sEnumAlias : colsEnumAlias){
					boolean bErg = this.createEntryByEnumSetAlias(sEnumAlias);
					if(bErg = true){					
						iReturn++;
					}else{						
					}
				}//end for
												

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
			}
		}//end main:
		return iReturn;		
	}
		
	boolean createEntryByEnumSetAlias(String sEnumAlias){
		boolean bReturn = false;
		main:{
			try{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START: .... Gefundener Enum-Name: " + sEnumAlias);
										
			Session session = this.getSessionOpen();
			if(session == null) break main;			
			
			TroopArmyVariant objValueTemp = new TroopArmyVariant();//Quasi als Dummy, aus dem die Enumeration (angelegt als innere Klasse) ausgelesen werden kann.

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
			
			//Speziell für ARMY:
			ReferenceZZZ<Integer> iDegreeOfCoverMax = new ReferenceZZZ("");
			this._fillValueImmutableByEnumAlias(objValueTemp, sEnumAlias, lngThisValue, sName, sUniquetext, sCategorytext, iMoveRange, fHealthInitial, sImageUrl, lngThisidDefaulttext, lngThisidImmutabletext, iDegreeOfCoverMax);
			
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
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContextProvider();
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
			session = this.getSessionOpen(); //Die Session am Anfang ist durch die vielen anderen DaoObjekte und deren Aktionen bestimmt schon geschlossen.
			validEntry:{
				boolean bGoon = false;
				String sMessage = new String("");
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:.... Gefundener Enum-Name: " + sEnumAlias);
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.			

				TroopArmyVariant objValueVariant = new TroopArmyVariant(lngThisValue.get().intValue(), sUniquetext.get(), sCategorytext.get(), iMoveRange.get().intValue(), fHealthInitial.get().floatValue(), sImageUrl.get(), objDefaulttext, objImmutableText, iDegreeOfCoverMax.get().intValue());		//Bei jedem Schleifendurchlauf neu machen, sonst wird lediglich nur 1 Datensatz immer wieder verändert.
				
				//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
				session.save(objValueVariant); //Hibernate Interceptor wird aufgerufen																				
				if (!session.getTransaction().wasCommitted()) {
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
					
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(objContextHibernate,"save",session.getTransaction());
					if(objResult!=null){
						sMessage = objResult.getVetoMessage();
						bGoon = !objResult.isVeto();
					}else{
						bGoon = true;
					}
				}else{
					if(session.getTransaction().isActive()){
						session.getTransaction().rollback();
						bGoon = false;
					}
				}	
				if(!bGoon){
					//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
					//this.getFacadeResult().setMessage(sMessage);
					break validEntry;
				}					
				}//end validEndtry:
				bReturn = true;

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE");
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
			}
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
		ArrayList<TroopArmyVariant> listaObject = (ArrayList<TroopArmyVariant>) this.findLazyAll();
		
		for(TroopArmyVariant objVariant : listaObject){
			System.out.println("Lösche: TroopArmyVariant.toString(): " + objVariant.toString());
			String sUniquetextStored = objVariant.getUniquetext();
			System.out.println("Uniquetext (gespeichert): " + sUniquetextStored);	

			this.delete(objVariant);
		}//End for
		bReturn = true;
		
	}//End main
	return bReturn;				
}

public boolean delete(TroopArmyVariant objVariant) {
	boolean bReturn = false;
	main:{
		if(objVariant==null)break main;
		bReturn = super.delete((T) objVariant);
	}//end main
	return bReturn;
}

//####### EIGENE METHODEN ###########
/* Das ist die Variante für Entities, die nicht mit der Annotation "Immutable" versehen sind.
* Die Entities mit der Annotation "Immutable" haben nämlich keine setter-Methoden.
*/
//Da Java nur ein CALL_BY_VALUE machen kann, weden hier für die eingefüllten Werte Referenz-Objekte verwendet.
//Erst die normalen Enum-Werte, dann ... sUniquetext / sCategorytext / iMoveRange / sImageUrl / iThisKeyDefaulttext / iThiskeyImmutabletext;
protected <T> void _fillValueImmutableByEnumAlias(ITroopArmyVariantTHM objValue,String sEnumAlias, ReferenceZZZ<Long> objlngThiskey, 
	ReferenceZZZ<String> objsName, ReferenceZZZ<String> objsUniquetext, ReferenceZZZ<String> objsCategorytext, 
	ReferenceZZZ<Integer> objintMoveRange, ReferenceZZZ<Float> obfltHealthInitial, ReferenceZZZ<String> objsImageUrl,
	ReferenceZZZ<Long> objlngThisidTextDefault, ReferenceZZZ<Long> objlngThisidTextImmutable,
	ReferenceZZZ<Integer> objintDegreeOfCoverMax){

	//Merke: Direktes Reinschreiben geht wieder nicht wg. "bound exception"
	//EnumSetDefaulttextUtilZZZ.getEnumConstant_DescriptionValue(EnumSetDefaulttextTestTypeTHM.class, sEnumAlias);
	
	//Einlesen der Werte, die für alle Varianten -Entities vorhanden sind: 
	super._fillValueImmutableByEnumAlias(objValue, sEnumAlias, objlngThiskey, objsName, objsUniquetext, objsCategorytext, objintMoveRange, obfltHealthInitial, objsImageUrl, objlngThisidTextDefault, objlngThisidTextImmutable);
		
	//Also: Klasse holen und danach CASTEN.
	Class<?> objClass = ((KeyImmutable) objValue).getThiskeyEnumClass();

	//Einlesen der Werte, die Speziell für ARMY Varainten Entities vorhanden sind.	
	Integer intDegreeOfCoverMax = EnumSetTroopArmyVariantUtilTHM.readEnumConstant_DegreeOfCoverMax((Class<IEnumSetTroopArmyVariantTHM>)objClass, sEnumAlias);
	System.out.println("Gefundener DegreeOfCoverMax: " + intDegreeOfCoverMax);
	objintDegreeOfCoverMax.set(intDegreeOfCoverMax); //Damit wird CALL_BY_VALUE quasi gemacht....		
}

@Override
public String getKeyTypeUsed() {
	return "TROOPARMYVARIANT";
}
@Override
public boolean isVariantValid(long lngThisIdKey) throws ExceptionZZZ {
	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
	boolean bReturn = false;
	main:{

	String sKeytype = "TROOPARMYVARIANT";
	TroopArmyVariant objKey02 = (TroopArmyVariant)this.searchKey(sKeytype, lngThisIdKey );
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
public boolean isVariantStandard(long lngThisIdKey) throws ExceptionZZZ {
	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START ##############");			
	boolean bReturn = false;
	main:{
//		try {				
//			KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
//			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
//			
			//###################
			//1. Ermittle Daten der TroopArmyVarianten
			//####################					
			//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
//			Session session = objContextHibernate.getSessionCurrent();
//			if(session == null) break main;			
			//Nein, wird innerhalb der Schleife gemacht. session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
		
			
			
			//Alle Enumerations hier einlesen.
			TroopArmyVariant objValueTemp = new TroopArmyVariant();//Quasi als Dummy, aus dem die Enumeration (angelegt als innere Klasse) ausgelesen werden kann.
						
			//Anders als bei der _fillValue(...) Lösung können hier nur die Variablen gefüllt werden. Die Zuweisung muss im Konstruktor des immutable Entity-Objekts passieren, das dies keine Setter-Methodne hat.				
			Collection<String> colsEnumAlias = EnumZZZ.getNames(TroopArmyVariant.getThiskeyEnumClassStatic());
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
				ReferenceZZZ<Integer> iGradeOfCover = new ReferenceZZZ("");
				
				//Einlesen der Werte, die für alle Entities vorhanden sind PLUS Werte für Amry
				ReferenceZZZ<Integer> iNumberOfTurret = new ReferenceZZZ("");
				this._fillValueImmutableByEnumAlias(objValueTemp, sEnumAlias, lngThisValue, sName, sUniquetext, sCategorytext, iMoveRange, fHealthInitial, sImageUrl, lngThisidDefaulttext, lngThisidImmutabletext, iGradeOfCover);
				
				if(lngThisValue.get().longValue() == lngThisIdKey ){
					bReturn = true;
					break main;
				}						
			}//end for
						
//		} catch (ExceptionZZZ e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");			
					
	}//end main:
	return bReturn;
}
public List<TroopArmyVariant> searchTroopArmyVariantsAll() throws ExceptionZZZ{ //TODO GOON: Sortierung... , int iSortedDirection, boolean bAscending){
	List<TroopArmyVariant> listReturn = new ArrayList<TroopArmyVariant>();
	main:{
		Session session = this.getSessionOpen();
		if(session == null) break main;	
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
		session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.

	
	String sKeyType = this.getKeyTypeUsed(); //z.B. "TROOPARMYVARIANT" , "TROOPFLEETVARIANT"
	String sTable = this.getDaoTableName();  //z.B. TroopArmyVariant
	String sQuery = "from " + sTable + " as tableVariant where tableVariant.keyType = :keyType";
	Query query = session.createQuery(sQuery); // AND tableTile.objHexCell.id.mapX = :mapX AND tableTile.objHexCell.id.mapY = :mapY");
	
	query.setString("keyType", sKeyType);
	//query.setString("mapX", sX);
	//query.setString("mapY", sY);
	
	//Object objResult = query.uniqueResult(); //Das sind aber ggfs. mehrere Werte		
	listReturn = query.list();
	session.getTransaction().commit();
	System.out.println("Ergebnis der Query. Es wurden " + listReturn.size() + " Datensätze gefunden.");
	
	//3. Beispiel
	//TODO: Nicht den statischen HQL Ansatz, sondern über die Criteria API, d.h. die Where - Bedingung zur Laufzeit zusammensetzen
			
	//TODO GOON 20171127: Nach dem Update soll mit dem UI weitergearbeitet werden können			
	this.getHibernateContextProvider().closeAll();
	System.out.println("SessionFactory über den HibernateContextProvider geschlossen.... Nun wieder bearbeitbar im Java Swing Client?");
	}//end main:
	return listReturn;
}
	

}//end class
