package basic.persistence.util;



import java.io.Serializable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
//import org.hibernate.classic.Session;
//FGL: Verwende diese Session:
import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import basic.zBasic.ReflectCodeZZZ;

/**
 * Singleton, holds the SessionFactory
 * 
 * @author DKatzberg
 */
public class HibernateUtilByAnnotation implements Serializable{
	
	private static final long serialVersionUID = 1L;

	//singleton
	private static HibernateUtilByAnnotation hibernateUtil;
	
	//holds config elements
	private SessionFactory sessionFactory;
	private Context jndiContext;
	private Statistics statistics;
	private Session currentSession;
	
	
	/* Singleton Constructor */
	private HibernateUtilByAnnotation() {
		try {
			//Create the SessionFactory
			new Configuration().configure().buildSessionFactory();
			
			jndiContext = (Context) new InitialContext();
			
			//FGL 20171120:Mitlerweile habe ich meine eigenen JNDI-Ressource bereitgestellt
			//             und meine eigenen SessionFactory Klasse. 
			//             Diese wird in den WebService Applikationen genutzt, um eine Session Factory per JNDI zu erstellen.

			//<!-- FGL 20171115. Merke: Laut Doku muss für eine Web Applikation das sqlite-jdbc-(version).jar in das Verzeichnis (TOMCAT_HOME)/lib gelegt werden (, um JNDI -also 'java naming und directory" - nutzen zu können). 
			//                          Da bei 'JNI' -also 'java native ...'- keine Applikation mehr als 1x vorhanden sein darf bekommt man in der Maven 2 Konfiguration (pom.xml) ohne die "provided" Angabe angeblich die Fehlermeldung 'no SQLite library found'. -->
			
			
			//FGL 20171121: Diese JNDI Quelle gibt es nicht.
			//              Falls diese Methode aufgerufen wird, dann soll man zumindest an einer Stelle im Log darauf aufmerksam gemacht werden.
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "ACHTUNG: VERALTETE ORIGINAL METHODE. JNDI QUELLE WIRD ES WOHK KAUM GEBEN.");
			sessionFactory = (SessionFactory) jndiContext.lookup("hibernate.session-factory.ServicePortal");
			
//			sessionFactory = (SessionFactory) jndiContext.lookup("hibernate.session-factory.serviceportal_usage");
			
			statistics = this.sessionFactory.getStatistics();
			statistics.setStatisticsEnabled(true);
			
		}
		catch (Throwable ex) {
			System.err.println("SessionFactory creation failed! " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	/**
	 * Close the SessionFactory.
	 */
	public void shutDown(){
		getSessionFactory().close();
	}
	
	/* Getter / Setter */
	public static HibernateUtilByAnnotation getHibernateUtil() {
		
		//singleton pattern, with optimize synchronization performance
		if(hibernateUtil==null){
			synchronized(HibernateUtilByAnnotation.class){
				//2nd check in synchronized area
				if(hibernateUtil==null){
					hibernateUtil = new HibernateUtilByAnnotation();
				}
			}
		}
		
		return hibernateUtil;
	}
	
	public SessionFactory getSessionFactory() {
		
//		System.out.println(this.statistics.getSessionOpenCount()+"--"+this.statistics.getSessionCloseCount());
		
		return sessionFactory;
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}
}
