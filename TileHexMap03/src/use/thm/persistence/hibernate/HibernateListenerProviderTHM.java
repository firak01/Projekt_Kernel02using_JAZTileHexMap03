package use.thm.persistence.hibernate;

import org.hibernate.event.spi.SaveOrUpdateEventListener;

import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.event.SaveOrUpdateListenerTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.persistence.hibernate.HibernateListenerProviderZZZ;
import basic.zKernel.IKernelZZZ;

public class HibernateListenerProviderTHM extends HibernateListenerProviderZZZ {
    public HibernateListenerProviderTHM() throws ExceptionZZZ{
    	super();
    }
	
	@Override
	public boolean fillListener() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			
			IKernelZZZ objKernel = KernelSingletonTHM.getInstance();
			
			//Ausgelagert aus MyIntegratorTHM.java
			PersistListenerTHM listenerPersist = new PersistListenerTHM(); //Funktioniert wahrscheinlich nur unter JPA. Mit Hibernate session.save(xxx) wird das nicht ausgeführt.
			listenerPersist.setKernelObject(objKernel);
			this.setPersistEventListener(listenerPersist);
			
			PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
			listenerPreInsert.setKernelObject(objKernel);
			this.setPreInsertEventListener(listenerPreInsert);
						
			SaveOrUpdateListenerTHM listenerSaveUpdate = new SaveOrUpdateListenerTHM();
			listenerSaveUpdate.setKernelObject(objKernel);
			this.setSaveOrUpdateEventListener(listenerSaveUpdate);
			
			bReturn = true;
		}
		return bReturn;
	}


	

	

	

}
