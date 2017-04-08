package debug.thm.persistence.model.association002;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import basic.persistence.model.IOptimisticLocking;

/**
 * @author lindhaueradmin
 * Merke: Diese Klasse muss in HibernateContextProviderTHM der Debug-Packages hinzugefügt werden. 
 * public boolean fillConfiguration() throws ExceptionZZZ{
 * 
 *
 */


@Entity(name="AssociationTester002") //Merke @Entiy muss eindeutig im ganzen Projekt sein
@Access(AccessType.PROPERTY)
@Table(name="ASSOCIATIONTESTER")
public class AssociationTester implements Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;

	private int iMyTestSequence;
	private String sDummy;
	
	 //Damit wird ein BLOB gespeichert, oder? JA, aber das will ich nicht!
//	 @Access(AccessType.FIELD)	
//	 @OneToOne(//Hiermit die 1:1 Beziehung aufbauen, muss an der Property stehen und nicht an der Klasse
//	 		fetch = FetchType.LAZY, //default = EAGER
//			// fetch = FetchType.EAGER, //default = EAGER
//	 		optional=false //required for lazy loading with properties
//	 )
//	 @PrimaryKeyJoinColumn(name="targetauto_id", referencedColumnName="TESTID_INCREMENTIERT")//, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
//	 
	
	 
	 //Speichert nur die ID ab. Das Abspeichern des Objekts wird mit @Transient über dem entsprechenden GETTER/SETTER verhindert
	//Siehe Buch Persistence with Hibernate (2016) (MeapV7 - S. 182)(Im spreäeren Buch allerdings woanders).
	 @Access(AccessType.FIELD)
	 @OneToOne(fetch = FetchType.LAZY)
	 @JoinTable(
			 name = "TESTER_TARGET", //Required !
			 joinColumns = @JoinColumn(name="AssociationTester_ID"),
			 inverseJoinColumns= @JoinColumn(name="targetauto_id", nullable = false, unique = true)
			 )
	private AssociationTargetTesterAutoKey objTargetAutoKey;
	 
	 
	 //Damit wird ein BLOB gespeichert, oder? JA, aber das will ich nicht!
//	 @Access(AccessType.FIELD)	
//	 @OneToOne(//Hiermit die 1:1 Beziehung aufbauen, muss an der Property stehen und nicht an der Klasse
//	 		fetch = FetchType.LAZY, //default = EAGER
//			// fetch = FetchType.EAGER, //default = EAGER
//	 		optional=false //required for lazy loading with properties
//	 )
//	 @PrimaryKeyJoinColumn(name="target_id", referencedColumnName="key")//, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
//	 
	 
	 
	//In diesem Test erst mal nur die AUTOTARGETs testen
	 //Speichert nur die ID ab. Das Abspeichern des Objekts wird mit @Transient über dem entsprechenden GETTER/SETTER verhindert
//	 @Access(AccessType.FIELD)
//	 @OneToOne(cascade=CascadeType.ALL)
//	 @JoinColumn(name="target_id")
//	 private AssociationTargetTestery objTarget;
	

	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public AssociationTester(){
	 }
	 public AssociationTester(String sDummy){
		 this.setDummyString(sDummy);
	 }
	 
	 
	public String getDummyString(){
		   	return this.sDummy;
		}
	 public void setDummyString(String sDummy){
		 this.sDummy=sDummy;		 
	 }
	
	 @Id				
	 @TableGenerator(name="lidGeneratorAssociation002", table="COMMON_FUER_IDGENERATOR_ASSOCIATION",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorAssociation002")
	 @Column(name="HAUPTID_INKREMENTIERT", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1") 
	 public int getKey(){
		 return this.iMyTestSequence;
	 }
	 public void setKey(int iLid){
		 this.iMyTestSequence = iLid;
	 }
	
	 //####################################################
	 //Damit wird ein BLOB gespeichert, oder?
//		 @Access(AccessType.PROPERTY)		
//		 @OneToOne(//Hiermit die 1:1 Beziehung aufbauen, muss an der Property stehen und nicht an der Klasse
//		 		//fetch = FetchType.LAZY, //default = EAGER
//				 fetch = FetchType.EAGER, //default = EAGER
//		 		optional=false //required for lazy loading with properties
//		 )
//		 @PrimaryKeyJoinColumn//(name="TARGETID", columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 
	 @Transient //Ich will nur den Schlüssel abspeichern, mit der JOINColumn - Lösung
	 	public AssociationTargetTesterAutoKey getTargetAutoKey(){
		 return this.objTargetAutoKey;		 
	 }
	 public void setTargetAutoKey(AssociationTargetTesterAutoKey objTarget){
		 this.objTargetAutoKey = objTarget;
	 }
	 
	 
	 //######################################
	 //Damit wird ein BLOB gespeichert, oder?
//	 @Access(AccessType.PROPERTY)		
//	 @OneToOne(//Hiermit die 1:1 Beziehung aufbauen, muss an der Property stehen und nicht an der Klasse
//	 		//fetch = FetchType.LAZY, //default = EAGER
//			 fetch = FetchType.EAGER, //default = EAGER
//	 		optional=false //required for lazy loading with properties
//	 )
//	 @PrimaryKeyJoinColumn//(name="TARGETID", columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1")
	 
	
	 
}
