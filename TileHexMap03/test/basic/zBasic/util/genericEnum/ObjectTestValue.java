package basic.zBasic.util.genericEnum;

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

import base.datatype.KeyEnum;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.persistence.interfaces.enums.IThiskeyValueZZZ;

//Merke: Diese Klasse könnte auch als Entity in eine Tabelle für Schlüsselwortlisten verwendet werden.
//           Da das aber hier nicht das ziel ist, habe ich entsprechende Annotationen einfach auskkommentiert.
//           Vergleiche entsprechende Klassen im TileHexMap Projekt.

//Merke: Neue Entities immer in HibernateContextProviderSingletonTHM hinzufügen. In hibernate.cfg.xml reicht nicht.

//@Entity
//@Access(AccessType.PROPERTY) //20171019: damit soll dann enum eingebunden werden, automatisch...
//@Table(name="k_tile_defaulttext")
public class ObjectTestValue extends AbstractValue<ObjectTestValue> implements Serializable{ //, IOptimisticLocking{
	private static final long serialVersionUID = 1L;

	//Variante 2: Realisierung eines Schlüssel über eine eindeutige ID, die per Generator erzeugt wird
	//@Id //wäre hier notwendig für AccessType.FIELD
	private int iMyTestSequence;
	
	private String sTextDefault;
	
	
	//### Variante 2: Verwende auf dieser Ebene einen Generator, zum Erstellen einer ID
	 //@Id	//ist hier notwendig für AccessType.PROPERTY			
	// @TableGenerator(name="lidGeneratorDefaulttext002", table="COMMON_FUER_IDGENERATOR_DEFAULTTEXT002",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 //@GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorDefaulttext001")		 //Das Klappt mit Hibernate Session, aber nicht mit dem JPA EntityManager...
	 //Bei dieser Column Definition ist die Spalte nicht für @OneToMany mit @JoinTable zu gebrauchen @Column(name="TILE_ID_INCREMENTED", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 //Entferne also das unique...
	 //@Column(name="DEFAULTTEXT_ID_INCREMENTED", nullable=false)
	 public int getId(){
		 return this.iMyTestSequence;
	 }
	 public void setId(int iLid){
		 this.iMyTestSequence = iLid;
	 }
     
	//@Column(name="defaulttext", nullable=false)	 
	public String getDefaulttext(){
		return this.sTextDefault;
	}
	public void setDefaulttext(String s){
		this.sTextDefault = s;
	}
		
    private Long hiskeyId;

    /** */
    public enum EnumSetInnerTestTypeZZZ  implements IThiskeyProviderZZZ<Long> {

        /**
         * Erster Testwert
         */
        @IFieldDescription(description = "Erster Testwert") TESTVALUE_FIRST(1),

        /**
         * Zweiter Testwert
         */
        @IFieldDescription(description = "Zweiter Testwert") TESTVALUE_SECOND(2),

        /**
         * Dritter Testwert
         */
        @IFieldDescription(description = "Dritter Testwert") TESTVALUE_THIRD(3),

        /**
         * Vierter Testwert
         */
        @IFieldDescription(description = "ierter Testwert") TESTVALUE_FOURTH(4),

        /**
         * Fünfter Testwert
         */
        @IFieldDescription(description = "Fünfter Testwert") TESTVALUE_FITH(5);

        private Long key;

        EnumSetInnerTestTypeZZZ(int key) {
            this.key = Long.valueOf(key);
        }

       
        @Override
        public Long getThiskey() {
            return key;
        }
    }


   // @Override
    public Class<EnumSetInnerTestTypeZZZ> getThiskeyEnumClass() {
        return EnumSetInnerTestTypeZZZ.class;
    }
 

    /* GENERATED_BEGIN */

    /**
     * Autogenerated Default-Constructor
     * cut&paste out of 'GENERATED'-Block to modify
     */
    public ObjectTestValue() {
       // super();
    }

    /**
     * Autogenerated Constructor to use if an already generated objGuid is to be used (e.g. from an EntityDto)
     * @param objGuid
     */
//    public TileDefaulttext(... objGuid.toString()) {
//   //     this();
//    //    setObjGuid(objGuid.toString());
//    }

    /**
     * @return hiskeyId
     */
  //  @Override
    public Long getThiskey() {
        return hiskeyId;
    }


    /**
     * @param newValue
     */
  //  @Override
    public void setThiskey(Long newValue) {
        this.hiskeyId = newValue;
    }


    private static final java.util.List<String> allAttributeNames = java.util.Arrays.asList(new String[]{"hiskeyId"});

    /**
     * {@inheritDoc}
     */
  //  @java.lang.Override
    public java.util.List<String> getAllAttributeNames() {
        return getAllAttributeNamesIntern();
    }

    /**
     * {@inheritDoc}
     */
//    @java.lang.Override
    protected java.util.List<String> getAllAttributeNamesIntern() {
   //TODO GOON: das holen 20171024     java.util.List<String> tmp = super.getAllAttributeNamesIntern();
     //   tmp.addAll(allAttributeNames);
   //     return tmp;
    	
    	return null;
    }
    /* GENERATED_END */
	
	
	
	
		
}//end class
