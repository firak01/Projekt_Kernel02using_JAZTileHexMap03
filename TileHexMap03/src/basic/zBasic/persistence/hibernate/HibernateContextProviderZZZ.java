package basic.zBasic.persistence.hibernate;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import use.thm.client.FrmMapSingletonTHM;
import use.thm.persistence.event.MyIntegratorTHM;
import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.interceptor.HibernateInterceptorTHM;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderUserZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderUserZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
/**Hier wird die Konfiguration programmatisch aufgebaut
 * und die globalen Hibernate Objekte sind hierüber überall verfügbar.
 * 
 */
public abstract class HibernateContextProviderZZZ  extends KernelUseObjectZZZ implements IHibernateContextProviderZZZ, IHibernateConfigurationProviderUserZZZ, IHibernateListenerProviderUserZZZ {	
	IHibernateConfigurationProviderZZZ objConfigurationProvider = null;
	IHibernateListenerProviderZZZ objListenerProvider = null;
	
	//Session NICHT hier speichern. Der Grund ist, dass es pro Transaction nur 1 Session geben darf
//	http://stackoverflow.com/questions/37602501/hibernate-2nd-transaction-in-same-session-doesnt-save-modified-object
//	Thanks to @Michal, the problem is solved. In my base DAO class, I had the session as an instance variable, which screwed things up. Not entirely sure why exactly, but I also agree that one transaction = one session.
//	So the solution was to make the session a method variable and basically always ask the session factory for the session.
	private Session objSession = null; //Wird zwar gespeichert, aber beim neuen Holen per Getter auf NULL gesetzt und komplett neu geholt.
	
	//20170415: Statt die Session zu speichern die SessionFactory speichern.
	private SessionFactoryImpl objSessionFactory = null;
	
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
			
