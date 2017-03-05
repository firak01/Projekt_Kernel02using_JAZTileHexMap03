package basic.zBasic.util.persistence.jdbc;

import java.util.EnumSet;

import basic.zBasic.util.abstractEnum.EnumSetMappedTestTypeZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;

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
//ALIAS("Beschreibung, also das, was 'connection.driver_class' steht. ,"Abkürzung, steht Bei Treibern vor dem Treibernamen (ohne Doppelpunkt), Merke: Falls gefüllt, dann wird der Doppelppunkt hinzugerechnet.")
public enum JdbcDriverClassTypeZZZ implements IEnumSetMappedZZZ {
	SQLITE("",""),
	MYSQL("",""),
	SQLSERVER_OPENSOURCE("net.sourceforge.jtds.jdbc.Driver","jtds"),
	SQLSERVER_MICROSOFT("com.microsoft.sqlserver.jdbc.SQLServerDriver","");
	

private String name, abbr;

JdbcDriverClassTypeZZZ(String fullName, String abbr) {
    this.name = fullName;
    this.abbr = abbr;
}

@Override
public String toString() {
    return this.name;
}

// the identifierMethod ---> Going in DB
public String getAbbreviation() {
    return this.abbr;
}

// the valueOfMethod <--- Translating from DB
public static JdbcDriverClassTypeZZZ fromAbbreviation(String s) {
    for (JdbcDriverClassTypeZZZ state : values()) {
        if (s.equals(state.getAbbreviation()))
            return state;
    }
    throw new IllegalArgumentException("Not a correct abbreviation: " + s);
}

public EnumSet<?>getEnumSetUsed(){
	return JdbcDriverClassTypeZZZ.getEnumSet();
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

public int order() {
	return ordinal()+1; 
}
	
}
