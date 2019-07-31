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
import basic.zBasic.ExceptionZZZ;
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
		try{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vor dem Einsetzen einer Armee.");
		}catch(ExceptionZZZ ez){
			String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
			System.out.println(sError);
		}
	}
	
	@PostPersist
	public void nachEinsetzen(TroopArmy objTroop){
		try{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Nach dem Einsetzen einer Armee.");
		}catch(ExceptionZZZ ez){
			String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
			System.out.println(sError);
		}
	}

	public boolean onPreUpdate(PreUpdateEvent event) {
		try{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onPreUpdate");
		}catch(ExceptionZZZ ez){
			String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
			System.out.println(sError);
		}
		return true;
	}

	public boolean onPreInsert(PreInsertEvent event) {
		try{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onPreInsert");
		}catch(ExceptionZZZ ez){
			String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
			System.out.println(sError);
		}
		return true;
	}


	//PreLoad-Klappt
	public void onPreLoad(PreLoadEvent event) {
		try{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": onPreLoad");
		}catch(ExceptionZZZ ez){
			String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
			System.out.println(sError);
		}
	}
	
	
}
