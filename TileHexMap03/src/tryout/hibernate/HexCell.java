package tryout.hibernate;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import use.thm.persistence.interfaces.IOptimisticLocking;

/**Klasse für eine HexEck Zelle - persistierbar per JPA. Wird von AreaCell geerbt. 
 * Die Klasse HexCellTHM hat darüber hinaus noch weitere Aufgaben deiner Swing - Komponente.
 * Wegen nicht zu persistierender Eigenschaften wurde dann diese speziell nur mit zu persistierenden Eigenschaften erstellt.
 * @author lindhaueradmin
 *
 */

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity
@Access(AccessType.PROPERTY)
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) 
@DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="HEXCELL")
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
	private Enum<HexType> enumHexType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.

	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public HexCell(){
	 }
	 public HexCell(CellId objId){
		 this.id = objId;
		 
		 //bisher nur die eine Sorte des HexType vorhanden, darum nicht im Konstruktor aufgenommen
		 this.enumHexType = HexType.AREA;
	 }
	 
	 //Siehe Buch "Java Persistence API", Seite 37ff
	 @Transient
	 public String getFieldAlias(){
		return this.getMapAlias() + "#" + this.getMapX() + "-" + this.getMapY(); 
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
		   	return this.getId().getMapAlias();
		}
	 
	 //Enumeration werden als BLOB gespeichert, insbesondere, wenn sie eine Liste sind, darum den Stringwert der Enumeration holen und das Objekt selbst nicht persisiteren.
		@Transient
		public Enum<HexType> getHexTypeObject(){
			return this.enumHexType;
		}	
		public void setHexTypeObject(Enum<HexType> enumHexType){
			this.enumHexType = enumHexType;	
		}
		
		//20170201: Versuche den Textwert in der Tabelle zu speichern, damit es kein BLOB ist. Das scheint nicht auszureichen 
		@Column(name="CELLTYPE")
		@Access(AccessType.PROPERTY)
		public String getHexType(){
			//das ist die Langbeschreibung return this.getAreaTypeObject().name();
			String sName = this.getHexTypeObject().name();	
			HexType at =HexType.valueOf(HexType.class, sName);
			return at.getAbbreviation();
		}	

		public void setHexType(String sHexType){
			HexType objType = HexType.fromAbbreviation(sHexType);
			this.setHexTypeObject(objType);
		}
		
			
		//Versuch mit MAX(X) darauf zuzugreifen aus der Methode fillMap(..)
		//ABER: Da das String ist, wird "9" als maximaler Wert zurückgeliefert und kein Integerwert.	
		@Access(AccessType.PROPERTY)
		@Column(name="XX", nullable=false, columnDefinition="integer default 0")
	    public int getMapX(){
	    	String stemp = this.getId().getMapX();
	    	Integer objReturn = new Integer(stemp);
	    	return objReturn.intValue();
	    	//return objReturn;
	    }
		public void setMapX(int iValue){
			Integer intValue = new Integer(iValue);
			String sX = intValue.toString();
			this.getId().setMapX(sX);
		}
	    
		@Access(AccessType.PROPERTY)
		@Column(name="YY", nullable=false, columnDefinition="integer default 0")
	    public int getMapY(){
			String stemp =  this.getId().getMapY();
	    	Integer objReturn = new Integer(stemp);
	    	return objReturn.intValue();
	    	//return objReturn;
	    }
		public void setMapY(int iValue){
			Integer intValue = new Integer(iValue);
			String sY = intValue.toString();
			this.getId().setMapY(sY);
		}
	    
}
