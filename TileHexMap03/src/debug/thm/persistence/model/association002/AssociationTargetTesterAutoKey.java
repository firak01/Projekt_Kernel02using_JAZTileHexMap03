package debug.thm.persistence.model.association002;

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
import basic.zBasic.ExceptionZZZ;

/**
 * @author lindhaueradmin
 * Merke: Diese Klasse muss in HibernateContextProviderTHM der Debug-Packages hinzugefügt werden. 
 * public boolean fillConfiguration() throws ExceptionZZZ{
 * 
 *
 */


@Entity(name="AssociationTargetTesterAutoKey002") //Merke @Entiy muss eindeutig im ganzen Projekt sein
@Access(AccessType.PROPERTY)
@Table(name="ASSOCIATIONTARGETTESTERAUTOKEY")
public class AssociationTargetTesterAutoKey implements Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;


	private String sDummy;
	private int iMyTestSequence;

	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public AssociationTargetTesterAutoKey(){
	 }
	 public AssociationTargetTesterAutoKey(String sDummy){
		 this.setDummyString(sDummy);
	 }
	 
	public String getDummyString(){
		   	return this.sDummy;
		}
	 public void setDummyString(String sDummy){
		 this.sDummy=sDummy;		 
	 }
	
	 @Id				
	 @TableGenerator(name="lidGeneratorAssociationTarget002", table="COMMON_FUER_IDGENERATOR_ASSOCIATIONTARGET",pkColumnName="nutzende_Klasse_als_String", pkColumnValue="SequenceTester",valueColumnName="naechster_id_wert",  initialValue=1, allocationSize=1)//@TableGenerator Name muss einzigartig im ganzen Projekt sein.
	 @GeneratedValue(strategy = GenerationType.TABLE, generator="lidGeneratorAssociationTarget002")
	 @Column(name="TESTID_INCREMENTIERT", nullable=false, unique=true, columnDefinition="INTEGER NOT NULL UNIQUE  DEFAULT 1") 
	 public int getKey(){
		 return this.iMyTestSequence;
	 }
	 public void setKey(int iLid){
		 this.iMyTestSequence = iLid;
	 }
	 

}
