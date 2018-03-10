package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import basic.persistence.model.IPrimaryKeys;

//Buch "Java Persistence API", S. 48
@Embeddable
@Access(AccessType.PROPERTY)
public class CellId implements IPrimaryKeys{ //IPrimaryKeys hat zwar @Embeddable aber das wird scheinbar nicht vererbt.

	/** Diese Klasse hat den Zweck eine ID zur Verfügung zu stellen.
	 *   @Id ist nicht erlaubt. Alle Properties bilden automatisch den Schlüssel.
	 */
	private static final long serialVersionUID = 1L;

    //TODO GOON 20180308: Diesen String Defaultwert nicht im Java-Code definieren, sondern in den Annotations. 
	private String sMapAlias = new String("TEST"); //Hiermit werden alle Felder einer Map zusammengehalten, momentan wird nur 1 Map in der Tabelle gespeichert	
	private String sMapX = null; //X-KoordinatedId
	private String sMapY = null; //Y-Koordinate
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public CellId(){
	}
	public CellId(String sMapAlias, String sMapX, String sMapY){
		this.sMapAlias = sMapAlias;
		this.sMapX = sMapX;
		this.sMapY = sMapY;		
	}
	
	
	
//	//##### BERECHNETE WERTE. Versuch diese automatisch zu erhalten
//	//Vgl. Buch "Java Persistence with Hibernate (2016)", Kapitel 5.1.5, (S. 117 in E-Book Readern), Listing 5.4.
//	//Variante 1:
//	//@Column(name="generatedStringTest", insertable = true, updatable = true)
//	//Das steht in meiner Hibernate Version nicht zur Verfügung @org.hibernate.annotations.ColumnDefault("Eins")
//	
//	//Also Variante 2: Das Klappt in einfachem Entity. Merke für Integer wäre der SQLITE spezifische Definitionsstring: "INTEGER DEFAULT 1"
//                           ABER:	Klappt nicht aber in diesem Eingebettetem Key.
//	@Column(name="mapAlias", insertable = true, updatable = true, columnDefinition="TEXT DEFAULT 'EINS")
//	@org.hibernate.annotations.Generated(
//			org.hibernate.annotations.GenerationTime.INSERT
//	)
	public String getMapAlias(){
		return this.sMapAlias;
	}
	public void setMapAlias(String sAlias){
		this.sMapAlias = sAlias;
	}

	public String getMapX(){
		return this.sMapX;
	}
	
	public void setMapX(String sX){
		this.sMapX = sX;
	}

	public String getMapY(){
		return this.sMapY;
	}
	public void setMapY(String sY){
		this.sMapY = sY;
	}
	
	@Override
	public int hashCode(){
		return this.sMapAlias.hashCode() + this.sMapX.hashCode() + this.sMapY.hashCode();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj == this){
			return true;
		}
		if(!(obj instanceof CellId)){
			return false;
		}
		
		CellId objId = (CellId)obj;
		if(!sMapAlias.equalsIgnoreCase(objId.getMapAlias())){
			return false;
		}
		if(!sMapX.equalsIgnoreCase(objId.getMapX())){
			return false;
		}
		if(!sMapY.equalsIgnoreCase(objId.getMapY())){
			return false;
		}
		
		return true;
		
	}
	 
	
}
