package tryout.zBasic.persistence.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.persistence.util.HibernateUtilByAnnotation;
import basic.zBasic.ExceptionZZZ;
import basic.zKernel.KernelZZZ;

public class TryoutGeneralDaoZZZ {
	public static void main(String[] args) {
		TryoutGeneralDaoZZZ objDebug = new TryoutGeneralDaoZZZ();
		objDebug.tryoutGetSessionFactoryAlternative();
	}
	
	public boolean tryoutGetSessionFactoryAlternative(){
		boolean bReturn = false;
		main:{
			try {				
				KernelZZZ objKernel = new KernelZZZ(); //Merke: Die Service Klasse selbst kann wohl nicht das KernelObjekt extenden!
				HibernateContextProviderSingletonTHM objContextHibernate;
				
				objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
				objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
								
				Session session = null;

				//############################
				//MERKE: DAS IST DER WEG wei bisher die SessionFactory geholt wird
				 //ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
			     //SessionFactory sf = cfg.buildSessionFactory(sr);
				//################################
				
				
				//### Ansatz Session-Factory über die Utility Funktion zu holen, die dann in der Hibernate Konfiguration nachsieht.
				//1. Versuch: In der Hibernate Configuration definiert
				//    Fehler: SessionFactory creation failed! javax.naming.NoInitialContextException: Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial
				
				//2. Versuch: In der Hibernate Configuration Erstellung per Java definiert
				objContextHibernate.getConfiguration().setProperty("hibernate.session_factory_name", "hibernate.session-factory.ServicePortal");				
				
				//Darin wird intern einen neue Configuration verwendet.
				//SessionFactory sf = HibernateUtilByAnnotation.getHibernateUtil().getSessionFactory();
				
				//Alternativer Ansatz, nimm die erstellte Konfiguration weiterhin
				Context jndiContext = (Context) new InitialContext();
				SessionFactory sf = (SessionFactory) jndiContext.lookup("hibernate.session-factory.ServicePortal");
								
				//TODO GOON 20171103: ABER DAS SCHEINT NUR ZU FUNKTIONIERN, WENN DIE ANWENDUNG IN EINEM SERVER (z.B. Tomcat läuft).
			      
				if(sf!=null){
					session = sf.openSession();
				}else{
					System.out.println("SessionFactory kann nicht erstellt werden. Tip: Alternativ den EntityManager verwenden oder ... (Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial). ");
				}
				
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;		
	}
}
