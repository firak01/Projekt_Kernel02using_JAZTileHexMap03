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
@DiscriminatorValue("troop") //Wird es wg. der Vererbung(!) von Tile zu Troop immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
                                      //Das muss in dem Root Entity, also in Tile defniert werden. @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
public class Troop extends Tile implements Serializable, IOptimisticLocking{


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
	private Enum<TroopType> enumTroopType = null; //weil der EnumType nun String ist. @Column wird verwendet, da sonst der technische Name enumAreaType als Tabellenspalte herhalten muss.
	
//	@Transient
//	private String sAreaType;
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public Troop(){
		super();
	}
	public Troop(TileId objId, Enum<TroopType> enumTroopType){
		super(objId);
		this.setTroopTypeObject(enumTroopType);
	}
	
	
	//### Getter / Setter
	//20170201: Dieser wird mit meiner SQLite Lösung als  BLOB gespeichert. Darum hier explicit nicht speichern, sondern den Textwert
	@Transient
	public Enum<TroopType> getTroopTypeObject(){
		return this.enumTroopType;
	}
	
	@Transient
	public void setTroopTypeObject(Enum<TroopType> enumTroopType){
		this.enumTroopType = enumTroopType;	
	}
	
	//Versuche den Textwert in der Tabelle zu speichern, damit es kein BLOB ist, wie bei einem Enumeration-Objekt. 
	@Column(name="SUBTYPE")
	@Access(AccessType.PROPERTY)
	public String getTroopType(){
		//das ist die Langbeschreibung return this.getAreaTypeObject().name();
		String sName = this.getTroopTypeObject().name();	
		TroopType at =  this.getTroopTypeObject().valueOf(TroopType.class, sName);
		return at.getAbbreviation();
	}	
	public void setTroopType(String sTroopType){
		TroopType objType = TroopType.fromAbbreviation(sTroopType);
		this.setTroopTypeObject(objType);
	}
	
	
}
