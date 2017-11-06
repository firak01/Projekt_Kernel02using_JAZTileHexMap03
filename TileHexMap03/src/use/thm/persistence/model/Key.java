package use.thm.persistence.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

/**Klasse für einen Spielstein - persistierbar per JPA. Wird nach Troop vererbt. 
* Die Klasse TileTHM hat im Vergleich zu dieser Klassen noch weitere Aufgaben einer Swing - Komponente.
* Wegen nicht zu persistierender Eigenschaften wurde dann diese Klasse Tile speziell nur mit zu persistierenden Eigenschaften erstellt.
* @author lindhaueradmin
*
*/

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen. In hibernate.cfg.xml reicht nicht.

//Vgl. Buch "Java Persistence API 2", Seite 34ff. für @Table, @UniqueConstraint

//Ich will kein eigenes Entity für Key haben
//@Entity  //Wenn man "Hibernate"-Vererbung nutzt, dann gibt es immer eine Tabelle der Entity Klasse 
//@Access(AccessType.PROPERTY)
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//                                                                          //Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
//@Table(name="Key")
public class Key extends AbstractValue<Key> implements IThiskeyValueZZZ, Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
//	private int iMyTestSequence;
    private Long hiskeyId;
	private String sKeyType;
	
	
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public Key(){
	 }
	 
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
//		 @Id				
//		 @TableGenerator(name="lidGeneratorKey001", table="COMMON_FUER_IDGENERATOR_KEY",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
//		 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorKey001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
//		 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
//		 //Entferne also das unique...
//		 @Column(name="KEY_ID_INCREMENTED", nullable=false)
//		 public int getId(){
//			 return this.iMyTestSequence;
//		 }
//		 public void setId(int iLid){
//			 this.iMyTestSequence = iLid;
//		 }
	 
	 //### getter / setter		
//		@Column(name="KEYTYPE")
//		@Access(AccessType.PROPERTY)
		public String getKeyType(){
			return this.sKeyType;
		}	
		public void setKeyType(String sKeyType){
			this.sKeyType = sKeyType;
		}

	    /**
	     * @return hiskeyId
	     */
	    @Override
	   //Merke: Wenn das dann im Entity eingesetzt werden soll, uniquewert festlegen
	    //@Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
	    public Long getThiskey() {
	        return hiskeyId;
	    }


	    /**
	     * @param newValue
	     */
	   @Override
	    public void setThiskey(Long newValue) {
	        this.hiskeyId = newValue;
	    }


	    private static final java.util.List<String> allAttributeNames = java.util.Arrays.asList(new String[]{"hiskeyId"});

	    /**
	     * {@inheritDoc}
	     */
//	    @Transient
//	   @java.lang.Override
	    public java.util.List<String> getAllAttributeNames() {
	        return getAllAttributeNamesIntern();
	    }

	    /**
	     * {@inheritDoc}
	     */
	    @Transient
	    @java.lang.Override
	    protected java.util.List<String> getAllAttributeNamesIntern() {
	    	//Merke: 20171024 super bezieht sich auf abstractValue. Darin wird ebenfalls super verewendet, das sich auf java.lang bezieht.    
	    	java.util.List<String> tmp = super.getAllAttributeNamesIntern();
	        tmp.addAll(allAttributeNames);
	        return tmp;
	    }
	    /* GENERATED_END */

//	    @Transient
//		@Override
		public Class getThiskeyEnumClass() {
			// TODO Auto-generated method stub
			return null;
		}
	    
}
