package use.thm.persistence.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.interfaces.IModelDateTimestampProviderZZZ;
import basic.zBasic.persistence.model.AbstractPersistentObjectTimestampedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

/**Klasse für einen Spielstein - persistierbar per JPA. Wird nach Troop vererbt. 
* Die Klasse TileTHM hat im Vergleich zu dieser Klassen noch weitere Aufgaben einer Swing - Komponente.
* Wegen nicht zu persistierender Eigenschaften wurde dann diese Klasse Tile speziell nur mit zu persistierenden Eigenschaften erstellt.
* @author lindhaueradmin
*
*/

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity  //Ich will eigentlich  keine Tabelle für TILE haben, geht aber nicht. 
@Access(AccessType.PROPERTY)

//VERERBUNG und STRATEGIEN:
//ZIEL: Anders als bei der SINGLE_TABLE Variante werden hier die sich unterscheidenden Spalten in eine eigenen Tabelle gepackt.
//      Der Vorteil ist, dass es keine NULL WERTE gibt. Allerdings hat man als Nachteil nicht alles im Blick.
//@Inheritance(strategy =  InheritanceType.JOINED )

//Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
//@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS)
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.

//Ziel: Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhand des Discriminator Wertes unterschieden
//DAS IST DER DEFAULT, wenn nur java-mäßig verebt wird.
//PROBLEM: NULL WERTE WENN SICH DIE SPALTEN DER ENTITIES UNTERSCHEIDEN. VORTEIL: Es sind definitv weniger Tabellen.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  
//Merke: Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@DiscriminatorColumn(name="Entityclass", discriminatorType = DiscriminatorType.STRING) 
//


//FGL: ALTE VERSION 20180209: @Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="TILE")
public class Tile  implements Serializable, IOptimisticLocking, IModelDateTimestampProviderZZZ {//Merke: Mit Hibernate pur ist das scheinbar schwierig, da man kein @Superclass nutzen kann .... extends AbstractPersistentObjectTimestampedZZZ{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	private int iMyTestSequence;

	private TileId id;
	
	private String sUniquedate; //wird mit einer @Formula zur Laufzeit berechnet. 
	
	//Aus dem IModelDateTimestampProvider. FGL: 20180220: Versuche dies in eine abstrakte Superklasse zu verschieben und so Vererbung zu nutzen sind mit pur Hibernate gescheitert.
	private Date dateCreatedThis;
	private String sDateCreatedThis;
	private Date dateUpdatedAt;
	
	private int iXstarted=-1;
	private int iYstarted=-1;
	
	//DAS PERSISTIERT ALS BLOB
	//@Enumerated(EnumType.STRING)	
	//	@Column(name="AREATYPE")
	//	@Type(type = "tryout.hibernate.AreaType", parameters = @Parameter(name = "type", value = "tryout.hibernate.AreaType"))
	// @Embedded  aber geht nicht, da ENUM-Type 
	@Transient
	private Enum<TileType> enumTileType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.

	
	
	//1:1 Beziehung aufbauen
	//Siehe Buch "Java Persistence API 2", Seite 90ff.	
	//Variante 1) mit einer gemeinsamen Spalte
	//@Transient //Ich will kein BLOB speichern
	//@JoinColumn(name="FIELD_ALIAS", nullable = false)
	
