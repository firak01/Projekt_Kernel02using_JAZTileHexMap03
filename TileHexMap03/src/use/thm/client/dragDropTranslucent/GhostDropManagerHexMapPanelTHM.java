package use.thm.client.dragDropTranslucent;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import custom.zKernel.LogZZZ;
import use.thm.IHexMapUserTHM;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.FleetTileTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.TileTHM;
import use.thm.client.component.VariantCatalogTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.client.event.EventTileDroppedToCellTHM;
import use.thm.client.event.TileMetaEventBrokerTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.daoFacade.TroopFleetDaoFacade;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.dto.TileDtoFactory;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleetVariant;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.AbstractGhostDropManager;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropEvent;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.util.JScrollPaneHelperZZZ;

public class GhostDropManagerHexMapPanelTHM extends AbstractGhostDropManager implements IKernelUserZZZ, IHexMapUserTHM{
    private JComponent target;
    private KernelZZZ objKernel; 
    private LogZZZ objLog;
    
    private HexMapTHM objMap; //Die HexMap ist der Speicher der einzelnen Zellen UND aller EventBroker. 
    						  //Wird der "Ghost" fallengelassen,
                              //kann �ber die target Komponente die Position, z.B. auf dem GlassPane, ScrollPane, etc Zelle geholt werden.
                              //als "Point".
                              //
                              //Letztendlich ist es aber das panelParent der HexMapTHM Klasse, von dem aus dann die einzelnen Zellen geholt werden
                              //mit HexCellTHM objCell = (HexCellTHM) panelParent.getComponentAt(Point)

   
    /**
     * Merke: HexMap ist keine JComponent, nur die einzelnen Zellen sind JPanels und damit JComponents, bzw. hier das ganze Panel der Map

     * @param target: Ein Panel, bzw. ein ScrollPane
     * @param objMap: Die Hex-Karte, als Speicher der einzelnen Zellen. 
     *                Diese sind in einer HashMapMultiZZZ gespeichert und �ber die Koordinatenstrings X und Y abrufbar.  
     */
    public GhostDropManagerHexMapPanelTHM(KernelZZZ objKernel, JComponent target, HexMapTHM objMap) {
        super(target);
        this.setKernelObject(objKernel);
        this.setLogObject(objLog);
        this.setHexMap(objMap);
    }
    
