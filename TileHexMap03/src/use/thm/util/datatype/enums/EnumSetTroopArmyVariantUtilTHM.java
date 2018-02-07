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

public class EnumSetTroopArmyVariantUtilTHM extends EnumSetTroopVariantUtilTHM{

	public EnumSetTroopArmyVariantUtilTHM(){		
	}
	public EnumSetTroopArmyVariantUtilTHM(EnumSet<?>enumSetUsed){
		super(enumSetUsed);
	}
	public EnumSetTroopArmyVariantUtilTHM(Class<?>objClass)throws ExceptionZZZ{
		super(objClass);
	}
	public EnumSetTroopArmyVariantUtilTHM(IEnumSetFactoryZZZ objEnumSetFactory, Class<?> objClass) throws ExceptionZZZ{
		super(objEnumSetFactory,objClass);
	}
	public EnumSetTroopArmyVariantUtilTHM(IEnumSetFactoryZZZ objEnumSetFactory){
		super(objEnumSetFactory);
	}

	//###############	Hier werden die Unterschiede zur erweiterten Klasse behandelt. Also die Eigenschaften, die nicht jede TroopVariant hat.
	
}
