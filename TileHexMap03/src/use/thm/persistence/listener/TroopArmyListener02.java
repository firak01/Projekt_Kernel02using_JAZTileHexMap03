package use.thm.persistence.listener;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.type.Type;

import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ReflectCodeZZZ;

/**20170409: Funktioniert noch nicht..... Hibernate Weg:
 * Ich denke es liegt daran, dass dieser Listener an der Sesion registriert werden muss.Ausserdem muss derListener EmptyListener extenden.
 * SessionBuilder builder = factory.withOptions().interceptor(new ConsoleLogInterceptor());
 * 
 * @author Fritz Lindhauer
 *
 */
public class TroopArmyListener02 implements PersistEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public void onPersist(PersistEvent event) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Ideale Stelle für validierung....");
	}

	public void onPersist(PersistEvent event, Map createdAlready)
			throws HibernateException {
		// TODO Auto-generated method stub
		
	}    
	
}
