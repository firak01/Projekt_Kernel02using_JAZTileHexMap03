package tryout.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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
	@Enumerated(EnumType.STRING)	
	@Column(name="AREATYPE")
	@Type(type = "tryout.hibernate.AreaType", parameters = @Parameter(name = "type", value = "tryout.hibernate.AreaType"))
	private Enum<AreaType> enumAreaType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public AreaCell(){
		super();
	}
	public AreaCell(CellId objId, Enum<AreaType> enumAreaType){
		super(objId);
		this.setAreaType(enumAreaType);
	}
	
	
	//### Getter / Setter
//Weil der EnumType nun String ist
//	public int getAreaType(){
//		return this.iAreaType;
//	}
//	public void setAreaType(int iAreaType){
//		this.iAreaType = iAreaType;
//	}
	
	
	public Enum<AreaType> getAreaType(){
		return this.enumAreaType;
	}
	public void setAreaType(Enum<AreaType> enumAreaType){
		this.enumAreaType = enumAreaType;
	}
}
