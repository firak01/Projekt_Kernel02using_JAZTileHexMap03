package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;

@Entity
@DiscriminatorValue("army") //Wird es wg. der Vererbung(!) von Tile zu Troop immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
                                      //Das muss in dem Root Entity, also in Tile defniert werden. @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
public class TroopArmy extends Troop{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public TroopArmy(){
		super();
	}
	public TroopArmy(TileId objId){
		super(objId, TroopType.ARMY);
	}
	
	
	//### Getter / Setter
	
	
}
