package use.thm.persistence.model;

import java.io.Serializable;
import java.util.EnumSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import use.thm.persistence.interfaces.ITroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
import use.thm.persistence.model.Immutabletext.EnumImmutabletext;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.enums.ICategoryProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

/**Klasse für die Werte - persistierbar per JPA. Wird von Troop verwendet. 
*1:1 Beziehung in TroopArmy
*
* @author lindhaueradmin
 * @param <IEnumTileDefaulttext>
*
*/

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity  //Vererbung: Falls es Vererbung gibt, kann man die Elternklasse wohl nicht vermeiden. DAS WILL ICH ABER NICHT
@Access(AccessType.PROPERTY)
@org.hibernate.annotations.Immutable //Ziel: Performancesteigerung. Siehe Buch "Java Persistance with Hibernate", S. 107. Dafür dürfen die POJOs aber keine public Setter-Methoden haben.

//VERERBUNG...
//ZIEL: Nur bestimmte Entities in einer eigenen Klasse 
//@Inheritance(strategy =  InheritanceType.JOINED )

//Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
//@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.

//Ziel: Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhand des Discriminator Wertes unterschieden
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
//Merke: @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
//
@Table(name="ARMYVARIANT")
public class TroopArmyVariant  extends TroopVariant implements ITroopArmyVariantTHM{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	private int iMyTestSequence;
		
	//Variante 1) Beispielsweise für das Spielbrett gewählte Variante...
	//Realisierung eines Zusammengesetzten Schlüssels
	//Siehe Buch "Java Persistence API 2", Seite 48ff.
	//@EmbeddedId
	
	//Merke: Attribut Access über FIELD.
//	@AttributeOverrides({
//			@AttributeOverride(name = "sMapAlias", column= @Column(name="MAPALIAS")),
//			@AttributeOverride(name = "sMapX", column= @Column(name="X", length = 2)),
//			@AttributeOverride(name = "sMapY", column= @Column(name="Y", length = 2))
//	})
	
	/*Meine Variante VOR dem Lösungsversuch mit dem generierten, eindeutigem Schlüssel.... Beide Varainten kann man nicht vereinen. 
	//Merke: Attribut Access über PROPERTY	
	@AttributeOverrides({
			@AttributeOverride(name = "mapAlias", column= @Column(name="MAPALIAS")),
			@AttributeOverride(name = "player", column= @Column(name="PLAYER", length = 2)),
			@AttributeOverride(name = "uniquename", column= @Column(name="UNIQUENAME", length = 6))	//Merke UNIQUE selbst nicht verwendbar, ist ein Schlüsselwort				
	})
	*/
	
	//für die "thiskey_id"
	private Long lKey;
		
