package use.thm.client;


import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.JLabel;

import tryout.hibernate.HexCell;
import use.thm.IHexMapUserTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.HexagonalLayoutTHM;
import use.thm.client.component.HexagonalCellLayoutTHM;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropManagerDemo;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanel;

public class PanelMain_CENTERTHM extends KernelJPanelCascadedZZZ implements IHexMapUserTHM{
	private HexMapTHM objHexMap = null;
	
	//Default Konstruktor, damit die Klasse per Refelction einfachmit newInstance erzeugt werden kann.
	public PanelMain_CENTERTHM(){		
	}
			
	public PanelMain_CENTERTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, HashMap<String, Boolean>hmFlag) throws ExceptionZZZ{
		super(objKernel, panelParent, hmFlag);
		try{		
			this.setJComponentContentDraggable(false); //Nur die einzelnen Sechsecke ziehbar machen
			
			//Werte für die Kartengröße auslesen			
			String sModuleAlias = this.getModuleName();
			String sProgramAlias = this.getProgramAlias(); //
			
			System.out.println("Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'NumberOfColumn'");
			String sNumberOfColumn = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "NumberOfColumn" );
			int iNumberOfColumn = Integer.parseInt(sNumberOfColumn);
			
			String sNumberOfRow = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "NumberOfRow" );
			int iNumberOfRow = Integer.parseInt(sNumberOfRow);
			
			String sHexSideLength = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "HexSideLength" );
			int iHexSideLength = Integer.parseInt(sHexSideLength);
			
			//Die Hexadimensionale Karte aufbauen.			
			HexMapTHM objMap = new HexMapTHM(objKernel, this, iNumberOfColumn, iNumberOfRow, iHexSideLength);
			this.setHexMap(objMap);
			
			//+++ Das Ziel ist, beim Fallenlassen eines Objekts aus dem glassPane, reagieren zu können
			//ABER: HexMap ist keine JComponent, sondern ein KernelUseObject. Nur die einzelnen Zellen sind JPanels (und damit JComponents) und k�nnen direkter als Drop-Ziel verwendet werden.
			//GhostDropListener listener = new GhostDropManagerHexMapTHM(objMap);
		
			//##########################
			//### Fülle das Kartenpanel
			//### Merke: Die Zellen wurden zuvor im Konstruktor von HexMapTHM mit der Methode fillMap() erstellt, inklusive des persistierbaren Objekts.
			//this.setLayout(null);   //Dadurch muss jede Komponente selbst seine "Bounds" setzen. Merke: Ohne Layout-Manger ist das Panel nicht in der Lage sein Größe korrekt zu initialisieren. 
			HexagonalLayoutTHM layout = new HexagonalLayoutTHM(objKernel, objMap);
			this.setLayout(layout);
			this.setBackground(Color.white);
				
			HashMapMultiZZZ hmCell = objMap.getMapCell();
			
			HexCellTHM cellTemp = null;
			int iY=0;
			do{
				iY++;
				
				Integer intY=new Integer(iY);
				String sY = intY.toString();
				
				for(int iX=1; iX <= objMap.getColumnMax(); iX++){
					Integer intX = new Integer(iX);
					String sX = intX.toString();
					
					cellTemp = (HexCellTHM) hmCell.get(sX,sY);
					if(cellTemp!=null){
					cellTemp.setVisible(true);
					this.add(cellTemp);
					}else{
						break;
					};
				} //end for iX
			}while(cellTemp !=null);
			
		} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
}

	public HexMapTHM getHexMap() {
		return this.objHexMap;
	}

	public void setHexMap(HexMapTHM objMap) {
		this.objHexMap = objMap;		
	}

}
