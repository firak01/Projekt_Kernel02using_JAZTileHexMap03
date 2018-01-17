package use.thm.persistence.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.util.datatype.string.StringZZZ;

/**Klasse für eine HexEck Zelle - persistierbar per JPA. Wird nach AreaCell vererbt. 
 * Die Klasse HexCellTHM hat im Vergleich zu dieser Klassen noch weitere Aufgaben einer Swing - Komponente.
 * Wegen nicht zu persistierender Eigenschaften wurde dann diese Klasse HexCell speziell nur mit zu persistierenden Eigenschaften erstellt.
 * @author lindhaueradmin
 *
 */
//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.
//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity
@Access(AccessType.PROPERTY)
@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden
@DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="HEXCELL")
//@EntityListeners(SaveOrUpdateListenerTHM.class)//Versuch JPA Callback/ListenerMethoden, aber hier funktionieren nur Hibernate EventListener
public class HexCell implements Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Realisierung eines Zusammengesetzten Schlüssels
	//Siehe Buch "Java Persistence API 2", Seite 48ff.
	//@EmbeddedId
	
	//Merke: Attribut Access über FIELD.
//	@AttributeOverrides({
//			@AttributeOverride(name = "sMapAlias", column= @Column(name="MAPALIAS")),
//			@AttributeOverride(name = "sMapX", column= @Column(name="X", length = 2)),
//			@AttributeOverride(name = "sMapY", column= @Column(name="Y", length = 2))
//	})
	//Merke: Attribut Access über PROPERTY.
	@AttributeOverrides({
			@AttributeOverride(name = "mapAlias", column= @Column(name="MAPALIAS")),
			@AttributeOverride(name = "mapX", column= @Column(name="SX", length = 2)),
			@AttributeOverride(name = "mapY", column= @Column(name="SY", length = 2))
	})
	private CellId id;
	
	//DAS PERSISTIERT ALS BLOB
	//@Enumerated(EnumType.STRING)	
	//	@Column(name="AREATYPE")
	//	@Type(type = "tryout.hibernate.AreaType", parameters = @Parameter(name = "type", value = "tryout.hibernate.AreaType"))
	// @Embedded  aber geht nicht, da ENUM-Type 
	@Transient
	private Enum<HexCellType> enumHexType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.

	
	//1:n Beziehung von HexCell zu Tile, damit soll man schneller abfragen können welche "Tiles" in einer HexCell stehen
	 @Access(AccessType.FIELD)
	 @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	 @JoinTable(
			 name = "HEXCELL_TILE", //Required !
			 //joinColumns = {@JoinColumn(name="mapAlias", nullable = false, unique=false), @JoinColumn(name="mapX", nullable = false, unique=false), @JoinColumn(name="mapY", nullable = false, unique = false)}, //private String sMapAlias = new String("TEST");  	private String sMapX = null; //X-KoordinatedId	private String sMapY = null; //Y-Koordinate
			 joinColumns = {@JoinColumn(name="MAPALIAS", nullable=false,unique=false), @JoinColumn(name="SX", nullable=false,unique=false), @JoinColumn(name="SY", nullable=false,unique = false)}, //private String sMapAlias = new String("TEST");  	private String sMapX = null; //X-KoordinatedId	private String sMapY = null; //Y-Koordinate
			 inverseJoinColumns= {@JoinColumn(name="id",nullable = false, unique = true)}
			 )
	 //Aus dem Buch "PersistenceWith Hibernate (2016, second, S. 143)
