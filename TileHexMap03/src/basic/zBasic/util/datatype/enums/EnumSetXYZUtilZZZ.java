package basic.zBasic.util.datatype.enums;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectClassZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractEnum.EnumSetFactoryZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetFactoryZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;

public class EnumSetXYZUtilZZZ extends EnumSetUtilZZZ{
	private EnumSet<?> enumSetCurrent=null;
	private IEnumSetFactoryZZZ objEnumSetFactory=null;
	
	public EnumSetXYZUtilZZZ(){		
	}
	public EnumSetXYZUtilZZZ(EnumSet<?>enumSetUsed){
		this.setEnumSetCurrent(enumSetUsed);
	}
	public EnumSetXYZUtilZZZ(Class<?>objClass)throws ExceptionZZZ{
		super(objClass);		
	}
	public EnumSetXYZUtilZZZ(IEnumSetFactoryZZZ objEnumSetFactory, Class<?> objClass) throws ExceptionZZZ{
		super(objEnumSetFactory, objClass);
	}
	public EnumSetXYZUtilZZZ(IEnumSetFactoryZZZ objEnumSetFactory){
		super(objEnumSetFactory);
	}
			
	//getAbbreviation();
			@SuppressWarnings({ "unchecked", "rawtypes" })		
			//public static String getEnumConstant_DescriptionValue(Class<TileDefaulttext.EnumTileDefaulttext> clazz, String name) {
			public static String readEnumConstant_DescriptionValue(Class<IEnumSetTextTHM> clazz, String name) {
			//public static <E extends Enum<E>> String getEnumConstant_DescriptionValue(Class<E> clazz, String name) {
				String sReturn = null;
				main:{
			    if (clazz==null || name==null || name.isEmpty()) break main;
			  
			    
			    IEnumSetTextTHM[] enumaSetMapped = (IEnumSetTextTHM[]) clazz.getEnumConstants();
			    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
			    
				for(IEnumSetTextTHM driver : enumaSetMapped) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
					
				
					if(!StringZZZ.isEmpty(driver.getDescription())){
					  if(driver.getName().equals(name)){
						  sReturn = driver.getDescription();
						  break main;
					  }
				  }
			
				}//end for
				}//end main:
				return sReturn;
			}
}
