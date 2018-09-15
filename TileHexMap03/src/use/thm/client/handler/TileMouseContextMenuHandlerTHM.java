package use.thm.client.handler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import custom.zKernel.LogZZZ;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.ArrayListZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zKernel.IKernelModuleUserZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.KernelUIZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.TileTHM;
import use.thm.client.component.menu.TileCommonComponentHelperUITHM;
import use.thm.client.component.menu.TilePopupMenuTHM;
import use.thm.client.event.EventCellEnteredTHM;
import use.thm.client.event.EventCellLeavedTHM;
import use.thm.client.event.EventTileDroppedToCellTHM;
import use.thm.client.event.ITileMoveEventBrokerUserTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.daoFacade.TroopFleetDaoFacade;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileType;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopType;

/** Mit dieser Klasse werden alle Maus-Ereignisse für die Spielsteine zusammengfasst
 * Dadurch können gemeinsam genutzte Eigenschaften (z.B. Die Start-Zelle einer DragDrop-Bewegung oder der Spielstein selbst) einfacher verwendet werden.
 * 
 * Das Interface ITileMoveEventBroker ermöglicht es an die an den EventBroker registrierten Komponenten eine Event zu schicken (Zelle wurde betreten, Zelle wurde verlassen) 
 * @author lindhauer
 *
 */
public class TileMouseContextMenuHandlerTHM extends MouseAdapter implements MouseMotionListener, IKernelModuleUserZZZ {
	private TileTHM objTile;
	
	public TileMouseContextMenuHandlerTHM(TileTHM objTile){
		this.objTile = objTile;		
	}
	
	//### GETTER /SETTER	
	public TileTHM getTile(){
		return this.objTile;
	}
			
//	 ###########Die Events aus dem MOUSEADAPTER #######################
	public void mouseClicked(MouseEvent arg0) {
		main:{
			try{			
			if(SwingUtilities.isLeftMouseButton(arg0)){
				System.out.println(ReflectCodeZZZ.getPositionCurrent()+": Linksclick...");		
								
				//Momentan beim Doppelclick nur die Position anzeigen lassen
				int iCount =arg0.getClickCount();
				int iErg = 0; 
				switch(iCount){
				case 1:
					break;
				case 2:
					
					TileCommonComponentHelperUITHM.showTileDetailDialog(this.getTile(), arg0);
					
				} //END Switch	
												
			}else if(SwingUtilities.isRightMouseButton(arg0)){
				System.out.println(ReflectCodeZZZ.getPositionCurrent()+": Rechtsclick...");
				 if (arg0.isPopupTrigger()){
			            doPop(arg0);
			    }
				 
				 /*
				  * 	//Hole weitere Informationen aus dem DTO:
			GenericDTO<ITileDtoAttribute> objDto = this.getTile().getDto();
			String sShorttext = objDto.get(ITileDtoAttribute.VARIANT_SHORTTEXT);
			Integer intVariantUniquenumber = objDto.get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
			sMessage = sShorttext + "_" + intVariantUniquenumber.toString(); 
			
								
			//Anzeige der Koordinaten
			Point p = arg0.getPoint();
			String sCoordinates = "X=" + this.getTile().getMapX() + "; Y=" + this.getTile().getMapY()  + " (" + p.getX() + "/" + p.getY() + ")";
			sMessage = sMessage + StringZZZ.crlf() + sCoordinates;
			//Trennzeile
			sMessage = sMessage + StringZZZ.crlf();
			Float fltHealth = objDto.get(ITileDtoAttribute.HEALTH);
			float fHealthNormed = fltHealth.floatValue() * 100;
			Float fltHealthNormed = new Float(fHealthNormed);
			String sHealth = "Health: " + StringZZZ.left(fltHealthNormed.toString(),6)+"%";
			sMessage = sMessage + StringZZZ.crlf() + sHealth;
			
			//Trennzeile
			sMessage = sMessage + StringZZZ.crlf();
						
			String sUniquename = objDto.get(ITileDtoAttribute.UNIQUENAME);
			sMessage = sMessage + StringZZZ.crlf() + sUniquename;
				  */
				 
				 
				 /*
				  * 	// +++ Nicht einfach Löschen, sondern mal abfragen
			iErg = JOptionPane.showConfirmDialog(this.getTile(), "Would you realy like to remove this square ?", "Remove square ?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(iErg == JOptionPane.YES_OPTION){
				// Löschen
				panelCurrent.removeSquare(objRect2D);
			}
			// +++ Nicht einfach Löschen, sondern mal abfragen
					iErg = JOptionPane.showConfirmDialog(this.getTile(), "Would you realy like to remove this square ?", "Remove square ?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(iErg == JOptionPane.YES_OPTION){
						// Löschen
						panelCurrent.removeSquare(objRect2D);
					}
					*/
					
				/*
				//aktuelles Quadrat entfernen, wenn Doppelclick
				Rectangle2D objRect2D = panelCurrent.findSquare(arg0.getPoint());
						
				if(objRect2D != null){
					
				
				}
					*/
			}//end if SwingUtiilities.is...MouseButton(...)
		}catch(ExceptionZZZ ez){
			System.out.println("ExceptionZZZ Detail: " + ez.getDetailAllLast());
			ez.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}//End main:
		
	}

	public void mouseEntered(MouseEvent arg0) {
		super.mouseEntered(arg0);
	}

	public void mouseExited(MouseEvent arg0) {
		super.mouseExited(arg0);
	}

	public void mousePressed(MouseEvent arg0){	
		super.mousePressed(arg0);
	}

	public void mouseReleased(MouseEvent arg0) {
		super.mouseReleased(arg0);
			
		if(SwingUtilities.isLeftMouseButton(arg0)){
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Linksclick... ");
		}else if(SwingUtilities.isRightMouseButton(arg0)){
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Rechstclick... ");
			 if (arg0.isPopupTrigger()){
		            doPop(arg0);
		    }
		}//end if (SwingUtilities.is...Button(...)
	}
	
	//### Interface IKernelModuleUser
	@Override
	public String getModuleName() {
		//return KernelUIZZZ.getModuleName(this);
		
		String sReturn=null;
		try {
			KernelJPanelCascadedZZZ objPanelToSearch = this.getTile().getMapPanel().getPanelNeighbour("WEST");
			String sModuleName = objPanelToSearch.getModuleName();
			sReturn = sModuleName;
		} catch (ExceptionZZZ e) {			
			e.printStackTrace();
		}
		return sReturn;
	}

	@Override
	public String getProgramName() throws ExceptionZZZ {
		return KernelUIZZZ.getProgramName(this);
	}

	@Override
	public String getProgramAlias() throws ExceptionZZZ {
		return KernelUIZZZ.getProgramAlias(this);
	}
	
	//##########################################

	 private void doPop(MouseEvent e){
	        TilePopupMenuTHM menu = new TilePopupMenuTHM(this.getTile().getMapPanel().getKernelObject(), this.getTile().getMapPanel().getFrameParent(), this.getTile().getMapPanel(), this.getTile());
	        menu.show(e.getComponent(), e.getX(), e.getY());
	    }
}//end class
