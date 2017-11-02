package basic.zBasic.util.abstractEnum;

import java.util.EnumSet;

import use.thm.persistence.interfaces.enums.IEnumSetDefaulttextTHM;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import basic.persistence.model.IFieldDescription;
import basic.zBasic.persistence.interfaces.enums.IThiskeyProviderZZZ;

//MERKE:	Solche Enums werden für die Schlüsseltabellen einer datenbank in den entsprechenden Entities verwendet.
//				Diese sind dann aber als innere Klassen implementiert. Siehe unten
//#############################################################


   //#######################################################
	//### Eingebettete Enum-Klasse mit den Defaultwerten, diese Werte werden auch per Konstruktor übergeben.
	//### int Key, String shorttext, String longtext, String description
	//#######################################################
	public enum EnumSetDefaulttextTestTypeTHM implements IEnumSetDefaulttextTHM,  IThiskeyProviderZZZ<Long>{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
		
 	 @IFieldDescription(description = "The TEXTVALUE01") 
 	TESTVALUE01(1,"Test01","Test01 long","A Test01 value description"),
 	
 	 @IFieldDescription(description = "The TEXTVALUE02") 
 	TESTVALUE02(2,"Test02","Test02 long", "A Test02 value description"),
 	 
 	 @IFieldDescription(description = "The TEXTVALUE03") 
 	TESTVALUE03(2,"Test03","Test03 long", "A Test03 value description");
 	   	
 private Long lKey;
 private String sLongtext, sShorttext, sDescription;
 

 //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
 EnumSetDefaulttextTestTypeTHM(int iKey, String sShorttext, String sLongtext, String sDescription){
 	this.lKey = Long.valueOf(iKey);
     this.sShorttext = sShorttext;
     this.sLongtext = sLongtext;
     this.sDescription = sDescription;
 }
 
 //Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
 //           In der Util-Klasse habe ich aber einen Workaround gefunden ( basic/zBasic/util/abstractEnum/EnumSetMappedUtilZZZ.java ).
 EnumSetDefaulttextTestTypeTHM(){	
 }

//##################################################
//#### Folgende Methoden bring Enumeration von Hause aus mit:
 
 @Override
 public String getName(){
	   return super.name();
 }
 
 @Override
 public String toString() {
     return this.sShorttext;
 }

 @Override
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
 @Override
	public Long getThiskey() {
		return this.lKey;
	}

 //### Folgende Methoden sind zum komfortablen arbeiten gedacht.
 @Override
 public int getPosition() {
 	return getIndex() + 1;
 }

	
	
 // the valueOfMethod <--- Translating from DB
 public static EnumSetDefaulttextTestTypeTHM fromShorttext(String s) {
     for (EnumSetDefaulttextTestTypeTHM state : values()) {
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
	 Class<EnumSetDefaulttextTestTypeTHM> enumClass = EnumSetDefaulttextTestTypeTHM.class;
 	EnumSet<EnumSetDefaulttextTestTypeTHM> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
 	
 	for(Object obj : EnumSetDefaulttextTestTypeTHM.class.getEnumConstants()){
 		//System.out.println(obj + "; "+obj.getClass().getName());
 		set.add((EnumSetDefaulttextTestTypeTHM) obj);
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
