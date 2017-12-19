package use.thm.persistence.event;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import custom.zKernel.LogZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;

/**Es muss in META-INF\services die Datei org.hibernate.integrator.spi.Integrator vorhanden sein.
 * Darin den Pfad zu dieser Klasse aufnehmen.
 * https://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html_single/#registering-listeners-example
 * @author Fritz Lindhauer
 *
 */
public class MyIntegratorTHM implements Integrator, IKernelUserZZZ {
	private KernelZZZ objKernel;
	private LogZZZ objLog; 
	
        public void integrate(
            Configuration configuration,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );

            //Wird nicht ausgeführt, weder bei session.save noch bei session.update
            System.out.println("XXX In MyIntegratorTHM.java");     
            
            try {
            	//Problem: Man kann diesen Integrator nicht durch Änderung des Classpath ausblenden. 
                //         D.h. auch Projekte, die dieses Projekt nutzen, werden den Integrator aufrufen und damit die Listener registrieren.
                //         Idee deshalb: Hole ein Array der Listener aus der KernelHibernateKonfigurations-Klasse
            	
            	KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
            	//ALSO: Wenn dieses Singleton nicht mit einem Kernel-Objekt ausgestattet ist, dann ist es in diesem Projekt nicht definiert worden. 
            	//      Dann darf man auch dessen Listener nicht an den den EventLisenerRegistry übergeben.
            	
            	if(!objKernel.getFlagZ("init")){            		           
	            	IHibernateContextProviderZZZ objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
	            	           
					//Nun die darin erstellten Listener hier an eventListenerRegistry übergeben.
	            	//Merke: Statt die Listener zu ersetzen könnt man ggfs. auch welche anhängen ////eventListenerRegistry.appendListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
					IHibernateListenerProviderZZZ objListenerProvider = objContextHibernate.getListenerProviderObject();
					if(objListenerProvider!=null){
						PersistEventListener listenerPersist = objListenerProvider.getPersistEventListener(); //Das ist PersistListenerTHM
						if(listenerPersist!= null) eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
						
						PreInsertEventListener listenerPreInsert = objListenerProvider.getPreInsertEventListener();//Das ist PreInsertListenerTHM
						if(listenerPreInsert != null) eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert); 
						
						SaveOrUpdateEventListener listenerSaveUpdate = objListenerProvider.getSaveOrUpdateEventListener(); //Das ist SaveOrUpdateListenerTHM
						if(listenerSaveUpdate != null) eventListenerRegistry.setListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
						
						
					    //Wird nicht ausgeführt, weder bei session.save noch bei session.update
				        //PreUpdateListenerTHM listenerPreUpdate = new PreUpdateListenerTHM();
				        //eventListenerRegistry.prependListeners(EventType.PRE_UPDATE, listenerPreUpdate);
				        
						//Weitere Listener: Merke, eine Listener Klasse kann auch mehrere Interfaces implementieren. 
			            //Anbei eine kleine Auswahl weiterer möglicher Listener Events. Jeder Event entspricht einem Interface.
//			            eventListenerRegistry.setListeners(EventType.PRE_LOAD, listener);
//			            eventListenerRegistry.prependListeners(EventType.PRE_INSERT, listener);
			            //eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );			          
					}
            	}//end if objKernel.getFlagZ("init")            	
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 

        public void disintegrate(SessionFactoryImplementor arg0, SessionFactoryServiceRegistry arg1) {        
        }

        public void integrate(MetadataImplementor arg0,SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {
        	System.out.println("YYY In MyIntegrator.java");
        }

				
		//#######################################
		//Methods implemented by Interface
		public KernelZZZ getKernelObject() {
			return this.objKernel;
		}
		public void setKernelObject(KernelZZZ objKernel) {
			this.objKernel = objKernel;
		}
			
		public LogZZZ getLogObject() {
			return this.objLog;
		}
		public void setLogObject(LogZZZ objLog) {
			this.objLog = objLog;
		}

}
