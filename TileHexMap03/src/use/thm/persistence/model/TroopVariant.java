package use.thm.persistence.model;

import java.io.Serializable;
import java.util.EnumSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
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
import use.thm.persistence.interfaces.ITroopVariantTHM;
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

/**Klasse für die Werte der TroopVarianten - SO NICHT persistierbar.
 * Erst die Kindklassen sind per JPA Persistierbar. 
 * 
* @author lindhaueradmin
*/

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity  //Vererbung: Falls es Vererbung gibt, kann man die Elternklasse wohl nicht vermeiden. DAS WILL ICH ABER NICHT
@Access(AccessType.PROPERTY)
@org.hibernate.annotations.Immutable //Ziel: Performancesteigerung. Siehe Buch "Java Persistance with Hibernate", S. 107. Dafür dürfen die POJOs aber keine public Setter-Methoden haben.

//VERERBUNG und STRATEGIEN:
//ZIEL: Nur bestimmte Entities in einer eigenen Klasse 
//@Inheritance(strategy =  InheritanceType.JOINED )

//Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.

//Ziel: Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhand des Discriminator Wertes unterschieden
//DAS IST DER DEFAULT, wenn nur java-mäßig verebt wird.
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
//Merke: Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
//@DiscriminatorColumn(name="Entityclass", discriminatorType = DiscriminatorType.STRING) 
//
@Table(name="troopvariant")
public class TroopVariant  extends KeyImmutable implements ITroopVariantTHM, ICategoryProviderZZZ, Serializable, IOptimisticLocking{
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
	 public TroopVariant(){		
		 super();		
		 this.setKeyType("TROOPVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
	 }
	 	 
	 //Konstruktor, an den alles übergeben wird. Wg. "Immutable" gibt es keine 'public' Setter.
	 public TroopVariant(int iKey, String sUniquetext, String sCategorytext, int intMapMoveRange, String sImageUrl, TileDefaulttext objDefaulttext, TileImmutabletext objImmutabletext){
		 super();		
		 this.setKeyType("TROOPVARIANT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
		 
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
		 @TableGenerator(name="lidGeneratorTroopVariant001", table="COMMON_FUER_IDGENERATOR_TROOPVARIANT",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
		 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorTroopVariant001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
		 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
		 //Entferne also das unique...
		 @Column(name="TROOPVARIANT_ID_INCREMENTED", nullable=false)
		 public int getId(){
			 return this.iMyTestSequence;
		 }
		 public void setId(int iLid){
			 this.iMyTestSequence = iLid;
		 }
	 
	 //### getter / setter
		 //TODO: Dies in eine Oberklasse für alle "Varianten" verschieben.
		 @Column(name="KEYTYPE")
			@Access(AccessType.PROPERTY)
			public String getKeyType(){
				return super.getKeyType();
			}	
			public void setKeyType(String sKeyType){
				super.setKeyType(sKeyType);
			}
					
		 //### Aus ICategoryProviderZZZ
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
		 
		 //### Aus ITroopArmyVariant
	//TODO GOON: Diese Properties dann in einen Oberklasse für alle Spielsteine bringen TileVariant
	//1:1 Beziehung aufbauen
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		//Variante 1) mit einer gemeinsamen Spalte
		//@Transient //Ich will kein BLOB speichern
		@OneToOne(fetch = FetchType.LAZY)
		//@JoinColumn(name="defaulttext_thiskey_id", referencedColumnName = "thiskey_id") //Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		//Erst hierdurch wird die thiskey_id in der Spalte gespeichert. ABER: Fehlermeldung, weil der Wert ggfs. nicht unique sei. Allerdings sind logischerweise mehrere Objekte, die sich auf den gleichen Text beziehen erlaubt.
		//Ohne die columnDefinition funktioniert das bei der SQLite Datenbank nicht.
		@JoinColumn(name="defaulttext_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1")
	 public Defaulttext getDefaulttextObject(){
		return this.objDefaulttext;
	 }
	 
		//Ist protected wg. immutable
	 protected void setDefaulttextObject(TileDefaulttext objDefaulttext){
		 this.objDefaulttext = objDefaulttext;
	 }
	 
	 
	 //### getter / setter
	 	//### Aus ITroopArmyVariant
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
	 @Column(name="TILE_IMAGEURL", nullable=false)
	 public String getImageUrlString(){
		 return this.sImageUrl;
	 }
	 protected void setImageUrlString(String sImageUrl){
		 this.sImageUrl = sImageUrl;			 
	 }
	 
	 //#### abstracte Methoden
	 //20171106 KÄME DAS ETWA DRUCH VERERBUNG AUS DER KLASSE "KEY" ???
	 //JAAA!!!	 ABER DANN GIBT ES EINE EIGENE TABELLE "KEY" und das will ich nicht.
	 //Merke: Wenn man diese Definition nicht in den hieraus erbenden Klassen explzit überschreibt, gibt es beim Refernzieren der Spalte eine Fehlermeldung
	 //Exception in thread "main" org.hibernate.AnnotationException: referencedColumnNames(thiskey_id) of use.thm.persistence.model.TroopArmy.troopArmyVariantObject referencing use.thm.persistence.model.TroopArmyVariant not mapped to a single property
	 //Lösung: die Spalte explizit in die hierauserbende Klasse aufnehmen.
	 //Nachdem dadurch der oben genannte Fehler behoben ist, gibt es die Fehlermeldung, dass insert="false" update="false" notwendig ist. Also: , insertable=false, updatable=false, 
	 //in der oben neu hinzugefügten Spalte.
	 @JoinColumn(name="trooparmyvariant_thiskey_id", referencedColumnName = "thiskey_id", nullable = true, unique= false,  columnDefinition="LONG NOT NULL DEFAULT 1")	 
	 @Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
	 @Override
	public Long getThiskey() {
		 return this.lKey;
	}
	@Override
	public void setThiskey(Long thiskeyId) {
		this.lKey = thiskeyId;
	}
}
