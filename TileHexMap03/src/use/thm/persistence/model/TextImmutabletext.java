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
import use.thm.persistence.model.Defaulttext.EnumDefaulttext;
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

//Das braucht in der erbenden Klasse nicht angegeben zu werden:
//@Inheritance(strategy =  InheritanceType.TABLE_PER_CLASS) //Ziel: Jedes Entity der Vererbungshierarchie in einer eigenen Tabelle 
@Access(AccessType.PROPERTY) 
@Table(name="K_IMMUTABLETEXT_TEXT") //Eine eigene Tabelle notwendig, da Thiskey eindeutig sein soll.
//<E extends Enum<E>>
public class TextImmutabletext<IEnumDefaulttext>  extends Immutabletext  implements IOptimisticLocking{
		
	public TextImmutabletext(){
		super();
		this.setKeyType("IMMUTABLETEXTTEXT"); //TODO: HIER EINE ENUMERATION MACHEN ÜBER DIE VERSCHIEDENEN SCHLÜSSELWERTE?
	}
	
	public TextImmutabletext(long lngThiskey, String sShorttext, String sLongtext, String sDescription){
		super();
		this.setKeyType("IMMUTABLETEXTTEXT");
		this.setThiskey(lngThiskey);
		this.setShorttext(sShorttext);
		this.setLongtext(sLongtext);
		this.setDescription(sDescription);
	}
	
	//### ABSTRACTE METHODEN
	 @Transient
    public Class getThiskeyEnumClass() {
	      return TextImmutabletext.getThiskeyEnumClassStatic();
	   }
	 
    /* GENERATED_END */
	
    //### Statische Methode (um einfacher darauf zugreifen zu können)
    public static Class getThiskeyEnumClassStatic(){
    	return EnumTextImmutabletext.class;
    }

  //#######################################################
  	//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
  	//### int Key, String shorttext, String longtext, String description
  	//#######################################################
  	public enum EnumTextImmutabletext implements IEnumSetTextTHM,  IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
  		
     	 @IFieldDescription(description = "TXT01 IMMUTABLETEXTVALUES") 
     	T01(1,"TXT01","TEXT 01","A test text 01. (immutable)"),
     	
     	 @IFieldDescription(description = "TXT02 IMMUTABLETEXTVALUES") 
     	T02(2,"TXT02","TEXT 02", "A test text 02. (immutable)");
     	   	
     private Long lKey;
     private String sLongtext, sShorttext, sDescription;
     

     //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
     EnumTextImmutabletext(int iKey, String sShorttext, String sLongtext, String sDescription){
         this.lKey = Long.valueOf(iKey);
         this.sShorttext = sShorttext;
         this.sLongtext = sLongtext;
         this.sDescription = sDescription;
     }
     
     //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
     //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
     EnumTextImmutabletext(){	
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
     public static EnumTextImmutabletext fromShorttext(String s) {
         for (EnumTextImmutabletext state : values()) {
             if (s.equals(state.getShorttext()))
                 return state;
         }
         throw new IllegalArgumentException("Not a correct shorttext: " + s);
     }

     public EnumSet<?>getEnumSetUsed(){
     	return EnumTextImmutabletext.getEnumSet();
     }

     @SuppressWarnings("rawtypes")
     public static <E> EnumSet getEnumSet() {
     	
         //Merke: Das wird anders behandelt als FLAGZ Enumeration.
     	//String sFilterName = "FLAGZ"; /
     	//...
     	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
     	
     	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
     	Class<EnumTextImmutabletext> enumClass = EnumTextImmutabletext.class;
     	EnumSet<EnumTextImmutabletext> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
     	
     	for(Object obj : EnumTextImmutabletext.class.getEnumConstants()){
     		//System.out.println(obj + "; "+obj.getClass().getName());
     		set.add((EnumTextImmutabletext) obj);
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
