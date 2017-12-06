package basic.zBasic.persistence.interfaces;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateConfigurationProviderZZZ {
	public boolean fillConfiguration() throws ExceptionZZZ;	
	abstract boolean fillConfigurationGlobal() throws ExceptionZZZ;
	abstract boolean fillConfigurationLocalDb() throws ExceptionZZZ; //nicht bei Jndi
	abstract boolean fillConfigurationMapping() throws ExceptionZZZ;
	
	public boolean addConfigurationAnnotatedClass(Class cls) throws ExceptionZZZ;
	public boolean addConfigurationClass(Class cls)throws ExceptionZZZ;
	
	
	public Configuration getConfiguration();
}
