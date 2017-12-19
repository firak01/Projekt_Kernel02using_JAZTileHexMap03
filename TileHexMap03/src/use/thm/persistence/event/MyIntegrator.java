package use.thm.persistence.event;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import custom.zKernel.LogZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;

/**Es muss in META-INF\services die Datei org.hibernate.integrator.spi.Integrator vorhanden sein.
 * Darin den Pfad zu dieser Klasse aufnehmen.
 * https://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html_single/#registering-listeners-example
 * @author Fritz Lindhauer
 *
 */
public class MyIntegrator implements Integrator, IKernelUserZZZ {
	private KernelZZZ objKernel;
	private LogZZZ objLog; 
	
        public void integrate(
            Configuration configuration,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );

            //Wird nicht ausgeführt, weder bei session.save noch bei session.update
            System.out.println("XXX In MyIntegrator.java");     
            
            try {
				IHibernateContextProviderZZZ objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());

				//TODO GOON 20171219 - Nun die darin erstellten Listener hier an eventListenerRegistry übergeben.
				IHibernateListenerProviderZZZ objListenerProvider = objContextHibernate.getListenerProviderObject();				
				PersistEventListener listenerPersist = objListenerProvider.getPersistEventListener(); //Das ist PersistListenerTHM
				eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
				
				PreInsertEventListener listenerPreInsert = objListenerProvider.getPreInsertEventListener();//Das ist PreInsertListenerTHM
				eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert); 
				
				SaveOrUpdateEventListener listenerSaveUpdate = objListenerProvider.getSaveOrUpdateEventListener(); //Das ist SaveOrUpdateListenerTHM
				eventListenerRegistry.setListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);		          
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            
            //TODO GOON 20171218: Man kann diesen Integrator nicht durch Änderung des Classpath ausblenden. 
            //                    D.h. auch Projekte, die dieses Projekt nutzen, werden den Integartor aufrufen und damit die Listener registrieren.
            //                    Idee deshalb: Hole ein Array der Listener aus der KernelHibernateKonfigurations-Klasse
            //Wird nicht ausgeführt, weder bei session.save noch bei session.update
//            PersistListenerTHM listenerPersist = new PersistListenerTHM(); //Funktioniert wahrscheinlich nur unter JPA. Mit Hibernate session.save(xxx) wird das nicht ausgeführt.
//            eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
            
//            PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
//            eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert); 
            
           //Wird nicht ausgeführt, weder bei session.save noch bei session.update
          //PreUpdateListenerTHM listenerPreUpdate = new PreUpdateListenerTHM();
          //eventListenerRegistry.prependListeners(EventType.PRE_UPDATE, listenerPreUpdate);
            
     //das klappt       SaveOrUpdateListenerTHM listenerSaveUpdate = new SaveOrUpdateListenerTHM();
            //eventListenerRegistry.appendListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
     //das klappt       eventListenerRegistry.setListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
            
            
            //Weitere Listener: Merke, eine Listener Klasse kann auch mehrere Interfaces implementieren. 
            //Anbei eine kleine Auswahl weiterer möglicher Listener Events. Jeder Event entspricht einem Interface.
//            eventListenerRegistry.setListeners(EventType.PRE_LOAD, listener);
//            eventListenerRegistry.prependListeners(EventType.PRE_INSERT, listener);
            //eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );
            
        } 

        public void disintegrate(SessionFactoryImplementor arg0, SessionFactoryServiceRegistry arg1) {        
        }

        public void integrate(MetadataImplementor arg0,SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {
        	System.out.println("YYY In MyIntegrator.java");
        }

				
		//#######################################
		//Methods implemented by Interface
		public KernelZZZ getKernelObject() {
			return this.objKernel;
		}
		public void setKernelObject(KernelZZZ objKernel) {
			this.objKernel = objKernel;
		}
			
		public LogZZZ getLogObject() {
			return this.objLog;
		}
		public void setLogObject(LogZZZ objLog) {
			this.objLog = objLog;
		}

}
