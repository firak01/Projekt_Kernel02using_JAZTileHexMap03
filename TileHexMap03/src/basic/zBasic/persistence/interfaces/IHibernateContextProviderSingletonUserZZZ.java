package basic.zBasic.persistence.interfaces;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateContextProviderSingletonUserZZZ {
	//Wegen der Verwendung von Singleton in der getter-Methode, braucht diese Methode nicht implementiert zu werden... public void setHibernateContextProvider(IHibernateContextProviderZZZ objContextHibernate);
	public IHibernateContextProviderZZZ getHibernateContextProvider() throws ExceptionZZZ;
}
