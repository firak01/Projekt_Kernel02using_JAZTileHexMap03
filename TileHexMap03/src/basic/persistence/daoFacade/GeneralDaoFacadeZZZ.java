package basic.persistence.daoFacade;

import org.hibernate.Session;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;

public class GeneralDaoFacadeZZZ implements IFacadeDtoZZZ{
	private IHibernateContextProviderZZZ objContextHibernate = null;
	private FacadeResultDtoZZZ objFacadeResult = null;
	
	public GeneralDaoFacadeZZZ(){
	}
		
	public GeneralDaoFacadeZZZ(IHibernateContextProviderZZZ objContextHibernate){
		this.objContextHibernate = objContextHibernate;
	}
	
	@Override
	public FacadeResultDtoZZZ getFacadeResult() {
		if(this.objFacadeResult==null){
			this.objFacadeResult = new FacadeResultDtoZZZ();
		}
		return this.objFacadeResult;
	}

	protected IHibernateContextProviderZZZ getHibernateContext(){
		return this.objContextHibernate;
	}
	protected Session getSession() throws ExceptionZZZ{
		Session objReturn = null;		
		IHibernateContextProviderZZZ objHibernateContext = this.getHibernateContext();
		if(objHibernateContext!=null){
			objReturn = objHibernateContext.getSession();
		}
		return objReturn;
	}
}
