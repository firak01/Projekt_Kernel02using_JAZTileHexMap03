package basic.zBasic.persistence.interfaces;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateListenerProviderUserZZZ {
	public void setListenerProviderObject(IHibernateListenerProviderZZZ objHibernateListener);
	public IHibernateListenerProviderZZZ getListenerProviderObject() throws ExceptionZZZ;
}