			bErg = this.fillListener();
			if(!bErg){
				ExceptionZZZ ez = new ExceptionZZZ("Listener not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
		}
		
	public HibernateContextProviderZZZ(IKernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
		boolean bErg = this.fillConfiguration();
		if(!bErg){
			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		bErg = this.fillListener();
		if(!bErg){
			ExceptionZZZ ez = new ExceptionZZZ("Listener not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
	}
	
	public SessionFactoryImpl getSessionFactory(){
		SessionFactoryImpl objReturn = this.objSessionFactory;
		if(objReturn==null){
			  Configuration cfg = this.getConfiguration();
			
		      ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
		      SessionFactory sf = cfg.buildSessionFactory(sr);
			
		      this.objSessionFactory = (SessionFactoryImpl) sf;
			 objReturn = (SessionFactoryImpl) sf;	
			 //Merke: bei einer nagelneuen SessionFactory hier nicht die Session setzen.
			 
		}else{			
			if(objReturn.isClosed()){
				
				//LÖSUNG: Nicht eine neue SessionFactory erstellen. Das Öffnen der Session öffnet auch die SessionFactory.
				//https://stackoverflow.com/questions/15165681/how-to-reopen-hibernate-session-after-session-was-closed
				objReturn.openSession();
				
				
				if(objReturn.isClosed()){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Trotz Öffnen der Session ist die SessionFactory noch geschlossen. Erstelle neue SessionFactory.");
				//DAS MACHT BEIM CONNECTION POOLING PROBLEME
				 Configuration cfg = this.getConfiguration();
					
			      ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
			      SessionFactory sf = cfg.buildSessionFactory(sr);
							     
				 objReturn = (SessionFactoryImpl) sf;					 
												
				//AUCH DAS IST NICHT GUT.
				// this.setSessionFactoryWithNewSession(objReturn); //Merke: Das hier machen. Dann ist diese Anweisung in der Singelton Klasse nicht mehr notwendig.
				}
			}
		}		
		this.objSessionFactory=objReturn;
		return objReturn;
	}
	
	
	/** TODO GOON: Das habe ich noch nicht ausprobiert.
	 *                         Tolles CodeSnippet aus: https://stackoverflow.com/questions/15298437/hibernate-closing-the-session-factory-does-not-close-the-c3p0-connection-pool
	 * @param factory
	 * @author lindhaueradmin, 11.11.2018, 11:44:28
	 */
	private void closeSessionFactory(SessionFactory factory) { 
		   if(factory instanceof SessionFactoryImpl) {
		      SessionFactoryImpl sf = (SessionFactoryImpl)factory;
		      ConnectionProvider conn = sf.getConnectionProvider();
		      if(conn instanceof C3P0ConnectionProvider) { 
		        ((C3P0ConnectionProvider)conn).close(); 
		      }
		   }
		   factory.close();
		}
	
	
	/**Das wird auch dafür benutzt, um eine neue SessionFactory zu erzwingen, fall sich die Hibernate Configuration geändert hat.
	 * Z.B. wenn die Datenbank nicht mehr neu aufgebaut werden soll, sondern für Folgeabfragen weiterverwendet werden soll.
	 * In diesem Fall setzt man die SessionFactory auf NULL.
	 * 
	 * Merke: Eine neue Session wird dem HibernateContextProviderZZZ ebenfalls zur Verfügung gestellt.
	 *        Grund: Sonst wird das xyzDaoZZZ gezwungen weiter in den Elternklassen nach einer getSession() Methode zu suchen und dann wird etwas mit HibernateAnotationUtility herangezogen.
	 * 
	 * @param objSessionFactory
	 */
	public void setSessionFactoryWithNewSession(SessionFactoryImpl objSessionFactory){
		if(this.objSessionFactory!=null){						
			if(this.objSessionFactory.isClosed()){
			}else{
				
				//Sicherheitshalber alle bestehenden Sessions schliessen
				Session ss = this.objSessionFactory.getCurrentSession();
				if(ss!=null){
					if(ss.isOpen()){
						ss.clear();
						ss.close();					
					}
				}
				
				
				if(this.objSession!=null){
					if(this.objSession.isOpen()){						
						this.objSession.clear();
						this.objSession.close();
					}
				}
				this.setSession(null);  //session darf nicht gespeichert werden 1 Transaktion ==> 1 Session 
								
				this.objSessionFactory.close(); //Die alte SessionFactory schliessen.
			}
		}
		
		//Die neue SessionFactory setzen.
		this.objSessionFactory = objSessionFactory;
		if(this.objSessionFactory!=null){
			this.objSession = this.objSessionFactory.openSession(); //Explizit eine neue Session anbieten. Sonst wird das xyzDaoZZZ gezwungen weiter in den Elternklassen nach einer getSession() Methode zu suchen und dann wird etwas mit HibernateUtilityByAnnotiation herangezogen. Der darin verwendete JNDI-String ist aber nicht vorhanden.
		}
	}
	
	/*
	 * Notwendig, um z.B. bei der Nutzung von JNDI (in Webservices) nicht jedes mal eine neue SessionFactory zu erstellen 
	 * - per eigens zur Verfügung gestellter SessionFactory (Klasse HibernateSessionFactoryTomcatFactory) 
	 */
	public boolean hasSessionFactory(){
		if(this.objSessionFactory==null){
			return false;
		}else{
			return true;
		}
	}
	
	/*
	 * Notwendig, um z.B. bei der Nutzung von JNDI (in Webservices) nicht jedes mal eine neue SessionFactory zu erstellen 
	 * - per eigens zur Verfügung gestellter SessionFactory (Klasse HibernateSessionFactoryTomcatFactory) 
	 */
	public boolean hasSessionFactory_open(){
		boolean bReturn = false;
		main:{
			boolean bErg = this.hasSessionFactory();
			if(!bErg) break main;
			
			bErg = this.objSessionFactory.isClosed(); 
			if(bErg) break main;
			
			bReturn = true;
		}
		return bReturn;
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
				IHibernateConfigurationProviderZZZ objHibernateCfgProvider = this.getConfigurationProviderObject();
				Configuration objCfg = objHibernateCfgProvider.getConfiguration();
				
				//20171213 VERSUCH mache hier die Mappings fertig.... dann sind vielleicht die Klassen auch in den Properties?				
//				Mappings m = objCfg.createMappings();
//				objCfg.buildMappings(); klappt letztendlich nicht
				
				
				Properties objProps = objCfg.getProperties();
				
				//Konvertiere die Properties der Configuation in eine Map, die dann der EntityManagerFactory übergeben werden kann.
				Map<String, String> hmCfgProps = new HashMap<String, String>();
				Enumeration<?> eCfgProps = objProps.propertyNames();
				while(eCfgProps.hasMoreElements()){
					String s = (String) eCfgProps.nextElement();
					hmCfgProps.put(s, objProps.getProperty(s));
				}
				
				
				
				/* Das ist vielleicht ein Lösungsansatz zur Übergabe der gemappten Klassen an die EntityManagerFactory?
				 * 
I have a similar problem and solved it with Hibernate's Integrator SPI:

@Override
public void integrate(Configuration configuration,
    SessionFactoryImplementor sessionFactory,
    SessionFactoryServiceRegistry serviceRegistry) {

    configuration.addAnnotatedClass(MyEntity.class);
    configuration.buildMappings();
}
				 */
				//objCfg.buildMappings();
				//Iterator<PersistentClass>  it = objCfg.getClassMappings();
				
				
				/*ODER ALTERNATIVE ?
				 *  Ejb3Configuration config = new Ejb3Configuration();
        return (HibernateEntityManagerFactory) config.addProperties(hibernateProperties()).
                addAnnotatedClass(User.class).
                buildEntityManagerFactory();
				 */
				
				//TODO GOON: 20171212 fülle  die Werte aus dem Hibernate - Configuration - Objekt hier rein.
				//Ohne das müssen für die Verwendung des EntityManagers alle Konfigurationen in der Datei hibernate.cfg.xml hinterlegt werden.
				//https://stackoverflow.com/questions/30124826/creating-entitymanagerfactory-from-hibernate-configuration				
				//EntityManagerFactory emf = Persistence.createEntityManagerFactory(sSchemaName);
				EntityManagerFactory emf = Persistence.createEntityManagerFactory(sSchemaName, hmCfgProps);				
				this.setEntityManagerFactory(emf);//TODO Goon: IM Destruktor: if(this.getEntityManager()!=null) this.getEntityManager().close();
												
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
		//alte Version, jetzt ausgelagert return this.cfgHibernate;
		Configuration objReturn = null;
		try {
			objReturn = this.getConfigurationProviderObject().getConfiguration();
		} catch (ExceptionZZZ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objReturn;
	}
	
	public Session getSession() throws ExceptionZZZ{
		Session objReturn = null;
		if(this.objSession!=null){
			objReturn = this.objSession;
		}else{		
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
			SessionFactoryImpl sf= this.getSessionFactory();
						
			//########### Hibernaten Event Listener / Callback Methoden zur Verfügung stellen.
			//MERKE 20170412: In Hibernate 4 ist dies nur auf folgendem Weg möglich
			//1. Mache einen Integrator, also eine Klasse mit ...  implements Integrator
			//2. Mache diesen Integrator als Service bekannt. Dazu erzeuge eine Datei org.hibernate.integrator.spi.Integrator im Verzeichnis META-INF/services
			//                                                                   Trage in der Datei den Namen der Klasse, inklusive Package-Pfad ein.
			//3. Registriere den Listerner innerhalb der integrate(...)-Methode der neu erstellten Integrator-Klasse
			// PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
            // eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert);
			//4. Merke, dass scheinbar nicht immer alle Events aufgerufen werden. Ich habe z.B. noch keine PERSIST - Event aufrufen können, wenn ich .save() mache.
			
	
			//###########  A) INTERCEPTOR im jeweiligen Projekt / den jeweiligen HibernateContextProvider + KernelKeyZZZ zur Verfügung zu stellen. 
			objReturn = this.declareSessionHibernateIntercepted((SessionFactoryImpl) sf);

			//###########  B) also wenn kein Session mit Interceptro durch den SessionBuilder gebaut wurde ....  
			if(objReturn==null) objReturn = sf.openSession();					        			
			
			this.objSession = objReturn; //session wird zwar gespeichert, aber nicht dauerhaft. Darf nicht gespeichert werden 1 Transaktion ==> 1 Session. Wird daher immer neu geholt.
		}
		return objReturn;
	}
	
	 //session darf nicht gespeichert werden 1 Transaktion ==> 1 Session, darum wird in getSession() immer eine neue erzeugt. 
	public void setSession(Session objSession){
		if(this.objSession!=null){
			if(this.objSession.isOpen()){
				this.objSession.clear();//Die Reihenfolge ist wichtig: Erst clear(), dann close()
				this.objSession.close();
			}
		}
		this.objSession=objSession;
	}	
	public Session getSessionOpen() throws ExceptionZZZ{		
		if(this.objSession==null){
			this.objSession = this.getSession();
			if(!this.objSession.isOpen()){
				this.objSession = this.getSessionFactory().openSession();
			}
		}else{
			if(!this.objSession.isOpen()){
				this.objSession = this.getSessionFactory().openSession();
			}
		}
		return this.objSession;
	}
	public Session getSessionCurrent() throws ExceptionZZZ{
		if(this.objSession==null){
			this.objSession = this.getSession();
		}else{
			if(!this.objSession.isOpen()){
				this.objSession = this.getSessionFactory().openSession();
			}
		}
		return this.objSession;
	}
	
	public Session getSessionNew() throws ExceptionZZZ{
		Session objReturn = null;		
		this.setSession(null); //Session objReturn = this.objSession; //!!! Session darf nicht als Variable gespeichert und wiederverwendet werden. Der Grund ist 1 Transaktion ==> 1 Session.
		objReturn = this.getSession();			
		return objReturn;
	}
	
	//Wichtig, um z.B. nach dem Einlesen der Karte (TileHexMap-Projekt) die SQLite Datenbank wieder freizugeben, so dass andere Backendoperationen mit weiteren Programmen durchgeführt werden können.
		public void closeAll() throws ExceptionZZZ{
			if(this.objSession!=null){
				if(this.objSession.isOpen()){
					this.objSession.close();
					if(this.objSession.isOpen()){
						this.objSession.clear();
					}
				}				
			}
			//this.getSessionFactory().close();//Wenn man nur die SessionFactory schliesst gibt es anschliessend z.B. beim Bewegen eines Spielsteins einen "unknown Service requested" Fehler. 		
		}
	
	//+++++ Configuration ++++++++
	public boolean fillConfiguration() throws ExceptionZZZ{
		if(this.getConfigurationProviderObject()!=null){
			return this.getConfigurationProviderObject().fillConfiguration();
		}else{
			return false;
		}
	}
		

	public IHibernateConfigurationProviderZZZ getConfigurationProviderObject() throws ExceptionZZZ{
		return this.objConfigurationProvider;
	}
	
	//Hier wird dann das spezielle Konfigurationsobjekt, für die Spezielle Konfiguration verwendet.
	//Merke: Wenn z.B. eine spezielle JNDI - Konfiguration verwendet werden soll, dann von dieser aktuellen Klasse erben (Klasse B),
	//        eine andere Konfigurationsklasse erstellen. Und dann in Klasse B untenstehende Methode überschreiben.
	public void setConfigurationProviderObject(IHibernateConfigurationProviderZZZ objHibernateConfiguration) {
		this.objConfigurationProvider = objHibernateConfiguration;
	}
	
	
	
	//++++ Listener +++++++++
	public boolean fillListener() throws ExceptionZZZ{
		if(this.getListenerProviderObject()!=null){
			return this.getListenerProviderObject().fillListener();
		}else{
			return false;
		}
	}
	
	public IHibernateListenerProviderZZZ getListenerProviderObject() throws ExceptionZZZ{
		return this.objListenerProvider;
	}
	
	//Hier wird dann das spezielle Konfigurationsobjekt, für die Spezielle Konfiguration verwendet.
	//Merke: Wenn z.B. eine spezielle JNDI - Konfiguration verwendet werden soll, dann von dieser aktuellen Klasse erben (Klasse B),
	//        eine andere Konfigurationsklasse erstellen. Und dann in Klasse B untenstehende Methode überschreiben.
	public void setListenerProviderObject(IHibernateListenerProviderZZZ objHibernateListener) {
		this.objListenerProvider = objHibernateListener;
	}
	
	//############## Abstracte Methoden, die auf jeden Fall überschrieben werden müssen.
	//Verwende intern den SessionBuilder um eine Session zurückzuliefern, die einen Interceptor benutzt.
	public abstract Session declareSessionHibernateIntercepted(SessionFactoryImpl sf);

	//Füge Events hinzu, die auf dem Hibernate Event System basieren.
	public abstract boolean declareConfigurationHibernateEvent(Configuration cfg);
	
}
