package tryout.zBasic.persistence.hibernate;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateSessionFactoryTomcatFactory implements ObjectFactory{
//FGL: 20171112: Das funktioniert. Die Methoden des Interface DataSource braucht es aber scheinbar nicht, weil ich die Ressource als <ResourceLink> in der Context.xml eingebunden habe:
                 // public class HibernateSessionFactoryTomcatFactory implements DataSource, ObjectFactory{
//Merke: Das ist keine Lösung: Die Tomcat Klasse wird hier nicht gefunden:
	            //public class HibernateSessionFactoryTomcatFactory extends org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory{
	
	private Configuration cfgHibernate = new Configuration(); 
	
	public Configuration getConfiguration(){
		return this.cfgHibernate;
	}
	
	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> environment) throws Exception {
		 SessionFactory objReturn = null;  
		 try{
			  Configuration cfg = this.getConfiguration();
			  
			  //Fehlermedlung: org.hibernate.HibernateException: Connection cannot be null when 'hibernate.dialect' not set
			  //ALSO: SETZE TESTWEISE ALLES MÖGLICHE, das funktioniert.
			  //TODO GOON 20171116: Kann man hier ggfs. aus meiner HibernateContextProviderZZZ - als Singleton - die Konfiguration verwenden.
			  cfg.configure("hibernate.cfg.xml");
			  		  		 
			  //So wird bisher die SessionFactory geholt. Merke: Das ist anlalog aus meiner HibernateContextProviderZZZ - Klasse
			 	                                                 //Methode: public SessionFactoryImpl getSessionFactory(){
		      ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
		      SessionFactory sf = cfg.buildSessionFactory(sr);
	
		     objReturn = (SessionFactoryImpl) sf;
		}catch(Exception ex){  
	        throw new javax.naming.NamingException(ex.getMessage());  
	     }  
	     return objReturn;
		 		 
		 /* original aus dem Beispiel:
		  * https://stackoverflow.com/questions/2475950/hibernate-sessionfactory-how-to-configure-jndi-in-tomcat
		  
	      RefAddr addr = null;  
	        
	      try{  
	         Enumeration addrs = ((Reference)(obj)).getAll();  
	              
	         while(addrs.hasMoreElements()){  
	            addr = (RefAddr) addrs.nextElement();  
	            if("configuration".equals((String)(addr.getType()))){  
	               sessionFactory = (new Configuration())  
	                    .configure((String)addr.getContent()).buildSessionFactory();  
	            }  
	         }  
	      }catch(Exception ex){  
	         throw new javax.naming.NamingException(ex.getMessage());  
	      }  
	      return sessionFactory; 
	  	*/
	}  
	}  
