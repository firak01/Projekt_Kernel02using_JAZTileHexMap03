package basic.rule.facade;

import org.hibernate.Session;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;

public class GeneralRuleHibernateFacadeZZZ extends GeneralRuleFacadeZZZ{
	private IHibernateContextProviderZZZ objContextHibernate = null;
	
	public GeneralRuleHibernateFacadeZZZ(){
		super();
	}
	
	public GeneralRuleHibernateFacadeZZZ(IHibernateContextProviderZZZ objContextHibernate){
		this();
		this.objContextHibernate = objContextHibernate;
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
