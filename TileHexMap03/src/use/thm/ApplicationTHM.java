package use.thm;

import java.io.File;
import java.util.HashMap;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.KernelReportContextProviderZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import use.thm.client.FrmMapSingletonTHM;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.dao.TileImmutabletextDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.TroopArmy;

public class ApplicationTHM extends KernelUseObjectZZZ{
	private String sBaseDirectoryImages = null;
	
	public ApplicationTHM(KernelZZZ objKernel) throws ExceptionZZZ{
		super(objKernel);
	}
	public ApplicationTHM(){
		super(); 
	}
	
	public boolean launchIt() throws ExceptionZZZ {
		ReportLogZZZ.write(ReportLogZZZ.DEBUG, "launch - thread: " + ". ApplicationSingleton....");
		main:{
			KernelZZZ objKernel = this.getKernelObject();
			FrmMapSingletonTHM frameInfo = FrmMapSingletonTHM.getInstance(objKernel, null);
				
			//---- Bereite Hibernate und die SQLite Datenbank vor.
			//Die eigentliche Session und Configuration wird in der Klasse use.thm.client.hibernate.HibernateContextProviderTHM gemacht.
			//Hier könnten dann noch vorgelagerte Dinge gemacht werden.
			
			//### THEMA PERSISTIERUNG IN EINER DATENBANK
			//Merke: 20180326: Das ist auf Applikationsebene verschoben worden, da nun das Panel_WEST auf die Varianten zugreifen soll,
		    //                         die ggfs. jetzt erst befüllt werden.
			
			//TODO GOON 20180111: Nun werden weitere Informationen in die Datenbank gefüllt und nicht nur die Karte gefüllt.
			//Daher schon an dieser Stelle prüfen, ob die Datenbank existiert. Die entsprechenden Methoden dann mit einem neuen Parameter (bDatabaseNew) versehen.
			//IDEE: Das Füllen der Schlüsselwerttabellen (Default- / Immutabletexte /Troopvarianten) sogar noch eher machen,
			//      weil ggfs. die Datensätze daraus zum Aufbau auch anderer(!) Panels (Buttontexte, zur Verfügung stehende Truppen, ...) benötigt werden.
			//
			//Den Namen der Datenbank/des Schemas aus der Kernelkonfiguration holen.
			//HibernateContextProviderSingletonTHM objContextHibernate = new HibernateContextProviderSingletonTHM(this.getKernelObject());
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());
									
			//Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.			
			boolean bDbExists = SQLiteUtilZZZ.databaseFileExists(objContextHibernate);	
			if(bDbExists){			
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");										
			}else{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");
			}
			//objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
			objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.
			
			boolean bSuccessDefaulttext = fillDefaulttextAll(bDbExists);
			boolean bSuccessImmutabletext = fillImmutabletextAll(bDbExists);
			boolean bSuccessTroopArmyVariant = fillTroopArmyVariantAll(bDbExists);
			boolean bSuccessTroopFleetVariant = fillTroopFleetVariantAll(bDbExists);
				
			//Falls Datenbank nicht existierte, gilt das hier als neue Datenbank....
			this.setFlag(FLAGZ.DATABASE_NEW.name(), !bDbExists);
			
			//---- Bereite das Reporten über Log4J vor...
			//KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frmMain.getClass().getName(), frmMain.getClass().getName());					
			KernelReportContextProviderZZZ objContext = new KernelReportContextProviderZZZ(objKernel, frameInfo.getClass().getName());  //Damit ist das ein Context Provider, der die Informationen auf "Modulebene" sucht.
			ReportLogZZZ.loadKernelContext(objContext, true);  //Mit dem true bewirkt man, dass das file immer neu aus dem ConfigurationsPattern erzeugt wird.		
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Launching Application. Start of main-frame");
					
			//---- Starte den Frame
			boolean bLaunched = frameInfo.launch(objKernel.getApplicationKey() + " - Client (Map)");
			if(bLaunched == true){
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: Launch 'TileHexMap', was successfull");
				
				boolean bCentered = frameInfo.centerOnParent();
				if(bCentered==true){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Performing action: CenterOnParent 'TileHexMap', was successfull");					
				}else{
					ReportLogZZZ.write(ReportLogZZZ.ERROR, "Performing action: CenterOnParent 'TileHexMap', was NOT successfull");	
				}
				
			}
			
			/*Merke: Dieser Code wird vor dem Fensterstart ausgeführt. Nur möglich, weil der EventDispatcher-Code nebenl�ufig ausgeführt.... wird.
			//            Und das ist nur möglich, wenn das der "Erste Frame/ der Hauptframe" der Applikation ist.
			 */
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "##############################################");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "#### TESTE MULTITHREADING #####################");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "##############################################");				
			try{			
				for(int icount = 0; icount <= 10; icount++){
					ReportLogZZZ.write(ReportLogZZZ.DEBUG, "main - thread (actionPerformed): " + icount + ". doing something....");
					Thread.sleep(10);
				}
			}catch(InterruptedException ie){		
				throw new ExceptionZZZ(ie.getMessage(), iERROR_RUNTIME, this, ReflectCodeZZZ.getPositionCurrent());
			} 
			
			
			//########################
			//TEST TESTE
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "##############################################");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "#### TESTE ABSCHLIESSEND #####################");
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + "##############################################");
				
			//HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());
			//objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
				
			TroopArmyDao daoTroop = new TroopArmyDao(objContextHibernate);
			int iTroopCounted = daoTroop.count();
			System.out.println("Es gibt platzierte Armeen: " + iTroopCounted);
			
			Integer primaryKey = new Integer(2);
			TroopArmy objTroopTemp = (TroopArmy) daoTroop.findById(primaryKey.intValue());		
			if(objTroopTemp==null){
			System.out.println("Es gibt keine Troop mit der ID= "+primaryKey.intValue());	
			}else{
				System.out.println("Troop mit der ID = " + primaryKey.intValue() + " hat als Uniquename()= "+objTroopTemp.getUniquename());
				System.out.println(".... wurde erstellt:" + objTroopTemp.getCreatedThisAt());
				System.out.println(".... wurde erstellt (als String):" + objTroopTemp.getCreatedThisAtString());
						
				System.out.println(".... wurde erstellt (null Übergabe):" + objTroopTemp.getCreatedThis());
				System.out.println(".... wurde erstellt (als String mit null Übergabe):" + objTroopTemp.getCreatedThisString());				
				System.out.println(".... wurde erstellt (als String mit Kommentar):" + objTroopTemp.getCreatedThisStringComment());
				System.out.println(".... wurde erstellt (als String mit validierter Übergabe):" + objTroopTemp.getCreatedThisAtStringValid());
				
				System.out.println(".... wurde aktualisiert:" + objTroopTemp.getUpdated());
				
				HexCell objHexCell = objTroopTemp.getHexCell();
			if(objHexCell==null){
				System.out.println("Es gibt keine HexCell für diese Troop");	
			}else{
				System.out.println("HexCell für diese Troop ist: " + objHexCell.getFieldAlias());
			}
			}
			
			//### PROBLEM: Nach dem Einlesen der Datenbank bleibt diese "loaded",
			//                        Das wirft entsprechenden Fehler wenn man danach mit einer Debug Dao-Klasse z.B. Texte einlesen will (DebugKeyTable_Version_TileDefaulttextTHM.java).
			//+++ Lösungsansatz 20180309: Schliesse alles im objContextSingleton Objekt.
			//objContextHibernate.closeAll();
			//ABER: Anschliessend gilt die Database als "locked"??? Wenn man hier weiterarbeitet.

		}//end main;
		return true;
	}
	
	//#### METHODEN FÜR PFADE
	public String getBaseDirectoryStringForImages(){
		if(StringZZZ.isEmpty(this.sBaseDirectoryImages)){
			
			//Eclipse Workspace 
			File f = new File("");
		    String sPathEclipse = f.getAbsolutePath();
		    ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Eclipse absolut path: " + sPathEclipse);
	       //String sPathParent = sPathEclipse.substring(0, sPathEclipse.lastIndexOf(System.getProperty("file.separator")));
		    
	       String sBaseDirectory = sPathEclipse + File.separator + "images";
	       this.setBaseDirectoryStringForImages(sBaseDirectory);		       
		}
		return this.sBaseDirectoryImages;
	}
	public void setBaseDirectoryStringForImages(String sBaseDirectory){
		this.sBaseDirectoryImages = sBaseDirectory;
	}
	
	//#### METHODEN DER DATENBANKINITIALISIERUNG
	//### DEFAULTTEXTE ###########################################################
		public boolean fillDefaulttextAll() throws ExceptionZZZ{
			return this.fillDefaulttextAll(false);
		}
		
		public boolean fillDefaulttextAll(boolean bDbExists) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				boolean bFillDatabaseNew = true;
				
				//Kernel Objekt
				KernelZZZ objKernel = this.getKernelObject();
							
				//Der HibernateContext ist ein Singleton Objekt, darum braucht man ihn nicht als Parameter im Methodenaufruf weitergeben.
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());			
				if(bDbExists){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");
					
					//Momentan passiert noch nichts mit den Defaulttexten, also kein Auslesen und ggfs. irgendwoanders hineinfüllen...
					bFillDatabaseNew = false;
				}else{
					//Fall: Datenbank existiert noch nicht
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");						
					bFillDatabaseNew=true;
				}//end if bDbExists
				
				if(bFillDatabaseNew){
					if(bDbExists){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert zwar, es hat aber Probleme beim Einlesen gegeben.");
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank sollte gelöscht werden, damit der Neuaufbau keine Probleme bekommt.");
						//	TODO ggfs. zur Sicherheit die gesamte Datenbankdatei löschen, was aber nur geht, wenn z.B. kein anderer Client darauf zugreift. Vor dem endgültigen Löschen immer ein Backup machen.
						
					}
					
					//Erzeuge neuen Datenbankinhalte:				
					//Per Hibernate & Session 
					int iTileDefaultTextCreated = fillTileDefaulttext_createNew(objContextHibernate);
					
					//Per EntityManager, aber das hat Probleme, zumindest mit SQLITE und den @TableGenerator Annotations zum automatischen Erstellen von IDs  
					//bReturn = fillMap_createNew_ENTITYMANAGER_EXAMPLE(objContextHibernate, panelMap);
				}else{
					bReturn = true;
				}
			}//end main:		
			return bReturn;
		}
		
		private int fillTileDefaulttext_createNew(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
			int iReturn = 0;
			main:{								
				TileDefaulttextDao daoTileText = new TileDefaulttextDao(objContextHibernate);	
				iReturn = daoTileText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TileDefaultTexte: " + iReturn);

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");						
			}//end main:
			return iReturn;	
		}
		
		//### IMMUTABLETEXTE #############################################################################
		public boolean fillImmutabletextAll() throws ExceptionZZZ{
			return this.fillImmutabletextAll(false);
		}
		public boolean fillImmutabletextAll(boolean bDbExists) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				boolean bFillDatabaseNew = true;
				
				//Kernel Objekt
				KernelZZZ objKernel = this.getKernelObject();
							
				//Der HibernateContext ist ein Singleton Objekt, darum braucht man ihn nicht als Parameter im Methodenaufruf weitergeben.
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());			
				if(bDbExists){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");
					
					//Momentan passiert noch nichts mit den Defaulttexten, also kein Auslesen und ggfs. irgendwoanders hineinfüllen...
					bFillDatabaseNew = false;
				}else{
					//Fall: Datenbank existiert noch nicht
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");						
					bFillDatabaseNew=true;
				}//end if bDbExists
				
				if(bFillDatabaseNew){
					if(bDbExists){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert zwar, es hat aber Probleme beim Einlesen gegeben.");
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank sollte gelöscht werden, damit der Neuaufbau keine Probleme bekommt.");
						//	TODO ggfs. zur Sicherheit die gesamte Datenbankdatei löschen, was aber nur geht, wenn z.B. kein anderer Client darauf zugreift. Vor dem endgültigen Löschen immer ein Backup machen.
						
					}
					
					//Erzeuge neuen Datenbankinhalte:				
					//Per Hibernate & Session 
					int iTileImmutbleTextCreated = fillTileImmutabletext_createNew(objContextHibernate);
					
					//Per EntityManager, aber das hat Probleme, zumindest mit SQLITE und den @TableGenerator Annotations zum automatischen Erstellen von IDs  
					//bReturn = fillMap_createNew_ENTITYMANAGER_EXAMPLE(objContextHibernate, panelMap);
				}else{
					bReturn = true;
				}
			}//end main:		
			return bReturn;
		}
				
		private int fillTileImmutabletext_createNew(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
			int iReturn = 0;
			main:{							
				TileImmutabletextDao daoTileText = new TileImmutabletextDao(objContextHibernate);	
				iReturn = daoTileText.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TileImmutableTexte: " + iReturn);

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");						
			}//end main:
			return iReturn;	
		}

		
	
	//#### GETTER SETTER
	
	//### FlagMethods ##########################
	public enum FLAGZ{
		DATABASE_NEW,TERMINATE; //Merke: DEBUG und INIT aus ObjectZZZ sollen über IObjectZZZ eingebunden werden, weil von ObjectkZZZ kann man ja nicht erben. Es wird schon von File geerbt.
	}
	private HashMap<String, Boolean>hmFlag = new HashMap<String, Boolean>(); //Neu 20130721 ersetzt die einzelnen Flags, irgendwann...
		
	
		@Override
		public Class getClassFlagZ() {		
			return FLAGZ.class;
		}

		public HashMap<String, Boolean>getHashMapFlagZ(){
			return this.hmFlag;
		} 
		
		/* @see basic.zBasic.IFlagZZZ#getFlagZ(java.lang.String)
		 * 	 Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen müssen auch public deklariert werden.(non-Javadoc)
		 */
		@Override
		public boolean getFlagZ(String sFlagName) {
			boolean bFunction = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName)) break main;
											
				HashMap<String, Boolean> hmFlag = this.getHashMapFlagZ();
				Boolean objBoolean = hmFlag.get(sFlagName.toUpperCase());
				if(objBoolean==null){
					bFunction = false;
				}else{
					bFunction = objBoolean.booleanValue();
				}
								
			}	// end main:
			
			return bFunction;	
		}
		
		
		/**
		 * @param sFlagName
		 * @return
		 * lindhaueradmin, 06.07.2013
		 */
		public boolean getFlag(String sFlagName) {
//			boolean bFunction = false;
//			main:{
//				if(sFlagName == null) break main;
//				if(sFlagName.equals("")) break main;
//				
//				// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
//				// bFunction = super.getFlag(sFlagName);
//				// if(bFunction == true) break main;
//				
//				// Die Flags dieser Klasse setzen
//				String stemp = sFlagName.toLowerCase();
//				if(stemp.equals("debug")){
//					bFunction = this.bFlagDebug;
//					break main;
//				}else if(stemp.equals("init")){
//					bFunction = this.bFlagInit;
//					break main;
//				}else if(stemp.equals("terminate")){
//					bFunction = this.bFlagTerminate;
//				}else if(stemp.equals("isdraggable")){
//					bFunction = this.flagComponentDraggable;
//				}else if(stemp.equals("iskernelprogram")){
//					bFunction = this.flagComponentKernelProgram;
//				}else{
//					bFunction = false;
//				}		
//			}	// end main:
//			
//			return bFunction;
			return this.getFlagZ(sFlagName);
			}

		/** DIESE METHODE MUSS IN ALLEN KLASSEN VORHANDEN SEIN - über Vererbung -, DIE IHRE FLAGS SETZEN WOLLEN
		 * Weteire Voraussetzungen:
		 * - Public Default Konstruktor der Klasse, damit die Klasse instanziiert werden kann.
		 * - Innere Klassen müssen auch public deklariert werden.
		 * @param objClassParent
		 * @param sFlagName
		 * @param bFlagValue
		 * @return
		 * lindhaueradmin, 23.07.2013
		 */
		@Override
		public boolean setFlagZ(String sFlagName, boolean bFlagValue) throws ExceptionZZZ {
			boolean bFunction = false;
			main:{
				if(StringZZZ.isEmpty(sFlagName)) break main;
				

				bFunction = this.proofFlagZExists(sFlagName);												
				if(bFunction == true){
					
					//Setze das Flag nun in die HashMap
					HashMap<String, Boolean> hmFlag = this.getHashMapFlagZ();
					hmFlag.put(sFlagName.toUpperCase(), bFlagValue);
					bFunction = true;								
				}										
			}	// end main:
			
			return bFunction;	
		}
		
		/**
		 * @param sFlagName
		 * @param bFlagValue
		 * @return
		 * lindhaueradmin, 06.07.2013
		 * @throws ExceptionZZZ 
		 */
		public boolean setFlag(String sFlagName, boolean bFlagValue)  {
//			boolean bFunction = true;
//			main:{
//				if(sFlagName == null) break main;
//				if(sFlagName.equals("")) break main;
//				
//				// hier keine Superclass aufrufen, ist ja schon ObjectZZZ
//				// bFunction = super.setFlag(sFlagName, bFlagValue);
//				// if(bFunction == true) break main;
//				
//				// Die Flags dieser Klasse setzen
//				String stemp = sFlagName.toLowerCase();
//				if(stemp.equals("debug")){
//					this.bFlagDebug = bFlagValue;
//					bFunction = true;                            //durch diesen return wert kann man "reflexiv" ermitteln, ob es in dem ganzen hierarchie-strang das flag �berhaupt gibt !!!
//					break main;
//				}else if(stemp.equals("init")){
//					this.bFlagInit = bFlagValue;
//					bFunction = true;
//					break main;
//				}else if(stemp.equals("terminate")){
//					this.bFlagTerminate = bFlagValue;
//					bFunction = true;
//					break main;
//				}else if(stemp.equals("isdraggabel")){
//					this.flagComponentDraggable = bFlagValue;
//					bFunction = true;
//					break main;
//				}else if(stemp.equals("iskernelprogram")){
//					this.flagComponentKernelProgram = bFlagValue;
//					bFunction = true;
//					break main;
//				}else{
//					bFunction = false;
//				}	
//				
//			}	// end main:
//			
//			return bFunction;
			try{
				return this.setFlagZ(sFlagName, bFlagValue);
			} catch (ExceptionZZZ e) {
				System.out.println("ExceptionZZZ (aus compatibilitaetgruenden mit Version vor Java 6 nicht weitergereicht) : " + e.getDetailAllLast());
				return false;
			}
		}
	
		//### TROOPARMYVARIANT #############################################################################
		public boolean fillTroopArmyVariantAll() throws ExceptionZZZ{
			return this.fillTroopArmyVariantAll(false);
		}
		public boolean fillTroopArmyVariantAll(boolean bDbExists) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				boolean bFillDatabaseNew = true;
				
				//Kernel Objekt
				KernelZZZ objKernel = this.getKernelObject();
							
				//Der HibernateContext ist ein Singleton Objekt, darum braucht man ihn nicht als Parameter im Methodenaufruf weitergeben.
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());			
				if(bDbExists){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");
					
					//Momentan passiert noch nichts mit den Defaulttexten, also kein Auslesen und ggfs. irgendwoanders hineinfüllen...
					bFillDatabaseNew = false;
				}else{
					//Fall: Datenbank existiert noch nicht
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");						
					bFillDatabaseNew=true;
				}//end if bDbExists
				
				if(bFillDatabaseNew){
					if(bDbExists){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert zwar, es hat aber Probleme beim Einlesen gegeben.");
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank sollte gelöscht werden, damit der Neuaufbau keine Probleme bekommt.");
						//	TODO ggfs. zur Sicherheit die gesamte Datenbankdatei löschen, was aber nur geht, wenn z.B. kein anderer Client darauf zugreift. Vor dem endgültigen Löschen immer ein Backup machen.
						
					}
					
					//Erzeuge neuen Datenbankinhalte:					
					//Per Hibernate & Session 
					int iTroopArmyVariantCreated = fillTroopArmyVariant_createNew(objContextHibernate);
					
					//Per EntityManager, aber das hat Probleme, zumindest mit SQLITE und den @TableGenerator Annotations zum automatischen Erstellen von IDs  
					//bReturn = fillMap_createNew_ENTITYMANAGER_EXAMPLE(objContextHibernate, panelMap);
				}else{
					bReturn = true;
				}
			}//end main:		
			return bReturn;
		}
		
		private int fillTroopArmyVariant_createNew(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
			int iReturn = 0;
			main:{																											
				TroopArmyVariantDao daoTroopArmy = new TroopArmyVariantDao(objContextHibernate);	
				iReturn = daoTroopArmy.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TroopArmyVarianten: " + iReturn);

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");								
			}//end main:
			return iReturn;	
		}
		
		
		
		//### TROOFLEETVARIANT #############################################################################
		public boolean fillTroopFleetVariantAll() throws ExceptionZZZ{
			return this.fillTroopFleetVariantAll(false);
		}
		public boolean fillTroopFleetVariantAll(boolean bDbExists) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				boolean bFillDatabaseNew = true;
				
				//Kernel Objekt
				KernelZZZ objKernel = this.getKernelObject();
							
				//Der HibernateContext ist ein Singleton Objekt, darum braucht man ihn nicht als Parameter im Methodenaufruf weitergeben.
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());			
				if(bDbExists){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");
					
					//Momentan passiert noch nichts mit den Defaulttexten, also kein Auslesen und ggfs. irgendwoanders hineinfüllen...
					bFillDatabaseNew = false;
				}else{
					//Fall: Datenbank existiert noch nicht
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");						
					bFillDatabaseNew=true;
				}//end if bDbExists
				
				if(bFillDatabaseNew){
					if(bDbExists){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert zwar, es hat aber Probleme beim Einlesen gegeben.");
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank sollte gelöscht werden, damit der Neuaufbau keine Probleme bekommt.");
						//	TODO ggfs. zur Sicherheit die gesamte Datenbankdatei löschen, was aber nur geht, wenn z.B. kein anderer Client darauf zugreift. Vor dem endgültigen Löschen immer ein Backup machen.
						
					}
					
					//Erzeuge neuen Datenbankinhalte:					
					//Per Hibernate & Session 
					int iTroopFleetVariantCreated = fillTroopFleetVariant_createNew(objContextHibernate);
					
					//Per EntityManager, aber das hat Probleme, zumindest mit SQLITE und den @TableGenerator Annotations zum automatischen Erstellen von IDs  
					//bReturn = fillMap_createNew_ENTITYMANAGER_EXAMPLE(objContextHibernate, panelMap);
				}else{
					bReturn = true;
				}
			}//end main:		
			return bReturn;
		}
		private int fillTroopFleetVariant_createNew(HibernateContextProviderSingletonTHM objContextHibernate) throws ExceptionZZZ{
			int iReturn = 0;
			main:{																
				TroopFleetVariantDao daoTroopFleet = new TroopFleetVariantDao(objContextHibernate);	
				iReturn = daoTroopFleet.createEntriesAll();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Erstellte TroopFleetVarianten: " + iReturn);

				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": ENDE ##############");								
		}//end main:
		return iReturn;	
	}
}
