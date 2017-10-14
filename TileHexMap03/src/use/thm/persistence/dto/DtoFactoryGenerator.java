package use.thm.persistence.dto;

import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.FleetTileTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryGeneratorZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zKernel.KernelUseObjectZZZ;

/** Als Singleton umgesetzt. 
 * 
     * Merke:
	 * Mache die Factory - Klassen selbst NICHT zum Singleton:
	 * Diese erweitern eine abstrakte Kalsse. Singelteon funktioniert in der abstrakten Klasse nicht.
	 * Grund: In einer abstrakten Klasse kann zwar ein Konsturktor vorhanden sein, .... 
	 * ABER:  Es kann die Abstrakte Klasse nicht instantiiert werden.*/
public class DtoFactoryGenerator  extends KernelUseObjectZZZ implements IDtoFactoryGeneratorZZZ {
	private static DtoFactoryGenerator objFactorySingelton; //muss als Singleton static sein

	public static DtoFactoryGenerator getInstance() throws ExceptionZZZ{
		if(objFactorySingelton==null){
			objFactorySingelton = new DtoFactoryGenerator(); //Das geht in abstrakter Klasse eben nicht... !!!!!!!!!!!!!!!!!!!!1
		}
		return objFactorySingelton;	
	}
	
	//Die Konstruktoren nun verbergen, wg. Singleton
	private DtoFactoryGenerator() throws ExceptionZZZ{
		//super();
	}
	
	//TODO: Erlaube das setzen von Flags im Konstruktor.... 
	//dazu muss die Klasse aber auch die getFlag() / set Flag() Methodik verwenden, die in anderen Kernel-Objekten genutzt wird.
	//Diese Flags müssen vererbbar sein....

//	public static  TileDtoFactory getInstance(String sFlagControl) throws ExceptionZZZ{
//		if(objFactorySingelton==null){
//			objFactorySingelton = new TileDtoFactory(sFlagControl);
//		}
//		return objFactorySingelton;	
//	}
	
	
	

	
	//###########################################################################
	@Override
	public IDtoFactoryZZZ getDtoFactory(Class classUsingTheDto) throws ExceptionZZZ{
		IDtoFactoryZZZ objReturn = null;
		
		if(classUsingTheDto == null){
			ExceptionZZZ ez  = new ExceptionZZZ("Klasse, welche eine Dto nutzen will nicht uebergeben.", iERROR_PARAMETER_MISSING, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		
		//ohje, noch 1.6 compatible schreiben, also mit if-Anweisungen
		if(ArmyTileTHM.class.equals(classUsingTheDto)){
			objReturn = new ArmyTileDtoFactory();
		}else if(FleetTileTHM.class.equals(classUsingTheDto)){
				objReturn = new FleetTileDtoFactory();
		}else{
			ExceptionZZZ ez  = new ExceptionZZZ("Noch keine DTOFactory für die Klasse '" + classUsingTheDto.getName() + "' vorgesehen.", iERROR_PARAMETER_VALUE, this, ReflectCodeZZZ.getMethodCurrentName());
			throw ez;	
		}
		
//		switch(classUsingTheDto) {
//		case ArmyTileTHM.class :
//			
//			break;
//		default: 
//			break;
//		}
		return objReturn;
		}


}
