package use.thm.persistence.event;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;

import basic.zBasic.ReflectCodeZZZ;

public class PersistListenerTHM implements PersistEventListener {
    // this is the single method defined by the LoadEventListener interface
//    public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType)
//            throws HibernateException {
//        if ( !MySecurity.isAuthorized( event.getEntityClassName(), event.getEntityId() ) ) {
//            throw MySecurityException("Unauthorized access");
//        }
//    }

	public void onPersist(PersistEvent arg0) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event...");
	}

	public void onPersist(PersistEvent arg0, Map arg1)
			throws HibernateException {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event 02...");
	}
}
