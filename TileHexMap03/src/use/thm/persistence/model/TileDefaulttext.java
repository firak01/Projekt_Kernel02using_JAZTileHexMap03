package use.thm.persistence.model;

import java.io.Serializable;
import java.util.EnumSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import basic.persistence.model.IFieldDescription;
import basic.persistence.model.IKeyEnum;
import basic.persistence.model.IOptimisticLocking;
import basic.zBasic.persistence.interfaces.enums.AbstractValue;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.genericEnum.ObjectTestMappedValue.EnumSetInnerMappedTestTypeZZZ;

/**
 * Lösung für Schlüssselwerttabellen.
 * Hier wird eine innere Klasse verwendet, um ein enum anbieten zu können.
 * 
 * @author Fritz Lindhauer
 * @param <IEnumTileDefaulttext>
 *
 */

//Merke: Neue Entities immer auch in HibernateContextProviderSingletonTHM hinzufügen (in HibernateConfigurationProviderTHM.fillConfigurationMapping() ). In hibernate.cfg.xml reicht nicht.

@Entity //Merke: Ein eingenes Entity trotz der Verebung notwendig, Es ist auch eine eigene Tabelle notwendig, das Thiskey eindeutig sein soll. s. Vererbungsstrategie
            //           Der id Schlüssel wird aber über eine gemeinsame Tabelle erstellt.
//@Inheritance(strategy =  InheritanceType.JOINED )//ZIEL: Nur bestimmte Entiteis in einer eigenen Klasse //
// InheritanceType.SINGLE_TABLE) //Hiermit werden alle Datensätze der Vererbungshierarchieklassen in einer Tabelle zusammengafasst und nur anhan ddes Discriminator Wertes unterschieden
//@DiscriminatorValue("xxx")  //Wird es wg. der Vererbung(!) von HEXCell zu AreaType immer geben. Ohne Annotation ist das DTYPE und der wert ist gleich dem Klassennamen.

//Das braucht in der erbenden Klassenicht angegeben zu werden.
//@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
@Access(AccessType.PROPERTY) 
@Table(name="K_DEFAULTTEXT_TILE") //Eine eigene Tabelle notwendig, da Thiskey eindeutig sein soll.
public class TileDefaulttext<IEnumTileDefaulttext> extends Defaulttext  implements IOptimisticLocking{
		
	public TileDefaulttext(){
		super();
		this.setKeyType("DEFAULTTILETEXT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE? 
		//20180130: Besser eine Konstante hier. Merke: Diese Konstante wird dann in den Dao Klassen auch verwendet . Z.B. in TileDefaulttextDao.searchKey(...)
	}
	
	//### ABSTRACTE METHODEN
	 @Transient
    public Class getThiskeyEnumClass() {
	      return TileDefaulttext.getThiskeyEnumClassStatic();
	   }
	 
    /* GENERATED_END */
	
    //### Statische Methode (um einfacher darauf zugreifen zu können)
    public static Class getThiskeyEnumClassStatic(){
    	return EnumTileDefaulttext.class;
    }

	//#######################################################
	//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
	//### int Key, String shorttext, String longtext, String description
	//#######################################################
	public enum EnumTileDefaulttext implements IEnumSetTextTHM,  IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
		
   	 @IFieldDescription(description = "ARMY TYPE TEXTVALUES") 
   	ARMYINFANTRY01(110,"InfanteryArmy","Land infantery army","A slow unit."),
   	ARMYTANK01(120,"TankArmy","Land tank army","A motorized unit, very fast."),
   	
   	 @IFieldDescription(description = "FLEET TYPE TEXTVALUES") 
   	FLEETDESTROYER01(210,"Destroyer01","Fleet Destroyer 01", "A very fast Fleet Destroyer"),
   	FLEETDESTROYER02(220,"Destroyer02","Fleet Destroyer 02", "A slower Fleet Destroyer");
   	   	
   private Long lKey;
   private String sLongtext, sShorttext, sDescription;
   

   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   EnumTileDefaulttext(int iKey, String sShorttext, String sLongtext, String sDescription){
       this.lKey = Long.valueOf(iKey);
       this.sShorttext = sShorttext;
       this.sLongtext = sLongtext;
       this.sDescription = sDescription;
   }
   
   //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
   //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
   EnumTileDefaulttext(){	
   }

  //##################################################
  //#### Folgende Methoden bring Enumeration von Hause aus mit:

   public String getName(){
	   return super.name();
   }
   
   @Override
   public String toString() {
       return this.sShorttext;
   }

   public int getIndex() {
   	return ordinal();
   }

   //##################################################
   //#### Folgende Methoden holen die definierten Werte.
   // the identifierMethod ---> Going in DB
   public String getShorttext() {
       return this.sShorttext;
   }
   
   public String getLongtext(){
	   return this.sLongtext;
   }
   
   public String getDescription(){
	   return this.sDescription;
   }
   
   //#### Methode aus IKeyProviderZZZ
	public Long getThiskey() {
		return this.lKey;
	}

   //### Folgende Methoden sind zum komfortablen arbeiten gedacht.
   public int getPosition() {
   	return getIndex() + 1;
   }

	
	
   // the valueOfMethod <--- Translating from DB
   public static EnumTileDefaulttext fromShorttext(String s) {
       for (EnumTileDefaulttext state : values()) {
           if (s.equals(state.getShorttext()))
               return state;
       }
       throw new IllegalArgumentException("Not a correct shorttext: " + s);
   }

   public EnumSet<?>getEnumSetUsed(){
   	return EnumTileDefaulttext.getEnumSet();
   }

   @SuppressWarnings("rawtypes")
   public static <E> EnumSet getEnumSet() {
   	
       //Merke: Das wird anders behandelt als FLAGZ Enumeration.
   	//String sFilterName = "FLAGZ"; /
   	//...
   	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
   	
   	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
   	Class<EnumTileDefaulttext> enumClass = EnumTileDefaulttext.class;
   	EnumSet<EnumTileDefaulttext> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
   	
   	for(Object obj : EnumTileDefaulttext.class.getEnumConstants()){
   		//System.out.println(obj + "; "+obj.getClass().getName());
   		set.add((EnumTileDefaulttext) obj);
   	}
   	return set;
   	
   }

   //TODO: Mal ausprobieren was das bringt
   //Convert Enumeration to a Set/List
   private static <E extends Enum<E>>EnumSet<E> toEnumSet(Class<E> enumClass,long vector){
   	  EnumSet<E> set=EnumSet.noneOf(enumClass);
   	  long mask=1;
   	  for (  E e : enumClass.getEnumConstants()) {
   	    if ((mask & vector) == mask) {
   	      set.add(e);
   	    }
   	    mask<<=1;
   	  }
   	  return set;
   	}

 

   }//End inner class

		
}//end class
