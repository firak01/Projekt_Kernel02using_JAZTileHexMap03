package tryout.hibernate;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

/**Klasse für eine HexEck Zelle - persistierbar per JPA. Wird von AreaCell geerbt. 
 * Die Klasse HexCellTHM hat darüber hinaus noch weitere Aufgaben deiner Swing - Komponente.
 * Wegen nicht zu persistierender Eigenschaften wurde dann diese speziell nur mit zu persistierenden Eigenschaften erstellt.
 * @author lindhaueradmin
 *
 */

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
@Table(name="HEXCELL")
public class HexCell implements Serializable{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Realisierung eines Zusammengesetzten Schlüssels
	//Siehe Buch "Java Persistence API 2", Seite 48ff.
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "sMapAlias", column= @Column(name="MAPALIAS")),
			@AttributeOverride(name = "sMapX", column= @Column(name="X", length = 2)),
			@AttributeOverride(name = "sMapY", column= @Column(name="Y", length = 2))
	})
	private CellId id;

	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public HexCell(){
	 }
	 public HexCell(CellId objId){
		 this.id = objId;
	 }
	 
	 //Siehe Buch "Java Persistence API", Seite 37ff
	 @Transient
	 public String getFieldAlias(){
		return this.getMapAlias() + "#" + this.getMapX() + "-" + this.getMapY(); 
	 }
	 
	 
	 //### getter / setter
		public CellId getId(){
			return this.id;
		}
		
		public String getMapAlias(){
		   	return this.getId().getMapAlias();
		}
		
		
		//Versuch mit MAX(X) darauf zuzugreifen aus der Methode fillMap(..)
		@Column(name="X")
	    public String getMapX(){
	    	return this.getId().getMapX();
	    }
	    
	    public String getMapY(){
	    	return this.getId().getMapY();
	    }
	    
}
