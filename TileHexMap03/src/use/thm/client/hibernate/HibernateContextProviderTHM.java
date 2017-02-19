package use.thm.client.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import tryout.hibernate.AreaCell;
import tryout.hibernate.HexCell;
import use.thm.client.component.AreaCellTHM;
import use.thm.client.component.HexCellTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public class HibernateContextProviderTHM extends KernelUseObjectZZZ{
	
	/**Hier wird die Konfiguration programmatisch aufgebaut
	 * und die globalen Hibernate Objekte sind hierüber überall verfügbar.
	 * 
	 */
	private Configuration cfgHibernate = new Configuration();
	private Session objSession = null;
	
	//Über die EntityManagerFactory erstellte EntityManager werden in dieser Hashmap verwaltet: hm("Name des Schemas/der Datenbank") = objEntityManager;
	HashMapExtendedZZZ<String, EntityManager> hmEntityManager = new HashMapExtendedZZZ<String, EntityManager>();
	public HibernateContextProviderTHM() throws ExceptionZZZ{
		super();
		boolean bErg = this.fillConfiguration();
		if(!bErg){
			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	

	public HibernateContextProviderTHM(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
		boolean bErg = this.fillConfiguration();
		if(!bErg){
			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
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
		//+++ Die für Hiberante konfigurierten Klassen hinzufügen
		//Merke: Wird eine Klasse ohne @Entity hinzugefügt, gibt es folgende Fehlermeldung: Exception in thread "main" org.hibernate.AnnotationException: No identifier specified for entity: use.thm.client.component.AreaCellTHM
		bReturn = addConfigurationAnnotatedClass(HexCell.class);
		bReturn = addConfigurationAnnotatedClass(AreaCell.class);
		
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
				this.getConfiguration().setProperty("hibernate.dialect","tryout.hibernate.SQLiteDialect" );
				this.getConfiguration().setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
				this.getConfiguration().setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
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
	
	/** Wird eine zu persisierende Klasse nicht der Konfiguration übergeben, kommt es z.B. zu folgender Fehlermeldung
	 *  Exception in thread "main" org.hibernate.MappingException: Unknown entity: use.thm.client.component.AreaCellTHM
	 * @param cls
	 * @return
	 * @throws ExceptionZZZ
	 */
	public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.getConfiguration().addAnnotatedClass(cls);
		return true;
	}
	/**Für die so hinzugefügte Klasse muss es eine XML Konfigurationsdatei geben.
	 * Ansonsten Fehler, z.B. für eine User.class : org.hibernate.MappingNotFoundException: resource: tryout/hibernate/User.hbm.xml not found
	 * 
	 * Wird eine zu persisierende Klasse nicht der Konfiguration übergeben, kommt es z.B. zu folgender Fehlermeldung
	 * Exception in thread "main" org.hibernate.MappingException: Unknown entity: use.thm.client.component.AreaCellTHM
	 * @param cls
	 * @return
	 * @throws ExceptionZZZ
	 */
	public boolean addConfigurationClass(Class cls)throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.getConfiguration().addClass(cls);
		return true;
	}
	
	//####### GETTER / SETTER
	public Configuration getConfiguration(){
		return this.cfgHibernate;
	}
	
	public Session getSession() throws ExceptionZZZ{
		Session objReturn = this.objSession;
		if(objReturn==null || objReturn.isOpen()==false){
			Configuration cfg = this.getConfiguration();
			if(cfg==null){
				ExceptionZZZ ez = new ExceptionZZZ("Configuration-Object not (yet) created.", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
			//deprecated: SessionFactory sf = cfg.buildSessionFactory();
			SessionFactory sf = cfg.buildSessionFactory(sr);
			objReturn = sf.openSession();
			this.objSession = objReturn;
		}
		return objReturn;
	}

	@SuppressWarnings("unchecked")
	public EntityManager getEntityManager(String sSchemaName)throws ExceptionZZZ{
		EntityManager objReturn = null;
		
		main:{
			try{
			if(StringZZZ.isEmpty(sSchemaName)){
				ExceptionZZZ ez = new ExceptionZZZ("No database/schema name provided", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			HashMapExtendedZZZ<String, EntityManager> hmEntityManager = this.hmEntityManager;
			if(hmEntityManager.containsKey(sSchemaName)){
				//wiederverwenden
				objReturn = (EntityManager) hmEntityManager.get(sSchemaName);
			}else{
				//Neu erstellen
				
				//TODO GOON: Dies in eine spezielle HibernateJpa...-Klasse auslagern.
				//TODO GOON: Properties für .createEntityManagerFactory(..., properties) übergeben!!!
				EntityManagerFactory emf = Persistence.createEntityManagerFactory(sSchemaName);
				objReturn = emf.createEntityManager();
				
				this.hmEntityManager.put(sSchemaName, objReturn);
			}
			
			}catch(PersistenceException pe){
				pe.printStackTrace();
				ExceptionZZZ ez = new ExceptionZZZ("PersistenceException: '" + pe.getMessage() + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return objReturn;
	}
}
