package use.thm.client;


import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zKernel.IKernelConfigSectionEntryZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;

import javax.swing.JLabel;

import use.thm.ApplicationSingletonTHM;
import use.thm.IHexMapUserTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.HexagonaMapLayoutTHM;
import use.thm.client.component.HexagonalCellLayoutTHM;
import use.thm.persistence.model.HexCell;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropManagerDemo;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanel;

public class PanelMain_CENTERTHM extends KernelJPanelCascadedZZZ implements IHexMapUserTHM{
	private HexMapTHM objHexMap = null;
	
	//Default Konstruktor, damit die Klasse per Reflection einfach mit newInstance erzeugt werden kann.
	public PanelMain_CENTERTHM(){		
	}
			
	public PanelMain_CENTERTHM(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, HashMap<String, Boolean>hmFlag) throws ExceptionZZZ{
		super(objKernel, panelParent, hmFlag);
		try{		
			this.setJComponentContentDraggable(false); //Nur die einzelnen Spielsteine ziehbar machen
			
			//Werte für die Kartengröße auslesen			
			String sModuleAlias = this.getModuleName();
			String sProgramAlias = this.getProgramAlias();
			
			System.out.println("Suche Modul: '" + sModuleAlias +"'/ Program: '" + sProgramAlias + "'/ Parameter: 'NumberOfColumn'");
			String sNumberOfColumn = null;
			IKernelConfigSectionEntryZZZ objEntry = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "NumberOfColumn" );
			if(!objEntry.hasAnyValue()){
				String serror = "Parameter existiert nicht in der Konfiguration: 'NumberOfColumn'";
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +serror);
				ExceptionZZZ ez = new ExceptionZZZ(serror,ExceptionZZZ.iERROR_CONFIGURATION_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else{
				sNumberOfColumn = objEntry.getValue();
			}
			int iNumberOfColumn = Integer.parseInt(sNumberOfColumn);
			
			String sNumberOfRow = null;
			objEntry = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "NumberOfRow" );
			if(!objEntry.hasAnyValue()){
				String serror = "Parameter existiert nicht in der Konfiguration: 'NumberOfRow'";
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +serror);
				ExceptionZZZ ez = new ExceptionZZZ(serror,ExceptionZZZ.iERROR_CONFIGURATION_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else{
				sNumberOfRow = objEntry.getValue();
			}
			int iNumberOfRow = Integer.parseInt(sNumberOfRow);
			
			String sHexSideLengthUsed = null;
			objEntry = this.getKernelObject().getParameterByProgramAlias(sModuleAlias, sProgramAlias, "HexSideLength" ); 
			if(!objEntry.hasAnyValue()){
				String serror = "Parameter existiert nicht in der Konfiguration: 'xxxxx'";
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +serror);
				ExceptionZZZ ez = new ExceptionZZZ(serror,ExceptionZZZ.iERROR_CONFIGURATION_VALUE, this,  ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}else{
				sHexSideLengthUsed = objEntry.getValue();
			}
			int iHexSideLengthUsed = StringZZZ.toInteger(sHexSideLengthUsed);
			
			//Die Hexadimensionale Karte aufbauen.			
			HexMapTHM objMap = new HexMapTHM(objKernel, this, iNumberOfColumn, iNumberOfRow, iHexSideLengthUsed);
			this.setHexMap(objMap);
			
			//+++ Das Ziel ist, beim Fallenlassen eines Objekts aus dem glassPane, reagieren zu können
			//ABER: HexMap ist keine JComponent, sondern ein KernelUseObject. Nur die einzelnen Zellen sind JPanels (und damit JComponents) und können direkter als Drop-Ziel verwendet werden.
			//GhostDropListener listener = new GhostDropManagerHexMapTHM(objMap);
		
			//##########################
			//### Fülle das Kartenpanel
			//### Merke: Die Zellen wurden zuvor im Konstruktor von HexMapTHM mit der Methode fillMap() erstellt, inklusive des persistierbaren Objekts.
			//this.setLayout(null);   //Dadurch muss jede Komponente selbst seine "Bounds" setzen. Merke: Ohne Layout-Manger ist das Panel nicht in der Lage sein Größe korrekt zu initialisieren. 
			HexagonaMapLayoutTHM layout = new HexagonaMapLayoutTHM(objKernel, objMap);
			this.setLayout(layout);
			this.setBackground(Color.white);
				
			HashMapMultiZZZ hmCell = objMap.getMapCell();
			
			HexCellTHM cellTemp = null;
			for(int iY=1; iY <= objMap.getRowMax(); iY++){			
				Integer intY=new Integer(iY);
				String sY = intY.toString();
				
				for(int iX=1; iX <= objMap.getColumnMax(); iX++){
					Integer intX = new Integer(iX);
					String sX = intX.toString();
					
					cellTemp = (HexCellTHM) hmCell.get(sX,sY);
					if(cellTemp!=null){
						cellTemp.setVisible(true);
						this.add(cellTemp);
					}
				} //end for iX
			} //end for iY
			
		} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
			
			String sMessage = "Ein Fehler ist aufgetreten; " + ez.getDetailAllLast();
			System.out.println(sMessage);
			
			//Mache nun eine Ausgabe, im UI, da der Fehler ja für den Anwender sichtbar werden muss. 				
			JOptionPane.showMessageDialog (panelParent, sMessage);//TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
		}
}

	public HexMapTHM getHexMap() {
		return this.objHexMap;
	}

	public void setHexMap(HexMapTHM objMap) {
		this.objHexMap = objMap;		
	}
	
	/* 20180901: Die Karte Zoombar zu machen und dafür sorgen, dass die HexCell-Objekte aneinander gefügt bleiben. */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
			String stemp = ReflectCodeZZZ.getMethodCurrentName() + ": Zeichne alle Komponenten erneut.";
			System.out.println(stemp);
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, stemp);
								
			//20180903: Größe der HexCell - Objekte wird nun verändert, aber... die Postion der HexCell-Objekte nicht. Das sieht dann so aus, als gäbe es große Zwischen räume.
			//                 Es hilft es den layout-Manager erneut aufzurufen, dann werden die Sechsecke wieder aneinander gefügt.
			HexagonaMapLayoutTHM layout = (HexagonaMapLayoutTHM) this.getLayout();
			if(layout!=null) layout.layoutContainer(this);
			
			//20180901 Problem: nach dem Aneinanderfügen der Sechsecke sind die Spielsteine auf ihrer alten Postion geblieben.
			//20180901 Lösungsansatz: HexCellTHM-Objekte mit einem eigenen LayoutManager versehen. (HexagonalCellLayoutTHM).
			//                Diesen jeweils in HexMapTHM.fillMap() hinzufügen und dann in wird beim repaint() automatisch HexagonalCellLayoutTHM.layoutcontainer(container) aufgerufen.
	}	
}
