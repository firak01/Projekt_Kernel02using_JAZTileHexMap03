package use.thm.client.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import use.thm.ApplicationSingletonTHM;
import use.thm.ApplicationTHM;
import use.thm.ITileEventUserTHM;
import use.thm.IVariantCatalogUserTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.client.event.TileMetaEventBrokerTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopDao;
import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.dao.TroopVariantDao;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.daoFacade.TroopFleetDaoFacade;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.Immutabletext;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.util.datatype.enums.EnumSetTroopVariantUtilTHM;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumSetInnerUtilZZZ.ThiskeyEnumMappingExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropEvent;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostMotionAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostDropListenerUser;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanelUser;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class VariantCatalogTHM  extends KernelUseObjectZZZ implements IGhostGlassPanePanelUser, IGhostDropListenerUser{
	private HashMapMultiZZZ hmCatalog = new HashMapMultiZZZ();  //Hashmap mit Hashmap, in der die Sechseckzellen in Form von Koordinaten (z.B. "1","1") abgelegt sind.
	
	private KernelJPanelCascadedZZZ panelParent;
	private int iNrOfEntries=0;
	
	//GhostDragDrop Interface
	private GhostGlassPane glassPane; //etwas, das per Drag/Drop bewegt wird, wird dorthin als Bild kopiert.
	private GhostDropListener listenerForDropToTarget; //
		
	public VariantCatalogTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, GhostGlassPane glassPane, GhostDropListener listenerForDropToHexMap) throws ExceptionZZZ{
		super(objKernel);
		if(panelParent==null){
			ExceptionZZZ ez = new ExceptionZZZ("No ParentPanel provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(glassPane==null){
			ExceptionZZZ ez = new ExceptionZZZ("No GlassPanePanel provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(listenerForDropToHexMap==null){
			ExceptionZZZ ez = new ExceptionZZZ("No ListenerForDropToTarget provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		this.setPanelParent(panelParent);
		this.setGhostGlassPane(glassPane);
		this.setGhostDropListener(listenerForDropToHexMap);
								 	
		//20180326	
		//THEMA : Persistierung in einer Datenbank. Falls die Datenbank neu ist, müssen ggfs. noch die GEBIETE und die DEFAULTTRUPPEN erstellt werden.
		//             Die anderen Tabellen (Defaulttexte, ImmutableTexte, Army und Fleetvarianten) wurden schon auf Applikationsebene erstellt.
		//FGL: Nachdem die Überprüfung, ob die Datenbank existiert Aufgabe des Application-Objekt gewerden ist, hier das entsprechende Flag auslesen
		//boolean bDbIsNew = !ApplicationSingletonTHM.getInstance().getFlag(ApplicationTHM.FLAGZ.DATABASE_NEW.name());
		
	    //Also: Die Varianten-Objekte werden schon vorher auf Applikationsebene erzeugt.
		//Hier also alle nur auslesen....... 
		HibernateContextProviderSingletonTHM objContextHibernate= HibernateContextProviderSingletonTHM.getInstance(objKernel);
		boolean bFleetReadSuccessful = this.fillCatalog_(true);
		
		
	}
	
	/**Hashmap mit Hashmap, in der die Box-Objekte in Form von Variantenklasse und Typ (z.B. "FLEET","1") abgelegt sind.
	 */
	public HashMapMultiZZZ getMapCatalog(){
		return this.hmCatalog;
	}
	
	public boolean fillCatalog() throws ExceptionZZZ{
		return this.fillCatalog(false);
	}
	
	public boolean fillCatalog(boolean bDbExists) throws ExceptionZZZ{
		return this.fillCatalog_(bDbExists);
	}
	
	private boolean fillCatalog_(boolean bDbExists) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			HashMapMultiZZZ hmCell = this.getMapCatalog();
			hmCell.clear();
			boolean bFillDatabaseNew = true;
						
			//Kernel Objekt
			KernelZZZ objKernel = this.getKernelObject();
			
//			//MoveEventBroker für die Bewegung von einer Zelle zur anderen
//			TileMoveEventBrokerTHM objTileMoveEventBroker = new TileMoveEventBrokerTHM(objKernel);
//			this.setTileMoveEventBroker(objTileMoveEventBroker);
//			
//			//20130630: MetaEventBroker für Ereignisse wie Erstellen, Vernichten, etc. am Spielstein
//			TileMetaEventBrokerTHM objTileMetaEventBroker = new TileMetaEventBrokerTHM(objKernel);
//			this.setTileMetaEventBroker(objTileMetaEventBroker);
//				
			//Der HibernateContext ist ein Singleton Objekt, darum braucht man ihn nicht als Parameter im Methodenaufruf weitergeben.
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());			
			if(bDbExists){
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");
				//Diese Methode hat darüber nicht zu entscheiden.... objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.				
							
				//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.
				//EntityManager em = null;
				//Wichtig: Das darf erst NACH dem Überprüfen auf die Datenbankexistenz passieren, da hierdurch die Datei erzeugt wird (wenn auch noch ohne Tabellen) und dies die Prüfung auf Existenz der Datei konterkariert.
				//EntityManager em = objContextHibernate.getEntityManager("c:\\server\\SQLite\\TileHexMap03.sqlite");
				//EntityManager em = objContextHibernate.getEntityManager("jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");				
								
				//Fall: Datenbank existiert. Wenn es einen Fehler gibt, dann wird Sie allerdings neu aufgebaut.
				//boolean bSuccess = fillMap_readCreated_IMPERFORMANT_EXAMPLE(objContextHibernate, panelMap);
				KernelJPanelCascadedZZZ panelMap=this.getPanelParent();
				boolean bSuccess = fillCatalog_readCreated(objContextHibernate, panelMap);
								
				bFillDatabaseNew = !bSuccess;
			}else{
				//Fall: Datenbank existiert noch nicht
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");
				///Diese Methode hat darüber nicht zu entscheiden... objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
			
				bFillDatabaseNew=true;
			}//end if bDbExists
			
			if(bFillDatabaseNew){
				if(bDbExists){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert zwar, es hat aber Probleme beim Einlesen gegeben.");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank sollte gelöscht werden, damit der Neuaufbau keine Probleme bekommt.");
					//	TODO ggfs. zur Sicherheit die gesamte Datenbankdatei löschen, was aber nur geht, wenn z.B. kein anderer Client darauf zugreift. Vor dem endgültigen Löschen immer ein Backup machen.
					bReturn = false;
				}else{
					//Erzeuge neuen Datenbankinhalt
					
					//Merke: Die Varianten sollten schon bei Applikationsbeginn erstellt sein 
					
					
					//Per Hibernate & Session 
					//bReturn = fillMap_createNew(objContextHibernate, panelMap);
					//Per EntityManager, aber das hat Probleme, zumindest mit SQLITE und den @TableGenerator Annotations zum automatischen Erstellen von IDs  
					//bReturn = fillMap_createNew_ENTITYMANAGER_EXAMPLE(objContextHibernate, panelMap);
					bReturn = false;
				}
			}else{			
				bReturn = true;
			}
		}//end main:		
		return bReturn;
	}
	
	
	private boolean fillCatalog_readCreated(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			boolean bReadFleetVariant = fillCatalog_readCreatedFleet(objContextHibernate, panelMap);
			boolean bReadArmyVariant = fillCatalog_readCreatedArmy(objContextHibernate, panelMap);
			bReturn = true;
		}//main:
		return bReturn;
	}
	
	private boolean fillCatalog_readCreatedArmy(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			int iNrOfEntriesHere = 0;  //Die Gesamtzahl der HIER erstellten Einträge. Wird hochgezählt beim Füllen.
			String sVariantId = "ARMY";
			//Merke: Der im GhostrDropManager verwendete Searchkey ist "TROOPARMYVARIANT"
			
		//Steuerung über DAO - Klassen
		TroopArmyVariantDao daoTroopVariant = new TroopArmyVariantDao(objContextHibernate);
		
		//Es soll performanter sein erst die ganze Liste zu holen (wg. Lazy), statt Über die ID jede Zelle einzeln.
		ArrayList<TroopArmyVariant>listaVariant = (ArrayList<TroopArmyVariant>) daoTroopVariant.findLazyAll(0, -1);
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Anzahl gefundener ArmyVarianten = " + listaVariant.size());
								
		String sTileIconName = null;
		String sTileLabel  = null;
		String sCatalogVariantEntryId = null;
		Box boxCreated = null;
		
		//Aus den Daten müssen die "Box"-Objekte erstellt werden, die in die HashMapMultiZZZ abgelegt werden.
		//!!! nicht die Entities direkt irgendwo abspeichern.
			
		//HIER DANN WIRKLICH DYNAMISCH DIE BOXEN ERZEUGEN
		for(TroopArmyVariant objEntity : listaVariant){
			System.out.println("TroopArmyVariant.toString(): " + objEntity.toString());

			
			/* DAS IST NOCH NICHT WICHTIG
			//Vergleich des gespeicherten Textes mit dem Defaulttext
			Long lngThiskeyTemp = objEntity.getThiskey();
			String sCategorytextStored = objEntity.getCategorytext();
			System.out.println("Categorytext (gespeichert): " + sCategorytextStored);
			
			String sUniquetext = objEntity.getUniquetext();
			System.out.println("Uniquetext (gespeichert): " + sUniquetext);
			String sCategorytextDefault = null; 
			try {
				String sType = EnumSetInnerUtilZZZ.getThiskeyEnum(objEntity.getThiskeyEnumClass(), lngThiskeyTemp).name();							
				System.out.println("Typ: " + sType);
				
				sCategorytextDefault = EnumSetTroopVariantUtilTHM.readEnumConstant_CategorytextValue(objEntity.getThiskeyEnumClass(), sType);
				System.out.println("Categorytext (Default): " + sCategorytextDefault);
			} catch (ThiskeyEnumMappingExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(sCategorytextDefault.equals(sCategorytextStored)){
				System.out.println("Wert ist unverändert");							
			}else{
				System.out.println("WERT WURDE VERÄNDERT");
			}
			*/
			
			
			//Hole die Immutable Texte für die Variante im Katalog
			//0. Die ThiskeyId ist dier eindeutige Kennezichnung der Variante, für das Erstellen aus dem Katalog
			//1. Die Kategory ist ggfs. für eine Sortierung wichtig (TODO) UND ist Bestandteil einer 
			Long lngThiskeyTemp = objEntity.getThiskey();
			
			////1. Die Kategory ist ggfs. für eine Sortierung wichtig (TODO) 
			String sCategorytextStored = objEntity.getCategorytext();
			System.out.println("Categorytext (gespeichert): " + sCategorytextStored);
			
			//2. Immutabletexte: Merke: Theoretisch kann ein Text in mehreren Varianten verwendet werden.
			Immutabletext objImmutabletextTemp = objEntity.getImmutabletextObject();
			String sShorttextImmutable = objImmutabletextTemp.getShorttext();
			
			Defaulttext objDefaulttextTemp = objEntity.getDefaulttextObject();
			String sShorttextDefault = objDefaulttextTemp.getShorttext();
			sTileLabel = sShorttextImmutable + " - " + sShorttextDefault;
			sCatalogVariantEntryId = "new_" + objEntity.getThiskey();
			
		    //20180630: Lies nicht mehr ein Bild von der Platte, sondern aus dem Entity direkt	
			/*
			sTileIconName = objEntity.getImageUrlString();			
			  boxCreated = this.createBoxObject(sCatalogVariantEntryId, sTileLabel, sTileIconName);
			*/
			
			byte[] imageInByte = objEntity.getImage01Catalog(); //Diese Catalog Bilder sind in der Größe reduziert.
					
			boxCreated = this.createBoxObject(sCatalogVariantEntryId, sTileLabel, imageInByte);
			this.getMapCatalog().put(sVariantId, sCatalogVariantEntryId, boxCreated);
			 iNrOfEntriesHere++; //Zelle zur Summe hinzufügen
		} //end for ... Variant
		

		//################################################################################################
		if(iNrOfEntriesHere<=0){
			bReturn = false;// ist ja noch nix ausgelesen worden, warum auch immer a) Datenbank existiert ohne Inhalt, b) Keine Objekte für den Schlüssel gefunden.
		}else{
			bReturn = true;
		}
	}//main:
	return bReturn;
}
	
	private boolean fillCatalog_readCreatedFleet(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			int iNrOfEntriesHere = 0;  //Die Gesamtzahl der HIER erstellten Einträge. Wird hochgezählt beim Füllen.
			String sVariantId = "FLEET";
			//Merke: Der im GhostrDropManager verwendete Searchkey ist "TROOPFLEETVARIANT"
			
		//Steuerung über DAO - Klassen
		TroopFleetVariantDao daoTroopVariant = new TroopFleetVariantDao(objContextHibernate);
			
		
		//Es soll performanter sein erst die ganze Liste zu holen (wg. Lazy), statt Über die ID jede Zelle einzeln.
		ArrayList<TroopFleetVariant>listaVariant = (ArrayList<TroopFleetVariant>) daoTroopVariant.findLazyAll(0, -1);
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Anzahl gefundener FleetVarianten = " + listaVariant.size());
								
		String sTileIconName = null;
		String sTileLabel  = null;
		String sCatalogVariantEntryId = null;
		Box boxCreated = null;
		
		//Aus den Daten müssen die "Box"-Objekte erstellt werden, die in die HashMapMultiZZZ abgelegt werden.
		//!!! nicht die Entities direkt irgendwo abspeichern.
				
		//HIER DANN WIRKLICH DYNAMISCH DIE BOXEN ERZEUGEN
		for(TroopFleetVariant objEntity : listaVariant){
			System.out.println("TroopFleetVariant.toString(): " + objEntity.toString());

			
			/* DAS IST NOCH NICHT WICHTIG
			//Vergleich des gespeicherten Textes mit dem Defaulttext
			Long lngThiskeyTemp = objEntity.getThiskey();
			String sCategorytextStored = objEntity.getCategorytext();
			System.out.println("Categorytext (gespeichert): " + sCategorytextStored);
			
			String sUniquetext = objEntity.getUniquetext();
			System.out.println("Uniquetext (gespeichert): " + sUniquetext);
			String sCategorytextDefault = null; 
			try {
				String sType = EnumSetInnerUtilZZZ.getThiskeyEnum(objEntity.getThiskeyEnumClass(), lngThiskeyTemp).name();							
				System.out.println("Typ: " + sType);
				
				sCategorytextDefault = EnumSetTroopVariantUtilTHM.readEnumConstant_CategorytextValue(objEntity.getThiskeyEnumClass(), sType);
				System.out.println("Categorytext (Default): " + sCategorytextDefault);
			} catch (ThiskeyEnumMappingExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(sCategorytextDefault.equals(sCategorytextStored)){
				System.out.println("Wert ist unverändert");							
			}else{
				System.out.println("WERT WURDE VERÄNDERT");
			}
			*/
			
			
			//Hole die Immutable Texte für die Variante im Katalog
			//0. Die ThiskeyId ist dier eindeutige Kennezichnung der Variante, für das Erstellen aus dem Katalog
			//1. Die Kategory ist ggfs. für eine Sortierung wichtig (TODO) UND ist Bestandteil einer 
			Long lngThiskeyTemp = objEntity.getThiskey();
			
			////1. Die Kategory ist ggfs. für eine Sortierung wichtig (TODO) 
			String sCategorytextStored = objEntity.getCategorytext();
			System.out.println("Categorytext (gespeichert): " + sCategorytextStored);
			
			//2. Immutabletexte: Merke: Theoretisch kann ein Text in mehreren Varianten verwendet werden.
			Immutabletext objImmutabletextTemp = objEntity.getImmutabletextObject();
			String sShorttextImmutable = objImmutabletextTemp.getShorttext();
			
			Defaulttext objDefaulttextTemp = objEntity.getDefaulttextObject();
			String sShorttextDefault = objDefaulttextTemp.getShorttext();
			sTileLabel = sShorttextImmutable + " - " + sShorttextDefault;
			sCatalogVariantEntryId = "new_" + objEntity.getThiskey();
			
			   //20180630: Lies nicht mehr ein Bild von der Platte, sondern aus dem Entity direkt	
				/*
				sTileIconName = objEntity.getImageUrlString();			
				  boxCreated = this.createBoxObject(sCatalogVariantEntryId, sTileLabel, sTileIconName);
				*/
				
				byte[] imageInByte = objEntity.getImage01Catalog(); //Diese Catalog Bilder sind in der Größe reduziert.
						
				boxCreated = this.createBoxObject(sCatalogVariantEntryId, sTileLabel, imageInByte);
			  this.getMapCatalog().put(sVariantId, sCatalogVariantEntryId, boxCreated);
			iNrOfEntriesHere++; //Zelle zur Summe hinzufügen
		} //end for ... Variant
		

		//################################################################################################
		if(iNrOfEntriesHere<=0){
			bReturn = false;// ist ja noch nix ausgelesen worden, warum auch immer a) Datenbank existiert ohne Inhalt, b) Keine Objekte für den Schlüssel gefunden.
		}else{
			bReturn = true;
		}
	}//main:
	return bReturn;
}
	
	/*
	 * Mit der Methode wird in GhostDropManagerHexMapPanelTHM.ghostDropped(GhostDropEvent e) 
	 * geprüft, welche DAO - Klasse letztendlich verwendet werden soll. 
	 */
	public static boolean isEntryToCreate_Fleet(String sCatalogVariantThisKeyId) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			ApplicationTHM objApplication = ApplicationSingletonTHM.getInstance();
			KernelSingletonTHM objKernel = (KernelSingletonTHM) objApplication.getKernelObject();			
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
			
			//++++++++++++++
			TroopFleetVariantDao objDao = new TroopFleetVariantDao(objContextHibernate);
			Long lngThisKeyId = new Long(sCatalogVariantThisKeyId);
			long lThisKeyId = lngThisKeyId.longValue();
			bReturn = objDao.isVariantStandard(lThisKeyId);
			
			//Merke: Später dann  
			//bReturn = objDao.isVariantValid(lThisKeyId);
		}
		return bReturn;
	}
	
	/*
	 * Mit der Methode wird in GhostDropManagerHexMapPanelTHM.ghostDropped(GhostDropEvent e) 
	 * geprüft, welche DAO - Klasse letztendlich verwendet werden soll. 
	 */
	public static boolean isEntryToCreate_Army(String sCatalogVariantThisKeyId) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			ApplicationTHM objApplication = ApplicationSingletonTHM.getInstance();
			KernelSingletonTHM objKernel = (KernelSingletonTHM) objApplication.getKernelObject();			
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
			
			//++++++++++++++
			TroopArmyVariantDao objDao = new TroopArmyVariantDao(objContextHibernate);
			Long lngThisKeyId = new Long(sCatalogVariantThisKeyId);
			long lThisKeyId = lngThisKeyId.longValue();
			bReturn = objDao.isVariantStandard(lThisKeyId);
			
			//Merke: Später dann  
			//bReturn = objDao.isVariantValid(lThisKeyId);
		}
		return bReturn;
	}
	
	
	
	public Box createBoxObject(String sCatalogVariantEntryId, String sTileLabel, String sTileIconName) throws ExceptionZZZ{
	     Box objBoxReturn = Box.createVerticalBox();
	     main:{
	    	 objBoxReturn.setBorder(new EmptyBorder(0, 0, 0, 20));
	    	 
	    	 String sBaseDirectory = this.getBaseDirectoryForCatalogIcon();
	    	 String sFile = sBaseDirectory + File.separator + sTileIconName;
	    	 
	    	 //Ein Label hinzufügen mit dem entsprechenden Titel und dem Bild
	    	 

	    		//Modullnamen und Programnamen für die Position in der KernelKonfiguation  	 
	    	 	KernelZZZ objKernel = this.getKernelObject();
				String sModuleAlias =  this.getModuleUsed();// this.getModuleName();
				String sProgramAlias = this.getProgramUsed(); //this.getProgramAlias(); //				
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
				
		   //++++++++++
				 //Die Größe der Icons aus der KernelKonfiguration auslesen
				String sIconWidth = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );
				int iIconWidth = Integer.parseInt(sIconWidth);				
				String sIconHeight = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
				int iIconHeight = Integer.parseInt(sIconHeight);
	    	 //+++++++++	    	 
		     JLabel label = UIHelper.createLabelWithIconResized(sTileLabel, sFile, iIconWidth,iIconHeight); 
		     objBoxReturn.add(label);
		     
		     //### Funktionalität DRAG & DROP
		     //Merke: Verwendet man hier den bisherigen Picture Adapter und hängt noch einen weitern dropListener an, 
			 //       dann wird 2x ein drop durchgeführt. D.h. es wird 2x ein Entity erzeugt. Beim 2. Mal gibt es dann die Fehlermeldung:
			 //       'Maximale Anzahl der Amreen / Flotten im Feld erreicht'. Darum ist es wichtig hier immer einen NEUEN picture Adapter zu erzeugen, pro Variante.			    			    			
			 //Es muss also das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
		   
		     //Das Bild beim Ziehen über den Glaspane weiterverwenden. Anhand der sCatalogEntryId wird dann beim Fallenlassen entschieden um welches Entity es sich überhaupt handelt		     
			    GhostGlassPane glassPane = this.getGhostGlassPane();
			    if(glassPane==null) throw new ExceptionZZZ("Kein GhostGlassPane im FrameParent vorhanden");
			    		      
		     GhostDropListener listenerForDropToHexMap = this.getGhostDropListener(); //.. und hier entscheidet sich  wie beim Fallenlassen gehandelt wird.
		     
		     //++++++++++
	    	 //Die Größe der Icons beim Ziehen aus der KernelKonfiguration auslesen	    	 					
				String sIconWidthOnDrag = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidthOnDrag" );
				int iIconWidthOnDrag = Integer.parseInt(sIconWidthOnDrag);					
				String sIconHeightOnDrag = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeightOnDrag" );
				int iIconHeightOnDrag = Integer.parseInt(sIconHeightOnDrag);
	    	 //+++++++++	  		    
		     GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, sCatalogVariantEntryId, sFile, iIconWidthOnDrag, iIconHeightOnDrag);
			 pictureAdapter.addGhostDropListener(listenerForDropToHexMap);
			 
			 //Das DRAGGEN, ausgehend vom Label 			
		     label.addMouseListener(pictureAdapter); //Beim Clicken wird das Bild vom pictureAdapter an die passende Stelle im glassPane gesetzt.
		     label.addMouseMotionListener(new GhostMotionAdapter(glassPane));
			 
	     }//end main:
	     return objBoxReturn;
	}
	
	
	public Box createBoxObject(String sCatalogVariantEntryId, String sTileLabel, byte[] imageInByte) throws ExceptionZZZ{
	     Box objBoxReturn = Box.createVerticalBox();
	     main:{
	    	 objBoxReturn.setBorder(new EmptyBorder(0, 0, 0, 20));
	    	 	    	
	    	 //Ein Label hinzufügen mit dem entsprechenden Titel und dem Bild
	    	 
	    	//Modullnamen und Programnamen für die Position in der KernelKonfiguation  	 
	    	 	KernelZZZ objKernel = this.getKernelObject();
			
		   //++++++++++
				 //Die Größe der Icons aus der KernelKonfiguration auslesen
				//DAS IST IN DER ERSTELLUNG DES ENTITIES AUSGELAGERT UND WIRD EXTRA GESPEICHERT
//				String sIconWidth = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidth" );
//				int iIconWidth = Integer.parseInt(sIconWidth);				
//				String sIconHeight = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeight" );
//				int iIconHeight = Integer.parseInt(sIconHeight);
	    	 //+++++++++	    	 

			// JLabel label = UIHelper.createLabelWithIconResized(sTileLabel, imageInByte, iIconWidth,iIconHeight);
			JLabel label = UIHelper.createLabelWithIcon(sTileLabel,  imageInByte);
		    objBoxReturn.add(label);

		     //### Funktionalität DRAG & DROP
		     //Merke: Verwendet man hier den bisherigen Picture Adapter und hängt noch einen weitern dropListener an, 
			 //       dann wird 2x ein drop durchgeführt. D.h. es wird 2x ein Entity erzeugt. Beim 2. Mal gibt es dann die Fehlermeldung:
			 //       'Maximale Anzahl der Amreen / Flotten im Feld erreicht'. Darum ist es wichtig hier immer einen NEUEN picture Adapter zu erzeugen, pro Variante.			    			    			
			 //Es muss also das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
		   
		     //Das Bild beim Ziehen über den Glaspane weiterverwenden. Anhand der sCatalogEntryId wird dann beim Fallenlassen entschieden um welches Entity es sich überhaupt handelt		     
			    GhostGlassPane glassPane = this.getGhostGlassPane();
			    if(glassPane==null) throw new ExceptionZZZ("Kein GhostGlassPane im FrameParent vorhanden");
			    		      
		     GhostDropListener listenerForDropToHexMap = this.getGhostDropListener(); //.. und hier entscheidet sich  wie beim Fallenlassen gehandelt wird.
		     
		     //++++++++++
	    	 //Die Größe der Icons beim Ziehen aus der KernelKonfiguration auslesen	  
				String sModuleAlias =  this.getModuleUsed();// this.getModuleName();
				String sProgramAlias = this.getProgramUsed(); //this.getProgramAlias(); //				
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'IconWidth'");
	
				//TODO GOON FGL 20180630
				String sIconWidthOnDrag = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconWidthOnDrag" );
				int iIconWidthOnDrag = Integer.parseInt(sIconWidthOnDrag);					
				String sIconHeightOnDrag = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "IconHeightOnDrag" );
				int iIconHeightOnDrag = Integer.parseInt(sIconHeightOnDrag);
	    	 //+++++++++	  		    
			 GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, sCatalogVariantEntryId, imageInByte, iIconWidthOnDrag, iIconHeightOnDrag);
			 pictureAdapter.addGhostDropListener(listenerForDropToHexMap);
			 
			 //Das DRAGGEN, ausgehend vom Label 			
		     label.addMouseListener(pictureAdapter); //Beim Clicken wird das Bild vom pictureAdapter an die passende Stelle im glassPane gesetzt.
		     label.addMouseMotionListener(new GhostMotionAdapter(glassPane));
			 
	     }//end main:
	     return objBoxReturn;
	}
		
		//#### GETTER / SETTER
		public void setPanelParent(KernelJPanelCascadedZZZ panelParent){
			this.panelParent = panelParent;
		}
		public KernelJPanelCascadedZZZ getPanelParent(){
			return this.panelParent;
		}
		
		public void setNumberOfEntries(int iNrOfEntries){
			this.iNrOfEntries=iNrOfEntries;
		}
		public int getNumberOfEntries(){
			return this.iNrOfEntries;
		}

		@Override
		public GhostGlassPane getGhostGlassPane() {
			return this.glassPane;
		}

		@Override
		public void setGhostGlassPane(GhostGlassPane glassPane) {
			this.glassPane = glassPane;
		}

		@Override
		public GhostDropListener getGhostDropListener() {
			return this.listenerForDropToTarget;
		}

		@Override
		public void setGhostDropListener(
				GhostDropListener listenerForDropToTarget) {
			this.listenerForDropToTarget = listenerForDropToTarget;
		}	
				
		//Das Verzeichnis, in dem die ganzen Icons der Katalogeinträge liegen
		public String getBaseDirectoryForCatalogIcon() throws ExceptionZZZ{		
			return ApplicationSingletonTHM.getInstance().getBaseDirectoryStringForImages();
		}
}
