package use.thm.persistence.model;

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
import javax.persistence.Transient;

import use.thm.persistence.interfaces.enums.IEnumSetDefaulttextTHM;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IKeyProviderZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.genericEnum.ObjectTestMappedValue.EnumSetInnerMappedTestTypeZZZ;

/**
 * Lösung für Schlüssselwerttabellen.
 * Hier wird eine innere Klasse verwendet, um ein enum anbieten zu können.
 * 
 * @author Fritz Lindhauer
 *
 */

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen. In hibernate.cfg.xml reicht nicht.

@Entity
@Access(AccessType.PROPERTY) ///20171019: damit soll dann enum eingebunden werden, automatisch...
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="k_tile_defaulttext")
public class TileDefaulttext  extends AbstractValue<TileDefaulttext> implements Serializable, IOptimisticLocking{
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	//@Id //hier notwendig für AccessType.FIELD
	private int iMyTestSequence;
	
	//Entsprechend der internen Enumeration. 
	//Merke: Die Enumeration dient der Festlegung der Defaultwerte. In den Feldern des Entities werden die gespeicherten Werte gehalten.
	private Long lKey;
	private String sLongtext, sShorttext, sDescription;
	
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	 @Id	//hier notwendig für AccessType.PROPERTY			
	 @TableGenerator(name="lidGeneratorDefaulttext001", table="COMMON_FUER_IDGENERATOR_DEFAULTTEXT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorDefaulttext001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 //Entferne also das unique...
	 @Column(name="DEFAULTTEXT_ID_INCREMENTED", nullable=false)
	 public int getId(){
		 return this.iMyTestSequence;
	 }
	 public void setId(int iLid){
		 this.iMyTestSequence = iLid;
	 }
	 
	 @Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
	 @Override
	public Long getThiskey() {
		 return this.lKey;
	}
	@Override
	public void setThiskey(Long thiskeyId) {
		this.lKey = thiskeyId;
	}
	   
	@Column(name="shorttext", nullable=false)	 
	public String getShorttext(){
		return this.sShorttext;
	}
	public void setShorttext(String s){
		this.sShorttext = s;
	}
	
	@Column(name="longtext", nullable=false)	 
	public String getLongtext(){
		return this.sLongtext;
	}
	public void setLongtext(String s){
		this.sLongtext = s;
	}
	
	@Column(name="description", nullable=false)	 
	public String getDescription(){
		return this.sDescription;
	}
	public void setDescription(String s){
		this.sDescription = s;
	}
	


    private static final java.util.List<String> allAttributeNames = java.util.Arrays.asList(new String[]{"hiskeyId"});

    /**
     * {@inheritDoc}
     */
  //  @java.lang.Override
    @Transient
    public java.util.List<String> getAllAttributeNames() {
        return getAllAttributeNamesIntern();
    }

    /**
     * {@inheritDoc}
     */
//    @java.lang.Override
    @Transient
    protected java.util.List<String> getAllAttributeNamesIntern() {
    	java.util.List<String> tmp = super.getAllAttributeNamesIntern();
    	tmp.addAll(allAttributeNames);
        return tmp;
    }
    /* GENERATED_END */
	
	
	 // @Override
    @Transient
    public Class<EnumTileDefaulttext> getThiskeyEnumClass() {
	   //public Class<IEnumSetDefaulttextTHM> getThiskeyEnumClass() {
	      return EnumTileDefaulttext.class;
    	//return IEnumSetDefaulttextTHM.class;
	   }
    
    public static Class<EnumTileDefaulttext> getThiskeyEnumClassStatic(){
    	return EnumTileDefaulttext.class;
    }

	//#######################################################
	//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
	//### int Key, String shorttext, String longtext, String description
	//#######################################################
	public enum EnumTileDefaulttext implements IEnumSetDefaulttextTHM,  IKeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
		
   	 @IFieldDescription(description = "ARMY TEXTVALUES") 
   	ARMY(1,"Army","Land army","A tile which cannot enter ocean fields."),
   	
   	 @IFieldDescription(description = "FLEET TEXTVALUES") 
   	FLEET(2,"Fleet","Ocean fleet", "A tile which cannot enter landarea fields.");
   	   	
   private Long lKey;
   private String sLongtext, sShorttext, sDescription;
   

   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   EnumTileDefaulttext(int iKey, String sShorttext, String sLongtext, String sDescription){
       this.lKey = Long.valueOf(iKey);
       this.sShorttext = sShorttext;
       this.sLongtext = sLongtext;
       this.sDescription = sDescription;
   }
   
   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
   EnumTileDefaulttext(){	
   }

  //##################################################
  //#### Folgende Methoden bring Enumeration von Hause aus mit:
   
   @Override
   public String getName(){
	   return super.name();
   }
   
   @Override
   public String toString() {
       return this.sShorttext;
   }

   @Override
   public int getIndex() {
   	return ordinal();
   }

   //##################################################
   //#### Folgende Methoden holen die definierten Werte.
   // the identifierMethod ---> Going in DB
   public String getShorttext() {
       return this.sShorttext;
   }
   
   public String getLongtext(){
	   return this.sLongtext;
   }
   
   public String getDescription(){
	   return this.sDescription;
   }
   
   //#### Methode aus IKeyProviderZZZ
   @Override
	public Long getThiskey() {
		return this.lKey;
	}

   //### Folgende Methoden sind zum komfortablen arbeiten gedacht.
   @Override
   public int getPosition() {
   	return getIndex() + 1;
   }

	
	
   // the valueOfMethod <--- Translating from DB
   public static EnumTileDefaulttext fromShorttext(String s) {
       for (EnumTileDefaulttext state : values()) {
           if (s.equals(state.getShorttext()))
               return state;
       }
       throw new IllegalArgumentException("Not a correct shorttext: " + s);
   }

   public EnumSet<?>getEnumSetUsed(){
   	return EnumTileDefaulttext.getEnumSet();
   }

   @SuppressWarnings("rawtypes")
   public static <E> EnumSet getEnumSet() {
   	
       //Merke: Das wird anders behandelt als FLAGZ Enumeration.
   	//String sFilterName = "FLAGZ"; /
   	//...
   	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
   	
   	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
   	Class<EnumTileDefaulttext> enumClass = EnumTileDefaulttext.class;
   	EnumSet<EnumTileDefaulttext> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
   	
   	for(Object obj : EnumTileDefaulttext.class.getEnumConstants()){
   		//System.out.println(obj + "; "+obj.getClass().getName());
   		set.add((EnumTileDefaulttext) obj);
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

 

   }//End inner class

		
}//end class
