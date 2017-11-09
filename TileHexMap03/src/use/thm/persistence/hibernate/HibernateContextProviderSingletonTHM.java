package use.thm.persistence.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.client.FrmMapSingletonTHM;
import use.thm.client.component.AreaCellTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.interceptor.HibernateInterceptorTHM;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttextValue;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.HibernateContextProviderZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;

public class HibernateContextProviderSingletonTHM extends HibernateContextProviderZZZ{
	private static HibernateContextProviderSingletonTHM objContextHibernate; //muss als Singleton static sein

	public static HibernateContextProviderSingletonTHM getInstance() throws ExceptionZZZ{
		if(objContextHibernate==null){
			objContextHibernate = new HibernateContextProviderSingletonTHM();
		}
		return objContextHibernate;		
	}
	
	public static  HibernateContextProviderSingletonTHM getInstance(KernelZZZ objKernel) throws ExceptionZZZ{
		if(objContextHibernate==null){
			objContextHibernate = new HibernateContextProviderSingletonTHM(objKernel);
		}
		return objContextHibernate;	
	}
			
	//Die Konstruktoren nun verbergen, wg. Singleton
	private HibernateContextProviderSingletonTHM() throws ExceptionZZZ{
		super();
	}
	
	//Die Konstruktoren nun verbergen, wg. Singleton
	private HibernateContextProviderSingletonTHM(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	
	/**Fülle die Configuration
	 * a) mit globalen Werten, z.B. Datenbankname, Dialekt
	 * b) mit den zu betrachtenden Klassen, entweder annotiert oder per eigener XML Datei.
	 * 
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public boolean fillConfiguration(Configuration cfg) throws ExceptionZZZ{
		boolean bReturn = false;
		
		bReturn = fillConfigurationGlobal(cfg);
		//+++ Die für Hiberante konfigurierten Klassen hinzufügen
		//Merke: Wird eine Klasse ohne @Entity hinzugefügt, gibt es folgende Fehlermeldung: Exception in thread "main" org.hibernate.AnnotationException: No identifier specified for entity: use.thm.client.component.AreaCellTHM
		//bReturn = addConfigurationAnnotatedClass(HexCell.class);
		//bReturn = addConfigurationAnnotatedClass(AreaCell.class);
		bReturn = addConfigurationAnnotatedClass(cfg, AreaCellOcean.class);
		bReturn = addConfigurationAnnotatedClass(cfg, AreaCellLand.class);
		
		//bReturn = addConfigurationAnnotatedClass(Tile.class);
		//bReturn = addConfigurationAnnotatedClass(Troop.class);
		bReturn = addConfigurationAnnotatedClass(cfg, TroopArmy.class);
		bReturn = addConfigurationAnnotatedClass(cfg, TroopFleet.class);
		
		bReturn = addConfigurationAnnotatedClass(cfg, TileDefaulttextValue.class);//wird aber nicht genutz. Nur Demonstrator
		
		bReturn = addConfigurationAnnotatedClass(cfg, Defaulttext.class); //Darf kein Entitiy sein, oder? 
		bReturn = addConfigurationAnnotatedClass(cfg, TextDefaulttext.class);
		bReturn = addConfigurationAnnotatedClass(cfg, TileDefaulttext.class);
		
		
		bReturn = addConfigurationAnnotatedClass(cfg, TroopArmyVariant.class);
		
		//FGL 20170409: Versuch Callbacks in Hibernate
		//this.getConfiguration().setListener("persist",new TroopArmyListener());
		
		
		return bReturn;
	}
	
	/** Fülle globale Werte in das Configuration Objekt, z.B. der Datenbankname, Dialekt, etc.
	 * 
	 */
	public boolean fillConfigurationGlobal(Configuration cfg){
				//TODO: Die hier verwendeten Werte aus der Kernel-Konfiguration auslesen.
				//Programmatisch das erstellen, das in der hibernate.cfg.xml Datei beschrieben steht.
				//Merke: Irgendwie funktioniert es nicht die Werte in der hibernate.cfg.xml Datei zu überschreiben.
		 		//			Darum muss z.B. hibernate.hbm2ddl.auto in der Konfigurationdatei auskommentiert werden, sonst ziehen hier die Änderungen nicht.
				cfg.setProperty("hiberate.show_sql", "true");
				cfg.setProperty("hiberate.format_sql", "true");
				cfg.setProperty("hibernate.dialect","basic.persistence.hibernate.SQLiteDialect" );
				cfg.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
				cfg.setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
				cfg.setProperty("hibernate.connection.username", "");
				cfg.setProperty("hibernate.connection.password", "");

				/*
				 * So the list of possible options are,
    validate: validate the schema, makes no changes to the database.
    update: update the schema.
    create: creates the schema, destroying previous data.
    create-drop: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.
				 */
				cfg.setProperty("hibernate.hbm2ddl.auto", "create"); //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
				//cfg.setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.
				cfg.setProperty("cache.provider_class", "org.hiberniate.cache.NoCacheProvider");
				cfg.setProperty("current_session_context_class", "thread");				
				return true;
	}
	
	/* Wenn man einen Interceptor verwendet, dann ist das für alle Entities des Projektes / der Session mit diesem HibernateContextProvider + KernelKeyZZZ aktiv. */ 
	@Override
	public Session declareSessionHibernateIntercepted(SessionFactoryImpl sf) {
		Session objReturn = null;
		SessionBuilder builder = sf.withOptions().interceptor(new HibernateInterceptorTHM());
        objReturn =     builder.openSession();	        
        return objReturn;
	}
	
	/*Versuch über das Hibernte Event System flexibler als mit dem Interceptor auf die Ereignisse reagieren zu können und ggfs. sogar auf mehrerer
	 * Merke: Das Event System hat sich von Hibernate 3 nach Hibernate 4 ziemlich geändert. 
	 *            UNTER HIBERNATE 4. mit einer INEGRATOR - Klasse arbeiten, 
	 *            die dann unter META-INF/services in der Datei org.hibernate.integrator.spi.Integrator bekannt gemacht werden muss */
	@Override
	public boolean declareConfigurationHibernateEvent(Configuration cfg) {

		//MERKE: Daas ist dann unter Hibernate 4, überflüssig....
//		ServiceRegistryBuilder registry = new ServiceRegistryBuilder();
//		registry.applySettings(cfg.getProperties());
//		ServiceRegistry serviceRegistry = registry.buildServiceRegistry();
//
//            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
//
//            PersistListenerTHM listener = new PersistListenerTHM();
//            eventListenerRegistry.appendListeners(EventType.PERSIST, listener);
            
            //UpdateBookEventListener listener = new UpdateBookEventListener(); 
//            eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, listener );
//            eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );
  
		return true;
	}	
	
	
}
