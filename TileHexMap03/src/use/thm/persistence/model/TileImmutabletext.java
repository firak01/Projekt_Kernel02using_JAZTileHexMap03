package use.thm.persistence.model;

import java.io.Serializable;
import java.util.EnumSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.NaturalId;

import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;
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

@Entity //Merke: Ein eingenes Entity trotz der Verebung notwendig, Es ist auch eine eigene Tabelle notwendig, das Thiskey eindeutig sein soll. s. Vererbungsstrategie
            //           Der id Schlüssel wird aber über eine gemeinsame Tabelle erstellt.
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //
// InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden
//@DiscriminatorValue("xxx")  //Wird es wg. der Vererbung(!) von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.

//Das braucht in der erbenden Klasse nicht angegeben zu werden.
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
@Access(AccessType.PROPERTY) 
@Table(name="K_IMMUTABLETEXT_TILE") //Eine eigene Tabelle notwendig, da Thiskey eindeutig sein soll.
public class TileImmutabletext<IEnumTileDefaulttext> extends Immutabletext  implements IOptimisticLocking{
		
	public TileImmutabletext(){
		super();
		this.setKeyType("IMMUTABLETILETEXT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? !!! WIRD IN DER DAO-KLASSE WIEDERVERWENDET als getKeyType()....
	}
	
	public TileImmutabletext(long lngThiskey, String sShorttext, String sLongtext, String sDescription){
		super();
		this.setKeyType("IMMUTABLETILETEXT");
		this.setThiskey(lngThiskey);
		this.setShorttext(sShorttext);
		this.setLongtext(sLongtext);
		this.setDescription(sDescription);
	}
	
	//FGL: RAUSGENOMMEN, WG VERERBUNG
//	 @Id	//hier notwendig für AccessType.PROPERTY			
//	 @TableGenerator(name="lidGeneratorTileImmutabletext001", table="COMMON_FUER_IDGENERATOR_TileIMMUTABLETEXT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
//	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorImmutabletext001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
//	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
//	 //Entferne also das unique...
//	 @Column(name="TILEIMMUTABLETEXT_ID_INCREMENTED", nullable=false)
//	 public int getId(){
//		 return super.getId();
//	 }
//	 protected void setId(int iLid){
//		 super.setId(iLid);
//	 }
//	 
//		 	 
	 //Merke: Bei Nutzung der "Hibernate"-Verebung käme dies aus der Key Klasse. ABER: Dann hätte ich auch eine Tabelle "KEY" und das will ich nicht.
	 //           Also: Hier die Methoden seperat anbieten.
	//@NaturalId //20180203: Für @ManyToOne... Hier soll dann die ThiskeyId in der anderen Tabelle gespeichert werden und nicht die normale, generierte ID. WICHTIG: @NaturalId nur im Root oder der Superklasse verwenden
	@Column(name="thiskey_id", insertable=false, updatable=false, nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")		 	
	public Long getThiskey() {
		 return super.getThiskey();
	}
	protected void setThiskey(Long thiskeyId) {
		super.setThiskey(thiskeyId);
	}
	
	//### ABSTRACTE METHODEN
	 @Transient
    public Class getThiskeyEnumClass() {
	      return TileImmutabletext.getThiskeyEnumClassStatic();
	   }
	 
    /* GENERATED_END */
	
    //### Statische Methode (um einfacher darauf zugreifen zu können)
    public static Class getThiskeyEnumClassStatic(){
    	return EnumTileImmutabletext.class;
    }

	//#######################################################
	//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
	//### int Key, String shorttext, String longtext, String description
	//#######################################################
	public enum EnumTileImmutabletext implements IEnumSetTextTHM,  IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
		
   	 @IFieldDescription(description = "ARMY IMMUTABLETEXTVALUES") 
   	ARMY(10,"Army","Land army","A tile which cannot enter ocean fields. (immutable text)"),
   	TANKARMY(11,"Tank","Land  tank army","A heavy armored tile which cannot enter ocean fields. (immutable text)"),
   	
   	 @IFieldDescription(description = "FLEET IMMUTABLETEXTVALUES") 
   	FLEET(20,"Fleet","Ocean fleet", "A tile which cannot enter landarea fields. (immutable text)");
   	   	
   private Long lKey;
   private String sLongtext, sShorttext, sDescription;
   

   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   EnumTileImmutabletext(int iKey, String sShorttext, String sLongtext, String sDescription){
       this.lKey = Long.valueOf(iKey);
       this.sShorttext = sShorttext;
       this.sLongtext = sLongtext;
       this.sDescription = sDescription;
   }
   
   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
   EnumTileImmutabletext(){	
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
   public static EnumTileImmutabletext fromShorttext(String s) {
       for (EnumTileImmutabletext state : values()) {
           if (s.equals(state.getShorttext()))
               return state;
       }
       throw new IllegalArgumentException("Not a correct shorttext: " + s);
   }

   public EnumSet<?>getEnumSetUsed(){
   	return EnumTileImmutabletext.getEnumSet();
   }

   @SuppressWarnings("rawtypes")
   public static <E> EnumSet getEnumSet() {
   	
       //Merke: Das wird anders behandelt als FLAGZ Enumeration.
   	//String sFilterName = "FLAGZ"; /
   	//...
   	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
   	
   	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
   	Class<EnumTileImmutabletext> enumClass = EnumTileImmutabletext.class;
   	EnumSet<EnumTileImmutabletext> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
   	
   	for(Object obj : EnumTileImmutabletext.class.getEnumConstants()){
   		//System.out.println(obj + "; "+obj.getClass().getName());
   		set.add((EnumTileImmutabletext) obj);
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
