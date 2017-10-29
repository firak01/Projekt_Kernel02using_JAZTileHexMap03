package basic.zBasic.util.genericEnum;

import java.io.Serializable;
import java.util.EnumSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import base.datatype.KeyEnum;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IKeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IKeyValueZZZ;
import basic.zBasic.util.abstractEnum.EnumSetMappedTestTypeZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;

//Merke: Diese Klasse könnte auch als Entity in eine Tabelle für Schlüsselwortlisten verwendet werden.
//           Da das aber hier nicht das ziel ist, habe ich entsprechende Annotationen einfach auskkommentiert.
//           Vergleiche entsprechende Klassen im TileHexMap Projekt.

//Merke: Neue Entities immer in HibernateContextProviderSingletonTHM hinzufügen. In hibernate.cfg.xml reicht nicht.

//@Entity
//@Access(AccessType.PROPERTY) //20171019: damit soll dann enum eingebunden werden, automatisch...
//@Table(name="k_tile_defaulttext")
public class ObjectTestMappedValue extends AbstractValue<ObjectTestMappedValue> implements Serializable{ //, IOptimisticLocking{
	private static final long serialVersionUID = 1L;

	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	//@Id //wäre hier notwendig für AccessType.FIELD
	private int iMyTestSequence;
	
	private String sTextDefault;
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	 //@Id	//ist hier notwendig für AccessType.PROPERTY			
	// @TableGenerator(name="lidGeneratorDefaulttext002", table="COMMON_FUER_IDGENERATOR_DEFAULTTEXT002",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 //@GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorDefaulttext001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 //Entferne also das unique...
	 //@Column(name="DEFAULTTEXT_ID_INCREMENTED", nullable=false)
	 public int getId(){
		 return this.iMyTestSequence;
	 }
	 public void setId(int iLid){
		 this.iMyTestSequence = iLid;
	 }
     
	//@Column(name="defaulttext", nullable=false)	 
	public String getDefaulttext(){
		return this.sTextDefault;
	}
	public void setDefaulttext(String s){
		this.sTextDefault = s;
	}
		
    private Long hiskeyId;
    
    public enum EnumSetInnerMappedTestTypeZZZ implements IEnumSetMappedZZZ,  IKeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
    	 @IFieldDescription(description = "Erster Testwert") 
    	ONE(1,"eins","e."),
    	
    	 @IFieldDescription(description = "Zweiter Testwert") 
    	TWO(2,"zwei","z."),
    	
    	 @IFieldDescription(description = "Dritter Testwert") 
    	THREE(3,"drei","d.");
    	
    private Long key;
    private String name, abbr;
    


    //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
//              In der Util-Klasse habe ich aber einen Workaround gefunden.
    EnumSetInnerMappedTestTypeZZZ(){	
    }

    //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
    EnumSetInnerMappedTestTypeZZZ(int key, String fullName, String abbr) {
    	this.key = Long.valueOf(key);
        this.name = fullName;
        this.abbr = abbr;
    }


    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int getIndex() {
    	return ordinal();
    }

    // the identifierMethod ---> Going in DB
    public String getAbbreviation() {
        return this.abbr;
    }

    // the valueOfMethod <--- Translating from DB
    public static EnumSetInnerMappedTestTypeZZZ fromAbbreviation(String s) {
        for (EnumSetInnerMappedTestTypeZZZ state : values()) {
            if (s.equals(state.getAbbreviation()))
                return state;
        }
        throw new IllegalArgumentException("Not a correct abbreviation: " + s);
    }

    @Override
    public String getDescription(){
    	return this.name;
    }

    public EnumSet<?>getEnumSetUsed(){
    	return EnumSetInnerMappedTestTypeZZZ.getEnumSet();
    }

    @SuppressWarnings("rawtypes")
    public static <E> EnumSet getEnumSet() {
    	
        //Merke: Das wird anders behandelt als FLAGZ Enumeration.
    	//String sFilterName = "FLAGZ"; /
    	//...
    	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
    	
    	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
    	Class<EnumSetInnerMappedTestTypeZZZ> enumClass = EnumSetInnerMappedTestTypeZZZ.class;
    	EnumSet<EnumSetInnerMappedTestTypeZZZ> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
    	
    	for(Object obj : EnumSetInnerMappedTestTypeZZZ.class.getEnumConstants()){
    		//System.out.println(obj + "; "+obj.getClass().getName());
    		set.add((EnumSetInnerMappedTestTypeZZZ) obj);
    	}
    	return set;
    	
    }

    //TODO: Mal ausprobieren was das bringt
    //Convert Enumeration to a Set/List
    private static <E extends Enum<E>>EnumSet<E> toEnumSet(Class<E> enumClass,long vector){
    	  EnumSet<E> set=EnumSet.noneOf(enumClass);
    	  long mask=1;
    	  for (  E e : enumClass.getEnumConstants()) {
    	    if ((mask & vector) == mask) {
    	      set.add(e);
    	    }
    	    mask<<=1;
    	  }
    	  return set;
    	}

    @Override
    public int getPosition() {
    	return getIndex() + 1;
    }

	@Override
	public Long getThiskey() {
		return this.key;
	}

    }//End inner class
    

   // @Override
    public Class<EnumSetInnerMappedTestTypeZZZ> getThiskeyEnumClass() {
        return EnumSetInnerMappedTestTypeZZZ.class;
    }
 

    /* GENERATED_BEGIN */

    /**
     * Autogenerated Default-Constructor
     * cut&paste out of 'GENERATED'-Block to modify
     */
    public ObjectTestMappedValue() {
       // super();
    }

    /**
     * Autogenerated Constructor to use if an already generated objGuid is to be used (e.g. from an EntityDto)
     * @param objGuid
     */
//    public TileDefaulttext(... objGuid.toString()) {
//   //     this();
//    //    setObjGuid(objGuid.toString());
//    }

    /**
     * @return hiskeyId
     */
  //  @Override
    public Long getThiskeyId() {
        return hiskeyId;
    }


    /**
     * @param newValue
     */
  //  @Override
    public void setThiskeyId(Long newValue) {
        this.hiskeyId = newValue;
    }


    private static final java.util.List<String> allAttributeNames = java.util.Arrays.asList(new String[]{"hiskeyId"});

    /**
     * {@inheritDoc}
     */
  //  @java.lang.Override
    public java.util.List<String> getAllAttributeNames() {
        return getAllAttributeNamesIntern();
    }

    /**
     * {@inheritDoc}
     */
    @java.lang.Override
    protected java.util.List<String> getAllAttributeNamesIntern() {
    	//Merke: 20171024 super bezieht sich auf abstractValue. Darin wird ebenfalls super verewendet, das sich auf java.lang bezieht.    
    	java.util.List<String> tmp = super.getAllAttributeNamesIntern();
        tmp.addAll(allAttributeNames);
        return tmp;
    }
    /* GENERATED_END */
	
}//end class