package use.thm.persistence.model;

import java.io.Serializable;
import java.util.EnumSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.genericEnum.ObjectTestMappedValue.EnumSetInnerMappedTestTypeZZZ;

/**
 * Lösung für Schlüssselwerttabellen.
 * Hier wird eine innere Klasse verwendet, um ein enum anbieten zu können.
 * 
 * @author Fritz Lindhauer
 * @param <IEnumTileDefaulttext>
 *
 */

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

@Entity
@Access(AccessType.PROPERTY)
@org.hibernate.annotations.Immutable //Ziel: Performancesteigerung. Siehe Buch "Java Persistance with Hibernate", S. 107. Dafür dürfen die POJOs aber keine public Setter-Methoden haben.

//Das muss in der Elternklasse angegeben werden
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle  Es ist eine eigene Tabelle notwendig, da Thiskey eindeutig sein soll.

// InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan des Discriminator Wertes unterschieden
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //
//@DiscriminatorColumn(name="KEYTYPE", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen. ////Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ...

@Table(name="k_immutabletext")
public class Immutabletext<IEnumImmutabletext>  extends KeyImmutable implements IOptimisticLocking{
	private static final long serialVersionUID = -8400471235691822606L; //auch wenn serialized nicht implementiert wird (sondern in der elternklasse), muss der staische Schlüssel hier eingetragen werden. 
	
	//Entsprechend der internen Enumeration. 
	//Merke: Die Enumeration dient der Festlegung der Defaultwerte. In den Feldern des Entities werden die gespeicherten Werte gehalten.
	private String sLongtext, sShorttext, sDescription;
		
	public Immutabletext(){
		super();
		this.setKeyType("IMMUTABLETEXT");
	}

	public Immutabletext(long lngThiskey, String sShorttext, String sLongtext, String sDescription){
		super();
		this.setKeyType("IMMUTABLETEXT");
		this.setThiskey(lngThiskey);
		this.setShorttext(sShorttext);
		this.setLongtext(sLongtext);
		this.setDescription(sDescription);
	}
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	//ABER NICHT AUF DIESER EBENEN, DA SIE ERBT VON KEY.java
	//ABER: BEIM ERBEN VON KEY wira automatisch eine Tabelle Key erstellt.... das will ich nicht
	
	 @Id	//hier notwendig für AccessType.PROPERTY			
	 @TableGenerator(name="lidGeneratorImmutabletext001", table="COMMON_FUER_IDGENERATOR_IMMUTABLETEXT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorImmutabletext001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 //Entferne also das unique...
	 @Column(name="IMMUTABLETEXT_ID_INCREMENTED", nullable=false)
	 public int getId(){
		 return super.getId();
	 }
	 protected void setId(int iLid){
		 super.setId(iLid);
	 }
	 
	//### ABSTRACTE METHODEN
	 
	 //Merke: Bei Nutzung der "Hibernate"-Verebung käme dies aus der Key Klasse. ABER: Dann hätte ich auch eine Tabelle "KEY" und das will ich nicht.
	 //           Also: Hier die Methoden seperat anbieten.
	 @Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
	public Long getThiskey() {
		 return super.getThiskey();
	}
	protected void setThiskey(Long thiskeyId) {
		super.setThiskey(thiskeyId);
	}
	
	@Column(name="KEYTYPE")
	@Access(AccessType.PROPERTY)
	public String getKeyType(){
		return super.getKeyType();
	}	
	protected void setKeyType(String sKeyType){
		super.setKeyType(sKeyType);
	}
	   
	@Column(name="shorttext", nullable=false)	 
	public String getShorttext(){
		return this.sShorttext;
	}
	protected void setShorttext(String s){
		this.sShorttext = s;
	}
	
	@Column(name="longtext", nullable=false)	 
	public String getLongtext(){
		return this.sLongtext;
	}
	protected void setLongtext(String s){
		this.sLongtext = s;
	}
	
	@Column(name="description", nullable=false)	 
	public String getDescription(){
		return this.sDescription;
	}
	protected void setDescription(String s){
		this.sDescription = s;
	}
	


    private static final java.util.List<String> allAttributeNames = java.util.Arrays.asList(new String[]{});

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
    
	 @Transient
    public Class getThiskeyEnumClass() {
	      return Immutabletext.getThiskeyEnumClassStatic();
	   }
	 
	
    //### Statische Methode (um einfacher darauf zugreifen zu können)
    public static Class getThiskeyEnumClassStatic(){    	
    	return EnumImmutabletext.class;    	
    }

	//#######################################################
	//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
	//### int Key, String shorttext, String longtext, String description
	//#######################################################
	public enum EnumImmutabletext implements IEnumSetTextTHM,  IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
		
   	 @IFieldDescription(description = "DTXT01 TEXTIMMUTABLES") 
   	T01(1,"DTXT01","DTEXT 01","A test dtext 01 immutable."),
   	
   	 @IFieldDescription(description = "DTXT02 TEXTIMMUTABLES") 
   	T02(2,"DTXT02","DTEXT 02", "A test dtext 02 immutable.");
   	   	
   private Long lKey;
   private String sLongtext, sShorttext, sDescription;
   

   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   EnumImmutabletext(int iKey, String sShorttext, String sLongtext, String sDescription){
       this.lKey = Long.valueOf(iKey);
       this.sShorttext = sShorttext;
       this.sLongtext = sLongtext;
       this.sDescription = sDescription;
   }
   
   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
   EnumImmutabletext(){	
   }

  //##################################################
  //#### Folgende Methoden bring Enumeration von Hause aus mit:

   public String getName(){
	   return super.name();
   }
   
   @Override
   public String toString() {
       return this.sShorttext;
   }

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
	public Long getThiskey() {
		return this.lKey;
	}

   //### Folgende Methoden sind zum komfortablen arbeiten gedacht.
   public int getPosition() {
   	return getIndex() + 1;
   }

	
	
   // the valueOfMethod <--- Translating from DB
   public static EnumImmutabletext fromShorttext(String s) {
       for (EnumImmutabletext state : values()) {
           if (s.equals(state.getShorttext()))
               return state;
       }
       throw new IllegalArgumentException("Not a correct shorttext: " + s);
   }

   public EnumSet<?>getEnumSetUsed(){
   	return EnumImmutabletext.getEnumSet();
   }

   @SuppressWarnings("rawtypes")
   public static <E> EnumSet getEnumSet() {
   	
       //Merke: Das wird anders behandelt als FLAGZ Enumeration.
   	//String sFilterName = "FLAGZ"; /
   	//...
   	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
   	
   	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
   	Class<EnumImmutabletext> enumClass = EnumImmutabletext.class;
   	EnumSet<EnumImmutabletext> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
   	
   	for(Object obj : EnumImmutabletext.class.getEnumConstants()){
   		//System.out.println(obj + "; "+obj.getClass().getName());
   		set.add((EnumImmutabletext) obj);
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
