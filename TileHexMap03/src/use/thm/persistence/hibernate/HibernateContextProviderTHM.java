package use.thm.persistence.hibernate;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.client.component.AreaCellTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ObjectZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.HibernateContextProviderZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zBasic.util.abstractList.HashMapExtendedZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public class HibernateContextProviderTHM extends HibernateContextProviderZZZ{
	
	
	
//	//Über die EntityManagerFactory erstellte EntityManager werden in dieser Hashmap verwaltet: hm("Name des Schemas/der Datenbank") = objEntityManager;
//	HashMapExtendedZZZ<String, EntityManager> hmEntityManager = new HashMapExtendedZZZ<String, EntityManager>();
//	public HibernateContextProviderTHM() throws ExceptionZZZ{
//		super();
//		boolean bErg = this.fillConfiguration();
//		if(!bErg){
//			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//			throw ez;
//		}
//	}
	
	public HibernateContextProviderTHM() throws ExceptionZZZ{
		super();
	}
//
//	public HibernateContextProviderTHM(KernelZZZ objKernel) throws ExceptionZZZ{
//		super(objKernel);
//		boolean bErg = this.fillConfiguration();
//		if(!bErg){
//			ExceptionZZZ ez = new ExceptionZZZ("Configuration not successfully filled.", iERROR_RUNTIME, this, ReflectCodeZZZ.getMethodCurrentName());
//			throw ez;
//		}
//	}
	public HibernateContextProviderTHM(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	
	/**Fülle die Configuration
	 * a) mit globalen Werten, z.B. Datenbankname, Dialekt
	 * b) mit den zu betrachtenden Klassen, entweder annotiert oder per eigener XML Datei.
	 * 
	 * @return
	 * @throws ExceptionZZZ 
	 */
	public boolean fillConfiguration() throws ExceptionZZZ{
		boolean bReturn = false;
		
		bReturn = fillConfigurationGlobal();
		//+++ Die für Hiberante konfigurierten Klassen hinzufügen
		//Merke: Wird eine Klasse ohne @Entity hinzugefügt, gibt es folgende Fehlermeldung: Exception in thread "main" org.hibernate.AnnotationException: No identifier specified for entity: use.thm.client.component.AreaCellTHM
		//bReturn = addConfigurationAnnotatedClass(HexCell.class);
		//bReturn = addConfigurationAnnotatedClass(AreaCell.class);
		bReturn = addConfigurationAnnotatedClass(AreaCellOcean.class);
		bReturn = addConfigurationAnnotatedClass(AreaCellLand.class);
		
		//bReturn = addConfigurationAnnotatedClass(Tile.class);
		//bReturn = addConfigurationAnnotatedClass(Troop.class);
		bReturn = addConfigurationAnnotatedClass(TroopArmy.class);
		bReturn = addConfigurationAnnotatedClass(TroopFleet.class);
		
		return bReturn;
	}
	
	/** Fülle globale Werte in das Configuration Objekt, z.B. der Datenbankname, Dialekt, etc.
	 * 
	 */
	public boolean fillConfigurationGlobal(){
				//TODO: Die hier verwendeten Werte aus der Kernel-Konfiguration auslesen.
				//Programmatisch das erstellen, das in der hibernate.cfg.xml Datei beschrieben steht.
				//Merke: Irgendwie funktioniert es nicht die Werte in der hibernate.cfg.xml Datei zu überschreiben.
		 		//			Darum muss z.B. hibernate.hbm2ddl.auto in der Konfigurationdatei auskommentiert werden, sonst ziehen hier die Änderungen nicht.
				this.getConfiguration().setProperty("hiberate.show_sql", "true");
				this.getConfiguration().setProperty("hiberate.format_sql", "true");
				this.getConfiguration().setProperty("hibernate.dialect","basic.persistence.hibernate.SQLiteDialect" );
				this.getConfiguration().setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC");
				this.getConfiguration().setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
				this.getConfiguration().setProperty("hibernate.connection.username", "");
				this.getConfiguration().setProperty("hibernate.connection.password", "");

				/*
				 * So the list of possible options are,
    validate: validate the schema, makes no changes to the database.
    update: update the schema.
    create: creates the schema, destroying previous data.
    create-drop: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.
				 */
				this.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create"); //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
				//this.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.
				this.getConfiguration().setProperty("cache.provider_class", "org.hiberniate.cache.NoCacheProvider");
				this.getConfiguration().setProperty("current_session_context_class", "thread");
				
				return true;
	}	
}
