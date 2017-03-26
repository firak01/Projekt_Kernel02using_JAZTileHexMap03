package debug.thm.persistence.model.association001;

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

@Entity
@Access(AccessType.PROPERTY)
@Table(name="ASSOCIATIONTARGETTESTER")
public class AssociationTargetTester implements Serializable, IOptimisticLocking{
	private static final long serialVersionUID = 1113434456411176970L;


	private String sDummy;
	private String sKey;

	
	//Der Default Contruktor wird für JPA - Abfragen wohl benötigt
	 public AssociationTargetTester(){
	 }
	 public AssociationTargetTester(String sKey, String sDummy){
		 this.setKey(sKey);
		 this.setDummyString(sDummy);
	 }
	 
	public String getDummyString(){
		   	return this.sDummy;
		}
	 public void setDummyString(String sDummy){
		 this.sDummy=sDummy;		 
	 }
	
	 @Id					
	 public String getKey(){
		 return this.sKey;
	 }
	 public void setKey(String sKey){
		 this.sKey = sKey;
	 }
	 

}
