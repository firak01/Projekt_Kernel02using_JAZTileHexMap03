package use.thm.persistence.hibernate;

import javax.persistence.EntityManager;

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
import basic.zBasic.persistence.hibernate.HibernateContextProviderJndiZZZ;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.abstractList.HashMapIndexedZZZ;
import basic.zKernel.KernelZZZ;

public class HibernateContextProviderJndiSingletonTHM extends HibernateContextProviderJndiZZZ{
	private static HibernateContextProviderJndiSingletonTHM objContextHibernate; //muss als Singleton static sein
	//in eine Elternklasse verschoben ... private IHibernateConfigurationProviderZZZ objConfigurationProvider;
	
	//Verschiedene Contexte hier verwalten
	private static HashMapIndexedZZZ<String, HibernateContextProviderJndiSingletonTHM> hmContextProvider = new HashMapIndexedZZZ<String, HibernateContextProviderJndiSingletonTHM>();

	public static  HibernateContextProviderJndiSingletonTHM getInstance(String sContextJndi) throws ExceptionZZZ{
		if(objContextHibernate==null){			
			objContextHibernate = new HibernateContextProviderJndiSingletonTHM(sContextJndi);
		}else{
			//HashMapIndexedZZZ<String, HibernateContextProviderJndiZZZ> hmContextProvider = hmContextProvider;
			if(hmContextProvider.containsKey(sContextJndi)){
				objContextHibernate = (HibernateContextProviderJndiSingletonTHM) hmContextProvider.get(sContextJndi);			
			}
			if(objContextHibernate==null){
				objContextHibernate = new HibernateContextProviderJndiSingletonTHM(sContextJndi);
			}
		}
		return objContextHibernate;	
	}
	
	public static  HibernateContextProviderJndiSingletonTHM getInstance(KernelZZZ objKernel, String sContextJndi) throws ExceptionZZZ{
		if(objContextHibernate==null){			
			objContextHibernate = new HibernateContextProviderJndiSingletonTHM(objKernel, sContextJndi);
		}else{
			//HashMapIndexedZZZ<String, HibernateContextProviderJndiZZZ> hmContextProvider = hmContextProvider;
			if(hmContextProvider.containsKey(sContextJndi)){
				objContextHibernate = (HibernateContextProviderJndiSingletonTHM) hmContextProvider.get(sContextJndi);				
			}
			if(objContextHibernate==null){
				objContextHibernate = new HibernateContextProviderJndiSingletonTHM(objKernel, sContextJndi);
			}
		}
		return objContextHibernate;	
	}
	
	//########################################################
	//Die Konstruktoren nun verbergen, wg. Singleton, sind schon in der Elternkalsse verborgen
//	private HibernateContextProviderJndiSingletonTHM() throws ExceptionZZZ{
//		//super();
//	}
//	
//	private HibernateContextProviderJndiSingletonTHM(KernelZZZ objKernel) throws ExceptionZZZ{
//		//super(objKernel);
//	}
	
	//Die richtigen Konstruktoren
	public HibernateContextProviderJndiSingletonTHM(String sContextJndi) throws ExceptionZZZ{
		super(sContextJndi);
		KernelZZZ objKernel = new KernelZZZ();
		HibernateContextProviderJndiNew_(objKernel, sContextJndi);		
	}
	
	public HibernateContextProviderJndiSingletonTHM(KernelZZZ objKernel, String sContextJndi) throws ExceptionZZZ{
		super(objKernel, sContextJndi);
		HibernateContextProviderJndiNew_(objKernel, sContextJndi);					
	}
	
	private boolean HibernateContextProviderJndiNew_(KernelZZZ objKernel, String sContextJndi) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			
			//+++ Packe dieses neue Objekt in die HashMap, mit dem sContextJndi als Schlüssel
			HashMapIndexedZZZ<String, HibernateContextProviderJndiSingletonTHM> hm = this.getContextProviderMap();
			hm.put(sContextJndi, this);
			
		}//end main
		return bReturn;
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
	
	//#### GETTER / SETTER ###########################
	public HashMapIndexedZZZ<String, HibernateContextProviderJndiSingletonTHM> getContextProviderMap(){
		return this.hmContextProvider;
	}

    //### AUS Interfaces ##############################
	@Override
	public IHibernateConfigurationProviderZZZ getConfigurationProviderObject() throws ExceptionZZZ {
		IHibernateConfigurationProviderZZZ objReturn = super.getConfigurationProviderObject(); //nutze hier die "Speicherung in der Elternklasse".
		if(objReturn==null){
			objReturn = new HibernateConfigurationProviderJndiTHM();
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
}
