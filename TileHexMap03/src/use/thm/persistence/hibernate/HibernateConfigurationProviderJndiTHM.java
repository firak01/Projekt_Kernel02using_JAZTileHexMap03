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
			//MERKE: man kann Konfigurationsbefehle für SQLITE an sich auch in .sqliter Datei im Benutzerheimatverzeichnis anlegen.
			//              So kann man Performance und Zugriff steuern über PRAGMA - Befehle.
			//              PRAGMA journal_mode=WAL soll besser sein.
			// 
			//Aber das wird wohl nicht automatisch beim Erstellen durch Hibernate mit hibernate.hbm2ddl.auto ausgeführt.
			//               sondern erst, wenn man den Befehl per dos startet sqlite3 und dahinter den Datenbanknamen: sqlite3 TileHexMap03
			//Hat man solch eine Datenbank mit journal_mode=WAL, dann funktioniert auch der ConnectionPool?
			
			bReturn = this.fillConfigurationGlobal();
			bReturn = this.fillConfigurationMapping();
			//20181111: Wie beim lokalen Zugriff auch, so kann man nur LeseZugriffe hier mit dem Connection Pooling realisieren. Schreibzugriffe brechen ab.
			//Keine Ahnung warum
			bReturn = this.fillConfigurationConnectionPool(); //ConnectionPooling wird ebenfalls durch serverseitige JNDI Configuration bereitgestellt, funktioniert aber auch lokal.
			
			//das wird nicht benötigt, da hier JNDI und Konfiguration der Datenquelle in der der context.xml vorausgesetzt wird
			//bReturn = this.fillConfigurationLocalDb();			
		}
		return bReturn;
	}
}
