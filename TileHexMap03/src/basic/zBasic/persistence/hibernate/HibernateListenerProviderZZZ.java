package basic.zBasic.persistence.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;

import use.thm.persistence.event.IVetoFlagZZZ;
import use.thm.persistence.event.SaveOrUpdateListenerTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;

public abstract class HibernateListenerProviderZZZ implements IHibernateListenerProviderZZZ, IConstantZZZ{					
	private PersistEventListener objPersistEventListener = null;
	private PreInsertEventListener objPreInsertEventListener = null;	
	private SaveOrUpdateEventListener objSaveOrUpdateEventListener = null;
		
	public HibernateListenerProviderZZZ() throws ExceptionZZZ{
		//NEIN: Sonst wird die Konfiguration 2x gefüllt. fillConfiguration();
		//      Grund: HibernateContextPRoviderZZZ.fillConfiguration() ruft explizit fillConfiguration() auf.
	}
	
	public abstract boolean fillListener() throws ExceptionZZZ;

		
	public PersistEventListener getPersistEventListener(){
		return this.objPersistEventListener;
	}	
	public void setPersistEventListener(PersistEventListener objPersistEventListener) {
		this.objPersistEventListener = objPersistEventListener;		
	}
	
	public PreInsertEventListener getPreInsertEventListener(){
		return this.objPreInsertEventListener;
	}
	public void setPreInsertEventListener(PreInsertEventListener objPreInsertEventListener) {
		this.objPreInsertEventListener = objPreInsertEventListener;
	}
	
	public SaveOrUpdateEventListener getSaveOrUpdateEventListener(){
		return this.objSaveOrUpdateEventListener;
	}
	public void setSaveOrUpdateEventListener(SaveOrUpdateEventListener objSaveOrUpdateEventListener){
		this.objSaveOrUpdateEventListener = objSaveOrUpdateEventListener;
	}
}
