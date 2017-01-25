package use.thm.client.dragDropTranslucent;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import custom.zKernel.LogZZZ;

import use.thm.IHexMapUserTHM;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.TileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.client.event.EventTileDroppedToCellTHM;
import use.thm.client.event.TileMetaEventBrokerTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
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
	   
	   Point p0 = e.getDropLocation(); //Das der GlassPane �ber den ganzen Frame geht, ist das ein Punkt bezogen auf den ganzen Frame.
	   Point p1 = getTranslatedPoint(e.getDropLocation());  //Punkt beziehen auf den JScrollPane

	   //Aber, wenn in einem JScrollpane gedroppt wird: Dann ist die "tats�chliche Karte" gr��er als der angezeigte ViewPort.
	   Point p = JScrollPaneHelperZZZ.toWorldCoordinate((JScrollPane)this.getDropTargetComponent(), p1);
	   if (!isInTarget(p)) {  //Das ist dann das Ziel f�r den GhostDrop, aber noch nicht die einzelne Zelle
		   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Punkt NICHT im 'Ziel des GhostDrop' x: " + p.getX() + " | y: " + p.getY());
	   }else{
		   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Punkt im 'Ziel des GhostDrop' x: " + p.getX() + " | y: " + p.getY());
		   
		   //### Droppe in die Karte
		   HexMapTHM objMap = this.getHexMap();
		   HexCellTHM objCell = (HexCellTHM) objMap.getPanelParent().getComponentAt(p);
		   if(objCell!=null){
			   System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "### Gefunden am Punkt ein Objekt der Klasse '" + objCell.getClass().getName() + "'");
			   
			   //+++ Erstelle den Spielstein. Schl�ssel daf�r ist im GhostDropEvent vorhanden
			   String sAction = e.getAction();
			   TileTHM objTile = null; //wird gleich konkret gef�llt, falls der Action-Key passt.
			   
			  //MoveEventBroker f�r die Bewegung von einer Zelle zur anderen
			  
			   TileMetaEventBrokerTHM objTileMetaEventBroker = objMap.getTileMetaEventBroker();//new TileMoveEventBrokerTHM(this.getKernelObject());
		      // JOptionPane.showMessageDialog(this.component, ReflectCodeZZZ.getMethodCurrentName() + "### Action: '" + action + "'");
			   if(!StringZZZ.isEmpty(sAction)){
				   if(sAction.equalsIgnoreCase("new_sale")){
					   objTile = new ArmyTileTHM(objMap.getPanelParent(), objMap.getTileMoveEventBroker(), objCell.getMapX(), objCell.getMapY(), objMap.getSideLength());
				   }
			   }
			   if(objTile!=null){
				  /* Merke EventTileDroppedToCellTHM w�re der FALSCHER EVENT !!! */
				  				   
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