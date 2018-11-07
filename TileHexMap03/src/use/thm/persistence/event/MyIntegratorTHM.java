package use.thm.persistence.event;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.internal.EventListenerRegistryImpl;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import use.thm.persistence.hibernate.HibernateContextProviderJndiSingletonTHM;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.util.HibernateUtilTHM;
import custom.zKernel.LogZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelZZZ;

/**Es muss in META-INF\services die Datei org.hibernate.integrator.spi.Integrator vorhanden sein.
 * Darin den Pfad zu dieser Klasse aufnehmen.
 * https://docs.jboss.org/hibernate/orm/4.2/devguide/en-US/html_single/#registering-listeners-example
 * @author Fritz Lindhauer
 *
 */
public class MyIntegratorTHM implements Integrator, IKernelUserZZZ {
	private IKernelZZZ objKernel;
	private LogZZZ objLog; 
	
        public void integrate(
            Configuration configuration,
            SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {

            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );

            System.out.println("XXX In MyIntegratorTHM.java");                 
            try {
            	//Problem: Man kann diesen Integrator nicht durch Änderung des Classpath ausblenden. 
                //         D.h. auch Projekte, die dieses Projekt nutzen, werden den Integrator aufrufen und damit die Listener registrieren.
                //         Idee deshalb: Hole ein Array der Listener aus der KernelHibernateKonfigurations-Klasse
            	
            	KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
            	this.setKernelObject(objKernel);//Da es keinen Konstruktor mit der Übergabe des Kernel Objekts gibt.
            	
            	//ALSO: Wenn dieses Singleton nicht mit einem Kernel-Objekt ausgestattet ist, dann ist es in diesem Projekt nicht definiert worden. 
            	//      Dann darf man auch dessen Listener nicht an den den EventLisenerRegistry übergeben.
            	
            	if(!objKernel.getFlagZ("init")){ 
            		System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Listener werden jetzt gesetzt.");
            			            	
            		//IHibernateContextProviderZZZ objContextHibernate = HibernateContextProviderJndiSingletonTHM.getInstance();
            		IHibernateContextProviderZZZ objHibernateContext = HibernateUtilTHM.getHibernateContextProviderUsed(this.getKernelObject());
	            	          
					//Nun die darin erstellten Listener hier an eventListenerRegistry übergeben.
	            	//Merke: Statt die Listener zu ersetzen könnt man ggfs. auch welche anhängen ////eventListenerRegistry.appendListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
					IHibernateListenerProviderZZZ objListenerProvider = objHibernateContext.getListenerProviderObject();            		
					if(objListenerProvider!=null){
						PersistEventListener listenerPersist = objListenerProvider.getPersistEventListener(); //Das ist PersistListenerTHM
						if(listenerPersist!= null){
							//Das Ziel ist es den Listener nur 1x zu registrieren... Sonst droht der Fehler "Illegal attempt to associate a collection with two open sessions"
							if(eventListenerRegistry.getEventListenerGroup(EventType.PERSIST)==null){
								eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
							}else{
								System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Listener schon vorhanden. Entferne ihn und setze erneut (PERSIST).");								
								//eventListenerRegistry.prependListeners(EventType.PERSIST, listenerPersist);
								eventListenerRegistry.setListeners(EventType.PERSIST, listenerPersist);
								System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Doppeltes Setzen des Listeners wurde vermieden (PERSIST).");			            		
							}
						}
													
						PreInsertEventListener listenerPreInsert = objListenerProvider.getPreInsertEventListener();//Das ist PreInsertListenerTHM
						if(listenerPreInsert != null){
							//Das Ziel ist es den Listener nur 1x zu registrieren... Sonst droht der Fehler "Illegal attempt to associate a collection with two open sessions"
							if(eventListenerRegistry.getEventListenerGroup(EventType.PRE_INSERT)==null){
								eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert); 
							}else{
								System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Listener schon vorhanden. Entferne ihn und setze erneut (PRE_INSERT).");
								//eventListenerRegistry.prependListeners(EventType.PRE_INSERT, listenerPreInsert);
								eventListenerRegistry.setListeners(EventType.PRE_INSERT, listenerPreInsert);
								System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Doppeltes Setzen des Listeners wurde vermieden (PRE_INSERT).");								
							}
						}
						
						SaveOrUpdateEventListener listenerSaveUpdate = objListenerProvider.getSaveOrUpdateEventListener(); //Das ist SaveOrUpdateListenerTHM
						if(listenerSaveUpdate != null){
							//Das Ziel ist es den Listener nur 1x zu registrieren... Sonst droht der Fehler "Illegal attempt to associate a collection with two open sessions"
							if(eventListenerRegistry.getEventListenerGroup(EventType.SAVE_UPDATE)==null){
								eventListenerRegistry.setListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
							}else{
								System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Listener schon vorhanden. Entferne ihn und setze erneut (SAVE_UPDATE).");
								//eventListenerRegistry.prependListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
								eventListenerRegistry.setListeners(EventType.SAVE_UPDATE, listenerSaveUpdate);
								System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=false FlagZ. Doppeltes Setzen des Listeners wurde vermieden (SAVE_UPDATE).");			            		
							}
						}
						
						
					    //Wird nicht ausgeführt, weder bei session.save noch bei session.update
				        //PreUpdateListenerTHM listenerPreUpdate = new PreUpdateListenerTHM();
				        //eventListenerRegistry.prependListeners(EventType.PRE_UPDATE, listenerPreUpdate);
				        
						//Weitere Listener: Merke, eine Listener Klasse kann auch mehrere Interfaces implementieren. 
			            //Anbei eine kleine Auswahl weiterer möglicher Listener Events. Jeder Event entspricht einem Interface.
//			            eventListenerRegistry.setListeners(EventType.PRE_LOAD, listener);
//			            eventListenerRegistry.prependListeners(EventType.PRE_INSERT, listener);
			            //eventListenerRegistry.appendListeners(EventType.POST_UPDATE, listener );			          
					}else{
						System.out.println("XXX MyIntegratorTHM.java: Listener würden gesetzt, sind aber keine per .getListenerProviderObject() vorhanden.");
					}
            	}else{
            		System.out.println("XXX MyIntegratorTHM.java: Kernel Objekt hat init=true FlagZ. Listener werden NICHT gesetzt.");
            	}//end if !objKernel.getFlagZ("init")  
            	
            	System.out.println("XXX MyIntegratorTHM.java beendet");     
                
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } 

        public void disintegrate(SessionFactoryImplementor arg0, SessionFactoryServiceRegistry arg1) {        
        }

        public void integrate(MetadataImplementor arg0,SessionFactoryImplementor arg1, SessionFactoryServiceRegistry arg2) {
        	System.out.println("YYY In MyIntegratorTHM.java");
        	System.out.println("YYY MyIntegratorTHM.java beendet");  
        }

				
		//#######################################
		//Methods implemented by Interface
		public IKernelZZZ getKernelObject() {
			return this.objKernel;
		}
		public void setKernelObject(IKernelZZZ objKernel) {
			this.objKernel = objKernel;
		}
			
		public LogZZZ getLogObject() {
			return this.objLog;
		}
		public void setLogObject(LogZZZ objLog) {
			this.objLog = objLog;
		}

}
