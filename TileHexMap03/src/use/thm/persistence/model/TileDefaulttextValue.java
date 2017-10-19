package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;

@Entity
@Access(AccessType.FIELD) ///20171019: damit soll dann enum eingebunden werden, automatisch...
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
@Table(name="k_tile_defaulttext_value")
public class TileDefaulttextValue  implements Serializable, IOptimisticLocking{
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	@Id //hier notwendig für AccessType.FIELD
	private int iMyTestSequence;
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	 //@Id	//hier notwendig für AccessType.PROPERTY			
	 @TableGenerator(name="lidGeneratorDefaulttext001", table="COMMON_FUER_IDGENERATOR_ASSOCIATION",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorDefaulttext001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 //Entferne also das unique...
	 @Column(name="DEFAULTTEXT_ID_INCREMENTED", nullable=false)
	 public int getId(){
		 return this.iMyTestSequence;
	 }
	 public void setId(int iLid){
		 this.iMyTestSequence = iLid;
	 }
	
	
	public enum TileDefaulttextValueEnum implements IKeyEnum<Long>{		
		
        /**
         * Präsenzstudium 
         */
        @IFieldDescription(description = "Präsenzstudium") Paesenzstudium(1),

        /**
         * Fernstudium
         */
        @IFieldDescription(description = "Fernstudium") Fernstudium(2),
        
        /**
         * Praxissemester Ausland
         */
        @IFieldDescription(description = "Praxissemester Ausland") PraxissemesterImAusland(5);
		
        
        private Long objKeyLong;
        TileDefaulttextValueEnum(int key) {
	            this.objKeyLong = Long.valueOf(key);
	        }

	        @Override
	        public Long getKey() {
	            return this.objKeyLong;
	        }		
	        
	       

	}//end enum
}//end class
