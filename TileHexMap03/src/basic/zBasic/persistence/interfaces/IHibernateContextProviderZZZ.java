package basic.zBasic.persistence.interfaces;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateContextProviderZZZ {
	public boolean fillConfiguration() throws ExceptionZZZ;	
	public boolean fillConfigurationGlobal() throws ExceptionZZZ;
	
	public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ;
	public boolean addConfigurationClass(Class cls)throws ExceptionZZZ;
	
	public Configuration getConfiguration();
	public SessionFactoryImpl getSessionFactory();
	
	public Session getSession() throws ExceptionZZZ;    //Liefere immer eine neue Session
	public Session getSessionCurrent() throws ExceptionZZZ; //Versuche eine Session wiederzuverwenden, falls nicht vorhanden, liefere eine neue Session
	
	public EntityManager getEntityManager(String sSchemaName)throws ExceptionZZZ;
}
