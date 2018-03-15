package basic.zBasic.persistence.interfaces;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import basic.zBasic.persistence.hibernate.DateMapping;

public interface IModelDateTimestampProviderZZZ {

	//MERKE: Die Annotations werden dann aus dem Interface IModelDateTimestampProviderZZZ NICHT automatisch in den Entities ergänzt.
	
//	//###### BERECHNETE DATUMSWERTE: Versuch CreatedAt automatisch zu erhalten
//	//Fazit 20180219: Funktioniert nicht
//	//Variante 1: funktioniert nicht
//	//@Temporal(TemporalType.TIMESTAMP)
//	//@org.hibernate.annotations.Generated( //Merke Fehler beim Versuch Variante 1 & 2 zu kombinieren: org.hibernate.AnnotationException: @Generated(INSERT) on a @Version property not allowed, use ALWAYS: use.thm.persistence.model.Tile.createdAt
//	//	org.hibernate.annotations.GenerationTime.INSERT
//	//	)
//	
//	//Variante 2: funktioniert nicht
//	//@Version 
//	//@Type(type = "timestamp")//Versuch mehr als das Jahr zu bekommen, ja, das ist dann ein Timestamp basierend auf einer LONG - Zahl, wie ich sie schon beim UNIQUE-Namen verwende		
//	@Column(name="createdAt", insertable = true, updatable = true)
//	public Date getCreatedAt(){
//		return this.dateCreatedAt;
//	}
//	protected void setCreatedAt(Date dateCreateddAt){
//		this.dateCreatedAt = dateCreatedAt;
//	}
		
	//###### BERECHNETE DATUMSWERTE: Versuch CreatedAt automatisch zu erhalten. Automatisch klappt nicht, aber das Setzen klappt so ganz gut.
	//DAS IST DIE VERSION DER HIS mit einem USER_TYPE ....
	//FGL: das wäre der Versuch mit @Version. Dafür muss die verwendete Klasse aber implementieren: UserVersionType
	//Das klappt aber so nicht automatisch ... @Version, daher diesen Wert über das DAO explizit setzen.
	//Sie funktioniert, wenn zahlreiche Klassen für das Date-Handling und andere Bibliothek (z.B. aspectj - Tools) eingebunden werden.
	//Vielleicht der Vorteil, dass man hier beliebige Datumsformate "reinwerfen kann".
	//@Column(name="createdThisAt", insertable = true, updatable = false)
	
    //20180314: Hier wird es dem CustomType überlassen einen konkreten Wert zu setzen.
	//           Der null-Fall wird in den DateMapping / DateMappingString Klassen gezielt angesprungen		
	@Column(name="createdThis", insertable = true, updatable = false)
	@Type(type = DateMapping.USER_TYPE_NAME)
	public Date getCreatedThis();	
	//public void setCreatedThis();////Das setzen dem CustomUserType s. @Type Annotation überlassen
	//Ohne Argument findet Hibernate aber den Setter nicht... public void setCreatedThis(){
    //                               Exception in thread "main" org.hibernate.MappingException: Could not get constructor for org.hibernate.persister.entity.SingleTableEntityPersister
	public void setCreatedThis(Date dateCreatedThis);////Das setzen dem CustomUserType s. @Type Annotation überlassen
	
	//Buch "Java Persistence with Hibernate" Kapitel 5.1.5 Listing 5.4. = So wird die Spalte auf ReadOnly gesetzt. Ziel ist, dass der übergebene Wert eingetragen wird und nicht der Datenbankwert
	//hmm dann wird einfach nix geschrieben @Column(name="createdThisAt", insertable = false, updatable = false)
	@Column(name="createdThisAt", insertable = true, updatable = false)
	@Type(type = DateMapping.USER_TYPE_NAME)
	public Date getCreatedThisAt();	
	public void setCreatedThisAt(Date dateCreatedThis);
	
	//*wegen der Probleme 20180212 auskommenteiert
	@Column(name="createdThisAtString", insertable = true, updatable = false)
	//Buch "Java Persistence with Hibernate" Kapitel 5.1.5 Listing 5.4. = So wird die Spalte auf ReadOnly gesetzt. Ziel ist, dass der übergebene Wert eingetragen wird und nicht der Datenbankwert
	//hmm dann wird einfach nix geschrieben @Column(name="createdThisAtString", insertable = false, updatable = false)
	@Type(type = DateMapping.USER_TYPE_NAME)
	public String getCreatedThisAtString();	
	public void setCreatedThisAtString(String sDateCreatedThis);
	
	public String getCreatedThisString();
	public void setCreatedThisString(String sDateOrEmpty);
	
