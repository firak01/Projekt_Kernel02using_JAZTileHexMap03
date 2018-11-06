package use.thm.persistence.daoFacade;

import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopType;
import use.thm.persistence.model.TroopVariant;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopFleetVariantDaoFacade extends TroopVariantDaoFacade{
	private TroopFleetVariant objTroopFleetVariant = null;	
	
	public TroopFleetVariantDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
		
	public boolean fillTroopFleetVariantDto(long lngThiskey, GenericDTO<IBoxDtoAttribute> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopArmyVariantDto(lngThiskey)  ####################");
						
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################								
			//HibernateContextProviderSingletonTHM objContextHibernate = (HibernateContextProviderSingletonTHM) this.getHibernateContext();
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopFleetVariantDao objTroopVariantDao = new TroopFleetVariantDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopFleetVariant objTroopVariant = (TroopFleetVariant) objTroopVariantDao.searchThiskey(lngThiskey);
			if(objTroopVariant == null) break main;
			
			bReturn = this.fillTroopFleetVariantDto(objTroopVariant, dto);
			
		}//end main:
		return bReturn;
	}
	
	public boolean fillTroopFleetVariantDto(TroopFleetVariant objTroopVariant, GenericDTO<IBoxDtoAttribute> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopFleetVariantDto(objTroopArmyVariant)  ####################");
			bReturn = super.fillTroopVariantDto(objTroopVariant, dto);
			
			// Besondere Eigenschaften, nur für Flotten hier dann noch hinzufügen....
		}//end main:
		return bReturn;
	}
	
	
	@Override
	public String getFacadeType() {
		return TroopType.ARMY.name();
	}

	@Override
	public TroopVariant getEntityUsed() {
		return this.objTroopFleetVariant;
	}
	
	@Override
	public void setEntityUsed(TroopVariant objTroopFleetVariant){
		this.objTroopFleetVariant =  (TroopFleetVariant) objTroopFleetVariant;
	}
}
