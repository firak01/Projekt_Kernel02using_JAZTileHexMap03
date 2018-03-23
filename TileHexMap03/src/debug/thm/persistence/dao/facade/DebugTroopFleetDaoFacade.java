package debug.thm.persistence.dao.facade;

import java.util.ArrayList;
import java.util.List;

import debug.thm.persistence.dao.tilevariant.DebugTroopArmyVariantDao;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.FleetTileTHM;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopFleetDao;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.daoFacade.TroopFleetDaoFacade;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import use.thm.web.webservice.axis2.pojo.TroopArmyPojo;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.math.RandomZZZ;
import basic.zKernel.KernelZZZ;

public class DebugTroopFleetDaoFacade {
	public static void main(String[] args) {				
		try {
			KernelZZZ objKernel = new KernelZZZ();		
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
			objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
			//##################################
			
			DebugTroopFleetDaoFacade objDebug = new DebugTroopFleetDaoFacade();				
			objDebug.debugFillDto();
					
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
		} 
		
	}
	public DebugTroopFleetDaoFacade(){		
	}
	public boolean debugFillDto(){
		boolean bReturn = false;
		main:{
			try {
				
				KernelZZZ objKernel = new KernelZZZ();		
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
				//Hole eine Fleet für den Test
				String sMap = "EINS";
				TroopFleet objToFill = null;
				String sUniquename = null;
				
				TroopFleetDao dao = new TroopFleetDao(objContextHibernate);				
				List<TroopFleet>listTroop = dao.searchTroopFleetsAll(sMap);			
				if(listTroop.size()==0){
					System.out.println("Es gibt auf der Karte '" + sMap + " keine platzierte Armeen. Beende die Funktion.");				
					break main;
				}else{
					System.out.println("Es gibt auf der Karte '" + sMap + " platzierte Armeen: " + listTroop.size());				
					
					int iChoose = RandomZZZ.getRandomNumber(0, listTroop.size()-1);
					System.out.println("Verwende eine zufällig ... iChoose="+iChoose);
					
//					for(TroopArmy objTroop : listTroop){
//						TroopArmyPojo objPojo = new TroopArmyPojo();
//						objPojo.setUniquename(objTroop.getUniquename());
//						objPojo.setPlayer(new Integer(objTroop.getPlayer()));
//						objPojo.setType(objTroop.getTroopType());
//						
//						objPojo.setMapAlias(sMap);
//									
//						objPojo.setMapX(new Integer(objTroop.getMapX()));
//						objPojo.setMapY(new Integer(objTroop.getMapY()));
//						
//						listReturn.add(objPojo);
//					}
					
					objToFill = listTroop.get(iChoose);
					sUniquename = objToFill.getUniquename();
				}
				
				
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();			 
				GenericDTO dto = objFactoryGenerator.createDtoForClass(FleetTileTHM.class);
			
				
				TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
				bReturn = objTroopDaoFacade.fillTroopFleetDto(sUniquename, dto);
                
				//###########################################
				//### DEBUG AUSGABEN ########################
				//###########################################
				String sShorttext = (String) dto.get(ITileDtoAttribute.VARIANT_SHORTTEXT);
				System.out.println("Dto - Shorttext: " + sShorttext);
				
				Integer intInstanceUniqueNumber = (Integer) dto.get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
				System.out.println("Dto - Instance_VARIANT_Uniquenumber: " + intInstanceUniqueNumber.toString());
								
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				bReturn = false;
			}
		}//end main:
		return bReturn;
	}
	
}
