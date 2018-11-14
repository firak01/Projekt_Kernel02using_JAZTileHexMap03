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
	
	public Session getSession() throws ExceptionZZZ;         //Öffne falls möglich eine geschlossen Session über die SessionFactory
	public Session getSessionCurrent() throws ExceptionZZZ; //Versuche eine Session wiederzuverwenden, falls nicht vorhanden, liefere eine neue Session
	public Session getSessionNew() throws ExceptionZZZ; //Liefere immer eine neue Session
	
	public EntityManager getEntityManager(String sSchemaName)throws ExceptionZZZ;
	
	public boolean hasSessionFactory();
	public boolean hasSessionFactory_open();
	public void setSessionFactoryWithNewSession(SessionFactoryImpl sf);
	
	public void closeAll() throws ExceptionZZZ; //Wichtig, um z.B. nach dem Einlesen der Karte (TileHexMap-Projekt) die SQLite Datenbank wieder freizugeben, so dass andere Backendoperationen mit weiteren Programmen durchgeführt werden können.
}
