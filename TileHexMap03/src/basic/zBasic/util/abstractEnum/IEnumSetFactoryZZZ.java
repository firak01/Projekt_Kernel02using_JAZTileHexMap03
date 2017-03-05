package basic.zBasic.util.abstractEnum;

import java.util.EnumSet;

import basic.zBasic.ExceptionZZZ;

public interface IEnumSetFactoryZZZ {
	public EnumSet<?> getEnumSet(Class objClass) throws ExceptionZZZ;
	public EnumSet<?> getEnumSet(String sClassNamePath) throws ExceptionZZZ;
}
