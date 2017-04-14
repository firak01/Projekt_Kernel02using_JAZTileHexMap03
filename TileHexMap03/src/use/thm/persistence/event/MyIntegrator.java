package use.thm.persistence.event;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.Metadata;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import use.thm.persistence.listener.TroopArmyListener;

/**Es muss in META-INF\services die Datei org.hibernate.integrator.spi.Integrator vorhanden sein.
 * Darin den Pfad zu dieser Klasse aufnehmen.
 * https://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html_single/#registering-listeners-example
 * @author Fritz Lindhauer
 *
 */
public class MyIntegrator implements Integrator {
	
        public void integrate(
            Configuration configuration,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );

            PersistListenerTHM listenerPersist = new PersistListenerTHM();
            eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
            
            PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
            eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert);
            
            //Weitere Listener: Merke, eine Listener Klasse kann auch mehrere Interfaces implementieren.
//            TroopArmyListener  listenerTroop = new TroopArmyListener();
//            eventListenerRegistry.setListeners(EventType.PRE_LOAD, listenerTroop);
//            eventListenerRegistry.prependListeners(EventType.PERSIST, listenerTroop);
//            eventListenerRegistry.prependListeners(EventType.PRE_INSERT, listenerTroop);
//            eventListenerRegistry.prependListeners(EventType.PRE_UPDATE, listenerTroop);
            //eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );
        } 

        public void disintegrate(SessionFactoryImplementor arg0, SessionFactoryServiceRegistry arg1) {        
        }

        public void integrate(MetadataImplementor arg0,SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {        
        }

}
