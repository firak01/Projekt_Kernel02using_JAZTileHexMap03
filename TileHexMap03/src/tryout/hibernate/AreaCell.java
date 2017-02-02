package tryout.hibernate;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Parameter;

@Entity
@DiscriminatorValue("area")
public class AreaCell extends HexCell implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	//s. Buch "Java Persistence API 2", Seite 41ff.
	//@Type verwendet, weil es eine Fehlermeldung gab, das der Typ nicht gefunden wird. 
	//Ergänzend zu der Buchlösung die Enumeration anders aufgebaut. So kommt man auch per JPQL an Objekte der Enumeration im Resultset.
	//Versuch dies nicht mehr als BLOB zu persistieren in SQLITe, sondern als String => embedded, was aber nicht geht
	
	//DAS PERSISTIERT ALS BLOB
	//@Enumerated(EnumType.STRING)	
	//	@Column(name="AREATYPE")
	//	@Type(type = "tryout.hibernate.AreaType", parameters = @Parameter(name = "type", value = "tryout.hibernate.AreaType"))
	// @Embedded  aber geht nicht, da ENUM-Type 
	@Transient
	private Enum<AreaType> enumAreaType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.
	
//	@Transient
//	private String sAreaType;
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public AreaCell(){
		super();
	}
	public AreaCell(CellId objId, Enum<AreaType> enumAreaType){
		super(objId);
		this.setAreaTypeObject(enumAreaType);
	}
	
	
	//### Getter / Setter
	//20170201: Dieser wird mit meiner SQLite Lösung als  BLOB gespeichert. Darum hier explicit nicht speichern, sondern den Textwert
	@Transient
	public Enum<AreaType> getAreaTypeObject(){
		return this.enumAreaType;
	}
	
	@Transient
	public void setAreaTypeObject(Enum<AreaType> enumAreaType){
		this.enumAreaType = enumAreaType;	
	}
	
	//20170201: Versuche den Textwert in der Tabelle zu speichern, damit es kein BLOB ist. Das scheint nicht auszureichen 
	@Column(name="AREATYPE")
	@Access(AccessType.PROPERTY)
	//@Enumerated(EnumType.STRING)
	public String getAreaType(){
		//das ist die Langbeschreibung return this.getAreaTypeObject().name();
		String sName = this.getAreaTypeObject().name();	
		AreaType at =  this.getAreaTypeObject().valueOf(AreaType.class, sName);
		return at.getAbbreviation();
	}	
//	public void setAreaTypeString(String sAreaType){
//		this.sAreaType = sAreaType;
//	}
	public void setAreaType(String sAreaType){
		AreaType objType = AreaType.fromAbbreviation(sAreaType);
		this.setAreaTypeObject(objType);
//		this.setAreaTypeString(sAreaType);
	}
	
	
}
