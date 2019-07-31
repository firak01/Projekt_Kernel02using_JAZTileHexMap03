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

import org.hibernate.annotations.NaturalId;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.enums.AbstractImmutable;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IThiskeyImmutableZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;


//Ich will kein eigenes Entity für Key haben
//@Entity  //Wenn man "Hibernate"-Vererbung nutzt, dann gibt es immer eine Tabelle der Entity Klasse 
//@Access(AccessType.PROPERTY)
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //InheritanceType.TABEL_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle // InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden 
//                                                                          //Bei InheritanceType.TABLE_PER_CLASS gilt, es darf keinen Discriminator geben ... @DiscriminatorColumn(name="Disc", discriminatorType = DiscriminatorType.STRING) //Bei InheritanceType.SINGLE_TABLE) gilt: Voraussetzung für DiscriminatorValue in der AreaCell-Klasse. //Wird es wg. der Vererbung von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.
//@Table(name="Key")
public class KeyImmutable extends AbstractImmutable<KeyImmutable> implements IThiskeyImmutableZZZ, Serializable { //, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;
	
	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	private int iMyTestSequence;
    private Long thiskeyId;
	private String sKeyType;
	
	
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public KeyImmutable(){
	 }
	 
//	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
//		 @Id				
//		 @TableGenerator(name="lidGeneratorKeyImmutable001", table="COMMON_FUER_IDGENERATORIMMUTABLE_KEY",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
//		 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorKeyImmutable001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
//		 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
//		 //Entferne also das unique...
//		 @Column(name="KEYIMMUTABLE_ID_INCREMENTED", nullable=false)
		 public int getId(){
			 return this.iMyTestSequence;
		 }
		 protected void setId(int iLid){
			 this.iMyTestSequence = iLid;
		 }
		 
		    /**
		     * @return hiskeyId
		     */		
//			//20180203: Für @ManyToOne... Hier soll dann die ThiskeyId in der anderen Tabelle gespeichert werden und nicht die normale, generierte ID.
//			@NaturalId
//			
////		    @Override
//		   //Merke: Wenn das dann im Entity eingesetzt werden soll, uniquewert festlegen
//		    //@Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")	
//		    public Long getThiskey() {
//		        return hiskeyId;
//		    }
	//
	//
//		    /**
//		     * @param newValue
//		     */
////		   @Override
//		    protected void setThiskey(Long newValue) {
//		        this.hiskeyId = newValue;
//		    }
		    
		    
			 //Merke: Bei Nutzung der "Hibernate"-Verebung käme dies aus der Key Klasse. ABER: Dann hätte ich auch eine Tabelle "KEY" und das will ich nicht.
			 //           Also: Hier die Methoden seperat anbieten.
//			@NaturalId //20180203: Für @ManyToOne... Hier soll dann die ThiskeyId in der anderen Tabelle gespeichert werden und nicht die normale, generierte ID.
//			@Column(name="thiskey_id",  nullable=false, unique=true, columnDefinition="LONG NOT NULL UNIQUE  DEFAULT 1")		 	
			public Long getThiskey() {
				 return this.thiskeyId;
			}
			protected void setThiskey(Long thiskeyId) {
				this.thiskeyId = thiskeyId;
			}
	 
	 //### getter / setter		
//		@Column(name="KEYTYPE")
//		@Access(AccessType.PROPERTY)
		public String getKeyType(){
			return this.sKeyType;
		}	
		protected void setKeyType(String sKeyType){
			this.sKeyType = sKeyType;
		}




	    private static final java.util.List<String> allAttributeNames = java.util.Arrays.asList(new String[]{"thiskey_id"});

	    /**
	     * {@inheritDoc}
	     */
//	   @Transient
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
			try{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Diese Methode muss in den daraus erbenden Klassen überschrieben werden.");
			}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
			}
			return null;
		}
	    
}
