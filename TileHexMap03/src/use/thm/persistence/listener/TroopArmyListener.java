package use.thm.persistence.listener;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.type.Type;

import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ReflectCodeZZZ;

/**20170409: Funktioniert nicht..... Hibernate Weg.
 *                Sollte mit JPA und EntityManagerFactory funktionieren, aber... auf diesem Weg funktioniert die automatische Schlüsselwertberechnung nicht mehr,
 *                zumindest unter SQLITE Datenbank.
 *                TODO: Falls die Datenbank mal ausgetauscht wird, dies wieder versuchen zu nutzen.
 * 
 * @author Fritz Lindhauer
 *
 */
public class TroopArmyListener implements PersistEventListener, PreInsertEventListener,PreUpdateEventListener,PreLoadEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PostPersist
	public void onPersist(PersistEvent event) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Ideale Stelle für validierung....");
	}
	
	
@PostPersist
	public void onPersist(PersistEvent event, Map createdAlready)
			throws HibernateException {
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Ideale Stelle für validierung 02....");
	}    
	
	@PrePersist
	public void vorEinsetzen(TroopArmy objTroop){
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vor dem Einsetzen einer Armee.");
	}
	
	@PostPersist
	public void nachEinsetzen(TroopArmy objTroop){
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Nach dem Einsetzen einer Armee.");
	}


	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onPreUpdate");
		return true;
	}


	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onPreInsert");
		return true;
	}


	//PreLoad-Klappt
	@Override
	public void onPreLoad(PreLoadEvent event) {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onPreLoad");
		
	}
	
	
}
