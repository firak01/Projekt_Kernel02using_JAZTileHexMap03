package basic.zBasic.util.abstractEnum;

import static java.lang.System.out;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;

import basic.zBasic.ReflectClassZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.persistence.jdbc.JdbcDatabaseTypeZZZ;

/*
Properties that are database specific are:

    hibernate.connection.driver_class: JDBC driver class
    hibernate.connection.url: JDBC URL
    hibernate.connection.username: database user
    hibernate.connection.password: database password
    hibernate.dialect: The class name of a Hibernate org.hibernate.dialect.Dialect which allows Hibernate to generate SQL optimized for a particular relational database.

To change the database, you must:

    Provide an appropriate JDBC driver for the database on the class path,
    Change the JDBC properties (driver, url, user, password)
    Change the Dialect used by Hibernate to talk to the database

There are two drivers to connect to SQL Server; the open source jTDS and the Microsoft one. The driver class and the JDBC URL depend on which one you use.
With the jTDS driver

The driver class name is net.sourceforge.jtds.jdbc.Driver.

The URL format for sqlserver is:

 jdbc:jtds:sqlserver://<server>[:<port>][/<database>][;<property>=<value>[;...]]

So the Hibernate configuration would look like (note that you can skip the hibernate. prefix in the properties):

<hibernate-configuration>
  <session-factory>
    <property name="connection.driver_class">net.sourceforge.jtds.jdbc.Driver</property>
    <property name="connection.url">jdbc:jtds:sqlserver://<server>[:<port>][/<database>]</property>
    <property name="connection.username">sa</property>
    <property name="connection.password">lal</property>

    <property name="dialect">org.hibernate.dialect.SQLServerDialect</property>

    ...
  </session-factory>
</hibernate-configuration>

With Microsoft SQL Server JDBC 3.0:

The driver class name is com.microsoft.sqlserver.jdbc.SQLServerDriver.

The URL format is:

jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]

So the Hibernate configuration would look like:

<hibernate-configuration>
  <session-factory>
    <property name="connection.driver_class">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>
    <property name="connection.url">jdbc:sqlserver://[serverName[\instanceName][:portNumber]];databaseName=<databaseName></property>
    <property name="connection.username">sa</property>
    <property name="connection.password">lal</property>

    <property name="dialect">org.hibernate.dialect.SQLServerDialect</property>

    ...
  </session-factory>
</hibernate-configuration>
 */
//Merke: Obwohl fullName und abbr nicht direkt abgefragt werden, müssen Sie im Konstruktor sein, um die Enumeration so zu definieren.
//ALIAS("Beschreibung, wird nicht genutzt....","Abkürzung, also das, was im URL String steht. Meist gefolgt von einem  Doppelpunkt, der hinzugerechnet wird, wenn die Abkürzung nicht leer ist.")
public enum EnumSetMappedTestTypeZZZ implements IEnumSetMappedZZZ{//Folgendes geht nicht, da alle Enums schon von einer Java BasisKlasse erben... extends EnumSetMappedBaseZZZ{
	ONE("eins","1"),
	TWO("zwei","2"),
	THREE("drei","3");
	

private String name, abbr;

//Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
//          In der Util-Klasse habe ich aber einen Workaround gefunden.
EnumSetMappedTestTypeZZZ(){	
}

//Merke: Enums haben keinen public Konstruktor, können also nicht intiantiiert werden, z.B. durch Java-Reflektion.
EnumSetMappedTestTypeZZZ(String fullName, String abbr) {
    this.name = fullName;
    this.abbr = abbr;
}


@Override
public String toString() {
    return this.name;
}

public int order() {
	return ordinal()+1;
}

// the identifierMethod ---> Going in DB
public String getAbbreviation() {
    return this.abbr;
}

// the valueOfMethod <--- Translating from DB
public static EnumSetMappedTestTypeZZZ fromAbbreviation(String s) {
    for (EnumSetMappedTestTypeZZZ state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}

public String getDescription(){
	return this.name;
}

public EnumSet<?>getEnumSetUsed(){
	return EnumSetMappedTestTypeZZZ.getEnumSet();
}

@SuppressWarnings("rawtypes")
public static <E> EnumSet getEnumSet() {
	
    //Merke: Das wird anders behandelt als FLAGZ Enumeration.
	//String sFilterName = "FLAGZ"; /
	//...
	//ArrayList<Class<?>> listEmbedded = ReflectClassZZZ.getEmbeddedClasses(this.getClass(), sFilterName);
	
	//Erstelle nun ein EnumSet, speziell für diese Klasse, basierend auf  allen Enumrations  dieser Klasse.
	Class<EnumSetMappedTestTypeZZZ> enumClass = EnumSetMappedTestTypeZZZ.class;
	EnumSet<EnumSetMappedTestTypeZZZ> set = EnumSet.noneOf(enumClass);//Erstelle ein leeres EnumSet
	
	for(Object obj : EnumSetMappedTestTypeZZZ.class.getEnumConstants()){
		//System.out.println(obj + "; "+obj.getClass().getName());
		set.add((EnumSetMappedTestTypeZZZ) obj);
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



//TODO: Mal sehen wie man das kombineiren von 2 EnumSets als Utility - Methode nutezn kann
/*
Combining Java EnumSets
enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
EnumSet<Suit> reds = EnumSet.of(Suit.HEARTS, Suit.DIAMONDS);
EnumSet<Suit> blacks = EnumSet.of(Suit.CLUBS, Suit.SPADES);

EnumSet<Suit> redAndBlack = EnumSet.copyOf(reds);
redAndBlack.addAll(blacks);
*/
	
	
	//#######################################################################################
	/* für enumeration als Klasse:
	 * 
	 * How to use Java reflection when the enum type is a Class?
	 * public enum PropertyEnum {

  SYSTEM_PROPERTY_ONE("property.one.name", "property.one.value"),

  SYSTEM_PROPERTY_TWO("property.two.name", "property.two.value");

  private String name;  

  private String defaultValue;

  PropertyEnum(String name) {
    this.name = name;
  }

  PropertyEnum(String name, String value) {
    this.name = name;
    this.defaultValue = value;
  } 

  public String getName() {
    return name;
  }

  public String getValue() {
    return System.getProperty(name);
  }

  public String getDefaultValue() {
    return defaultValue;
  }  
	 * 
	 * 
	 * hier die Auswertung
	 *  Class<?> clz = Class.forName("test.PropertyEnum");
    Object[] consts = clz.getEnumConstants();
    Class<?> sub = consts[0].getClass();
    Method mth = sub.getDeclaredMethod("getDefaultValue");
    String val = (String) mth.invoke(consts[0]);
    System.out.println("getDefaultValue " + 
      val.equals(PropertyEnum.SYSTEM_PROPERTY_ONE.getDefaultValue()));
	 */

	
}//End class
