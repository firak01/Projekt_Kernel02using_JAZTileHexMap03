package use.thm.persistence.event;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.Metadata;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**Es muss in META-INF\services die Datei org.hibernate.integrator.spi.Integrator vorhanden sein.
 * Darin den Pfad zu dieser Klasse aufnehmen.
 * @author Fritz Lindhauer
 *
 */
public class MyIntegrator implements Integrator {
	
        public void integrate(
            Configuration configuration,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );

            PersistListenerTHM listener = new PersistListenerTHM();
            eventListenerRegistry.appendListeners(EventType.PERSIST, listener);
            
            //UpdateBookEventListener listener = new UpdateBookEventListener(); 

            //eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, listener );
            //eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );
        } 

        public void disintegrate(SessionFactoryImplementor arg0, SessionFactoryServiceRegistry arg1) {        
        }

        public void integrate(MetadataImplementor arg0,SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {        
        }

}
