package use.thm.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

import use.thm.persistence.interceptor.HibernateInterceptorTHM;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttextValue;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zKernel.KernelZZZ;

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
	 *            UNTER HIBERNATE 4. mit einer INTEGRATOR - Klasse arbeiten, 
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

	
	@Override
	//Hier wird dann das spezielle Konfigurationsobjekt, für die Spezielle Konfiguration verwendet.
	//Merke: Wenn eine spezielle JNDI - Konfiguration verwendet werden soll, dann von dieser aktuellen Klasse erben (Klasse B),
	//        eine andere Konfigurationsklasse erstellen. Und dann in Klasse B untenstehende Methode überschreiben.
	public IHibernateConfigurationProviderZZZ getConfigurationProviderObject() throws ExceptionZZZ {
		IHibernateConfigurationProviderZZZ objReturn = super.getConfigurationProviderObject(); //nutze hier die "Speicherung in der Elternklasse"		
		if(objReturn==null){
			objReturn = new HibernateConfigurationProviderTHM();
			this.setConfigurationProviderObject(objReturn);
		}
		return objReturn;
	}
	
	@Override
	//Hier wird dann das spezielle Konfigurationsobjekt, für die Spezielle Konfiguration verwendet.
	public IHibernateListenerProviderZZZ getListenerProviderObject() throws ExceptionZZZ {
		IHibernateListenerProviderZZZ objReturn = super.getListenerProviderObject(); //nutze hier die "Speicherung in der Elternklasse"		
		if(objReturn==null){
			objReturn = new HibernateListenerProviderTHM();
			this.setListenerProviderObject(objReturn);
		}
		return objReturn;
	}

	public boolean hasSessionFactory_Open() {
		return super.hasSessionFactory_open();
	}
}
