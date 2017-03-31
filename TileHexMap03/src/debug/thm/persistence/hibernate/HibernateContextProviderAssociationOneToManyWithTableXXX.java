package debug.thm.persistence.hibernate;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.HibernateContextProviderZZZ;
import basic.zKernel.KernelZZZ;
import debug.thm.persistence.model.association002.AssociationTargetTesterAutoKey;
import debug.thm.persistence.model.association002.AssociationTester;

public class HibernateContextProviderAssociationOneToManyWithTableXXX extends HibernateContextProviderZZZ{
	
	
	
//	//Über die EntityManagerFactory erstellte EntityManager werden in dieser Hashmap verwaltet: hm("Name des Schemas/der Datenbank") = objEntityManager;
//	HashMapExtendedZZZ<String, EntityManager> hmEntityManager = new HashMapExtendedZZZ<String, EntityManager>();
//	public HibernateContextProviderTHM() throws ExceptionZZZ{
//		super();
//		boolean bErg = this.fillConfiguration();
//		if(!bErg){
//			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//			throw ez;
//		}
//	}
	
	public HibernateContextProviderAssociationOneToManyWithTableXXX() throws ExceptionZZZ{
		super();
	}
//
//	public HibernateContextProviderTHM(KernelZZZ objKernel) throws ExceptionZZZ{
//		super(objKernel);
//		boolean bErg = this.fillConfiguration();
//		if(!bErg){
//			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//			throw ez;
//		}
//	}
	public HibernateContextProviderAssociationOneToManyWithTableXXX(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	
	/**Fülle die Configuration
	 * a) mit globalen Werten, z.B. Datenbankname, Dialekt
	 * b) mit den zu betrachtenden Klassen, entweder annotiert oder per eigener XML Datei.
	 * 
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public boolean fillConfiguration() throws ExceptionZZZ{
		boolean bReturn = false;
		
		bReturn = fillConfigurationGlobal();
		//+++ Die für Hibernate DEBUG Konfigurierten Klassen hinzufügen
		//Merke: Wird eine Klasse ohne @Entity hinzugefügt, gibt es folgende Fehlermeldung: Exception in thread "main" org.hibernate.AnnotationException: No identifier specified for entity: use.thm.client.component.AreaCellTHM
		bReturn = addConfigurationAnnotatedClass(AssociationTester.class);
		bReturn = addConfigurationAnnotatedClass(AssociationTargetTesterAutoKey.class);
		
		return bReturn;
	}
	
	/** Fülle globale Werte in das Configuruation Objekt, z.B. der Datenbankname, Dialekt, etc.
	 * 
	 */
	public boolean fillConfigurationGlobal(){
				//TODO: Die hier verwendeten Werte aus der Kernel-Konfiguration auslesen.
				//Programmatisch das erstellen, das in der hibernate.cfg.xml Datei beschrieben steht.
				//Merke: Irgendwie funktioniert es nicht die Werte in der hibernate.cfg.xml Datei zu überschreiben.
		 		//			Darum muss z.B. hibernate.hbm2ddl.auto in der Konfigurationdatei auskommentiert werden, sonst ziehen hier die Änderungen nicht.
				this.getConfiguration().setProperty("hiberate.show_sql", "true");
				this.getConfiguration().setProperty("hiberate.format_sql", "true");
				this.getConfiguration().setProperty("hibernate.dialect","basic.persistence.hibernate.SQLiteDialect" );
				this.getConfiguration().setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
				this.getConfiguration().setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\DebugAssociationOneToManyWithTableTester.sqlite");
				this.getConfiguration().setProperty("hibernate.connection.username", "");
				this.getConfiguration().setProperty("hibernate.connection.password", "");

				/*
				 * So the list of possible options are,
    validate: validate the schema, makes no changes to the database.
    update: update the schema.
    create: creates the schema, destroying previous data.
    create-drop: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.
				 */
				this.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create"); //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
				//this.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.
				this.getConfiguration().setProperty("cache.provider_class", "org.hiberniate.cache.NoCacheProvider");
				this.getConfiguration().setProperty("current_session_context_class", "thread");
				
				return true;
	}	
}