	//Variante 2) mit einer gemeinsamen Tabelle
	 //Speichert nur die ID ab. Das Abspeichern des Objekts wird mit @Transient über dem entsprechenden GETTER/SETTER verhindert
	//Siehe Buch Persistence with Hibernate (2016) (MeapV7 - S. 182)(Im späteren fertigen Buch allerdings woanders).
	 @Access(AccessType.FIELD)
	 @OneToOne(fetch = FetchType.LAZY)
	 @JoinTable(
			 name = "TILE_HEXCELL", //Required !
			 joinColumns = {@JoinColumn(name="id")},
			 inverseJoinColumns= {@JoinColumn(name="mapAlias", nullable = false, unique = true), @JoinColumn(name="mapX", nullable = false, unique = true), @JoinColumn(name="mapY", nullable = false, unique = true)} //private String sMapAlias = new String("TEST");  	private String sMapX = null; //X-KoordinatedId	private String sMapY = null; //Y-Koordinate
			 )
	private HexCell objHexCell;
	 
		 
	//########################################################################################################################
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public Tile(){
		 //bisher nur die eine Sorte des TileType vorhanden, darum nicht im Konstruktor als Parameter aufgenommen
		 this.enumTileType = TileType.TROOP;
	 }
	 public Tile(TileId objId){
		this.id = objId;
		 
		 //bisher nur die eine Sorte des TileType vorhanden, darum nicht im Konstruktor als Parameter aufgenommen
		 this.enumTileType = TileType.TROOP;
	 }
	 
		//Variante 1) Beispielsweise für das Spielbrett gewählte Variante...
		//Realisierung eines Zusammengesetzten Schlüssels
		//Siehe Buch "Java Persistence API 2", Seite 48ff.
		//@EmbeddedId
		
		//Merke: Attribut Access über FIELD.
//		@AttributeOverrides({
//				@AttributeOverride(name = "sMapAlias", column= @Column(name="MAPALIAS")),
//				@AttributeOverride(name = "sMapX", column= @Column(name="X", length = 2)),
//				@AttributeOverride(name = "sMapY", column= @Column(name="Y", length = 2))
//		})
		
