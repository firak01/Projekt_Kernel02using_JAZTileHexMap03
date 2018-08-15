package use.thm.persistence.daoFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Session.LockRequest;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.EventType;

import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopFleetDao;
import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopType;
import use.thm.persistence.model.TroopVariant;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.model.TroopArmyRuleType;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopArmyVariantDaoFacade extends TroopVariantDaoFacade{
	private TroopArmyVariant objTroopArmyVariant = null;	
	
	public TroopArmyVariantDaoFacade(HibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
		
	public boolean fillTroopArmyVariantDto(long lngThiskey, GenericDTO<IBoxDtoAttribute> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopArmyVariantDto(lngThiskey)  ####################");
						
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################								
			HibernateContextProviderSingletonTHM objContextHibernate = (HibernateContextProviderSingletonTHM) this.getHibernateContext();
			TroopArmyVariantDao objTroopVariantDao = new TroopArmyVariantDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopArmyVariant objTroopVariant = (TroopArmyVariant) objTroopVariantDao.searchThiskey(lngThiskey);
			if(objTroopVariant == null) break main;
			
			bReturn = this.fillTroopArmyVariantDto(objTroopVariant, dto);
			
		}//end main:
		return bReturn;
	}
	
	public boolean fillTroopArmyVariantDto(TroopArmyVariant objTroopVariant, GenericDTO<IBoxDtoAttribute> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopArmyVariantDto(objTroopArmyVariant)  ####################");
			bReturn = super.fillTroopVariantDto(objTroopVariant, dto);

			// Besondere Eigenschaften, nur für Armeen hier dann noch hinzufügen....
		}//end main:
		return bReturn;
	}
	
	
	@Override
	public String getFacadeType() {
		return TroopType.ARMY.name();
	}
	
	@Override
	public void setEntityUsed(TroopVariant objTroopArmyVariant){
		this.objTroopArmyVariant =  (TroopArmyVariant) objTroopArmyVariant;
	}

	@Override
	public TroopVariant getEntityUsed() {
		return this.objTroopArmyVariant;
	}
}
