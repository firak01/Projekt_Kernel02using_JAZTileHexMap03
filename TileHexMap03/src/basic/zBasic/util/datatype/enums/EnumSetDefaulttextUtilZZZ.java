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

public class EnumSetDefaulttextUtilZZZ extends EnumSetUtilZZZ{
	private EnumSet<?> enumSetCurrent=null;
	private IEnumSetFactoryZZZ objEnumSetFactory=null;
	
	public EnumSetDefaulttextUtilZZZ(){		
	}
	public EnumSetDefaulttextUtilZZZ(EnumSet<?>enumSetUsed){
		this.setEnumSetCurrent(enumSetUsed);
	}
	public EnumSetDefaulttextUtilZZZ(Class<?>objClass)throws ExceptionZZZ{
		super(objClass);		
	}
	public EnumSetDefaulttextUtilZZZ(IEnumSetFactoryZZZ objEnumSetFactory, Class<?> objClass) throws ExceptionZZZ{
		super(objEnumSetFactory, objClass);
	}
	public EnumSetDefaulttextUtilZZZ(IEnumSetFactoryZZZ objEnumSetFactory){
		super(objEnumSetFactory);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Integer readEnumConstant_IndexValue(Class<IEnumSetTextTHM> clazz, String name) {
		Integer intValue = null;
		main:{
	    if (clazz==null || name==null || name.isEmpty()) break main;
	  
	    
	    IEnumSetTextTHM[] enumaSetMapped = clazz.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
		for(IEnumSetTextTHM driver : enumaSetMapped) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
			
			  if(driver.getName().equals(name)){
				 int iValue = driver.getIndex();
				intValue = new Integer(iValue);
			  }
	
		}//end for
		}//end main:
		return intValue;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Integer readEnumConstant_PositionValue(Class<IEnumSetTextTHM> clazz, String name) {
		Integer intValue = null;
		main:{
	    if (clazz==null || name==null || name.isEmpty()) break main;
	  
	    
	    IEnumSetTextTHM[] enumaSetMapped = clazz.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
		for(IEnumSetTextTHM driver : enumaSetMapped) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
			
			  if(driver.getName().equals(name)){
				 int iValue = driver.getPosition();
				intValue = new Integer(iValue);
			  }
	
		}//end for
		}//end main:
		return intValue;
	}
	
