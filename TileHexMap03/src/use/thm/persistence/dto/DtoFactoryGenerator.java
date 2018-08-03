package use.thm.persistence.dto;

import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.FleetTileTHM;
import use.thm.client.component.VariantCatalogTHM;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
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
		}else if(VariantCatalogTHM.class.equals(classUsingTheDto)){
				objReturn = new VariantCatalogDtoFactory();
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
	
	//#####################
	/** Komfortfunktion: 
	 *   Damit muss man an den nutzenden Stellen die einzelnen DTO Factories nicht erst erzeugen. 
	 *   Das spart eine Zeile Code, wenn das die Generator Klasse selbst liefert.
	 */
	public GenericDTO<IDTOAttributeGroup> createDtoForClass(Class classUsingTheDto) throws ExceptionZZZ{
		
		//1. Hole die zu verwendende Factory für diese Klasse.
		IDtoFactoryZZZ objFactory = this.getDtoFactory(classUsingTheDto);
				
	    //2. Erzeuge dann generisch das DTO
		GenericDTO<IDTOAttributeGroup> dto = (GenericDTO<IDTOAttributeGroup>) objFactory.createDTO();
		
		return dto;
	}

}
