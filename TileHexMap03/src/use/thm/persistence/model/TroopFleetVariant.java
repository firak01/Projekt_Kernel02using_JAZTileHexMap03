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

import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
import use.thm.persistence.model.Immutabletext.EnumImmutabletext;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.ReflectCodeZZZ;
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

//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//                                                                                                                   //Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="FLEETVARIANT")
public class TroopFleetVariant  extends KeyImmutable implements Serializable, IOptimisticLocking{
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
	
	//Jetzt die verschiedenene Eigenschaften eines Armeetypens hier festlegen.
	private TileDefaulttext objDefaulttext;
	private TileImmutabletext objImmutabletext;
	private String sUniquetext;
	private String sCategorytext;
	private Integer intMapMoveRange;
	private String sImageUrl;
	
	//... und weitere Eigenschaften.
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public TroopFleetVariant(){		
	 }
	 	 
	 //TODO 200180119: Konstruktor, an den alles übergeben wird. Wg. "Immutable".
	 public TroopFleetVariant(int iKey, String sUniquetext, String sCategorytext, int intMapMoveRange, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
		 this.setThiskey(Long.valueOf(iKey));
		 this.setUniquetext(sUniquetext);
		 this.setCategorytext(sCategorytext);
		 this.setMapMoveRange(Integer.valueOf(intMapMoveRange));
		 this.setDefaulttextObject(objDefaulttext);
		 this.setImmutabletextObject(objImmutabletext);
		 this.setImageUrlString(sImageUrl);
	 }
	 
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
		//ABER NICHT AUF DIESER EBENEN, DA SIE ERBT VON KEY.java
	   //ABER: BEIM ERBEN VON KEY wira automatisch eine Tabelle Key erstellt.... das will ich nicht
		
	 
		 @Id				
		 @TableGenerator(name="lidGeneratorFleetVariant001", table="COMMON_FUER_IDGENERATOR_VARIANT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
		 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorFleetVariant001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
		 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
		 //Entferne also das unique...
		 @Column(name="TILE_ID_INCREMENTED", nullable=false)
		 public int getId(){
			 return this.iMyTestSequence;
		 }
		 public void setId(int iLid){
			 this.iMyTestSequence = iLid;
		 }
	 
	 //### getter / setter
		 //TODO: Dies in eine Oberklasse für alle "Varianten" verschieben. 
		 @Column(name="TILE_UNIQUETEXT", nullable=false)
		 public String getUniquetext(){
			 return this.sUniquetext;
		 }
		 protected void setUniquetext(String sUniquetext){
			 this.sUniquetext = sUniquetext;
		 }
		 
		 @Column(name="TILE_CATEGORYTEXT", nullable=false)
		 public String getCategorytext(){
			 return this.sCategorytext;
		 }
		 protected void setCategorytext(String sCategorytext){
			 this.sCategorytext= sCategorytext;
		 }
		 
	//TODO GOON: Diese Properties dann in einen Oberklasse für alle Spielsteine bringen TileVariant
	//1:1 Beziehung aufbauen
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		//Variante 1) mit einer gemeinsamen Spalte
		//@Transient //Ich will kein BLOB speichern
		 
		 //20180204: Nein, das ist eine 1:1 Beziehung
//		@OneToOne(fetch = FetchType.LAZY)
//		@JoinColumn(name="defaulttext_thiskey_id", nullable = true)//Hiermit wird die ID Spalte gespeichert
		
		 @ManyToOne(fetch = FetchType.LAZY)
		 //@JoinColumn(name="defaulttext_thiskey_id", referencedColumnName = "thiskey_id") //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		//Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		//Ohne die columnDefinition funktioniert das bei der SQLite Datenbank nicht.
		 @JoinColumn(name="defaulttext_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1") 
	 public Defaulttext getDefaulttextObject(){
		return this.objDefaulttext;
	 }
	 
	 //Ist protected wg. immutable. Zwar ist der DefaultText an sich ängerbar (anders als der immutableText) aber die Fleet Variante ist immutable.
	 protected void setDefaulttextObject(TileDefaulttext objDefaulttext){
		 this.objDefaulttext = objDefaulttext;
	 }
	 
	 
	 //### getter / setter
		//1:1 Beziehung aufbauen
			//Siehe Buch "Java Persistence API 2", Seite 90ff.	
			//Variante 1) mit einer gemeinsamen Spalte
			//@Transient //Ich will kein BLOB speichern
		 
		 //20180203: NEIN: Das ist eine n:1 Beziehung
			//@OneToOne(fetch = FetchType.LAZY)
			//@JoinColumn(name="immutabletext_thiskey_id", nullable = true) //Hiermit wird die ID in der Spalte gespeichert
		 
		 @ManyToOne(fetch = FetchType.LAZY)
		 //@JoinColumn(name="immutabletext_thiskey_id", referencedColumnName = "thiskey_id") //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		 //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
  		 //Ohne die columnDefinition funktioniert das bei der SQLite Datenbank nicht.
		 @JoinColumn(name="immutabletext_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1") 
		 public TileImmutabletext getImmutabletextObject(){
			return this.objImmutabletext;
		 }
	 
	 //Ist protected wg. immutable
	 protected void setImmutabletextObject(TileImmutabletext objImmutabletext){
		 this.objImmutabletext = objImmutabletext;
	 }
	 
	 //TODO GOON: Diese Reichweite dann in einen Oberklasse für alle Spielsteine bringen
	 @Column(name="TILE_MAPMOVE_RANGE", nullable=false)
	 public Integer getMapMoveRange(){
		 return this.intMapMoveRange;
	 }
	 protected void setMapMoveRange(Integer intMapMoveRange){
		 this.intMapMoveRange = intMapMoveRange;				 
	 }
	 
	 //TODO GOON: Diese Bildressource dann in eine Oberklasse für alle Spielsteine bringen
	 //Merke: Das sollte NULL sein dürfen.
	 @Column(name="TILE_IMAGEURL", nullable=true)
	 public String getImageUrlString(){
		 return this.sImageUrl;
	 }
	 protected void setImageUrlString(String sImageUrl){
		 this.sImageUrl = sImageUrl;			 
	 }
	 
	 //#### abstracte Methoden
	 //20171106 KÄME DAS ETWA DURCH VERERBUNG AUS DER KLASSE "KEY" ???
	 //JAAA!!!	 ABER DANN GIBT ES EINE EIGENE TABELLE "KEY" und das will ich nicht, darum hier extra definieren.
	 @Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
	 @Override
	public Long getThiskey() {
		 return this.lKey;
	}
	@Override
	public void setThiskey(Long thiskeyId) {
		this.lKey = thiskeyId;
	}
	
	 @Transient
	 public Class getThiskeyEnumClass() {
		 return Immutabletext.getThiskeyEnumClassStatic();
	}
		 
		
	//### Statische Methode (um einfacher darauf zugreifen zu können). Muss überschrieben werden aus der Key(Immutable)... Klasse.
	public static Class getThiskeyEnumClassStatic(){	    
	    return EnumTroopFleetVariant.class;    	
	}

		//#######################################################
		//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
		//### int Key, String shorttext, String longtext, String description
		//#######################################################
		public enum EnumTroopFleetVariant implements IEnumSetTroopFleetVariantTHM,  IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
			
		//lKey / sUniquetext / sCategorytext / iMoveRange / sImageUrl / iThisKeyDefaulttext / iThiskeyImmutabletext;
	   	 @IFieldDescription(description = "DFLEETVARIANT01") 
	   	T01(1,"DFLEET01","Destroyer",5,"url01",210,20),
	   	
	   	 @IFieldDescription(description = "DFLEETVARIANT02") 
	   	T02(2,"DFLEET02","Destroyer",3,"url02",220,20);
	   	   	
	   private Long lKey;
	   private String sUniquetext, sCategorytext;
	   private int iThiskeyDefaulttext, iThiskeyImmutabletext;
	   
	   private String sImageUrl;
	   private int iMoveRange;
	   
	   

	   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
	   EnumTroopFleetVariant(int iKey, String sUniquetext,String sCategorytext, int iMoveRage, String ImageUrl, int iThisKeyDefaulttext, int iThiskeyImmutabletext){
	       this.lKey = Long.valueOf(iKey);
	       this.sUniquetext = sUniquetext;
	       this.sCategorytext = sCategorytext;
	       this.iMoveRange = iMoveRange;
	       this.sImageUrl = sImageUrl;
	       this.iThiskeyDefaulttext = iThisKeyDefaulttext;
	       this.iThiskeyImmutabletext = iThiskeyImmutabletext;	       
	   }
	   
	   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
	   //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
	   EnumTroopFleetVariant(){	
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

		public String getCategorytext() {			
			return this.sCategorytext;
		}
		
	   public int getMapMoveRange() {
			return this.iMoveRange;
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
	      
	   //#### Methode aus IKeyProviderZZZ
		public Long getThiskey() {
			return this.lKey;
		}

	   //### Folgende Methoden sind zum komfortablen arbeiten gedacht.
	   public int getPosition() {
	   	return getIndex() + 1;
	   }
				
	   // the valueOfMethod <--- Translating from DB
	   public static EnumTroopFleetVariant fromUniquetext(String s) {
	       for (EnumTroopFleetVariant state : values()) {
	           if (s.equals(state.getUniquetext()))
	               return state;
	       }
	       throw new IllegalArgumentException("Not a correct shorttext: " + s);
	   }

	   public EnumSet<?>getEnumSetUsed(){
	   	return EnumTroopFleetVariant.getEnumSet();
	   }

	   @SuppressWarnings("rawtypes")
	   public static <E> EnumSet getEnumSet() {
	   	
	       //Merke: Das wird anders behandelt als FLAGZ Enumeration.
	   	//String sFilterName = "FLAGZ"; /
	   	//...
	   	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
	   	
	   	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
	   	Class<EnumTroopFleetVariant> enumClass = EnumTroopFleetVariant.class;
	   	EnumSet<EnumTroopFleetVariant> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
	   	
	   	for(Object obj : EnumTroopFleetVariant.class.getEnumConstants()){
	   		//System.out.println(obj + "; "+obj.getClass().getName());
	   		set.add((EnumTroopFleetVariant) obj);
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
}