	public String getCreatedThisStringComment();
	public void setCreatedThisStringComment(String sComment);
	
	
	//	*/
		
	//##### BERECHNETE DATUMSWERTE. Versuch UpdatedAt automatisch zu erhalten
//	@Version  //https://www.thoughts-on-java.org/hibernate-tips-use-timestamp-versioning-optimistic-locking/
	//@Type(type = "dbtimestamp")//Erstaunlicherweise gibt es hier einen Eintrag. Das ist zwar nur das Jahr (2018) aber immerhin				
//	@Type(type = "timestamp")//Versuch mehr als das Jahr zu bekommen, ja, das ist dann ein Timestamp basierend auf einer LONG - Zahl, wie ich sie schon beim UNIQUE-Namen verwende
	
	//Arbeite mit @Version. Der Vorteil ist, das man den Zeitstempel nicht extra setzen muss, da dies automatisch passiert.	
	//Alternative: HIS-Lösung
//	@Type(type = DateMapping.DATE_TYPE_TIMESTAMP)
//	@Column(name="updatedAt", insertable = true, updatable = false)
	//Buch "Java Persistence with Hibernate" Kapitel 5.1.5 Listing 5.4. = So wird die Spalte auf ReadOnly gesetzt. Ziel ist, dass der übergebene Wert eingetragen wird und nicht der Datenbankwert
	//hmm dann wird einfach nix geschrieben @Column(name="updatedAt", insertable = false, updatable = false)
		
	//##### BERECHNETE DATUMSWERTE. Versuch UpdatedAt automatisch zu erhalten	
	//Vgl. Buch "Java Persistence with Hibernate (2016)", Kapitel 5.1.5, (S. 117 in E-Book Readern).
	//SQLITE Datenbank: Das Arbeiten mit TemporalType.TIMESTAMP funktioniert nicht
	
	//+++VERSUCH Variante 1:
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(insertable = false, updatable = false)
//	@org.hibernate.annotations.Generated(
//			org.hibernate.annotations.GenerationTime.ALWAYS
//	)
//	public Date getUpdatedAt(){
//		return this.dateUpdatedAt;
//	}
//	protected void setUpdatedAt(Date dateUpdatedAt){
//		this.dateUpdatedAt = dateUpdatedAt;
//	}
	
	//+++VERSUCH Variante 2: insertable auf "true" und nur beim INSERT
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="updatedAt", insertable = true, updatable = false)
//	@org.hibernate.annotations.Generated(
//			org.hibernate.annotations.GenerationTime.INSERT
//	)
//	public Date getUpdatedAt(){
//		return this.dateUpdatedAt;
//	}
//	protected void setUpdatedAt(Date dateUpdatedAt){
//		this.dateUpdatedAt = dateUpdatedAt;
//	}
	
	//
	//Das wäre das geplante UpdatedAt.... in diesem Interface
	//Merke: Scheinbar kann das nicht gleichzeitig mit @Version verwendet werden.
	//public Date getUpdatedAt();
	//nicht im Interface... protected void setUpdatedAt(Date dateUpdatedAt);


	//Ebook-Reader Seite 118
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(insertable = false, updatable = false)
	//Das funktioniert nicht, das ist erst ab Hibernate 4.3.x vorhanden .... @org.hibernate.annotations.CreationTimestamp
	
	
	//##### BERECHNETE DATUMSWERTE. Versuch UpdatedAt automatisch zu erhalten
	//Arbeite mit @Version. Der Vorteil ist, das man den Zeitstempel nicht extra setzen muss, da dies atomatisch passiert.
//	@Version  //https://www.thoughts-on-java.org/hibernate-tips-use-timestamp-versioning-optimistic-locking/
	//@Type(type = "dbtimestamp")//Erstaunlicherweise gibt es hier einen Eintrag. Das ist zwar nur das Jahr (2018) aber immerhin				
//	@Type(type = "timestamp")//Versuch mehr als das Jahr zu bekommen, ja, das ist dann ein Timestamp basierend auf einer LONG - Zahl, wie ich sie schon beim UNIQUE-Namen verwende


	//+++ Das funktioniert
	@Version  //https://www.thoughts-on-java.org/hibernate-tips-use-timestamp-versioning-optimistic-locking/			
	@Type(type = "timestamp")//Versuch mehr als das Jahr zu bekommen, ja, das ist dann ein Timestamp basierend auf einer LONG - Zahl, wie ich sie schon beim UNIQUE-Namen verwende
//	//@Type(type = DateMapping.DATE_TYPE_TIMESTAMP) //CLASS NOT FOUND EXCEPTION "TIMESTAMP"
	public Date getUpdated();
	//nicht im Interface... protected void setUpdatedAt(Date dateUpdatedAt);		

}
