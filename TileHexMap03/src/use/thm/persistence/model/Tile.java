package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;

/**Klasse für einen Spielstein - persistierbar per JPA. Wird nach Troop vererbt. 
* Die Klasse TileTHM hat im Vergleich zu dieser Klassen noch weitere Aufgaben einer Swing - Komponente.
* Wegen nicht zu persistierender Eigenschaften wurde dann diese Klasse Tile speziell nur mit zu persistierenden Eigenschaften erstellt.
* @author lindhaueradmin
*
*/

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity  //Ich will eigentlich  keine Tabelle für TILE haben, geht aber nicht. 
@Access(AccessType.PROPERTY)
@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="TILE")
public class Tile implements Serializable, IOptimisticLocking{
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
			@AttributeOverride(name = "player", column= @Column(name="PLAYER", length = 2)),
			@AttributeOverride(name = "uniquename", column= @Column(name="UNIQUENAME", length = 6))	//Merke UNIQUE ist ein Schlüsselwort		
	})
	private TileId id;
	
	//DAS PERSISTIERT ALS BLOB
	//@Enumerated(EnumType.STRING)	
	//	@Column(name="AREATYPE")
	//	@Type(type = "tryout.hibernate.AreaType", parameters = @Parameter(name = "type", value = "tryout.hibernate.AreaType"))
	// @Embedded  aber geht nicht, da ENUM-Type 
	@Transient
	private Enum<TileType> enumTileType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.

	
	//1:1 Beziehung aufbauen
	//Siehe Buch "Java Persistence API 2", Seite 90ff.	
	@OneToOne
	@JoinColumn(name="lid", nullable = false)
	@Transient //Ich will kein BLOB speichern
	private HexCell objHexCell;
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public Tile(){
	 }
	 public Tile(TileId objId){
		 this.id = objId;
		 
		 //bisher nur die eine Sorte des TileType vorhanden, darum nicht im Konstruktor aufgenommen
		 this.enumTileType = TileType.TROOP;
	 }
	 
	 //Siehe Buch "Java Persistence API", Seite 37ff
	 @Transient
	 public String getTileAlias(){
		return this.getMapAlias() + "#" + this.getPlayer() + "-" + this.getUniquename(); 
	 }
	 
	 
	 //### getter / setter
	 @EmbeddedId
	 public TileId getId(){
		return this.id;
	}
	 public void setId(TileId objId){
		 this.id = objId;
	 }
		
	 @Transient
		public String getMapAlias(){
		   	return this.getId().getMapAlias();
		}
	 
	 //Enumeration werden als BLOB gespeichert, insbesondere, wenn sie eine Liste sind, darum den Stringwert der Enumeration holen und das Objekt selbst nicht persisiteren.
		@Transient
		public Enum<TileType> getTileTypeObject(){
			return this.enumTileType;
		}	
		public void setTileTypeObject(Enum<TileType> enumTileType){
			this.enumTileType = enumTileType;	
		}
		
		//20170201: Versuche den Textwert in der Tabelle zu speichern, damit es kein BLOB ist. Das scheint nicht auszureichen 
		@Column(name="TILETYPE")
		@Access(AccessType.PROPERTY)
		public String getTileType(){
			//das wäre die Langbeschreibung return this.getTileTypeObject().name();
			String sName = this.getTileTypeObject().name();	
			TileType at =TileType.valueOf(TileType.class, sName);
			return at.getAbbreviation();
		}	

		public void setTileType(String sTileType){
			TileType objType = TileType.fromAbbreviation(sTileType);
			this.setTileTypeObject(objType);
		}
		
			
		//Um zu versuchen alle Spielsteine eines Spielers zu bekommen.	
		//Um zu versuchen das Maximum zu bekommen und einfach um +1 zu erhöhen
		@Access(AccessType.PROPERTY)
		@Column(name="PLAYER", nullable=false, columnDefinition="integer default 0")
	    public int getPlayer(){
	    	String stemp= this.getId().getPlayer();
	    	
	    	//Das ist notwendig, um mit Zahlenwerten zu arbeiten
	    	Integer objReturn = new Integer(stemp);
	    	return objReturn.intValue();
	    	
	    }
		public void setPlayer(String sPlayer){
			//Das wäre notwendig, wenn der PrimaryKey ein Integer Wert wäre
			//Integer intValue = new Integer(iValue);
			//String sX = intValue.toString();
			this.getId().setPlayer(sPlayer);
		}
	    
		//Um zu versuchen das Maximum zu bekommen und einfach um +1 zu erhöhen
		@Access(AccessType.PROPERTY)
		@Column(name="UNIQUENAME", nullable=false, columnDefinition="integer default 0")  //Merke UNIQUE ist ein Datenbankschlüsselwort
	    public int getUniquename(){	
			String stemp = this.getId().getUniquename();
	    	Integer objReturn = new Integer(stemp);
	    	return objReturn.intValue();		
	    }
		public void setUniquename(int iValue){
			Integer intValue = new Integer(iValue);
			String sUniquename = intValue.toString();
			this.getId().setUniquename(sUniquename);
		}
		
		//1:1 Beziehung aufbauen
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		@Access(AccessType.PROPERTY)
		@OneToOne
		@JoinColumn(name="LID", nullable = false)
		@Transient //Ich will kein BLOB speichern
		public void setHexCell(HexCell objHexCell){
			this.objHexCell = objHexCell;
		}
	    public HexCell getHexCell(){
	    	return this.objHexCell;
	    }
}
