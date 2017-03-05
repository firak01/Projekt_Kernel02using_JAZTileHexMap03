package basic.zBasic.util.abstractEnum;

import static java.lang.System.out;

import java.util.EnumSet;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.persistence.jdbc.JdbcDatabaseTypeZZZ;
import basic.zBasic.util.persistence.jdbc.JdbcDriverClassTypeZZZ;
import basic.zKernel.KernelZZZ;

public class EnumSetMappedTestFactoryZZZ extends ObjectZZZ implements IEnumSetFactoryZZZ{

private static EnumSetMappedTestFactoryZZZ objEnumFactory = null;  //muss static sein, wg. getInstance()!!!
	
	/**Konstruktor ist private, wg. Singleton
	 * @param objKernel
	 * @throws ExceptionZZZ
	 */
	private EnumSetMappedTestFactoryZZZ(KernelZZZ objKernel) throws ExceptionZZZ{
		super();
	}
	private EnumSetMappedTestFactoryZZZ(){
		super();
	}
	
	public static EnumSetMappedTestFactoryZZZ getInstance(){
		if(objEnumFactory==null){
			objEnumFactory = new EnumSetMappedTestFactoryZZZ();
		}
		return objEnumFactory;		
	}
	
	/*
	 * Das ist der Kerngedanke dieser Factory.
	 * Gib den Klassennamen an, dann kann man die static Methode aufrufen (hart verdrahtet, denn Reflection funktioniert leider nicht).
	 */
	public EnumSet<?>getEnumSet(Class objClassEnum) throws ExceptionZZZ{
		EnumSet<?>objEnumSet = null;
		main:{
			if(objClassEnum==null){
				ExceptionZZZ ez  = new ExceptionZZZ("ClassObject", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			String sClassNameEnum = objClassEnum.getName();
			objEnumSet = getEnumSet(sClassNameEnum);
		}//end main:
		return objEnumSet;
	}
	
	public EnumSet<?>getEnumSet(String sClassNameEnum) throws ExceptionZZZ{
		EnumSet<?>objEnumSetReturn = null;
		main:{
			if(StringZZZ.isEmpty(sClassNameEnum)){
				ExceptionZZZ ez  = new ExceptionZZZ("ClassObject", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}
			
			//Merke: Switch Anweisung mit String ist erst ab Java 1.7 möglich			
			if (sClassNameEnum.equals("basic.zBasic.util.abstractEnum.EnumSetMappedTestTypeZZZ")){
				//Auf ObjectZZZ Ebene gibt es noch keine Logging-Klassen
	        	out.format("%s# sClassNameEnum wurde hier gefunden: %s%n", ReflectCodeZZZ.getPositionCurrent(),sClassNameEnum);
	        	objEnumSetReturn= EnumSetMappedTestTypeZZZ.getEnumSet();     
			         
	        }else{
	        	//Wenn es die Klasse nicht gibt. Keinen Fehler werfen, da ggfs. über einen Vererbungsmechanismus ja zuerst in der Superklasse nachgesehen wurde und dann ggfs. weiter in Kindklassen gesucht wird.
	        	//Auf ObjectZZZ Ebene gibt es noch keine Logging-Klassen
	        	out.format("%s# sClassNameEnum wird hier NICHT gefunden: %s%n", ReflectCodeZZZ.getPositionCurrent(),sClassNameEnum);
	        	objEnumSetReturn=null;
	        }
		
	}//end main:
	return objEnumSetReturn;
	}
}
