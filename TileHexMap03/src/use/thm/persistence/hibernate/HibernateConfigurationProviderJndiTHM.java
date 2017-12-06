package use.thm.persistence.hibernate;

import org.hibernate.cfg.Configuration;

import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttextValue;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.hibernate.HibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;

public class HibernateConfigurationProviderJndiTHM extends HibernateConfigurationProviderTHM {
	public HibernateConfigurationProviderJndiTHM() throws ExceptionZZZ{
		super();
	}
	
	@Override
	public boolean fillConfiguration() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			bReturn = this.fillConfigurationGlobal();
			bReturn = this.fillConfigurationMapping();
			//das wird nicht benötigt, da hier JNDI und Konfiguration der Datenquelle in der der context.xml vorausgesetzt wird
			//bReturn = this.fillConfigurationLocalDb();
		}
		return bReturn;
	}
}
