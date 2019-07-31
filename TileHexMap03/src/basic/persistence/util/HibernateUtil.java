package basic.persistence.util;
 
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.internal.DefaultSaveEventListener;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import use.thm.persistence.dao.TroopVariantDaoFactory;
import use.thm.persistence.event.IVetoFlagZZZ;
import use.thm.persistence.event.PreInsertListenerTHM;
import use.thm.persistence.event.SaveOrUpdateListenerTHM;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateListenerProviderZZZ;
import basic.zKernel.IKernelZZZ;
 
public abstract class HibernateUtil {
 
    private static final SessionFactory sessionFactory = buildSessionFactory();
 
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new AnnotationConfiguration()
            		.configure()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
 
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    /**Wird eingesetzt, nach einem Commit, 
     *  beispielsweise, ein preInsert-Listerner kann durch sein Veto den commit (d.h. also den Insert) verhindern.
     *  ABER: EIN EINFACHES "WAS COMMITTED" IST KEINE LÖSUNG.
     *  DARUM: Erweitere meinen Listener so, dass er sein letztes Ergebnis in einer Property speichert.
     * @param objTransaction
     * @return
     */
    public static boolean wasCommitSuccessful(IHibernateContextProviderZZZ objContextHibernate, String sCommitedType, Transaction objTransaction){
    	boolean bReturn = false;
    	main:{    
    		try{
//	    	if (objTransaction.wasCommitted()) { //Keine LÖSUNG, da: //Always false: http://stackoverflow.com/questions/15503976/why-does-transaction-wascommitted-return-false
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": committed gilt als ausgeführt, aber es wurde nix inserted. Das hat preInsertListerner durch sein Veto verhindert.");
//				bReturn = true;
//			}else{
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": committed gilt als NICHT ausgeführt. Das hat preInsertListerner durch sein Veto verhindert.");
//				bReturn = false;
//			}
//			if (objTransaction.wasRolledBack()) {
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": rolledBack gilt als ausgeführt. Das hat preInsertListerner durch sein Veto ermöglicht.");
//			}else{
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": rolledBack gilt als NICHT ausgeführt, obwohl kein Insert gemacht wurde. Den hat preInsertListerner durch sein Veto verhindert.");
//			}
	    	
	    	
	    	Configuration hibernateConfiguration = objContextHibernate.getConfiguration();	    	
	    	 final EventListenerRegistry x = objContextHibernate.getSessionFactory().getServiceRegistry().getService(EventListenerRegistry.class);////org.hibernate.integrator.spi.Integrator in META-INF/services beaknnt machen.
	    	 
	    	 //TODO GOON 20170420: Je nachdem was vor dem commit gemacht worden ist  eine anderer EventListerenrGruop holen.
	    	 //TODO die Strings als Konstanten hinterlegen
	    	 if(sCommitedType.equalsIgnoreCase("save")){
		    	 EventListenerGroup eg = x.getEventListenerGroup(EventType.PRE_INSERT);
		    	for(Object objtemp : eg.listeners()){
		    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Listener der Klasse '"+ objtemp.getClass().getName() +"'");
		    		if(objtemp instanceof IVetoFlagZZZ){
		    			IVetoFlagZZZ  myListener = (IVetoFlagZZZ) objtemp;
			    		
			    		String sDateTime = "(ohne Datum)";
			    		Calendar cal = myListener.getVetoDate();	
			    		if(cal!=null){
				    		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd - hh:mm:ss");			    			    
				    		sDateTime = format1.format(cal.getTime());
			    		}		
			    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Letztes Ergebnis des Listeners isVeto() = " + myListener.isVeto() + " vom: " + sDateTime);
			    		bReturn = !myListener.isVeto();
			    		//20170704 mach das nicht mehr, s. MEthode, die das Objekt zurückliefert.  myListener.resetVeto(); //Nachdem man hier den Status abgefragt hat, diesen auf "nicht ausgeführt" zurücksetzen.
			    		System.out.println("xxxxxxxxxxxxxxxxxxxxxxx");
		    		}
		    	}
	    	}else if(sCommitedType.equalsIgnoreCase("update")){
	    		 EventListenerGroup eg = x.getEventListenerGroup(EventType.SAVE_UPDATE);
			    	for(Object objtemp : eg.listeners()){
			    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Listener der Klasse '"+ objtemp.getClass().getName() +"'");
			    		if(objtemp instanceof IVetoFlagZZZ){
			    			IVetoFlagZZZ  myListener = (IVetoFlagZZZ) objtemp;
				    		
				    		String sDateTime = "(ohne Datum)";
				    		Calendar cal = myListener.getVetoDate();	
				    		if(cal!=null){
					    		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd - hh:mm:ss");			    			    
					    		sDateTime = format1.format(cal.getTime());
				    		}			    		
				    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Letztes Ergebnis des Listeners hasVeto() = " + myListener.isVeto() + " vom: " + sDateTime);
				    		bReturn = !myListener.isVeto();
				    		//20170704 mach das nicht mehr, s. MEthode, die das Objekt zurückliefert. myListener.resetVeto(); //Nachdem man hier den Status abgefragt hat, diesen auf "nicht ausgeführt" zurücksetzen.
				    		System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyy");
			    		}
			    	}
	    		
	    	}	
    		}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				bReturn = false;
			}
    	}//end main:
    	return bReturn;
    }
    
    /**Wird eingesetzt, nach einem Commit, 
     *  beispielsweise, ein preInsert-Listerner kann durch sein Veto den commit (d.h. also den Insert) verhindern.
     *  ABER: EIN EINFACHES "WAS COMMITTED" IST KEINE LÖSUNG.
     *  DARUM: Erweitere meinen Listener so, dass er sein letztes Ergebnis in einer Property speichert.
     * @param objTransaction
     * @return
     */
    public static VetoFlag4ListenerZZZ getCommitResult(IHibernateContextProviderZZZ objContextHibernate, String sCommitedType, Transaction objTransaction){
    	VetoFlag4ListenerZZZ objReturn = null;
    	main:{  
    		try{
//	    	if (objTransaction.wasCommitted()) { //Keine LÖSUNG, da: //Always false: http://stackoverflow.com/questions/15503976/why-does-transaction-wascommitted-return-false
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": committed gilt als ausgeführt, aber es wurde nix inserted. Das hat preInsertListerner durch sein Veto verhindert.");
//				bReturn = true;
//			}else{
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": committed gilt als NICHT ausgeführt. Das hat preInsertListerner durch sein Veto verhindert.");
//				bReturn = false;
//			}
//			if (objTransaction.wasRolledBack()) {
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": rolledBack gilt als ausgeführt. Das hat preInsertListerner durch sein Veto ermöglicht.");
//			}else{
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": rolledBack gilt als NICHT ausgeführt, obwohl kein Insert gemacht wurde. Den hat preInsertListerner durch sein Veto verhindert.");
//			}
	    	
	    	
	    	Configuration hibernateConfiguration = objContextHibernate.getConfiguration();	    	
	    	 final EventListenerRegistry x = objContextHibernate.getSessionFactory().getServiceRegistry().getService(EventListenerRegistry.class);////org.hibernate.integrator.spi.Integrator in META-INF/services beaknnt machen.
	    	 
	    	 //TODO GOON 20170420: Je nachdem was vor dem commit gemacht worden ist  eine anderer EventListerenrGroup holen.
	    	 //TODO die Strings als Konstanten hinterlegen
	    	 if(sCommitedType.equalsIgnoreCase("save")){
		    	 EventListenerGroup eg = x.getEventListenerGroup(EventType.PRE_INSERT);
		    	for(Object objtemp : eg.listeners()){
		    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Listener der Klasse '"+ objtemp.getClass().getName() +"'");
		    		if(objtemp instanceof IVetoFlagZZZ){
		    			IVetoFlagZZZ  myListener = (IVetoFlagZZZ) objtemp;
			    		
			    		String sDateTime = "(ohne Datum)";
			    		Calendar cal = myListener.getVetoDate();	
			    		if(cal!=null){
				    		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd - hh:mm:ss");			    			    
				    		sDateTime = format1.format(cal.getTime());
			    		}		
			    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Letztes Ergebnis des Listeners ('save') isVeto() = " + myListener.isVeto() + " vom: " + sDateTime);
			    		if(myListener.isVeto()){
			    			objReturn = myListener.getCommitResult();
			    		}
			    		//AUF GAR KEINE FALL myListener.resetVeto(); //Nachdem man hier den Status abgefragt hat, diesen auf "nicht ausgeführt" zurücksetzen.
			    		System.out.println("xx2xx2xx2xx2xx2xx2xx2xx2xx2xx2xxx");
		    		}
		    	}
	    	}else if(sCommitedType.equalsIgnoreCase("update")){
	    		 EventListenerGroup eg = x.getEventListenerGroup(EventType.SAVE_UPDATE);
			    	for(Object objtemp : eg.listeners()){
			    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Listener der Klasse '"+ objtemp.getClass().getName() +"'");
			    		
			    		//DefaultSaveOrUpdateEventListener  myListenerX = (DefaultSaveOrUpdateEventListener) objtemp;//aber da fehlt meine IVetoZZZ erweiterung
			    		if(objtemp instanceof IVetoFlagZZZ){
			    			IVetoFlagZZZ  myListener = (IVetoFlagZZZ) objtemp;
				    		
				    		String sDateTime = "(ohne Datum)";
				    		Calendar cal = myListener.getVetoDate();	
				    		if(cal!=null){
					    		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd - hh:mm:ss");			    			    
					    		sDateTime = format1.format(cal.getTime());
				    		}			    		
				    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Letztes Ergebnis des Listeners ('update') hasVeto() = " + myListener.isVeto() + " vom: " + sDateTime);
				    		if(myListener.isVeto()){
				    			objReturn = myListener.getCommitResult();
				    		}
				    		//AUF GAR KEINE FALL myListener.resetVeto(); //Nachdem man hier den Status abgefragt hat, diesen auf "nicht ausgeführt" zurücksetzen.
				    		System.out.println("yy2yy2yy2yy2yy2yy2yy2yy2yy2yy2yy2yy2yy");
			    		} 
			    	}//end for
	    		
	    	}else if(sCommitedType.equalsIgnoreCase("preinsert")){
	    		 EventListenerGroup eg = x.getEventListenerGroup(EventType.PRE_INSERT);
			    	for(Object objtemp : eg.listeners()){
			    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Listener der Klasse '"+ objtemp.getClass().getName() +"'");
			    		
			    		//DefaultSaveOrUpdateEventListener  myListenerX = (DefaultSaveOrUpdateEventListener) objtemp;//aber da fehlt meine IVetoZZZ erweiterung
			    		if(objtemp instanceof IVetoFlagZZZ){
			    			IVetoFlagZZZ  myListener = (IVetoFlagZZZ) objtemp;
				    		
				    		String sDateTime = "(ohne Datum)";
				    		Calendar cal = myListener.getVetoDate();	
				    		if(cal!=null){
					    		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd - hh:mm:ss");			    			    
					    		sDateTime = format1.format(cal.getTime());
				    		}			    		
				    		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Letztes Ergebnis des Listeners ('preinsert') hasVeto() = " + myListener.isVeto() + " vom: " + sDateTime);
				    		if(myListener.isVeto()){
				    			objReturn = myListener.getCommitResult();
				    		}
				    		//AUF GAR KEINE FALL myListener.resetVeto(); //Nachdem man hier den Status abgefragt hat, diesen auf "nicht ausgeführt" zurücksetzen.
				    		System.out.println("yy2yy2yy2yy2yy2yy2yy2yy2yy2yy2yy2yy2yy");
			    		}
			    	}//end for	    		
	    	}	  	 
    		}catch(ExceptionZZZ ez){
				String sError = "ExceptionZZZ: " + ez.getMessageLast() + "+\n ThreadID:" + Thread.currentThread().getId() +"\n";			
				System.out.println(sError);
				objReturn = null;
			}
    	}//end main:
    	return objReturn;   
    }
    
    public static IHibernateContextProviderZZZ getHibernateContextProviderUsed(IKernelZZZ objKernel) throws ExceptionZZZ{
    	String stemp = "Diese Utility Methode muss überschrieben werden.";
		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
		ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, TroopVariantDaoFactory.class,  ReflectCodeZZZ.getMethodCurrentName());
		throw ez;
    	//return null;
    }
}

