package basic.zBasic.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

import basic.persistence.model.IOptimisticLocking;

/*Mit Hibernate Pur habe ich das nicht hinbekommen.
 *FGL 20180220: DIESE  ABSTRACTE KLASSE WIRD ALSO NICHT GENUTZT */
@MappedSuperclass //JPA Style
//@Entity //Hibernate Pur Style
public abstract class AbstractPersistentObjectZZZ implements Serializable, IOptimisticLocking {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4884552117282420188L;
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	private int iMyTestSequence;
	
	//Variante 1) Beispielsweise für das Spielbrett gewählte Variante...
		//Realisierung eines Zusammengesetzten Schlüssels
		//Siehe Buch "Java Persistence API 2", Seite 48ff.
		//@EmbeddedId
		
		//Merke: Attribut Access über FIELD.
//		@AttributeOverrides({
//				@AttributeOverride(name = "sMapAlias", column= @Column(name="MAPALIAS")),
//				@AttributeOverride(name = "sMapX", column= @Column(name="X", length = 2)),
//				@AttributeOverride(name = "sMapY", column= @Column(name="Y", length = 2))
//		})
		
		/*Meine Variante VOR dem Lösungsversuch mit dem generierten, eindeutigem Schlüssel.... Beide Varianten kann man nicht vereinen. 
		//Merke: Attribut Access über PROPERTY	
		@AttributeOverrides({
				@AttributeOverride(name = "mapAlias", column= @Column(name="MAPALIAS")),
				@AttributeOverride(name = "player", column= @Column(name="PLAYER", length = 2)),
				@AttributeOverride(name = "uniquename", column= @Column(name="UNIQUENAME", length = 6))	//Merke UNIQUE selbst nicht verwendbar, ist ein Schlüsselwort				
		})
		*/
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	 @Id				
	 @TableGenerator(name="lidGeneratorTile001", table="COMMON_FUER_IDGENERATOR_ASSOCIATION",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorTile001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 //Entferne also das unique...
	 @Column(name="TILE_ID_INCREMENTED", nullable=false)
	 public int getId(){
		 return this.iMyTestSequence;
	 }
	 public void setId(int iLid){
		 this.iMyTestSequence = iLid;
	 }
}
