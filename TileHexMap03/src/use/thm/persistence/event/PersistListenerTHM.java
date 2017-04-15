package use.thm.persistence.event;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;

import basic.zBasic.ReflectCodeZZZ;

/**Wird nicht eingesetzt. Funktioniert wahrscheinlich nur mit JPA, d.h. bei einem reinen Hibernate session.save(xxx) wird das nicht aufgerufen.
 *  TODO GOON: 20170415: Mache dafür einen DEBUG TRYOUT, in dem mit JPA (also dem EntityManager) gearbeitet wird.
 * @author Fritz Lindhauer
 *
 */
public class PersistListenerTHM implements PersistEventListener {
	private static final long serialVersionUID = 1L;


	public void onPersist(PersistEvent arg0) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event...");
	}

	public void onPersist(PersistEvent arg0, Map arg1) throws HibernateException {		
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPersist Hibernate-Event 02...");
	}
}
