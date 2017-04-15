package use.thm.persistence.event;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.Metadata;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import custom.zKernel.LogZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import use.thm.persistence.listener.TroopArmyListener;

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

            PersistListenerTHM listenerPersist = new PersistListenerTHM(); //Funktioniert wahrscheinlich nur unter JPA. Mit Hibernate session.save(xxx) wird das nicht ausgeführt.
            eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
            
            PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
            eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert);
            
            //Weitere Listener: Merke, eine Listener Klasse kann auch mehrere Interfaces implementieren. 
            //Anbei eine kleine Auswahl weiterer möglicher Listener Events. Jeder Event entspricht einem Interface.
//            eventListenerRegistry.setListeners(EventType.PRE_LOAD, listener);
//            eventListenerRegistry.prependListeners(EventType.PRE_INSERT, listener);
//            eventListenerRegistry.prependListeners(EventType.PRE_UPDATE, listener);
            //eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );
        } 

        public void disintegrate(SessionFactoryImplementor arg0, SessionFactoryServiceRegistry arg1) {        
        }

        public void integrate(MetadataImplementor arg0,SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {        
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
