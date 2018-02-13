package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;
//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.
@Entity
@DiscriminatorValue("ozean") //Wird es wg. der Vererbung(!) von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
                                      //Das muss in dem Root Entity, also in HEXCELL defniert werden. @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
@Table(name="AREACELLOCEAN")
public class AreaCellOcean extends AreaCell{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public AreaCellOcean(){
		super();
	}
	public AreaCellOcean(CellId objId){
		super(objId, AreaCellType.OCEAN);
	}
	
	
	//### Getter / Setter

	
}
