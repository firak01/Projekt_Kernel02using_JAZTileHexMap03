package basic.zBasic.persistence.interfaces;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

import basic.zBasic.ExceptionZZZ;
/**Interface muss IHibernateContextProvider erweitern. 
 * Grund: Alle Klassen, die IHbernateContextProviderUserZZZ implementieren (z.B. all ...DaoZZZ-Klassen) 
 *            sollen weiterhni nur IHibernateContextProviderZZZ Objekt als private Property zu haben brauchen.
 * @author lindhaueradmin
 *
 */
public interface IHibernateContextProviderJndiZZZ extends IHibernateContextProviderZZZ {
	public String getLookupPathJndi();
	
	public String getContextJndiString();
	abstract void setContextJndiString(String sContextJndi); //sollte dann private sein.
	
	public SessionFactoryImpl getSessionFactoryByJndi(String sContextJndi);	
}
