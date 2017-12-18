package use.thm.persistence.hibernate;

import org.hibernate.event.spi.SaveOrUpdateEventListener;

import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.event.SaveOrUpdateListenerTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.hibernate.HibernateListenerProviderZZZ;

public class HibernateListenerProviderTHM extends HibernateListenerProviderZZZ {
    public HibernateListenerProviderTHM() throws ExceptionZZZ{
    	super();
    }
	
	@Override
	public boolean fillListener() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			
			//Ausgelagert aus MyIntegrator.java
			PersistListenerTHM listenerPersist = new PersistListenerTHM(); //Funktioniert wahrscheinlich nur unter JPA. Mit Hibernate session.save(xxx) wird das nicht ausgeführt.
			this.setPersistEventListener(listenerPersist);
			
			PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
			this.setPreInsertEventListener(listenerPreInsert);
			
			//PROBLEME.... 20171219... vielleicht das Registrieren am Service hier auch schon reinnehmen 
			//SaveOrUpdateEventListener listenerSaveUpdate = new SaveOrUpdateListenerTHM();
			//this.setSaveOrUpdateEventListener(listenerSaveUpdate);
			
			bReturn = true;
		}
		return bReturn;
	}


	

	

	

}
