package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.util.datatype.string.StringZZZ;

@Entity
@DiscriminatorValue("land") //Wird es wg. der Vererbung(!) von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
                                      //Das muss in dem Root Entity, also in HEXCELL defniert werden. @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
public class AreaCellLand extends AreaCell{

	private static final long serialVersionUID = 1L;
	
	private int iPlayerOwner=0;
	
		//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public AreaCellLand(){
		super();
	}
	public AreaCellLand(CellId objId){
		super(objId, AreaCellType.LAND);
	}
	
	
	//### Getter / Setter
	@Column(name="OWNED_BY_PLAYER")
	public void setPlayerOwner(int iPlayerOwner){
		this.iPlayerOwner = iPlayerOwner;
	}	
    public int getPlayerOwner(){    	
    	return this.iPlayerOwner;
    }		    
}