	public static boolean startsWithAnyAlias(String sToFind, EnumSet<?> setEnumCurrent){
		boolean bReturn = false;
		main:{
			if(setEnumCurrent==null) break main; 			
			
			@SuppressWarnings("unchecked")
			Set<IEnumSetTextTHM> drivers = (Set<IEnumSetTextTHM>) setEnumCurrent;//..allOf(JdbcDriverClassTypeZZZ.class);
		    
			for(IEnumSetTextTHM driver : drivers) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
				
				if(!StringZZZ.isEmpty(driver.getName())){
					if(StringZZZ.startsWithIgnoreCase(driver.getName(),sToFind)){
					  bReturn = true;
					  break;
				  }
			  }
			}
		}
		return bReturn;
	}
	
	public static IEnumSetTextTHM startsWithAnyAlias_EnumMappedObject(String sToFind, EnumSet<?> setEnumCurrent){
		IEnumSetTextTHM objReturn = null;
		main:{
			if(setEnumCurrent==null) break main; 
			
			@SuppressWarnings("unchecked")
			Set<IEnumSetTextTHM> drivers = (Set<IEnumSetTextTHM>) setEnumCurrent;//..allOf(JdbcDriverClassTypeZZZ.class);
			for(IEnumSetTextTHM driver : drivers) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
				
				if(!StringZZZ.isEmpty(driver.getName())){
					if(StringZZZ.startsWithIgnoreCase(sToFind, driver.getName())){
					  objReturn = driver;
					  break;
				  }
			  }
			}
		}
		return objReturn;
	}
	
	
		public static boolean startsWithAnyDescription(String sToFind, EnumSet<?> setEnumCurrent){
			boolean bReturn = false;
			main:{
				if(setEnumCurrent==null) break main; 
				
				Set<IEnumSetTextTHM> drivers = (Set<IEnumSetTextTHM>) setEnumCurrent;//..allOf(JdbcDriverClassTypeZZZ.class);
				for(IEnumSetTextTHM driver : drivers) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
							
					if(!StringZZZ.isEmpty(driver.getDescription())){
					  if(driver.getDescription().startsWith(sToFind)){  //Groß-/Kleinschreibung beachten.
						  bReturn = true;
						  break;
					  }
				  }				
				}//end for
			}//end main:
			return bReturn;
		}
							
	public boolean startsWithAnyShorttext(String sToFind){
		boolean bReturn = false;
		main:{
			EnumSet<?> drivers = this.getEnumSetCurrent();//..allOf(JdbcDriverClassTypeZZZ.class);
			bReturn = EnumSetDefaulttextUtilZZZ.startsWithAnyShorttext(sToFind, drivers);
		}
		return bReturn;
	}
	
	public static boolean startsWithAnyShorttext(String sToFind, EnumSet<?> setEnumCurrent){
		boolean bReturn = false;
		main:{
			if(setEnumCurrent==null) break main; 
			
			Set<IEnumSetTextTHM> drivers = (Set<IEnumSetTextTHM>) setEnumCurrent;//..allOf(JdbcDriverClassTypeZZZ.class);
			for(IEnumSetTextTHM driver : drivers) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
			
				if(!StringZZZ.isEmpty(driver.getShorttext())){
				  if(driver.getShorttext().startsWith(sToFind)){  //!!! hier auch die Groß-/Kleinschreibung unterscheiden.
					  bReturn = true;
					  break;
				  }
			  }
		
			}//end for
		}//end main
		return bReturn;
	}
		
	
	public boolean startsWithAnyDescription(String sToFind){
		boolean bReturn = false;
		main:{
			EnumSet<?> drivers = this.getEnumSetCurrent();//..allOf(JdbcDriverClassTypeZZZ.class);
			bReturn = EnumSetDefaulttextUtilZZZ.startsWithAnyDescription(sToFind, drivers);
		}
		return bReturn;
	}
						
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static String readEnumConstant_NameValue(Class<?> clazz, String name) {
		    if (clazz==null || name==null || name.isEmpty()) {
		        return null;
		    }
		    String sReturn = Enum.valueOf((Class<Enum>)clazz, name).name();
		    return sReturn;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static String readEnumConstant_StringValue(Class<?> clazz, String name) {
		    if (clazz==null || name==null || name.isEmpty()) {
		        return null;
		    }
		    String sReturn = Enum.valueOf((Class<Enum>)clazz, name).toString();
		    return sReturn;
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
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
			
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

		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static String readEnumConstant_ShorttextValue(Class<IEnumSetTextTHM> clazz, String name) {
			String sReturn = null;
			main:{
		    if (clazz==null || name==null || name.isEmpty()) break main;
		  
		    
		    IEnumSetTextTHM[] enumaSetMapped = clazz.getEnumConstants();
		    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
		    
			for(IEnumSetTextTHM driver : enumaSetMapped) {
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
			
				if(!StringZZZ.isEmpty(driver.getShorttext())){
				  if(driver.getName().equals(name)){
					  sReturn = driver.getShorttext();
					  break main;
				  }
			  }
		
			}//end for
			}//end main:
			return sReturn;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static String readEnumConstant_LongtextValue(Class<IEnumSetTextTHM> clazz, String name) {
			String sReturn = null;
			main:{
		    if (clazz==null || name==null || name.isEmpty()) break main;
		  
		    
		    IEnumSetTextTHM[] enumaSetMapped = clazz.getEnumConstants();
		    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
		    
			for(IEnumSetTextTHM driver : enumaSetMapped) {
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
			
				if(!StringZZZ.isEmpty(driver.getLongtext())){
				  if(driver.getName().equals(name)){
					  sReturn = driver.getLongtext();
					  break main;
				  }
			  }
		
			}//end for
			}//end main:
			return sReturn;
		}
				
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Long readEnumConstant_ThiskeyValue(Class<IEnumSetTextTHM> clazz, String name) {
			Long lngReturn = new Long(0);
			main:{
		    if (clazz==null || name==null || name.isEmpty()) break main;
		  
		    
		    IEnumSetTextTHM[] enumaSetMapped = clazz.getEnumConstants();
		    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
		    
			for(IEnumSetTextTHM driver : enumaSetMapped) {
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//					  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
				
	            String sTest = new String(driver.getThiskey().toString());
				if(!StringZZZ.isEmpty(sTest)){
				  if(driver.getName().equals(name)){
					  lngReturn = driver.getThiskey();
					  break main;
				  }
			  }
		
			}//end for
			}//end main:
			return lngReturn;
		}
}
