package basic.zBasic.persistence.interfaces;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateContextProviderZZZ {		
	public IHibernateConfigurationProviderZZZ getConfigurationProviderObject()  throws ExceptionZZZ;
	public void setConfigurationProviderObject(IHibernateConfigurationProviderZZZ objHibernateConfigurationProvider);
	public boolean fillConfiguration() throws ExceptionZZZ;			
	public Configuration getConfiguration();
	
	public IHibernateListenerProviderZZZ getListenerProviderObject() throws ExceptionZZZ;
	public void setListenerProviderObject(IHibernateListenerProviderZZZ objHibernateListenerProvider);
	public boolean fillListener() throws ExceptionZZZ;
	
	public SessionFactoryImpl getSessionFactory();
	
	public Session getSession() throws ExceptionZZZ;    //Liefere immer eine neue Session
	public Session getSessionCurrent() throws ExceptionZZZ; //Versuche eine Session wiederzuverwenden, falls nicht vorhanden, liefere eine neue Session
	
	public EntityManager getEntityManager(String sSchemaName)throws ExceptionZZZ;
}
