package debug.thm.persistence.model.sequence001;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;

/**
 * @author lindhaueradmin
 *  * Merke: Diese Klasse muss in HibernateContextProviderTHM der Debug-Packages hinzugefügt werden. 
 * public boolean fillConfiguration() throws ExceptionZZZ{
 *
 */

@Entity
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
	 @Id
	 @TableGenerator(name="lidGeneratorSequence", table="COMMON_FUER_IDGENERATOR_SEQENCE",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorSequence")
	 @Column(name="TESTID_INCREMENTIERT", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1") 
	 public int getTest(){
		 return this.iMyTestSequence;
	 }
	 public void setTest(int iLid){
		 this.iMyTestSequence = iLid;
	 }
}
