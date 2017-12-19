package basic.zBasic.persistence.hibernate;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.client.FrmMapSingletonTHM;
import use.thm.persistence.event.MyIntegratorTHM;
import use.thm.persistence.event.PersistListenerTHM;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.hibernate.HibernateConfigurationProviderTHM;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interceptor.HibernateInterceptorTHM;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.listener.TroopArmyListener;
import use.thm.persistence.model.TroopArmy;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderJndiZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
/**Hier wird die Konfiguration programmatisch aufgebaut
 * und die globalen Hibernate Objekte sind hierüber überall verfügbar.
 * 
 * Die Erweiterung ist, dass diese Klasse dafür sorgt, dass eine Hibernate SessionFactory per JNDI erzeugt wird.
 * Sie ist also für WebServices gedacht.
 * 
 */
public abstract class HibernateContextProviderJndiZZZ  extends HibernateContextProviderZZZ implements IHibernateContextProviderJndiZZZ {
	//Session NICHT hier speichern. Der Grund ist, dass es pro Transaction nur 1 Session geben darf
//	http://stackoverflow.com/questions/37602501/hibernate-2nd-transaction-in-same-session-doesnt-save-modified-object
//	Thanks to @Michal, the problem is solved. In my base DAO class, I had the session as an instance variable, which screwed things up. Not entirely sure why exactly, but I also agree that one transaction = one session.
//	So the solution was to make the session a method variable and basically always ask the session factory for the session.
	private Session objSession = null; //Wird zwar gespeichert, aber beim neuen Holen per Getter auf NULL gesetzt und komplett neu geholt.
	
		//	20170415: Statt die Session zu speichern die SessionFactory speichern.
		private SessionFactoryImpl objSessionFactory = null;
		
		private String sContextJndi = null; 
	
		private HibernateContextProviderJndiZZZ() throws ExceptionZZZ{
			super();
//			boolean bErg = this.fillConfiguration();  //wird schon in der Elternklasse gemacht
//			if(!bErg){
//				ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
		}
		
		public HibernateContextProviderJndiZZZ(String sContextJndi) throws ExceptionZZZ{
			super();
			this.setContextJndiString(sContextJndi);
//			boolean bErg = this.fillConfiguration();  //wird schon in der Elternklasse gemacht
//			if(!bErg){
//				ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//				throw ez;
//			}
		}
		
		
	private HibernateContextProviderJndiZZZ(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
//		boolean bErg = this.fillConfiguration(); //wird schon in der Elternklasse gemacht
//		if(!bErg){
//			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//			throw ez;
//		}
	}
	
	public HibernateContextProviderJndiZZZ(KernelZZZ objKernel, String sContextJndi) throws ExceptionZZZ{
		this(objKernel);
		this.setContextJndiString(sContextJndi);
//		boolean bErg = this.fillConfiguration(); //wird schon in der Elternklasse gemacht
//		if(!bErg){
//			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//			throw ez;
//		}
	}
	
	public SessionFactoryImpl getSessionFactory(){
		return this.getSessionFactoryByJndi();
	}
	
