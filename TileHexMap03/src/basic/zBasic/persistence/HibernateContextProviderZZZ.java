package basic.zBasic.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.persistence.event.MyIntegrator;
import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.interceptor.HibernateInterceptorTHM;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public abstract class HibernateContextProviderZZZ  extends KernelUseObjectZZZ implements IHibernateContextProviderZZZ {
	/**Hier wird die Konfiguration programmatisch aufgebaut
	 * und die globalen Hibernate Objekte sind hierüber überall verfügbar.
	 * 
	 */
	private Configuration cfgHibernate = new Configuration();
	private Session objSession = null;
	
	//Über die EntityManagerFactory erstellte EntityManager werden in dieser Hashmap verwaltet: hm("Name des Schemas/der Datenbank") = objEntityManager;
	private	HashMapExtendedZZZ<String, EntityManager> hmEntityManager = new HashMapExtendedZZZ<String, EntityManager>();
	private EntityManagerFactory objEntityManagerFactory;
		
		
		
		
		public HibernateContextProviderZZZ() throws ExceptionZZZ{
			super();
			boolean bErg = this.fillConfiguration();
			if(!bErg){
				ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		
	public HibernateContextProviderZZZ(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
		boolean bErg = this.fillConfiguration();
		if(!bErg){
			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
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
			HashMapExtendedZZZ<String, EntityManager> hmEntityManager = this.getEntityManagerMap();
			if(hmEntityManager.containsKey(sSchemaName)){
				//Fall: Wiederverwenden
				objReturn = (EntityManager) hmEntityManager.get(sSchemaName);
			}else{
				//Fall: Neu erstellen
				
				//TODO GOON: Dies in eine spezielle HibernateJpa...-Klasse auslagern.
				//TODO GOON: Properties für .createEntityManagerFactory(..., properties) übergeben!!!
				EntityManagerFactory emf = Persistence.createEntityManagerFactory(sSchemaName);
				this.setEntityManagerFactory(emf);//TODO: IM Destruktor: if(this.getEntityManager()!=null) this.getEntityManager().close();
				
				objReturn = emf.createEntityManager();
				this.getEntityManagerMap().put(sSchemaName, objReturn);
			}
			
			}catch(PersistenceException pe){
				pe.printStackTrace();
				ExceptionZZZ ez = new ExceptionZZZ("PersistenceException: '" + pe.getMessage() + "'", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}//end main:
		return objReturn;
	}
	
	/** Wird eine zu persisierende Klasse nicht der Konfiguration übergeben, kommt es z.B. zu folgender Fehlermeldung
	 *  Exception in thread "main" org.hibernate.MappingException: Unknown entity: use.thm.client.component.AreaCellTHM
	 * @param cls
	 * @return
	 * @throws ExceptionZZZ
	 */
	public boolean addConfigurationAnnotatedClass(Configuration cfg, Class cls) throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		cfg.addAnnotatedClass(cls);
		return true;
	}
	public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ{
		if(cls==null){
			ExceptionZZZ ez = new ExceptionZZZ("Class-Object not passed.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		return this.addConfigurationAnnotatedClass(	this.getConfiguration(), cls);
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

	
	
	
	//################### GETTER / SETTER
	public EntityManagerFactory getEntityManagerFactory(){
		return this.objEntityManagerFactory;
	}
	public void setEntityManagerFactory(EntityManagerFactory emf){
		this.objEntityManagerFactory = emf;
	}
	public HashMapExtendedZZZ<String, EntityManager> getEntityManagerMap(){
		return this.hmEntityManager;
	}
	
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
			
			//TODO GOON: prüfe, ob die Datei vorhanden ist. Davon abhängig machen, ob die Datenbank neu erstellt oder nur upgedatet werden soll.
			//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.			
//			boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);
//			if(bDbExists){
//				System.out.println("Datenbank existiert als Datei.");
//				this.objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert.				
//			}else{
//				System.out.println("Datenbank exisitert nicht als Datei");
//				this.objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
//			}
			
			
//############
	        ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();
			SessionFactory sf = cfg.buildSessionFactory(sr);
			
			//########### Hibernaten Event Listener / Callback Methoden zur Verfügung stellen.
			//MERKE 20170412: In Hibernate 4 ist dies nur auf folgendem Weg möglich
			//1. Mache einen Integrator, also eine Klasse mit ...  implements Integrator
			//2. Mache diesen Integrator als Service bekannt. Dazu erzeuge eine Datei org.hibernate.integrator.spi.Integrator im Verzeichnis META-INF/services
			//                                                                   Trage in der Datei den Namen der Klasse, inklusive Package-Pfad ein.
			//3. Registriere den Listerner innerhalb der integrate(...)-Methode der neu erstellten Integrator-Klasse
			// PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
            // eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert);
			//4. Merke, dass scheinbar nicht immer alle Events aufgerufen werden. Ich habe z.B. noch keine PERSIST - Event aufrufen können, wenn ich .save() mache.
			
	
			//###########  INTERCEPTOR im jeweiligen Projekt / den jeweiligen HibernateContextProvider + KernelKeyZZZ zur Verfügung zu stellen. 
			objReturn = this.declareSessionHibernateIntercepted((SessionFactoryImpl) sf);
			//######################

			//also das ist ohne SessionBuilder ....  
			if(objReturn==null) 	objReturn = sf.openSession();					        
			
			this.objSession = objReturn;
		}
		return objReturn;
	}
	
	public boolean fillConfiguration() throws ExceptionZZZ{
		return this.fillConfiguration(this.getConfiguration());
	}
	
	public boolean fillConfigurationGlobal() throws ExceptionZZZ{
		return this.fillConfigurationGlobal(this.getConfiguration());
	}

	//############## Abstracte Methoden, die auf jeden Fall überschrieben werden müssen.
	public abstract boolean fillConfiguration(Configuration cfg) throws ExceptionZZZ ;

	public abstract boolean fillConfigurationGlobal(Configuration cfg) throws ExceptionZZZ;

	//Verwende intern den SessionBuilder um eine Session zurückzuliefern, die einen Interceptor benutzt.
	public abstract Session declareSessionHibernateIntercepted(SessionFactoryImpl sf);

	//Füge Events hinzu, die auf dem Hibernate Event System basieren.
	public abstract boolean declareConfigurationHibernateEvent(Configuration cfg);
	
}
