package basic.zBasic.persistence.interfaces;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateContextProviderZZZ {
	public boolean fillConfiguration() throws ExceptionZZZ;	
	public boolean fillConfigurationGlobal();
	
	public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ;
	public boolean addConfigurationClass(Class cls)throws ExceptionZZZ;
	
	public Configuration getConfiguration();
	public Session getSession() throws ExceptionZZZ;
	
	public EntityManager getEntityManager(String sSchemaName)throws ExceptionZZZ;
}
