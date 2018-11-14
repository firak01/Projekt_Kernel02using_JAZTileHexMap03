package use.thm.persistence.interfaces;

import basic.zBasic.ExceptionZZZ;

public interface ITroopVariantDaoTHM {	
	public boolean isVariantValid(long lngThisIdKey) throws ExceptionZZZ;
	public boolean isVariantStandard(long lngThisIdKey) throws ExceptionZZZ;
}
