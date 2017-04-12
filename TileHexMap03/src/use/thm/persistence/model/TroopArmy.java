package use.thm.persistence.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import basic.zBasic.ReflectCodeZZZ;
import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.listener.TroopArmyListener;

@Entity
@DiscriminatorValue("army") //Wird es wg. der Vererbung(!) von Tile zu Troop immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.  //Das muss in dem Root Entity, also in Tile defniert werden. @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
@Table(name="ARMY") 
//@EntityListeners(TroopArmyListener.class)//Versuch JPA Callback/ListenerMethoden.
@EntityListeners(PersistListenerTHM.class)//Versuch JPA Callback/ListenerMethoden.
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
	
	
	//Versuch Callback - Methoden aufzurufen..... aber das Scheint nicht mit Hibernate zu gehen, sondern nur mti JPA & EntityManagager
	@PrePersist
	private void vorEinfuegen(){
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vor dem Einfügen. Das wäre eine ideale Stelle für Validierung....");
	}
	
	//### Getter / Setter
	
	
	
}
