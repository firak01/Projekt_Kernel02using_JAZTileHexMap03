package use.thm.persistence.util;

import use.thm.persistence.dao.TroopVariantDaoFactory;
import use.thm.persistence.hibernate.HibernateContextProviderJndiSingletonTHM;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zKernel.IKernelZZZ;

public class HibernateUtilTHM extends HibernateUtil{
	
	public static IHibernateContextProviderZZZ getHibernateContextProviderUsed(IKernelZZZ objKernel) throws ExceptionZZZ{
		
		if(objKernel==null){
			String stemp = "Kein Kernel-Objekt übergeben.";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
			ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, TroopVariantDaoFactory.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;		
		}
		
		//Notwendige Fallunterscheidung, man muss die ganz zu Anfang geholte Instanz des Objekts (für die Sessionerstellung) wiederfinden.
		//Ansonsten gibt es so Fehlermeldungen wie: "Eine Collection in 2 offenen Sessions".
		IHibernateContextProviderZZZ objHibernateContext = null;
		if(objKernel.isOnServer()){
			//für WebService: HibernateContextProviderJndiSingletonTHM
			 objHibernateContext = HibernateContextProviderJndiSingletonTHM.getInstance();
		}else{
			//für SwingStandalone: HibernateContextProviderSingletonTHM
			 objHibernateContext = HibernateContextProviderSingletonTHM.getInstance();
		}
		return objHibernateContext;
	}
}
