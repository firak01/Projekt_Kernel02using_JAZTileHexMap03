package use.thm.persistence.daoFacade;

import use.thm.persistence.interfaces.IBackendPersistenceFacadeTHM;
import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.util.datatype.dateTime.DateTimeZZZ;

public abstract class TileDaoFacade extends GeneralDaoFacadeZZZ implements IBackendPersistenceFacadeTHM{
	public TileDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public abstract String getFacadeType();
	
	public  String computeUniquename(){
		String sReturn = new String("");
		main:{
			String sFacadeType = this.getFacadeType();
			sReturn = TileDaoFacade.computeUniquename(sFacadeType);			
		}
		return sReturn;
	}
	
	public static String computeUniquename(String sFacadeType){
		String sReturn = new String("");
		main:{
			String sTimestamp = DateTimeZZZ.computeTimestampUniqueString();
			sReturn = sFacadeType + "_" + sTimestamp;
		}//end main:
		return sReturn;
	}
	
}
