package use.thm.persistence.hibernate;

import org.hibernate.SessionFactory;
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
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.hibernate.HibernateConfigurationProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateConfigurationProviderZZZ;
import basic.zKernel.IKernelConfigSectionEntryZZZ;

public class HibernateConfigurationProviderTHM extends HibernateConfigurationProviderZZZ {
    public HibernateConfigurationProviderTHM() throws ExceptionZZZ{
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
			//Hat man solch eine Datenbank mit journal_mode=WAL, dann funktioniert auch der ConnectionPool? NEIN?
			
			bReturn = this.fillConfigurationGlobal();
			bReturn = this.fillConfigurationMapping();
			//20181111: Wie beim lokalen Zugriff auch, so kann man nur LeseZugriffe hier mit dem Connection Pooling realisieren. Schreibzugriffe brechen ab.
			//Keine Ahnung warum. Habe alle session mit einer Transaction.begin() versehen. Jetzt klappts das Bewegen der Spielsteine.
			//AUSSER BEIM EINFÜGEN EINES NEUEN SPIELSTEINS: 
			//Fehlermeldung: DEBUG com.mchange.v2.resourcepool.BasicResourcePool - acquire test -- pool is already maxed out. [managed: 1; max: 1]
			bReturn = this.fillConfigurationConnectionPool(); //ConnectionPooling wird ebenfalls durch serverseitige JNDI Configuration bereitgestellt, funktioniert aber auch lokal.

			// 20181111: Fehlermeldung bei eingebundem lokalen ConnetionPool, wenn man einen neuen Spielstein hinzufügt:
			//            org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (near "drop": syntax error)
			//            Ich weiss nicht woran das liegt.......... 
			//Vermutlich etwas mit der Erstellung einer neuen Session... oer mit dem auto-modus
			//HiberanteContextProviderTHM.getSessionFactory()
			//                              .....bReturn SessionFactory sf = cfg.buildSessionFactory(sr);
				
			bReturn = this.fillConfigurationLocalDb(); //Merke das wird in einer reinen Serverseitgen Anwendung nicht gebraucht.
			
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
		
		//Einfacher Dialekt this.getConfiguration().setProperty("hibernate.dialect","basic.persistence.hibernate.SQLiteDialect" );
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
		//20180929: Den Pfad aus der Kernel-Konfiguration auslesen....
		//String sDatabaseLocalPath="c:\\server\\SQLite\\TileHexMap03.sqlite";
		KernelSingletonTHM objKernelSingleton = KernelSingletonTHM.getInstance();
		String sDatabaseLocalPath = null;
		IKernelConfigSectionEntryZZZ objEntry = objKernelSingleton.getParameter("DatabaseLocalPath");
		if(!objEntry.hasAnyValue()){
			String serror = "Parameter existiert nicht in der Konfiguration: 'DatabaseLocalPath'";
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +serror);
			ExceptionZZZ ez = new ExceptionZZZ(serror,ExceptionZZZ.iERROR_CONFIGURATION_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}else{
			sDatabaseLocalPath = objEntry.getValue();
		}
		this.getConfiguration().setProperty("hibernate.connection.url", "jdbc:sqlite:"+sDatabaseLocalPath);
		
		
		//20180928 Merke: Derzeit ist es nicht möglich, dass sich der WebService und die Swing-Applikation die gleiche SQLite Datenbank teilen.
		//         Der Lesezugriff klappt über die Swing Applikation auch wenn der WebServer läuft.
		//         Der Schreibzugriff scheitert (hier beim Löschen einer Armee).
		//         Fehlermeldung Caused by: org.sqlite.SQLiteException: [SQLITE_BUSY]  The database file is locked (database is locked)
		//Wenn der Webserver dann beendet ist, klappt´s auch schreibend, ohne die Swing-Applikatio neu zu starten.
        //
		//Umgekehrt funktioniert der (Lese-) Zugriff über SOAP wenn die Swing - Applikation noch läuft
		//this.getConfiguration().setProperty("hibernate.connection.url", "jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03JndiTest.sqlite");
		
		return true;
	}
	
