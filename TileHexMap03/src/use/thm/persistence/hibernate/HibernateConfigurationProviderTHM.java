package use.thm.persistence.hibernate;

import org.hibernate.cfg.Configuration;

import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.TextImmutabletext;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileDefaulttextValue;
import use.thm.persistence.model.TileImmutabletext;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import use.thm.persistence.model.TroopFleetVariant;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.persistence.hibernate.HibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;

public class HibernateConfigurationProviderTHM extends HibernateConfigurationProviderZZZ {
    public HibernateConfigurationProviderTHM() throws ExceptionZZZ{
    	super();
    }
	
	@Override
	public boolean fillConfiguration() throws ExceptionZZZ {
		boolean bReturn = false;
		main:{
			bReturn = this.fillConfigurationGlobal();
			bReturn = this.fillConfigurationMapping();
			bReturn = this.fillConfigurationLocalDb();
		}
		return bReturn;
	}

	@Override
	public boolean fillConfigurationGlobal() throws ExceptionZZZ {
		//TODO: Die hier verwendeten Werte aus der Kernel-Konfiguration auslesen.
		//Programmatisch das erstellen, das in der hibernate.cfg.xml Datei beschrieben steht.
		//Merke: Irgendwie funktioniert es nicht die Werte in der hibernate.cfg.xml Datei zu überschreiben.
 		//		 Darum muss z.B. hibernate.hbm2ddl.auto in der Konfigurationdatei auskommentiert werden, sonst ziehen hier die Änderungen nicht.
		this.getConfiguration().setProperty("hiberate.show_sql", "true");
		this.getConfiguration().setProperty("hiberate.format_sql", "true");
		//Einfacher Dialtekt this.getConfiguration().setProperty("hibernate.dialect","basic.persistence.hibernate.SQLiteDialect" );
		this.getConfiguration().setProperty("hibernate.dialect","basic.persistence.hibernate.SQLiteDialect" );//Per Maven eingbundener Dialekt:			
		this.getConfiguration().setProperty("hibernate.connection.username", "");
		this.getConfiguration().setProperty("hibernate.connection.password", "");

		/*
		 * So the list of possible options are,
validate: validate the schema, makes no changes to the database.
update: update the schema.
create: creates the schema, destroying previous data.
create-drop: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.
		 */
		
		//Merke: Damit also die Werte in den Tabellen bleiben, muss man nach der Initialisierung des HibernateContextProviderObjekts explizit die "Speicherung" aktivieren.
		//       Und zwar so: objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.						
		this.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create"); //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
		//this.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.
		this.getConfiguration().setProperty("cache.provider_class", "org.hiberniate.cache.NoCacheProvider");
		this.getConfiguration().setProperty("current_session_context_class", "thread");				
		return true;
	}

	@Override
	public boolean fillConfigurationLocalDb() throws ExceptionZZZ {
		this.getConfiguration().setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
		this.getConfiguration().setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
		return true;
	}

	@Override
	public boolean fillConfigurationMapping() throws ExceptionZZZ {
		boolean bReturn = false;
	
		//+++ Die für Hiberante konfigurierten Klassen hinzufügen
		//Merke1: Wird eine Klasse ohne @Entity hinzugefügt, gibt es folgende Fehlermeldung: Exception in thread "main" org.hibernate.AnnotationException: No identifier specified for entity: use.thm.client.component.AreaCellTHM
		//Merke2: Auch die vererbenden Klassen/Entities wie z.B. HexCell hier aufnehmen. Ggfs. wird diese in einer HQL Abfrage verwendet.
		
		//Merke3: Wg. untenstehender Fehlermeldungen auch die hibernate.cfg.xml Datei weiter pflegen mit allen gemappten Entities. 
		//Exception in thread "main" java.lang.IllegalArgumentException: org.hibernate.hql.internal.ast.QuerySyntaxException: HexCell is not mapped [SELECT MAX(c.id.sMapX) FROM HexCell c]
		//Caused by: org.hibernate.AnnotationException: Use of @OneToMany or @ManyToMany targeting an unmapped class: use.thm.persistence.model.HexCell.objbagTile[use.thm.persistence.model.Tile]
		bReturn = addConfigurationAnnotatedClass(HexCell.class);
		bReturn = addConfigurationAnnotatedClass(AreaCell.class);		
		bReturn = addConfigurationAnnotatedClass(AreaCellOcean.class);
		bReturn = addConfigurationAnnotatedClass(AreaCellLand.class);
		
		bReturn = addConfigurationAnnotatedClass(Tile.class);
		bReturn = addConfigurationAnnotatedClass(Troop.class);
		bReturn = addConfigurationAnnotatedClass(TroopArmy.class);
		bReturn = addConfigurationAnnotatedClass(TroopFleet.class);
		
		bReturn = addConfigurationAnnotatedClass(TileDefaulttextValue.class);//wird aber nicht genutz. Nur Demonstrator
		
		bReturn = addConfigurationAnnotatedClass(Defaulttext.class);
		bReturn = addConfigurationAnnotatedClass(TextDefaulttext.class);
		bReturn = addConfigurationAnnotatedClass(TileDefaulttext.class);
		
		bReturn = addConfigurationAnnotatedClass(Immutabletext.class);
		bReturn = addConfigurationAnnotatedClass(TileImmutabletext.class);
		bReturn = addConfigurationAnnotatedClass(TextImmutabletext.class);
		
		bReturn = addConfigurationAnnotatedClass(TroopArmyVariant.class);
		bReturn = addConfigurationAnnotatedClass(TroopFleetVariant.class);
		
		//FGL 20170409: Versuch Callbacks in Hibernate
		//this.getConfiguration().setListener("persist",new TroopArmyListener());
		
		return bReturn;
	}
}
