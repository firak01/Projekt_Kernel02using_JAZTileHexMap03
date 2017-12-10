package basic.zBasic.persistence.interfaces;

import basic.zBasic.ExceptionZZZ;

public interface IHibernateConfigurationProviderUserZZZ {
	public void setConfigurationProviderObject(IHibernateConfigurationProviderZZZ objHibernateConfiguration);
	public IHibernateConfigurationProviderZZZ getConfigurationProviderObject() throws ExceptionZZZ;
}
