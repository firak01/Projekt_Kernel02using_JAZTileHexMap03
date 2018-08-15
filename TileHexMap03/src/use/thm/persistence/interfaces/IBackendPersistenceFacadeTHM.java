package use.thm.persistence.interfaces;

import basic.zBasic.ExceptionZZZ;

public interface IBackendPersistenceFacadeTHM{
	public String getFacadeType();
	public String computeUniquename() throws ExceptionZZZ;
}
