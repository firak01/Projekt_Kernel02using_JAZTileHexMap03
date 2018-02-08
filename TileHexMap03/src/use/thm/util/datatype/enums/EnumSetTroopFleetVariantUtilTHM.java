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

/**TODO GOON  */
public class EnumSetTroopFleetVariantUtilTHM extends EnumSetTroopVariantUtilTHM{

	public EnumSetTroopFleetVariantUtilTHM(){		
	}
	public EnumSetTroopFleetVariantUtilTHM(EnumSet<?>enumSetUsed){
		super(enumSetUsed);
	}
	public EnumSetTroopFleetVariantUtilTHM(Class<?>objClass)throws ExceptionZZZ{
		super(objClass);
	}
	public EnumSetTroopFleetVariantUtilTHM(IEnumSetFactoryZZZ objEnumSetFactory, Class<?> objClass) throws ExceptionZZZ{
		super(objEnumSetFactory,objClass);
	}
	public EnumSetTroopFleetVariantUtilTHM(IEnumSetFactoryZZZ objEnumSetFactory){
		super(objEnumSetFactory);
	}

	//###############	Hier werden die Unterschiede zur erweiterten Klasse behandelt. Also die Eigenschaften, die nicht jede TroopVariant hat.
    //#### Speziell für FLEET
		public static Integer readEnumConstant_NumberOfTurretValue(Class<IEnumSetTroopFleetVariantTHM> objClass, String sEnumAlias) {
			Integer intReturn = new Integer(-1);
			main:{
		    if (objClass==null || sEnumAlias==null || sEnumAlias.isEmpty()) break main;
		  
		    
		    IEnumSetTroopFleetVariantTHM[] enumaSetMapped = objClass.getEnumConstants();
		    if(enumaSetMapped==null) break main; //Das ist der Fall, wenn es isch um die übergebene Klasse nicht um eine Enumeration handelt
		    
		  	for(IEnumSetTroopFleetVariantTHM driver : enumaSetMapped) {
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver ALIAS  als driver.name() from Enumeration="+driver.name());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.toString() from Enumeration="+driver.toString());
//				  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Driver als driver.abbreviaton from Enumeration="+driver.getAbbreviation());
			
			  if(driver.getName().equals(sEnumAlias)){
				  intReturn = new Integer(driver.getNumberOfTurret());
				  break main;
			  }
			}//end for
			}//end main:
			return intReturn;
		}
}