	//TODO GOON 20171206: Hier die SessionFactory anders holen als für lokale Datenbanken, nämlich wie in den WebServices....
	/**HOLE DIE SESSIONFACTORY PER JNDI:
		Merke: DAS FUNKTIONIERT NUR, WENN DIE ANWENDUNG IN EINEM SERVER (z.B. Tomcat läuft). */
	public SessionFactoryImpl getSessionFactoryByJndi(){
		SessionFactoryImpl objReturn = null;
		main:{
			try {
				objReturn = this.objSessionFactory;
				if(objReturn==null){
								
				String sContextJndiPath=this.getContextJndiLookupPath();
				
				//############################
				//MERKE: DAS IST DER WEG wei bisher die SessionFactory direkt in einer Standalone J2SE Anwendung geholt wird
				  //Configuration cfg = this.getConfiguration();
			      //ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
			      //SessionFactory sf = cfg.buildSessionFactory(sr);
				//################################
			
				//xxxx Da wird mit normalen JDBC Datenbanenk als DataSource gearbeitet. Das ist mit Hibernate so nicht möglich
				//holds config elements
				//DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/yourdb");
				//Connection conn = ds.getConnection();
											
				//### Ansatz Session-Factory über die Utility Funktion zu holen, die dann in der Hibernate Konfiguration nachsieht.
				//1. Versuch: In der Hibernate Configuration definiert
				//    Fehler: SessionFactory creation failed! javax.naming.NoInitialContextException: Need to specify class name in environment or system property, or as an applet parameter, or in an application resource file:  java.naming.factory.initial
				
				//2. Versuch: In der Hibernate Configuration Erstellung per Java definiert
				//Die hier genannte SessionFactory muss tatsächlich als Klasse an der Stelle existieren.
											
				//3. Versuch:
				Context jndiContext = (Context) new InitialContext();
				//Betzemeier Original:  //SessionFactory sf = HibernateUtilByAnnotation.getHibernateUtil().getSessionFactory();
				//Betzemeier Original:  Hier wird JNDI für eine fest vorgegebeen Klasse verwendet. //SessionFactory sf = (SessionFactory) jndiContext.lookup("hibernate.session-factory.ServicePortal");
				
				//Mein Ansatz: Verwende eine eigene SessionFactory und nimm die erstellte Konfiguration (aus HibernateContextProviderTHM) weiterhin und überschreibe diese ggfs. aus der Konfiguration.
				//Merke: Damit diese Resource bekannt ist im Web Service, muss er neu gebaut werden. Nur dann ist die web.xml aktuell genug.
				//Merke: java:comp/env/ ist der JNDI "Basis" Pfad, der vorangestellt werden muss. Das ist also falsch: //SessionFactory sf = (SessionFactory) jndiContext.lookup("java:jdbc/ServicePortal");
				//Merke: /jdbc/ServicePortal ist in der context.xml im <RessourceLink>-Tag definiert UND in der web.xml im <resource-env-ref>-Tag
				SessionFactory sf = (SessionFactory) jndiContext.lookup(sContextJndiPath);
				objReturn = (SessionFactoryImpl) sf;
				
				//Merke: Bei einer nagelneuen SessionFactory keine Session setzen.
				
				}else{
					objReturn = this.objSessionFactory;

					//Wenn diese SessionFactory geschlossen ist, neu aufmachen.
					if(objReturn.isClosed()){
						String sContextJndiPath=this.getContextJndiLookupPath();
						
						Context jndiContext = (Context) new InitialContext();
						SessionFactory sf = (SessionFactory) jndiContext.lookup(sContextJndiPath);
						
						objReturn = (SessionFactoryImpl) sf;
						this.objSessionFactory = objReturn;
						//this.setSessionFactoryWithNewSession(objReturn); //Merke: Das hier machen. Dann ist diese Anweisung in der Singelton Klasse nicht mehr notwendig.
					}				
				}//end if objReturn=null
				
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return objReturn;
	}
	/**Das wird auch dafür benutzt, um eine neue SessionFactory zu erzwingen, fall sich die Hibernate Configuration geändert hat.
	 * Z.B. wenn die Datenbank nicht mehr neu aufgebaut werden soll, sondern für Folgeabfragen weiterverwendet werden soll.
	 * In diesem Fall setzt man die SessionFactory auf NULL.
	 * 
	 * Merke: Eine neue Session wird dem HibernateContextProviderZZZ ebenfalls zur Verfügung gestellt.
	 *        Grund: Sonst wird das xyzDaoZZZ gezwungen weiter in den Elternklassen nach einer getSession() Methode zu suchen und dann wird etwas mit HibernateAnotationUtility herangezogen.
	 * 
	 * @param objSessionFactory
	 */
	public void setSessionFactoryWithNewSession(SessionFactoryImpl objSessionFactory){
		if(this.objSessionFactory!=null){						
			if(this.objSessionFactory.isClosed()){
			}else{
				
				//Sicherheitshalber alle bestehenden Sessions schliessen
				Session ss = this.objSessionFactory.getCurrentSession();
				if(ss!=null){
					if(ss.isOpen()){
						
						//TODO GOON 20171208: Fehlermeldung, dass ss.clear() nur mit offenen Transactions durchgeführt werden darf...
						ss.clear();
						ss.close();					
					}
				}
				
				
				if(this.objSession!=null){
					if(this.objSession.isOpen()){						
						this.objSession.clear();
						this.objSession.close();
					}
				}
				this.setSession(null);  //session darf nicht gespeichert werden 1 Transaktion ==> 1 Session 
								
				this.objSessionFactory.close(); //Die alte SessionFactory schliessen.
			}
		}
		
		//Die neue SessionFactory setzen.
		this.objSessionFactory = objSessionFactory;
		if(this.objSessionFactory!=null){
			this.objSession = this.objSessionFactory.openSession(); //Explizit eine neue Session anbieten. Sonst wird das xyzDaoZZZ gezwungen weiter in den Elternklassen nach einer getSession() Methode zu suchen und dann wird etwas mit HibernateUtilityByAnnotiation herangezogen. Der darin verwendete JNDI-String ist aber nicht vorhanden.
		}
	}
	
	/*
	 * Notwendig, um z.B. bei der Nutzung von JNDI (in Webservices) nicht jedes mal eine neue SessionFactory zu erstellen 
	 * - per eigens zur Verfügung gestellter SessionFactory (Klasse HibernateSessionFactoryTomcatFactory) 
	 */
	public boolean hasSessionFactory(){
		if(this.objSessionFactory==null){
			return false;
		}else{
			return true;
		}
	}
	
	/*
	 * Notwendig, um z.B. bei der Nutzung von JNDI (in Webservices) nicht jedes mal eine neue SessionFactory zu erstellen 
	 * - per eigens zur Verfügung gestellter SessionFactory (Klasse HibernateSessionFactoryTomcatFactory) 
	 */
	public boolean hasSessionFactory_open(){
		boolean bReturn = false;
		main:{
			boolean bErg = this.hasSessionFactory();
			if(!bErg) break main;
			
			bErg = this.objSessionFactory.isClosed(); 
			if(bErg) break main;
			
			bReturn = true;
		}
		return bReturn;
	}
	
	//################### GETTER / SETTER	
	public Session getSession() throws ExceptionZZZ{
		Session objReturn = null;
		this.setSession(null); //Session objReturn = this.objSession; //!!! Session darf nicht als Variable gespeichert und wiederverwendet werden. Der Grund ist 1 Transaktion ==> 1 Session.
		
			Configuration cfg = this.getConfiguration();
			if(cfg==null){
				ExceptionZZZ ez = new ExceptionZZZ("Configuration-Object not (yet) created.", iERROR_PROPERTY_EMPTY, this, ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//TODO GOON: prüfe, ob die Datei vorhanden ist. Davon abhängig machen, ob die Datenbank neu erstellt oder nur upgedatet werden soll.
			//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.			
//			boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);
//			if(bDbExists){
//				System.out.println("Datenbank existiert als Datei.");
//				this.objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert.				
//			}else{
//				System.out.println("Datenbank exisitert nicht als Datei");
//				this.objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
//			}
			
			
			//############	
			SessionFactoryImpl sf= this.getSessionFactory();
						
			//########### Hibernaten Event Listener / Callback Methoden zur Verfügung stellen.
			//MERKE 20170412: In Hibernate 4 ist dies nur auf folgendem Weg möglich
			//1. Mache einen Integrator, also eine Klasse mit ...  implements Integrator
			//2. Mache diesen Integrator als Service bekannt. Dazu erzeuge eine Datei org.hibernate.integrator.spi.Integrator im Verzeichnis META-INF/services
			//                                                                   Trage in der Datei den Namen der Klasse, inklusive Package-Pfad ein.
			//3. Registriere den Listerner innerhalb der integrate(...)-Methode der neu erstellten Integrator-Klasse
			// PreInsertListenerTHM listenerPreInsert = new PreInsertListenerTHM();
            // eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert);
			//4. Merke, dass scheinbar nicht immer alle Events aufgerufen werden. Ich habe z.B. noch keine PERSIST - Event aufrufen können, wenn ich .save() mache.
			
	
			//###########  A) INTERCEPTOR im jeweiligen Projekt / den jeweiligen HibernateContextProvider + KernelKeyZZZ zur Verfügung zu stellen. 
			objReturn = this.declareSessionHibernateIntercepted((SessionFactoryImpl) sf);

			//###########  B) also wenn kein Session mit Interceptro durch den SessionBuilder gebaut wurde ....  
			if(objReturn==null) objReturn = sf.openSession();					        			
			
			this.objSession = objReturn; //session wird zwar gespeichert, aber nicht dauerhaft. Darf nicht gespeichert werden 1 Transaktion ==> 1 Session. Wird daher immer neu geholt. 
		return objReturn;
	}
	
	 //session darf nicht gespeichert werden 1 Transaktion ==> 1 Session, darum wird in getSession() immer eine neue erzeugt. 
	public void setSession(Session objSession){
		if(this.objSession!=null){
			if(this.objSession.isOpen()){
				this.objSession.clear();//Die Reihenfolge ist wichtig: Erst clear(), dann close()
				this.objSession.close();
			}
		}
		this.objSession=objSession;
	}
	
	public Session getSessionCurrent() throws ExceptionZZZ{
		if(this.objSession==null){
			this.objSession = this.getSession();
		}else{
			//Exception in thread "main" org.hibernate.TransactionException: nested transactions not supported
			//darum ggfs. vorhandene Transaction der Session schliessen.
//NEIN, das bewirkt ggfs. eine Endlosschleife.
//			Transaction tr = this.objSession.getTransaction();
//			if(tr!=null){
//				tr.commit();
//			}
			//DIE TRANSAKTION MUSS ALSO VORHER GESCHLOSSEN WORDEN SEIN.
		}
		return this.objSession;
	}
	
	public void setContextJndiString(String sContextJndi){
		this.sContextJndi = sContextJndi;
	}
	public String getContextJndiString(){
		return this.sContextJndi;
	}
	
	public String getContextJndiLookupPath(){
		String sReturn = null;
		main:{
			String sContextJndi = this.getContextJndiString();
			if(StringZZZ.isEmpty(sContextJndi)){
				
			}else{
				sReturn = "java:comp/env/" + sContextJndi;
			}
			
		}//end main:
		return sReturn;
	}

	//############## Abstracte Methoden, die auf jeden Fall überschrieben werden müssen.
	//....
	public SessionFactoryImpl getSessionFactoryByJndi(String sContextJndi) {
		this.setContextJndiString(sContextJndi);
		return this.getSessionFactoryByJndi();
	}	
}
