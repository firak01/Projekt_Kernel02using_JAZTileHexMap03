package use.thm.persistence.event;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;

import basic.zBasic.ReflectCodeZZZ;

public class PersistListenerTHM implements PersistEventListener {
	private static final long serialVersionUID = 1L;


	public void onPersist(PersistEvent arg0) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event...");
	}

	public void onPersist(PersistEvent arg0, Map arg1) throws HibernateException {		
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event 02...");
	}
}