	public boolean fillConfigurationConnectionPool() throws ExceptionZZZ {
		//!!!! DAS FUNKTIONIERT SOWOHL LOKAL als auch bei WebServices
						                                                    
	  //Damit das im WebService gefunden wird auf dem Tomcat Server in der context.xml Datei folgendes einstellen
	  /*
     <ResourceLink name="jdbc/ServicePortal" 
              global="jdbc/ServicePortal"
              auth="Container"
              
              driverClassName="org.sqlite.JDBC"          
              url="jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03JndiTest.sqlite"
              factory="use.thm.persistence.hibernate.HibernateSessionFactoryTomcatFactoryJndiTHM" 
              type="com.mchange.v2.c3p0.ComboPooledDataSource"
              maxPoolSize="1"
  			  minPoolSize="1"   />
		 */
		
		/*
		 <!-- Über Maven wurde dazu entsprechendes .jar eingebunden. Das reicht für WebService nicht.
            Über die WebService Projekteigenschaft Deployment Assembly die 3 notwendigen Bibliotheken aus dem Hibernate optional/c3p0/ Verzeichnis einbinden. 
     														optional/c3p0/c3p0-0.9.2.1.jar
		                                                    optional/c3p0/hibernate-c3p0-4.2.4.Final.jar
		                                                    optional/c3p0/mchange-commons-java-0.2.3.4.jar-->
		                                                    
		//Danach steht im Tocat Sever Log bzgl. des Connection Pools:
		/*
[INFO] HHH000130: Instantiating explicit connection provider: org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider
[INFO] HHH010002: C3P0 using driver: org.sqlite.JDBC at URL: jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03JndiTest.sqlite
[INFO] HHH000046: Connection properties: {user=, password=****}
[INFO] HHH000006: Autocommit mode: false
[INFO] Initializing c3p0 pool... com.mchange.v2.c3p0.PoolBackedDataSource@29803f1b [ connectionPoolDataSource -> com.mchange.v2.c3p0.WrapperConnectionPoolDataSource@fc600dfc [ acquireIncrement -> 3, acquireRetryAttempts -> 30, acquireRetryDelay -> 1000, autoCommitOnClose -> false, automaticTestTable -> null, breakAfterAcquireFailure -> false, checkoutTimeout -> 0, connectionCustomizerClassName -> null, connectionTesterClassName -> com.mchange.v2.c3p0.impl.DefaultConnectionTester, debugUnreturnedConnectionStackTraces -> false, factoryClassLocation -> null, forceIgnoreUnresolvedTransactions -> false, identityToken -> 1hge1lw9zjtdrcewimpj|61f321c6, idleConnectionTestPeriod -> 300, initialPoolSize -> 1, maxAdministrativeTaskTime -> 0, maxConnectionAge -> 0, maxIdleTime -> 1800, maxIdleTimeExcessConnections -> 0, maxPoolSize -> 1, maxStatements -> 50, maxStatementsPerConnection -> 0, minPoolSize -> 1, nestedDataSource -> com.mchange.v2.c3p0.DriverManagerDataSource@17a6675b [ description -> null, driverClass -> null, factoryClassLocation -> null, identityToken -> 1hge1lw9zjtdrcewimpj|337e9c58, jdbcUrl -> jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03JndiTest.sqlite, properties -> {user=******, password=******} ], preferredTestQuery -> null, propertyCycle -> 0, statementCacheNumDeferredCloseThreads -> 0, testConnectionOnCheckin -> false, testConnectionOnCheckout -> false, unreturnedConnectionTimeout -> 0, usesTraditionalReflectiveProxies -> false; userOverrides: {} ], dataSourceName -> null, factoryClassLocation -> null, identityToken -> 1hge1lw9zjtdrcewimpj|73effb35, numHelperThreads -> 3 ]
		 */
		
		/*Danach steht im der Standalone Java Applikation bzgl. des Connectin Pools:
2018-11-11 10:13:31,812 [main] INFO  org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator - HHH000130: Instantiating explicit connection provider: org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider
2018-11-11 10:13:31,826 [main] INFO  org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider - HHH010002: C3P0 using driver: org.sqlite.JDBC at URL: jdbc:sqlite:c:\server\SQLite\TileHexMap03.sqlite
2018-11-11 10:13:31,826 [main] INFO  org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider - HHH000046: Connection properties: {user=, password=****}
2018-11-11 10:13:31,826 [main] INFO  org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider - HHH000006: Autocommit mode: false
2018-11-11 10:13:31,849 [main] INFO  com.mchange.v2.log.MLog - MLog clients using log4j logging.
2018-11-11 10:13:32,101 [main] INFO  com.mchange.v2.c3p0.C3P0Registry - Initializing c3p0-0.9.2.1 [built 20-March-2013 10:47:27 +0000; debug? true; trace: 10]
2018-11-11 10:13:32,120 [main] DEBUG com.mchange.v2.c3p0.management.DynamicPooledDataSourceManagerMBean - MBean: com.mchange.v2.c3p0:type=PooledDataSource,identityToken=1hge1lw9zkgaoc1vvx0j2|3d6a8d4c,name=1hge1lw9zkgaoc1vvx0j2|3d6a8d4c registered.
2018-11-11 10:13:32,203 [main] INFO  com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource - Initializing c3p0 pool... com.mchange.v2.c3p0.PoolBackedDataSource@1cd21ac [ connectionPoolDataSource -> com.mchange.v2.c3p0.WrapperConnectionPoolDataSource@ba26e7dc [ acquireIncrement -> 3, acquireRetryAttempts -> 30, acquireRetryDelay -> 1000, autoCommitOnClose -> false, automaticTestTable -> null, breakAfterAcquireFailure -> false, checkoutTimeout -> 0, connectionCustomizerClassName -> null, connectionTesterClassName -> com.mchange.v2.c3p0.impl.DefaultConnectionTester, debugUnreturnedConnectionStackTraces -> false, factoryClassLocation -> null, forceIgnoreUnresolvedTransactions -> false, identityToken -> 1hge1lw9zkgaoc1vvx0j2|7aa3107c, idleConnectionTestPeriod -> 300, initialPoolSize -> 1, maxAdministrativeTaskTime -> 0, maxConnectionAge -> 0, maxIdleTime -> 1800, maxIdleTimeExcessConnections -> 0, maxPoolSize -> 1, maxStatements -> 50, maxStatementsPerConnection -> 0, minPoolSize -> 1, nestedDataSource -> com.mchange.v2.c3p0.DriverManagerDataSource@ba789932 [ description -> null, driverClass -> null, factoryClassLocation -> null, identityToken -> 1hge1lw9zkgaoc1vvx0j2|1cd2efb2, jdbcUrl -> jdbc:sqlite:c:\server\SQLite\TileHexMap03.sqlite, properties -> {user=******, password=******} ], preferredTestQuery -> null, propertyCycle -> 0, statementCacheNumDeferredCloseThreads -> 0, testConnectionOnCheckin -> false, testConnectionOnCheckout -> false, unreturnedConnectionTimeout -> 0, usesTraditionalReflectiveProxies -> false; userOverrides: {} ], dataSourceName -> null, factoryClassLocation -> null, identityToken -> 1hge1lw9zkgaoc1vvx0j2|3d6a8d4c, numHelperThreads -> 3 ]
2018-11-11 10:13:32,224 [main] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - incremented pending_acquires: 1
2018-11-11 10:13:32,225 [main] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - Starting acquisition series. Incremented pending_acquires [1],  attempts_remaining: 30
2018-11-11 10:13:32,225 [main] DEBUG com.mchange.v2.async.ThreadPoolAsynchronousRunner - com.mchange.v2.async.ThreadPoolAsynchronousRunner@4c74da4e: Adding task to queue -- com.mchange.v2.resourcepool.BasicResourcePool$ScatteredAcquireTask@ba85e19
2018-11-11 10:13:32,225 [main] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - com.mchange.v2.resourcepool.BasicResourcePool@5ab9c149 config: [start -> 1; min -> 1; max -> 1; inc -> 3; num_acq_attempts -> 30; acq_attempt_delay -> 1000; check_idle_resources_delay -> 300000; mox_resource_age -> 0; max_idle_time -> 1800000; excess_max_idle_time -> 0; destroy_unreturned_resc_time -> 0; expiration_enforcement_delay -> 450000; break_on_acquisition_failure -> false; debug_store_checkout_exceptions -> false]
2018-11-11 10:13:32,226 [main] DEBUG com.mchange.v2.c3p0.impl.C3P0PooledConnectionPoolManager - Created new pool for auth, username (masked): ''.
2018-11-11 10:13:32,226 [main] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - acquire test -- pool size: 0; target_pool_size: 1; desired target? 1
2018-11-11 10:13:32,226 [main] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - awaitAvailable(): [unknown]
2018-11-11 10:13:32,226 [main] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - trace com.mchange.v2.resourcepool.BasicResourcePool@5ab9c149 [managed: 0, unused: 0, excluded: 0]
2018-11-11 10:13:32,493 [C3P0PooledConnectionPoolManager[identityToken->1hge1lw9zkgaoc1vvx0j2|3d6a8d4c]-HelperThread-#1] DEBUG com.mchange.v2.c3p0.stmt.GooGooStatementCache - checkinAll(): com.mchange.v2.c3p0.stmt.GlobalMaxOnlyStatementCache stats -- total size: 0; checked out: 0; num connections: 0; num keys: 0
2018-11-11 10:13:32,493 [C3P0PooledConnectionPoolManager[identityToken->1hge1lw9zkgaoc1vvx0j2|3d6a8d4c]-HelperThread-#1] DEBUG com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool - com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool$1PooledConnectionResourcePoolManager@32e716eb.acquireResource() returning. 
2018-11-11 10:13:32,494 [C3P0PooledConnectionPoolManager[identityToken->1hge1lw9zkgaoc1vvx0j2|3d6a8d4c]-HelperThread-#1] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - trace com.mchange.v2.resourcepool.BasicResourcePool@5ab9c149 [managed: 1, unused: 1, excluded: 0]
2018-11-11 10:13:32,494 [C3P0PooledConnectionPoolManager[identityToken->1hge1lw9zkgaoc1vvx0j2|3d6a8d4c]-HelperThread-#1] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - decremented pending_acquires: 0
2018-11-11 10:13:32,494 [C3P0PooledConnectionPoolManager[identityToken->1hge1lw9zkgaoc1vvx0j2|3d6a8d4c]-HelperThread-#1] DEBUG com.mchange.v2.resourcepool.BasicResourcePool - Acquisition series terminated successfully. Decremented pending_acquires [0],  attempts_remaining: 30		
		 */
		
		
		//################################################
		//Connection Pool aktivieren!!!!
		//Merke: Wenn man hier die URL definiert und man will Connection Pooling, dann muss man auch das Connection Pooling hier im programmatisch definieren. 
		//Ansonsten findet das Connectin Pooling wichtige Informationen nicht
				/* DEBUG com.mchange.v2.resourcepool.BasicResourcePool - An exception occurred while acquiring a poolable resource. Will retry.
								java.lang.NullPointerException
												at sun.jdbc.odbc.JdbcOdbcDriver.getProtocol(Unknown Source) */
						
				this.getConfiguration().setProperty("hibernate.connection.provider_class","org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
				this.getConfiguration().setProperty("hibernate.connection.pool_size","5");
				this.getConfiguration().setProperty("hibernate.c3p0.idle_test_period","300"); // In seconds   
				this.getConfiguration().setProperty("hibernate.c3p0.timeout","1800"); 
				this.getConfiguration().setProperty("hibernate.c3p0.max_statements","50");
		        
				//Connection Pool auf 1 beschränken. Aber das liefert beim Einfügen von Spielsteinen einen Fehler.
				//Also auf 2 Setzen. 
				//Dann bekommt man beim Starten aber folgenden Fehler:
				/*
				 2018-11-13 12:13:09,753 [main] DEBUG org.hibernate.tool.hbm2ddl.SchemaUpdate - alter table ARMYVARIANT drop constraint UK_cjy10hmy2dp615iaa655nru0a
				 2018-11-13 12:13:09,763 [main] DEBUG com.mchange.v2.c3p0.impl.NewPooledConnection - com.mchange.v2.c3p0.impl.NewPooledConnection@1c0644a handling a throwable.
				 org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (near "drop": syntax error)
				 */
				
				//Auf 1 Setzen wäre eine  Idee aus: https://stackoverflow.com/questions/29364837/hibernate-sqlite-sqlite-busy           
				//Ziel soll es sein keine "Database is locked" mehr zu bekommen
				this.getConfiguration().setProperty("hibernate.c3p0.min_size","1");
				this.getConfiguration().setProperty("hibernate.c3p0.max_size","2");
			
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