//	 A java.util.Collection property, initialized with a java.util.ArrayList.
//	 This collection has bag semantics; duplicates are possible, but the order of elements
//	 isn’t preserved. All JPA providers support this type.
	 //<> ist aber est ab 1.7 erlaubt .   private Collection<Tile> objbagTile=new ArrayList<>(); //null;//private Set<Tile> objsetTile=new HashSet<Tile>(); //Performance: Bag statt Set und erzeuge nur ein Objekt wenn nötig
	 private Collection<Tile> objbagTile=null;//new ArrayList<Tile>(); //null;//private Set<Tile> objsetTile=new HashSet<Tile>(); //Performance: Bag statt Set und erzeuge nur ein Objekt wenn nötig
	 
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public HexCell(){
	 }
	 public HexCell(CellId objId){
		 this.id = objId;
		 
		 //bisher nur die eine Sorte des HexType vorhanden, darum nicht im Konstruktor aufgenommen
		 this.enumHexType = HexCellType.AREA;
	 }
	 
	 //Siehe Buch "Java Persistence API", Seite 37ff
	 //@Transient
	 //TODO 20170322
	 //Versuch diese neue Spalte als JoinColumn für 1:1 Beziehung zwischen HexCell und Tile - Objekten zu nutzen
	 @Column(name="FIELD_ALIAS")
	 public String getFieldAlias(){
		return this.getMapAlias() + "#" + this.getMapX() + "-" + this.getMapY(); 
	 }
	 public void setFieldAlias(String sAlias){
		 if(!StringZZZ.isEmpty(sAlias)){
			 String sMap = StringZZZ.left(sAlias, "#");
			 
			 String sX = StringZZZ.right("#" + sAlias, "#");
			 sX = StringZZZ.left(sX, "-");			 
			 Integer intX = new Integer(sX);
			 
			 String sY = StringZZZ.rightback("#" + sAlias, "#");
			 sY = StringZZZ.right(sY,  "-");
			 Integer intY = new Integer(sY);
			 
			 this.setMapAlias(sMap);
			 this.setMapX(intX.intValue());
			 this.setMapY(intY.intValue());			 			 
		 }		 
	 }
	 
	 
	 //### getter / setter
	 @EmbeddedId
	 public CellId getId(){
		return this.id;
	}
	 public void setId(CellId objId){
		 this.id = objId;
	 }
		
	 @Transient
		public String getMapAlias(){
		 if(this.getId()!=null){
		   	return this.getId().getMapAlias();
		 }else{
			 return null;
		 }
		}	 
	 public void setMapAlias(String sAlias){
		 if(this.getId()!=null){
			 this.getId().setMapAlias(sAlias);
		 }
	 }
	 
		//Versuch mit MAX(X) darauf zuzugreifen aus der Methode fillMap(..)
		//ABER: Da das String in CellId ist, würde "9" als maximaler Wert zurückgeliefert und kein Integerwert.
	    //      Daher hier direkt (ohne "Umweg über CellId) auf einen Integer Wert zugreifen.
	    //      HQL: Aus DebugJpaQueryHexCellMain  "SELECT MAX(c.mapX) FROM HexCell c" statt "SELECT MAX(c.id.mapX) FROM HexCell c"
		@Access(AccessType.PROPERTY)
		@Column(name="XX", nullable=false, columnDefinition="integer default 0")
	    public int getMapX(){
			 if(this.getId()!=null){
		    	String stemp = this.getId().getMapX();
		    	Integer objReturn = new Integer(stemp);
		    	return objReturn.intValue();
			 }else{
				 return -1;
			 }
	    	//return objReturn;
	    }
		public void setMapX(int iValue){
			Integer intValue = new Integer(iValue);
			String sX = intValue.toString();
			 if(this.getId()!=null){
				 this.getId().setMapX(sX);
			 }
		}
	    
		@Access(AccessType.PROPERTY)
		@Column(name="YY", nullable=false, columnDefinition="integer default 0")
	    public int getMapY(){
			 if(this.getId()!=null){
				String stemp =  this.getId().getMapY();
		    	Integer objReturn = new Integer(stemp);
		    	return objReturn.intValue();
			 }else{
				 return -1;
			 }
	    	//return objReturn;
	    }
		public void setMapY(int iValue){
			Integer intValue = new Integer(iValue);
			String sY = intValue.toString();
			if(this.getId()!=null){
				this.getId().setMapY(sY);
			}
		}
	 
	//Merke:
	// @GeneratedValue(strategy= GenerationType.AUTO) //Klappt leider nicht
	//@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq") //Vorausgesetzt man hat einen SequenceGenerator
	 //usw...
	 //
	//201703: Klappt so nicht, darum folgende Strategie
	//1. Versuche einen eigenen Generator zu erstellen als eigene Klasse
	 //        Wird nicht funktionieren, weil @GeneratedValue angibt, dass die Datenbank die Erzeugung vornimmt.
	 //        Da dies SQLite nicht kann (nur Postgres kann es angeblich) wird es nichts bringen.
	 //        Der Generator "unterstützt" die Datenbank wohl nur bei der "wertfindung".
	 //        Bei mit Id annotierten Spalten klappt es.
	 //
	//2. Nimm als Verbindungswert einen zusammengesetzten AliasString der Schlüsse.
	//3. Probiere eine andere Datenbank als SQLite aus... Postgres?
	 //
	
	 /* +++++++++++++++++++++Zur Doku ++++++++++++++++++++++++++++++++++++
	  
	    @Entity - marks this class as a JPA persistable entity
	    @Table - denotes the name of the table in which this entity is stored
	    @Id - declares the field it refers to as the unique identifier for this entity
	    @TableGenerator - informs JPA how to generate unique values for this entity's identifier. It has several parameters:
	        name - identifier for the generator binding. This value must match the parameter in the @GeneratedValue annotation as described below.
	        table - must match the name of the table created to store the sequence values.
	        pkColumnName - the primary key column name that contains the name of the sequence we are using.
	        valueColumnName - the name of the column that contains the numeric sequence value
	        pkColumnValue - the value of the primary key column that identifies the sequence
	        allocationSize - the amount by which this sequence should be incremented each time a new entity is created. The default value for this is fifty (50).
	    @GeneratedValue - marks the field as having a generated value, either from the database or from some other ID generation strategy. This has two important parameters:
	        strategy - a value from the GenerationType enumeration that declares the the way in which values will be generated. In this example GenerationType.TABLE is appropriate since we are letting the value be managed in the relational store.
	        generator- must match the name of the @TableGenerator tag to provide the specifics on how the value is to be generated.
	    @Column - declares the field to be mapped to a database column.

		  */
	 
	 /* Grosser Fehlschlag... @TableGenerator funktioniert wohl nur mit @Id ... Da hier der Wert nicht verändert wird, gibt es eine constraint - Verletzung aufgrund des UNIQUE */
	 /* Zum Testen, siehe SequenceTester und DebugJpaSequenceTestMain001 - Hier funktioniert es mit der @Id Annotation */
	 /* 
	 @TableGenerator(name="lidGenerator", table="COMMON_LID",pkColumnName="lid_hexcell", pkColumnValue="HexCell",valueColumnName="XYZ",  initialValue=1, allocationSize=1)
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGenerator")
	 @Column(name="TEST", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1") 
	 public int getTest(){
		 return this.iMyTestSequence;
	 }
	 public void setTest(int iLid){
		 this.iMyTestSequence = iLid;
	 }
	 */
	 
	 //Enumeration werden als BLOB gespeichert, insbesondere, wenn sie eine Liste sind, darum den Stringwert der Enumeration holen und das Objekt selbst nicht persisiteren.
		@Transient
		public Enum<HexCellType> getHexTypeObject(){
			return this.enumHexType;
		}	
		public void setHexTypeObject(Enum<HexCellType> enumHexType){
			this.enumHexType = enumHexType;	
		}
		
		//20170201: Versuche den Textwert in der Tabelle zu speichern, damit es kein BLOB ist. Das scheint nicht auszureichen 
		@Column(name="CELLTYPE")
		@Access(AccessType.PROPERTY)
		public String getHexType(){
			//das wäre die Langbeschreibung return this.getAreaTypeObject().name();
			String sName = this.getHexTypeObject().name();	
			HexCellType at =HexCellType.valueOf(HexCellType.class, sName);
			return at.getAbbreviation();
		}	

		public void setHexType(String sHexType){
			HexCellType objType = HexCellType.fromAbbreviation(sHexType);
			this.setHexTypeObject(objType);
		}
		
		 @Transient //Ich will nur den Schlüssel abspeichern, mit der JOINColumn - Lösung
		public Collection<Tile> getTileBag(){
			 if(this.objbagTile==null){
				 this.objbagTile = new ArrayList<Tile>();
			 }
			 return this.objbagTile;		 
		 }
		 public void setTileBag(Collection<Tile> objbagTile){
			 if(this.objbagTile==null){
				 this.objbagTile = objbagTile;
			 }
			 
		 }	
	
	    
}