	//... und weitere Eigenschaften.
	private Integer intDegreeOfCoverMax;
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public TroopArmyVariant(){		
		 super();		
		 this.setKeyType("TROOPARMYVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
	 }
	 	 
	 //Konstruktor, an den alles übergeben wird. Wg. "Immutable" gibt es keine 'public' Setter.
	 public TroopArmyVariant(int iKey, String sUniquetext, String sCategorytext, int iMapMoveRange, float fHealthInitial, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext, int iDegreeOfCover){
		 super(iKey, sUniquetext, sCategorytext, iMapMoveRange, fHealthInitial, sImageUrl, objDefaulttext, objImmutabletext);		
		 this.setKeyType("TROOPARMYVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
		 
		 this.setDegreeOfCoverMax(Integer.valueOf(iDegreeOfCover));		 
	 }
	
	  //#### Methode aus IKeyProviderZZZ
	 //#### abstracte Methoden. Trotzdem hier implementiert wg. Fehler:
	 //Exception in thread "main" org.hibernate.AnnotationException: referencedColumnNames(thiskey_id) of use.thm.persistence.model.TroopArmy.troopArmyVariantObject referencing use.thm.persistence.model.TroopArmyVariant not mapped to a single property
	 //Nachdem dadurch der Fehler behoben ist, gibt es die Fehlermeldung, dass insert="false" update="false" notwendig ist. Also: , insertable=false, updatable=false, 
	 @Column(name="thiskey_id",  nullable=false, unique=true, insertable=false, updatable=false, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 0")	
	 @Override
	public Long getThiskey() {
		 return this.lKey;
	}
	@Override
	public void setThiskey(Long thiskeyId) {
		this.lKey = thiskeyId;
	}
	 
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
		//ABER NICHT AUF DIESER EBENEN, DA SIE ERBT VON KEY.java
	   //ABER: BEIM ERBEN VON KEY wira automatisch eine Tabelle Key erstellt.... das will ich nicht
		
	 
//		 @Id				
//		 @TableGenerator(name="lidGeneratorTroopArmyVariant001", table="COMMON_FUER_IDGENERATOR_TROPPARMYVARIANT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
//		 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorTroopArmyVariant001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
//		 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
//		 //Entferne also das unique...
//		 @Column(name="TILE_ID_INCREMENTED", nullable=false)
//		 public int getId(){
//			 return this.iMyTestSequence;
//		 }
//		 public void setId(int iLid){
//			 this.iMyTestSequence = iLid;
//		 }
	 
	 //### getter / setter
	 @Column(name="ARMY_COVER_MAX", nullable=true)//muss nullable erlauben, wenn alle Varianten (z.B. auch von FLEET) in der gleichen Tabelle gespeichert würeden, aufgrund der Vererbungsstrategie.
	 public Integer getDegreeOfCoverMax(){
		 return this.intDegreeOfCoverMax;
	 }
	 protected void setDegreeOfCoverMax(Integer intDegreeOfCoverMax){
		 this.intDegreeOfCoverMax = intDegreeOfCoverMax;				 
	 }
	 
	 
	 @Transient
	 public Class getThiskeyEnumClass() {
		 return TroopArmyVariant.getThiskeyEnumClassStatic();
	}
		 
		
	//### Statische Methode (um einfacher darauf zugreifen zu können). Muss überschrieben werden aus der Key(Immutable)... Klasse.
	public static Class getThiskeyEnumClassStatic(){	    
	    return EnumTroopArmyVariant.class;    	
	}

		//#######################################################
		//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
		//### int Key, String shorttext, String longtext, String description
		//#######################################################
		public enum EnumTroopArmyVariant implements IEnumSetTroopArmyVariantTHM,  ICategoryProviderZZZ, IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
			//Werte für alle Spielsteine:
			//lKey / sUniquetext / sCategorytext (Merke: "Land Unit" wird als hart verdrahteter Wert  im Konstruktor von TroopVariant verwendet, zur Steuerung der Bildverarbeitung)
			                                                       //TODO GOON 20180703: Hier soll kein String mehr rein (z.B. 'Infantry Unit', sondern die ThisKey-Id einer entsprechenden CategoryText Tabelle.
			//			/ iMoveRange / fHealthInitial /sImageUrl / 
			//iThisKeyDefaulttext / iThiskeyImmutabletext (Der Shorttext hiervon wird als Default, abgekürzt in der HexMap angezeigt)
			//
			//Speziell für TROOP; 
			//iDegreeOfCoverMax
	   	 @IFieldDescription(description = "DARMYVARIANT01") 
	   	T01(11,"DARMY01","Infantry Unit",2,0.5f,"ARMY\\US - M1 Garand Rifle.png",110,10,90),
	   	
	   	 @IFieldDescription(description = "DARMYVARIANT02") 
	   	T02(12,"DTANKARMY02","Tank Unit",3,0.75f,"ARMY\\US - M4A1 Sherman.png",120,11,20);
	   	   	
	   private Long lKey;
	   private String sUniquetext, sCategorytext;
	   private int iThiskeyDefaulttext, iThiskeyImmutabletext;
	   
	   private String sImageUrl;
	   private int iMoveRange;
	   private float fHealthInitial;
	   
	   //Spezielle für TROOP
	   private int iDegreeOfCoverMax;
	   
	   

	   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
	   EnumTroopArmyVariant(int iKey, String sUniquetext, String sCategorytext, int iMoveRange, float fHealthInitial, String sImageUrl, int iThisKeyDefaulttext, int iThiskeyImmutabletext, int iDegreeOfCoverMax){
	       this.lKey = Long.valueOf(iKey);
	       this.sUniquetext = sUniquetext;
	       this.sCategorytext = sCategorytext;
	       this.iMoveRange = iMoveRange;
	       
	       this.fHealthInitial = fHealthInitial;
	       
	       this.sImageUrl = sImageUrl;
	       this.iThiskeyDefaulttext = iThisKeyDefaulttext;
	       this.iThiskeyImmutabletext = iThiskeyImmutabletext;	
	       
	       this.iDegreeOfCoverMax = iDegreeOfCoverMax;
	   }
	   
	   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
	   //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
	   EnumTroopArmyVariant(){	
	   }

	  //##################################################
	  //#### Folgende Methoden bring Enumeration von Hause aus mit:

	   public String getName(){
		   return super.name();
	   }
	   
	   @Override
	   public String toString() {
	       return this.sUniquetext;
	   }

	   public int getIndex() {
	   	return ordinal();
	   }

	   //##################################################
	   //#### Folgende Methoden holen die definierten Werte.
		public String getUniquetext() {			
			return this.sUniquetext;
		}
				
	   public int getMapMoveRange() {
			return this.iMoveRange;
		}
	   
	   public float getHealthInitial(){
		   return this.fHealthInitial;
	   }

		public String getImageUrlString() {
			return this.sImageUrl;
		}

		public int getDefaulttextThisid() {
			return this.iThiskeyDefaulttext;
		}

		public int getImmutabletextThisid() {
			return this.iThiskeyImmutabletext;
		}
		
		//### Speziell für TROOP
		public int getDegreeOfCoverMax() {
			return this.iDegreeOfCoverMax;
		}
		
		//#### Methode aus ICategoryProviderZZZ
		public String getCategorytext() {			
			return this.sCategorytext;
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
	   public static EnumTroopArmyVariant fromUniquetext(String s) {
	       for (EnumTroopArmyVariant state : values()) {
	           if (s.equals(state.getUniquetext()))
	               return state;
	       }
	       throw new IllegalArgumentException("Not a correct uniquetext: " + s);
	   }

	   public EnumSet<?>getEnumSetUsed(){
	   	return EnumTroopArmyVariant.getEnumSet();
	   }

	   @SuppressWarnings("rawtypes")
	   public static <E> EnumSet getEnumSet() {
	   	
	       //Merke: Das wird anders behandelt als FLAGZ Enumeration.
	   	//String sFilterName = "FLAGZ"; /
	   	//...
	   	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
	   	
	   	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
	   	Class<EnumTroopArmyVariant> enumClass = EnumTroopArmyVariant.class;
	   	EnumSet<EnumTroopArmyVariant> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
	   	
	   	for(Object obj : EnumTroopArmyVariant.class.getEnumConstants()){
	   		//System.out.println(obj + "; "+obj.getClass().getName());
	   		set.add((EnumTroopArmyVariant) obj);
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

		@Override
		@Transient
		public String getSubtype() {
			return "AR";
		}
		protected void setSubtype(){			
		}
}
