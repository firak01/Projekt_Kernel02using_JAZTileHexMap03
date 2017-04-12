package debug.thm.persistence.model.sequence002;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import basic.persistence.model.IOptimisticLocking;

/**
 * @author lindhaueradmin
 *  * Merke: Diese Klasse muss in HibernateContextProviderxxxxxxx der Debug-Packages hinzugefügt werden. 
 * public boolean fillConfiguration() throws ExceptionZZZ{
 *
 *Sinn dieses TryOuts ist bei Verwendung des EntityManagers die Erzeugung von fortlaufenden schlüsselwerten durchzuführen.
 *Es soll mit iHilfe von @TableGenerator realisiert werden.
 *
 *FGL 20170412:
 *Anders las mit HibernateSessions habe ich das so nicht hinbekomen, obwohl ich die Datenbank schon zuvor erstellt hatte und auch die Tabellen so gweählt hatte wie SQLITE sie automatisch erstellt.
 */

@Entity(name="SquenceTester002") //Merke @Entiy muss eindeutig im ganzen Projekt sein
@Access(AccessType.PROPERTY)
@Table(name="SEQUENCETESTER")
public class SequenceTester implements Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;

	private String sDummy;

	private int iMyTestSequence;
	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public SequenceTester(){
	 }
	 public SequenceTester(String sDummy){
		 this.setDummyString(sDummy);
	 }
	 	 
	public String getDummyString(){
		   	return this.sDummy;
		}
	 public void setDummyString(String sDummy){
		 this.sDummy=sDummy;		 
	 }
	
	 
	 /*
	  
    @Entity - marks this class as a JPA persistable entity
    @Table - denotes the name of the table in which this entity is stored
    @Id - declares the field it refers to as the unique identifier for this entity
    @TableGenerator - informs JPA how to generate unique values for this entity's identifier. It has several parameters:
        name - identifier for the generator binding. This value must match the parameter in the @GeneratedValue annotation as described below.
        table - must match the name of the table created to store the sequence values.
        pkColumnName - the primary key column name that contains the name of the sequence we are using.
        valueColumnName - the name of the column that contains the numeric sequence value
        pkColumnValue - the value of the primary key column that identifies the sequence
        allocationSize - the amount by which this sequence should be incremented each time a new entity is created. The default value for this is fifty (50).
    @GeneratedValue - marks the field as having a generated value, either from the database or from some other ID generation strategy. This has two important parameters:
        strategy - a value from the GenerationType enumeration that declares the the way in which values will be generated. In this example GenerationType.TABLE is appropriate since we are letting the value be managed in the relational store.
        generator- must match the name of the @TableGenerator tag to provide the specifics on how the value is to be generated.
    @Column - declares the field to be mapped to a database column.

	 */
	 /* Grosser Fehlschlag... @TableGenerator funktioniert wohl nur mit @Id ... Da hier der Wert nicht verändert wird, gibt es ohne @Id eine constraint - Verletzung aufgrund des UNIQUE */
	 /*Aber immerhin, mit @Id funktioniert es so auch unter Hibernate und SQLite */
	 //SO funktioniert es mit Hibernate Session, aber nicht mit EntityManager
//	 @Id
//	 @TableGenerator(name="lidGeneratorSequence002", table="COMMON_FUER_IDGENERATOR_SEQENCE",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)
//	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorSequence002")
//	 @Column(name="TESTID_INCREMENTIERT", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1") 


	  /*	 
	 * Versuch dies mit dem EntityManger hinzubekommen...
	 */
	 @Id	
	 @TableGenerator(name="lidG002", table="sqlite_sequence",pkColumnName="name", valueColumnName="seq", pkColumnValue="lidG002SEQUENCETESTER", initialValue=1, allocationSize=1)
	 //@GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorSequence002") //Liefert mit JPA implementierung den Fehler SQL error or missing database (no such table: sqlite_sequence .... beachte den Wert für strategy=...TABLE
	 //@GeneratedValue(strategy = GenerationType.IDENTITY, generator="lidGeneratorSequence002") //Liefert mit JPA implementierung den Fehler [SQLITE_ERROR] SQL error or missing database (no such table: SEQUENCETESTER)
	 //@GeneratedValue(strategy=GenerationType.TABLE,generator="lidG002") //Liefert mit JPA implementierung den Fehler [SQLITE_ERROR] SQL error or missing database (no such table: SEQUENCETESTER)
	 //Darum Datenbank per Hand im SQLIteManager erstellt und folgende Tabelle in der Datenbank erzeugt.
	 //CREATE TABLE "SEQUENCETESTER" ("TESTID_INCREMENTIERT" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE  DEFAULT 1, "DummyString" TEXT)
	 //danach Fehler, das sqlite_sequence nicht existiert
	 //also einen Datensatz erzeugt. Dann wird sqlite_sequence automatisch erzeugt. Darum sind auch die Namen für plColimnName='name' und valueColumnName='seq'  nicht beliebig.
	 //trotzdem den Fehler, obwohl die Tabelle existiert.  Also GenerationType Identity gewählt..
	 //@GeneratedValue(strategy=GenerationType.IDENTITY,generator="lidG002")
	 //Nun wieder Fehler , das die Tabelle SEQUENCETESTER nicht vorhanden sei, obwohl die extra erstellt wurde.
	 @GeneratedValue(generator="lidG002")
	 @Column(name="TESTID_INCREMENTIERT", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1") 	 
	 public int getTest(){
		 return this.iMyTestSequence;
	 }
	 public void setTest(int iLid){
		 this.iMyTestSequence = iLid;
	 }
}
