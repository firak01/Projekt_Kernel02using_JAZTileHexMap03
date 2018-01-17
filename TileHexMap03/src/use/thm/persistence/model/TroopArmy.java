package use.thm.persistence.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import basic.zBasic.ReflectCodeZZZ;
import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.listener.TroopArmyListener;

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

@Entity
@DiscriminatorValue("army") //Wird es wg. der Vererbung(!) von Tile zu Troop immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.  //Das muss in dem Root Entity, also in Tile defniert werden. @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING)
@Table(name="ARMY") 
//@EntityListeners(PersistListenerTHM.class)//Versuch JPA Callback/ListenerMethoden, aber hier funktionieren nur Hibernate EventListener
public class TroopArmy extends Troop{

	private static final long serialVersionUID = 1L;
		
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	public TroopArmy(){
		super();
	}
	public TroopArmy(TileId objId){
		super(objId, TroopType.ARMY);
	}
	
	
	//Versuch Callback - Methoden aufzurufen..... aber das Scheint nicht mit Hibernate zu gehen, sondern nur mti JPA & EntityManagager
	//@PrePersist
//	private void vorEinfuegen(){
//		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vor dem Einfügen. Das wäre eine ideale Stelle für Validierung....");
//	}
	
	//### Getter / Setter
	/* FGL TODO GOON 20171106: ERST MAL DIE TRROPARMYVARIANT Tabelle sauber hinbekommen
	//1:1 Beziehung aufbauen
		//Siehe Buch "Java Persistence API 2", Seite 90ff.	
		//Variante 1) mit einer gemeinsamen Spalte
		//@Transient //Ich will kein BLOB speichern
		@OneToOne(fetch = FetchType.LAZY)
		@JoinColumn(name="thiskey_id", nullable = true)
		private TroopArmyVariant objVariant;
		*/
		
		//Variante 2) mit einer gemeinsamen Tabelle
		 //Speichert nur die ID ab. Das Abspeichern des Objekts wird mit @Transient über dem entsprechenden GETTER/SETTER verhindert
		//Siehe Buch Persistence with Hibernate (2016) (MeapV7 - S. 182)(Im späteren fertigen Buch allerdings woanders).
//		 @Access(AccessType.FIELD)
//		 @OneToOne(fetch = FetchType.LAZY)
//		 @JoinTable(
//				 name = "TILE_HEXCELL", //Required !
//				 joinColumns = {@JoinColumn(name="id")},
//				 inverseJoinColumns= {@JoinColumn(name="mapAlias", nullable = false, unique = true), @JoinColumn(name="mapX", nullable = false, unique = true), @JoinColumn(name="mapY", nullable = false, unique = true)} //private String sMapAlias = new String("TEST");  	private String sMapX = null; //X-KoordinatedId	private String sMapY = null; //Y-Koordinate
//				 )
//		private TroopArmyVariant objVariant;
	
}
