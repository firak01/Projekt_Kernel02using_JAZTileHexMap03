package use.thm.util.datatype.enums;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Set;

import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopArmyVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTroopVariantTHM;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TileDefaulttext.EnumTileDefaulttext;
import use.thm.persistence.model.TroopFleetVariant.EnumTroopFleetVariant;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IFlagZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectClassZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractEnum.EnumSetFactoryZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetFactoryZZZ;
import basic.zBasic.util.abstractEnum.IEnumSetMappedZZZ;
import basic.zBasic.util.datatype.enums.EnumSetMappedUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.file.FileEasyZZZ;

public class EnumSetTroopVariantUtilTHM extends EnumSetUtilZZZ{

	public EnumSetTroopVariantUtilTHM(){		
	}
	public EnumSetTroopVariantUtilTHM(EnumSet<?>enumSetUsed){
		super(enumSetUsed);
	}
	public EnumSetTroopVariantUtilTHM(Class<?>objClass)throws ExceptionZZZ{
		super(objClass);
	}
	public EnumSetTroopVariantUtilTHM(IEnumSetFactoryZZZ objEnumSetFactory, Class<?> objClass) throws ExceptionZZZ{
		super(objEnumSetFactory,objClass);
	}
	public EnumSetTroopVariantUtilTHM(IEnumSetFactoryZZZ objEnumSetFactory){
		super(objEnumSetFactory);
	}

	//###############	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Long readEnumConstant_ThiskeyValue(Class<IEnumSetTroopVariantTHM> clazz, String name) {
		Long lngReturn = new Long(-1);
		main:{
	    if (clazz==null || name==null || name.isEmpty()) break main;
	  
	    
	    //IEnumSetTroopFleetVariantTHM[] enumaSetMapped = clazz.getEnumConstants(); //Luse.thm.persistence.model.Immutabletext$EnumImmutabletext; cannot be cast to [Luse.thm.persistence.interfaces.enums.IEnumSetTroopFleetVariantTHM;
	    IEnumSetTroopVariantTHM[] enumaSetMapped = clazz.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
		for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
			
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
	
	public static String readEnumConstant_UniquetextValue(Class<IEnumSetTroopVariantTHM> clazz, String name) {
		String sReturn = new String("");
		main:{
	    if (clazz==null || name==null || name.isEmpty()) break main;
	  
	    
	    IEnumSetTroopVariantTHM[] enumaSetMapped = clazz.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
	  	for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
		
	
		if(!StringZZZ.isEmpty(driver.getUniquetext())){
		  if(driver.getName().equals(name)){
			  sReturn = driver.getUniquetext();
			  break main;
		  }
	  }
		}//end for
		}//end main:
		return sReturn;
	}
	
	public static String readEnumConstant_CategorytextValue(Class<IEnumSetTroopVariantTHM> clazz, String name) {
		String sReturn = new String("");
		main:{
	    if (clazz==null || name==null || name.isEmpty()) break main;
	  
	    
	    IEnumSetTroopVariantTHM[] enumaSetMapped = clazz.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
	  	for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
		
	
		if(!StringZZZ.isEmpty(driver.getCategorytext())){
		  if(driver.getName().equals(name)){
			  sReturn = driver.getCategorytext();
			  break main;
		  }
	  }
		}//end for
		}//end main:
		return sReturn;
	}
	
	public static Integer readEnumConstant_MoveRangeValue(Class<IEnumSetTroopVariantTHM> objClass, String sEnumAlias) {
		Integer intReturn = new Integer(-1);
		main:{
	    if (objClass==null || sEnumAlias==null || sEnumAlias.isEmpty()) break main;
	  
	    
	    IEnumSetTroopVariantTHM[] enumaSetMapped = objClass.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
	  	for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
		
		  if(driver.getName().equals(sEnumAlias)){
			  intReturn = new Integer(driver.getMapMoveRange());
			  break main;
		  }
		}//end for
		}//end main:
		return intReturn;
	}
	
	public static String readEnumConstant_ImageUrlStringValue(Class<IEnumSetTroopVariantTHM> objClass, String sEnumAlias) {
		String sReturn = new String("");
		main:{
		if (objClass==null || sEnumAlias==null || sEnumAlias.isEmpty()) break main;
			  	    
		 IEnumSetTroopVariantTHM[] enumaSetMapped = objClass.getEnumConstants();
		 if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
		    
	  	for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
		
	
		if(!StringZZZ.isEmpty(driver.getImageUrlString())){
		  if(driver.getName().equals(sEnumAlias)){
			  sReturn = driver.getImageUrlString();
			  break main;
		  }
	  }
		}//end for
		}//end main:
		return sReturn;
	}
	
	public static Long readEnumConstant_DefaulttextThisid(Class<IEnumSetTroopVariantTHM> objClass, String sEnumAlias) {
		Long lngReturn = new Long(-1);
		main:{
	    if (objClass==null || sEnumAlias==null || sEnumAlias.isEmpty()) break main;
	  
	    
	    IEnumSetTroopVariantTHM[] enumaSetMapped = objClass.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
	  	for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
		
		  if(driver.getName().equals(sEnumAlias)){
			  lngReturn = new Long(driver.getDefaulttextThisid());
			  break main;
		  }
		}//end for
		}//end main:
		return lngReturn;
	}
	
	public static Long readEnumConstant_ImmutabletextThisid(Class<IEnumSetTroopVariantTHM> objClass, String sEnumAlias) {
		Long lngReturn = new Long(-1);
		main:{
	    if (objClass==null || sEnumAlias==null || sEnumAlias.isEmpty()) break main;
	  
	    
	    IEnumSetTroopVariantTHM[] enumaSetMapped = objClass.getEnumConstants();
	    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
	    
	  	for(IEnumSetTroopVariantTHM driver : enumaSetMapped) {
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//			  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
		
		  if(driver.getName().equals(sEnumAlias)){
			  lngReturn = new Long(driver.getImmutabletextThisid());
			  break main;
		  }
		}//end for
		}//end main:
		return lngReturn;
	}
	
	
	
}
