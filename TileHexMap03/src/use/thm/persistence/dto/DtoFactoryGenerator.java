package use.thm.persistence.dto;

import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.FleetTileTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryGeneratorZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;
import basic.zBasic.util.file.FileEasyZZZ;
import basic.zKernel.KernelUseObjectZZZ;

/* TODO Mache daraus ein Singleton */
public class DtoFactoryGenerator  extends KernelUseObjectZZZ implements IDtoFactoryGeneratorZZZ {
	public DtoFactoryGenerator(){		
	}
	
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
