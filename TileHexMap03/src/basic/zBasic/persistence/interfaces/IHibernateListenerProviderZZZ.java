package basic.zBasic.persistence.interfaces;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.internal.SessionFactoryImpl;

import use.thm.persistence.event.IVetoFlagZZZ;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.event.SaveOrUpdateListenerTHM;
import basic.zBasic.ExceptionZZZ;

public interface IHibernateListenerProviderZZZ {
	public boolean fillListener() throws ExceptionZZZ;
	
	public PersistEventListener getPersistEventListener();
	public void setPersistEventListener(PersistEventListener objPersistEventListener);
	
	public PreInsertEventListener getPreInsertEventListener();
	public void setPreInsertEventListener(PreInsertEventListener objPreInsertEventListener);
	
	public SaveOrUpdateEventListener getSaveOrUpdateEventListener();
	public void setSaveOrUpdateEventListener(SaveOrUpdateEventListener objSaveOrUpdateEventListener);
}
