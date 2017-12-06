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
	//alte Version, jetzt ausgelagert public boolean fillConfigurationGlobal() throws ExceptionZZZ;
			
	public Configuration getConfiguration();
	//alte Version, jetzt ausgelagert public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ;
	//alte Version, jetzt ausgelagert public boolean addConfigurationClass(Class cls)throws ExceptionZZZ;
			
	public SessionFactoryImpl getSessionFactory();
	
	public Session getSession() throws ExceptionZZZ;    //Liefere immer eine neue Session
	public Session getSessionCurrent() throws ExceptionZZZ; //Versuche eine Session wiederzuverwenden, falls nicht vorhanden, liefere eine neue Session
	
	public EntityManager getEntityManager(String sSchemaName)throws ExceptionZZZ;
}