		/*Meine Variante VOR dem Lösungsversuch mit dem generierten, eindeutigem Schlüssel.... Beide Varianten kann man nicht vereinen. 
		//Merke: Attribut Access über PROPERTY	
		@AttributeOverrides({
				@AttributeOverride(name = "mapAlias", column= @Column(name="MAPALIAS")),
				@AttributeOverride(name = "player", column= @Column(name="PLAYER", length = 2)),
				@AttributeOverride(name = "uniquename", column= @Column(name="UNIQUENAME", length = 6))	//Merke UNIQUE selbst nicht verwendbar, ist ein Schlüsselwort				
		})
		*/
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	 @Id				
	 @TableGenerator(name="lidGeneratorTile001", table="COMMON_FUER_IDGENERATOR_ASSOCIATION",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorTile001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
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
	 //Siehe Buch "Java Persistence API", Seite 37ff
	 @Transient
	 public String getTileAlias(){
		return this.getMapAlias() + "#" + this.getPlayer() + "-" + this.getUniquename(); 
	 }
	 
	 
	 //### getter / setter
	 //Version 02: Dh. mit Generiertem Key und ohne @EmbeddedId
	 public TileId getTileIdObject(){
		return this.id;
	}
	 public void setTileIdObject(TileId objId){
		 this.id = objId;
	 }
		
	@Transient
	public String getMapAlias(){
	   	return this.getTileIdObject().getMapAlias();
	}
	 public void setMapAlias(String sAlias){
		 this.getTileIdObject().setMapAlias(sAlias);
	 }
	 
		//Versuch mit MAX(X) darauf zuzugreifen aus der Methode fillMap(..)
		//ABER: Da das String ist, wird "9" als maximaler Wert zurückgeliefert und kein Integerwert.	
		@Access(AccessType.PROPERTY)
		@Column(name="XXstarted", nullable=false, columnDefinition="integer default 0")
	    public int getMapXstarted(){
			if(this.iXstarted<=0){
				if(this.getHexCell()!=null){
			    	String stemp = this.getHexCell().getId().getMapX();
			    	Integer objReturn = new Integer(stemp);
			    	return objReturn.intValue();
				}else{
					return -1;
				}
			}else{
				return this.iXstarted;
			}
	    }
		private void setMapXstarted(int iValue){
			this.iXstarted = iValue;
		}
	    
		@Access(AccessType.PROPERTY)
		@Column(name="YYstarted", nullable=false, columnDefinition="integer default 0")
	    public int getMapYstarted(){
			if(this.iYstarted<=0){
				if(this.getHexCell()!=null){
					String stemp =  this.getHexCell().getId().getMapY();
			    	Integer objReturn = new Integer(stemp);
			    	return objReturn.intValue();
				}else{
					return -1;
				}
			}else{
				return this.iYstarted;
			}
	    	//return objReturn;
	    }
		private void setMapYstarted(int iValue){
			this.iYstarted = iValue;
		}
		
		
		//Merke 20180215: Versuch das auf @Formula umzustellen. IST ABER GESCHEITERT, DA ZU KOMPLIZERT auf den zusammengesetzten Schlüssel des gespeicherten HEXCELL 
		//                Die Ausgangsversion ist nun in getMapXstarted()
		//                       
		//Versuch mit MAX(X) darauf zuzugreifen aus der Methode fillMap(..)
		//ABER: Da das String ist, wird "9" als maximaler Wert zurückgeliefert und kein Integerwert.	
		@Access(AccessType.PROPERTY)
		//@Formula("(select substr(UNIQUENAME,6,19) from TILE t where t.TILE_ID_INCREMENTED = 1)")
		//So sollte man theoretisch casten können.   @Formula("select CAST( as decimal)")
		@Column(name="XX", nullable=false, columnDefinition="integer default 0")
	    public int getMapX(){
			if(this.getHexCell()!=null){
		    	String stemp = this.getHexCell().getId().getMapX();
		    	Integer objReturn = new Integer(stemp);
		    	return objReturn.intValue();
			}else{
				return -1;
			}	    	
	    }
		private void setMapX(int iValue){
			Integer intValue = new Integer(iValue);
			String sX = intValue.toString();
			
			//TODO GOON 20170715: Müsste hier jetzt nicht die korrekte HexCell ermittelt werden? 
//						this.getHexCell().getId().setMapX(sX);
//					}
		}
		
		@Access(AccessType.PROPERTY)
		@Column(name="YY", nullable=false, columnDefinition="integer default 0")
	    public int getMapY(){
			if(this.getHexCell()!=null){
				String stemp =  this.getHexCell().getId().getMapY();
		    	Integer objReturn = new Integer(stemp);
		    	return objReturn.intValue();
			}else{
				return -1;
			}
	    	//return objReturn;
	    }
		private void setMapY(int iValue){
			Integer intValue = new Integer(iValue);
			String sY = intValue.toString();
			
			//TODO GOON 20170715: Müsste hier jetzt nicht die korrekte HexCell ermittelt werden? Das Setzen klappt so nämlich nicht...
//			if(this.getHexCell()!=null){
//				this.getHexCell().getId().setMapY(sY);
//			}
		}	    	    
	 
	 
	 //Siehe Buch "Java Persistence API", Seite 37ff
	 //@Transient
	 //Die Idee war diese neue Spalte als JoinColumn für 1:1 Beziehung zwischen HexCell und Tile - Objekten zu nutzen, das wäre Version 1 gewesen
    // Aber realisert wurde Version 2, mit einer JoinTable
	/*
	 @Column(name="FIELD_ALIAS")
	 public String getFieldAlias(){
		return this.getMapAlias() + "#" + this.getMapX() + "-" + this.getMapY(); 
	 }
	 public void setFieldAlias(String sAlias){
		 if(!StringZZZ.isEmpty(sAlias)){
			 String sMap = StringZZZ.left(sAlias, "#");
			 
			 String sX = StringZZZ.rightback("#" + sAlias, "#");
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
	 */
	 
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
		
		
		//ACHTUNG: Player ist auch in TileId, darum hier nicht persistieren, sonst bekommt man ambigious columns!!
		
		//Um zu versuchen alle Spielsteine eines Spielers zu bekommen.	
		//Um zu versuchen das Maximum zu bekommen und einfach um +1 zu erhöhen
//		@Access(AccessType.PROPERTY)
//		@Column(nullable=false, columnDefinition="integer default 0")
		
		@Transient
	    public int getPlayer(){
	    	String stemp= this.getTileIdObject().getPlayer();
	    	
	    	//Das ist notwendig, um mit Zahlenwerten zu arbeiten
	    	Integer objReturn = new Integer(stemp);
	    	return objReturn.intValue();
	    	
	    }		
		public void setPlayer(String sPlayer){
			//Das wäre notwendig, wenn der PrimaryKey ein Integer Wert wäre
			//Integer intValue = new Integer(sPlayer);
			//String sX = intValue.toString();
			if(!StringZZZ.isNumeric(sPlayer)){
				Integer intP = new Integer(sPlayer.hashCode());
				this.getTileIdObject().setPlayer(intP.toString());
			}else{
				this.getTileIdObject().setPlayer(sPlayer);
			}
		}		
		public void setPlayer(int iPlayer){
			Integer intValue = new Integer(iPlayer);
			String sPlayer = intValue.toString();
			this.getTileIdObject().setPlayer(sPlayer);
		}
	    
		//ACHTUNG: Player ist auch in TileId, darum hier nicht persistieren, sonst bekommt man ambigious columns!!
//		@Access(AccessType.PROPERTY)
//		@Column(name="UNIQUENAME", nullable=false, columnDefinition="string default 'xxx'")  //Merke UNIQUE ist ein Datenbankschlüsselwort
		@Transient
	    public String getUniquename(){	
			String stemp = this.getTileIdObject().getUniquename();
	    	return stemp; 		
	    }
		public void setUniquename(String sValue){
			this.getTileIdObject().setUniquename(sValue);
		}
		
		//1:1 Beziehung aufbauen über den FieldAliasName
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		@Access(AccessType.PROPERTY)
		@Transient //Ich will kein BLOB speichern
		public HexCell getHexCell(){
	    	return this.objHexCell;
	    }
		public void setHexCell(HexCell objHexCell){
			this.objHexCell = objHexCell;
		}
		
		//#### Berechnete Formel-Werte
		//Siehe Buch "Java Persistence with Hibernate (2016) Kapitel 5.1.3. (S.115 im E-Book reader)
		//1. Thisid der TileVariante hier aufnehmen und n:1 gemäß mit defaulttext verknüpfen
		//2. Hier mit einem Subselect die TileVariante holen und dann daraus den Defaulttext holen.
		//@Formula("(select Shorttext from TileDefaulttext dt where dt.Thiskey = 110)") //1. Die innere klammer ist wichtig. 2. Momnetan noch Fehler "no such table: TileDefaulttext"
		//@Formula("(select Shorttext from K_DEFAULTTEXT_TILE dt where dt.Thiskey = 110)") //SQL error or missing database (no such column: tile1_.Shorttext)
		//IDEE:  @Formula("select CAST( as decimal)") , klappt aber in den Unterschiedlichsten Varianten as Integer, etc. nicht...
		
		//FUNKTIONIERT: DA SICH DIE SQL FORMEL AUF DAS GLEICHE ENTITY BEZIEHT 
		@Formula("(select substr(UNIQUENAME,6,19) from TILE t where t.TILE_ID_INCREMENTED = 1)")
		
		//funktioniert nicht @Formula("( (CAST(UNIQUEDATE) AS INTEGER) || (select substr(UNIQUENAME,6,19) from TILE t where t.TILE_ID_INCREMENTED = 1) )")
		//FUNKTIONIERT: BEIDE WERTE WERDEN ZUSAMMENGEZOGEN, so gibt es: ARMY_ARMY_1518628141555 ...... @Formula("substr(uniquename,1,5) || (select uniquename from TILE dt where dt.TILE_ID_INCREMENTED = 1)")
		@Column(name="UNIQUEDATE", nullable=true, columnDefinition="STRING default '0'")  //Merke UNIQUE ist ein Datenbankschlüsselwort
	    public String getUniqueDate(){
	    	return this.sUniquedate;
	    }
		protected void setUniqueDate(String sUniquedate){
			this.sUniquedate = sUniquedate;
		}
	
		
		//Probier mal etwas zu CASTEN
		//IDEE: Das kann man ggfs. für die XX und YY Spalte verwenden, die sich ja aus SX und SY - den Stringwerten - errechnet.
		//
		//klappt aber alles nicht:
		//@Formula("(CAST(select substr(UNIQUENAME,6,19) from TILE t where t.TILE_ID_INCREMENTED = 1) as \"DECIMAL\")")		
		//@Formula("(CAST((select substr(UNIQUENAME,6,19) from TILE t where t.TILE_ID_INCREMENTED = 1) as \"DECIMAL\"))")
		//@Formula("(CAST((select substr(UNIQUENAME,6,19) from TILE t where t.TILE_ID_INCREMENTED = 1) as DECIMAL))")
		
//		@Formula("(CAST(UNIQUEDATE as DECIMAL))")			
//		@Column(name="UNIQUEDATE_LONG", nullable=true, columnDefinition="long default 0")  //Merke UNIQUE ist ein Datenbankschlüsselwort
//	    public Long getUniqueDateLong(){
//	    	return this.lngUniquedate;
//	    }
//		protected void setUniqueDateLong(Long lngUniquedate){
//			this.lngUniquedate = lngUniquedate;
//		}
		
		
//		//###### BERECHNETE DATUMSWERTE: Versuch CreatedAt automatisch zu erhalten
		//MERKE: Die Annotations werden dann aus dem Interface IModelDateTimestampProviderZZZ hier NICHT automatisch ergänzt		
		@Column(name="createdThisAt", insertable = true, updatable = false)
		@Type(type = DateMapping.USER_TYPE_NAME)
		public Date getCreatedThisAt(){
			return this.dateCreatedThis;
		}
		public void setCreatedThisAt(Date dateCreatedThis){
			this.dateCreatedThis = dateCreatedThis;
		}
				
		@Column(name="createdThisAtString", insertable = true, updatable = false)		
		@Type(type = DateMapping.USER_TYPE_NAME)  //FGL TOD GOON 20180305: Fehler Exception in thread "main" org.hibernate.HibernateException: Could not save '06-03-18:12:07:357' as it could not be casted to 'java.util.Date'.
		//Merke zur Fehleranalyse: Vorher wird aufgerufen: XXXXXX  FGL DateMapping.nullSaveSet(...) für die als Usertype angegebene DateMapping Klasse. Hier: In der Klasse selbstxxxxxxxxxxxxxx
		//                         Der Fehler tritt beim Speichern auf.
		public String getCreatedThisAtString(){
			return this.sDateCreatedThis;
		}
		public void setCreatedThisAtString(String sDateCreatedThis){
			this.sDateCreatedThis = sDateCreatedThis;
		}

				
		//##### BERECHNETE DATUMSWERTE. Versuch UpdatedAt automatisch zu erhalten		
		//MERKE: Die Annotations werden dann aus dem Interface IModelDateTimestampProviderZZZ NICHT automatisch hier ergänzt. Warum ?????
		@Version  //https://www.thoughts-on-java.org/hibernate-tips-use-timestamp-versioning-optimistic-locking/		//
		@Type(type = DateMapping.DATE_TYPE_TIMESTAMP_SQLITE_FGL) //Das ist "timestamp"		
		public Date getUpdatedAt(){
			return this.dateUpdatedAt;
		}
		protected void setUpdatedAt(Date dateUpdatedAt){
			this.dateUpdatedAt = dateUpdatedAt;
		}
}