	public void ghostDropped(GhostDropEvent e) {
	   System.out.println("GhostDropManagerHexMapPanelTHM.ghostDropped(..)");
	   main:{
		   try{
	   String action = e.getAction();
	   
	   Point p0 = e.getDropLocation(); //Das der GlassPane über den ganzen Frame geht, ist das ein Punkt bezogen auf den ganzen Frame.
	   Point p1 = getTranslatedPoint(e.getDropLocation());  //Punkt beziehen auf den JScrollPane

	   //Aber, wenn in einem JScrollpane gedroppt wird: Dann ist die "tatsächliche Karte" grö�er als der angezeigte ViewPort.
	   Point p = JScrollPaneHelperZZZ.toWorldCoordinate((JScrollPane)this.getDropTargetComponent(), p1);
	   if (!isInTarget(p)) {  //Das ist dann das Ziel für den GhostDrop, aber noch nicht die einzelne Zelle
		   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Punkt NICHT im 'Ziel des GhostDrop' x: " + p.getX() + " | y: " + p.getY());
	   }else{
		   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Punkt im 'Ziel des GhostDrop' x: " + p.getX() + " | y: " + p.getY());
		   
		   //### Droppe in die Karte
		   HexMapTHM objMap = this.getHexMap();
		   HexCellTHM objCell = (HexCellTHM) objMap.getPanelParent().getComponentAt(p);
		   if(objCell!=null){
			   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Gefunden am Punkt ein Objekt der Klasse '" + objCell.getClass().getName() + "'");
			   
			   //+++ Erstelle den Spielstein. Schlüssel dafür ist im GhostDropEvent vorhanden
			   String sAction = e.getAction();
			   TileTHM objTile = null; //wird gleich konkret gefüllt, falls der Action-Key passt.
			   
			 //################# TODO GOON 20180326: DAS DYNAMISCH MACHEN ################
			   //20180702: Hole erst einmal den key aus z.B. new_21 heraus. Der String wird so erstellt:
			   //	              GhostDropEvent.mouseRelease() - CataalogTHM.createBoxObject()
			   String sCatalogVariantThisKeyId = StringZZZ.right(sAction, "_");
			   
			  //MoveEventBroker für die Bewegung von einer Zelle zur anderen			  
			   TileMetaEventBrokerTHM objTileMetaEventBroker = objMap.getTileMetaEventBroker();//new TileMoveEventBrokerTHM(this.getKernelObject());
		      // JOptionPane.showMessageDialog(this.component, ReflectCodeZZZ.getMethodCurrentName() + "### Action: '" + action + "'");
			   if(!StringZZZ.isEmpty(sCatalogVariantThisKeyId)){				   
					   
				   //################# TODO GOON 20180326: DAS DYNAMISCH MACHEN ################
				   boolean bFleet = VariantCatalogTHM.isEntryToCreate_Fleet(sCatalogVariantThisKeyId);
				   boolean bArmy = VariantCatalogTHM.isEntryToCreate_Army(sCatalogVariantThisKeyId);
				   
				   //dann neue Methoden: .isFleetCatalogEntryToCreate(lng key)
				   //darin aufrufen: TroopFleetVariantDao.isVariantValid(lng key)
				   //dito mit isArmyCatalogEntryToCreate(lng key)
				   //darin aufrufen: TroopArmyVariantDao.isVariantValid(lng key)
				   //TODO: Mache dann in den Dao-Klassen auch .isVariantStandard(lng key), wobei nur auf die in den Enumerations definierten Werte zugegriffen wird. 
				   //IDEE: So könnte man ggfs. den Spieler neue Varianten "erfinden" lassen und in das System einfügen lassen.
				   
				   					   
				
				   
					   //if(sAction.equalsIgnoreCase("new_11") || sAction.equalsIgnoreCase("new_12")){ //Das sind die thisIds der Variante "ARMY"
				   	if(bArmy){
						  						   
					   //FGL: 20170703 - Hier erst einmal im Backend prüfen, ob eine neue Army hier überhaupt erstellt werden darf.
					   boolean bGoon = false;	
					 //###################
						//Hole das passende Area-Objekt
						//###################
						//Die in eine Methode gekapselte (DAO Klasse) Vorgehensweise verwenden. //Der Code stammt aus HexMapTH.fillMap_createNewTiles(...)
					   //Allerdings müssen erst einmal alle Voraussetzungen erfüllt werden. HibernateContext,..., PrimaryKey..., AreaCell Objekt...,
					    HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());
					    AreaCellDao objAreaDao = new AreaCellDao(objContextHibernate);
					    
					    String sXDropped = objCell.getMapX();
						String sYDropped = objCell.getMapY();
					    CellId primaryKeyCell = new CellId("EINS", sXDropped, sYDropped);
					    
					    AreaCell objCellTemp = objAreaDao.findByKey(primaryKeyCell);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
					    
						//###################
						//Hole das passende TroopVariant-Objekt
						//###################
					    String sVariantThiskeyId = sAction.substring(4);//Aus dem GhostDropEvent (via GhostpictureAdapter) die im PANEL_WEST ausgewählte Variante holen. Der String wird da übergeben in der BOX-ERSTELLUNG.
						   Long lngTroopFleetVariant_Thiskeyid = new Long(sVariantThiskeyId);
						   long lngTroopArmyVariant_Thiskeyid = lngTroopFleetVariant_Thiskeyid.longValue(); 					  		
						TroopArmyVariantDao daoKey = new TroopArmyVariantDao(objContextHibernate);
						TroopArmyVariant objTroopArmyVariant = (TroopArmyVariant) daoKey.searchKey("TROOPARMYVARIANT", lngTroopArmyVariant_Thiskeyid );
											    
						TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);
						String sUniquename = objTroopDaoFacade.computeUniquename();						
						bGoon = objTroopDaoFacade.insertTroopArmy(sUniquename, objTroopArmyVariant, objCellTemp);//Falls das aus irgendwelchen Gründen nicht erlaubt ist, ein Veto einlegen.
						if(!bGoon){
							//0170703: Hole auch irgendwie einen Grund ab, warum an dieser Stelle nix eingefügt werden darf.//Dies muss aus TroopArmyDaoFacade abgeholt werden.							
							String sMessage = objTroopDaoFacade.getFacadeResult().getMessage(); //Hole die Meldung ab.
							
							//Mache nun eine Ausgabe, wie sonst in AreaCellTHM.onTileCreated(EventTileCreatedInCellTHM) 				
							JOptionPane.showMessageDialog (this.component, sMessage);//TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
							
						}else{

							//Hole Werte über das DTO-Objekt
							//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);		
							
							//FGL 20180321: Auch hier die  DTO mit den Werten aus der Variante füllen
							objTroopDaoFacade.fillTroopArmyDto(sUniquename, dto);
							
							//Übergib das DTO-Objekt an das Frontend-Objekt. Dieses kann sich dann daraus weiterhin bedienen.
							objTile = new ArmyTileTHM(objMap.getPanelParent(), objMap.getTileMoveEventBroker(), dto, objCell.getMapX(), objCell.getMapY(), objMap.getSideLength());
						}
				   }//new_sale
				   
				
				  // if(sAction.equalsIgnoreCase("new_21") || sAction.equalsIgnoreCase("new_22")){ //Das sind die thisIds der Variante "FLEET"
				   	if(bFleet){
					   //FGL: 20170703 - Hier erst einmal im Backend prüfen, ob eine neue Fleet hier überhaupt erstellt werden darf.
					   boolean bGoon = false;	
					 //###################
						//Hole das passende Area-Objekt
						//###################
						//Die in eine Methode gekapselte (DAO Klasse) Vorgehensweise verwenden. //Der Code stammt aus HexMapTH.fillMap_createNewTiles(...)
					   //Allerdings müssen erst einmal alle Voraussetzungen erfüllt werden. HibernateContext,..., PrimaryKey..., AreaCell Objekt...,
					    HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());
					    AreaCellDao objAreaDao = new AreaCellDao(objContextHibernate);
					    
					    String sXDropped = objCell.getMapX();
						String sYDropped = objCell.getMapY();
					    CellId primaryKeyCell = new CellId("EINS", sXDropped, sYDropped);
					    
					    AreaCell objCellTemp = objAreaDao.findByKey(primaryKeyCell);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
					    
						//###################
						//Hole das passende TroopVariant-Objekt
						//###################
					    String sVariantThiskeyId = sAction.substring(4);//Aus dem GhostDropEvent (via GhostpictureAdapter) die im PANEL_WEST ausgewählte Variante holen. Der String wird da übergeben in der BOX-ERSTELLUNG.
					    Long lngTroopFleetVariant_Thiskeyid = new Long(sVariantThiskeyId);
					    long lTroopFleetVariant_Thiskeyid = lngTroopFleetVariant_Thiskeyid.longValue(); 			
						TroopFleetVariantDao daoKey = new TroopFleetVariantDao(objContextHibernate);
						TroopFleetVariant objTroopFleetVariant = (TroopFleetVariant) daoKey.searchKey("TROOPFLEETVARIANT", lngTroopFleetVariant_Thiskeyid );
											    
						TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
						String sUniquename = objTroopDaoFacade.computeUniquename();						
						bGoon = objTroopDaoFacade.insertTroopFleet(sUniquename, objTroopFleetVariant, objCellTemp);//Falls das aus irgendwelchen Gründen nicht erlaubt ist, ein Veto einlegen.
						if(!bGoon){
							//0170703: Hole auch irgendwie einen Grund ab, warum an dieser Stelle nix eingefügt werden darf.//Dies muss aus TroopArmyDaoFacade abgeholt werden.							
							String sMessage = objTroopDaoFacade.getFacadeResult().getMessage(); //Hole die Meldung ab.
							
							//Mache nun eine Ausgabe, wie sonst in AreaCellTHM.onTileCreated(EventTileCreatedInCellTHM) 				
							JOptionPane.showMessageDialog (this.component, sMessage);//TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
							
						}else{

							//Hole Werte über das DTO-Objekt
							//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);		
							
							//FGL 20180321: Auch hier die  DTO mit den Werten aus der Variante füllen
							objTroopDaoFacade.fillTroopFleetDto(sUniquename, dto);
							
							//Übergib das DTO-Objekt an das Frontend-Objekt. Dieses kann sich dann daraus weiterhin bedienen.
							objTile = new FleetTileTHM(objMap.getPanelParent(), objMap.getTileMoveEventBroker(), dto, objCell.getMapX(), objCell.getMapY(), objMap.getSideLength());
						}					   
				   }//new_sale02
			   }
			   if(objTile!=null){
				  /* Merke EventTileDroppedToCellTHM wäre der FALSCHER EVENT !!! */
				  				   
				   String sXDropped = objCell.getMapX();
				   String sYDropped = objCell.getMapY();
				   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Zellenposition in Karte am 'Ziel des GhostDrop' MapX: " + sXDropped + " | MapY: " + sYDropped);
				   EventTileCreatedInCellTHM objEvent = new EventTileCreatedInCellTHM(objTile, 1004, sXDropped, sYDropped);
				   objTileMetaEventBroker.fireEvent(objEvent);
		   		}
		   }
	   }//end if isIntarget
		   }catch(ExceptionZZZ ez){
			   ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		   }
	   }//end main
	}

	public HexMapTHM getHexMap() {
		return this.objMap;
	}

	public void setHexMap(HexMapTHM objMap) {
		this.objMap = objMap;
	}

	public KernelZZZ getKernelObject() {
		return this.objKernel;
	}

	public void setKernelObject(KernelZZZ objKernel) {
		this.objKernel = objKernel;
	}

	public LogZZZ getLogObject() {
		return this.objLog;
	}

	public void setLogObject(LogZZZ objLog) {
		this.objLog = objLog;
	}

	protected boolean isInTarget(Point point) {
		boolean bReturn = false;
		try{
			bReturn = JScrollPaneHelperZZZ.isInTargetWorldCoordinate((JScrollPane)this.component, point);
		}catch(ExceptionZZZ ez){
			   ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
		return bReturn;
	}
}