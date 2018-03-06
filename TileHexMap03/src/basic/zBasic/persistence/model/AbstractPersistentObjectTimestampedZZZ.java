package basic.zBasic.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.interfaces.IModelDateTimestampProviderZZZ;

/*FGL DIESE ABSTRACTE KLASSE WIRD NICHT GENUTZT.
 *    Mit Hibernate pur habe ich das nicht hinbekommen.
 */
public abstract class AbstractPersistentObjectTimestampedZZZ extends AbstractPersistentObjectZZZ implements IModelDateTimestampProviderZZZ {
	
	//Aus dem IModelDateTimestampProvider. FGL: 20180220: Versuche dies in eine abstrakte Superklasse zu verschieben und so Vererbung zu nutzen sind mit pur Hibernate gescheitert.
	private Date dateCreatedThis;
	private String sDateCreatedThis;
	private Date dateUpdatedAt;
	
	
	//MERKE: Die Annotations werden dann aus dem Interface IModelDateTimestampProviderZZZ NICHT automatisch hier ergänzt. Warum ?????
	@Column(name="createdThisAt", insertable = true, updatable = false)
	@Type(type = DateMapping.USER_TYPE_NAME)
	public Date getCreatedThisAt(){
		return this.dateCreatedThis;
	}
	public void setCreatedThisAt(Date dateCreatedThis){
		this.dateCreatedThis = dateCreatedThis;
	}
	
	@Column(name="createdThisAtString", insertable = true, updatable = false)		
	@Type(type = DateMapping.USER_TYPE_NAME)
	public String getCreatedThisAtString(){
		return this.sDateCreatedThis;
	}
	public void setCreatedThisAtString(String sDateCreatedThis){
		this.sDateCreatedThis = sDateCreatedThis;
	}
	
			
	//MERKE: Die Annotations werden dann aus dem Interface IModelDateTimestampProviderZZZ NICHT automatisch hier ergänzt. Warum ?????
	@Version  //https://www.thoughts-on-java.org/hibernate-tips-use-timestamp-versioning-optimistic-locking/		//
	@Type(type = DateMapping.DATE_TYPE_TIMESTAMP_SQLITE_FGL) //Das ist "timestamp"		
	public Date getUpdatedAt(){
		return this.dateUpdatedAt;
	}
	protected void setUpdatedAt(Date dateUpdatedAt){
		this.dateUpdatedAt = dateUpdatedAt;
	}
}
