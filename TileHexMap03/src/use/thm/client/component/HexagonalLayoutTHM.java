package use.thm.client.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

public class HexagonalLayoutTHM extends KernelUseObjectZZZ implements LayoutManager{
	private HexMapTHM objHexMap = null;

	private int iPreferredWidth=0;  //wird berechnet: Die bevorzugte breite des darunterliegenden Panels
	private int iPreferredHeigth=0; //dito
	private int iMinimalWidth = 0;
	private int iMinimalHeigth = 0; //dito 
	
	private int iGapLeftBase = 90; //Bundsteg. Eingef�hrt, damit die Karte richtig reagiert, wenn von aussen gezogene Objekte GEDROPPT werden. Basiswert, weil der Bundsteg ggf. verkleinert werden kann, wenn die Zellen kleiner werden, bzw. es mehr Zellen in einer Zeile gibt.
	private int iGapDownBase = 10; //Bundsteg nach unten, eingef�hrt, damit die Spitzen der Hexecke in der untersten Reihe auch gezeichnet werden. 
	
	public HexagonalLayoutTHM(KernelZZZ objKernel, HexMapTHM map){
		super(objKernel);
		this.objHexMap=map;
	}
	
	public HexMapTHM getHexMap(){
		return this.objHexMap;
	}
	public int getColumnMax(){
		HexMapTHM objHexMap = this.getHexMap();
		if(objHexMap!=null){
			return objHexMap.getColumnMax();
		}else{
			return 0;
		}
	}
	public int getSideLength(){
		HexMapTHM objHexMap = this.getHexMap();
		if(objHexMap!=null){
			return objHexMap.getSideLength();
		}else{
			return 0;
		}
	}
	
	/** Berechne ausgehend von der Seitenl�nge der Sechsecke und der Anzahl der Sechsecke pro Zeile die Breite des Panels
	* @param parent
	* 
	* lindhaueradmin; 11.09.2008 09:43:26
	 */
	public void computeSizes(Container parent){
		//int iCompTotal = parent.getComponentCount(); //Gesamtzahl der Sechsecke im Panel
		//!!! Achtung: Was tun, wenn die Spielfiguren auch hinzugefügt werden ??? Dann ggf. die Gesamtzahl im Konstruktor des Layout Managers mitgeben !!!
		HexMapTHM objMap = this.getHexMap();
		int iCompTotal = objMap.getTotalNumberOfCell();
		
		Dimension d = null;
		
		//Reset preferred/minimum width/heigth
		this.iPreferredWidth=0;
		this.iPreferredHeigth=0;
		this.iMinimalWidth = 0;
		this.iMinimalHeigth = 0;
		
 		for (int i = 0; i < iCompTotal; i++){
			//!!! Vielleicht ist es eine Lösung auf den Namen der Komponente/ Classe der Komponente zuzugreifen und nur bestimmten Komponenten
			//     in die Berechnung aufzunehmen.
			Component c = parent.getComponent(i);
			if(c.isVisible()){
				d=c.getPreferredSize();
			}
			
			if(d!=null) break; //Es reicht eine g�ltige Sechseck - Komponente zu finden. Alle Sechseck-Komponenten sind gleich gro�.
		} //end for
		
		if(d!=null){
			//Berechnung der Breite			
			this.iPreferredWidth += d.width/2;  //in der zweiten Reihe erfolgt ein Versatz um eine Halbe breite. Dies muss zu gesamtbreite hinzugerechnete werden.
			//FGL VOR 20130625 this.iPreferredWidth += (d.width * this.getColumnMax());
			//FGL 20130626 Füge den Bundsteg hinzu, der notwendig geworden ist, damit das Panel auf das DROP Event eines von au�en reingezogenen Objekts reagiert.
			this.iPreferredWidth += (d.width * this.getColumnMax())+ this.getGapLeft();
			
			//Berechnung der Höhe über die Anzahl der Zeilen. Achtung: Es gibt einen Versatz			
			int iRow = objMap.getRowTotal();
			int iOffset = HexCellTHM.getRowHeightOffset(this.getSideLength()) * iRow;
			this.iPreferredHeigth += (d.height*iRow)- iOffset + this.getGapDown();
		}
		
	}
	public int getPreferredWidth(){
		return this.iPreferredWidth;
	}
	public int getPreferredHeight(){
		return this.iPreferredHeigth;
	}
	
	
	//#### Interface bedingte Methoden
	public void addLayoutComponent(String name, Component comp) {
		// TODO Auto-generated method stub
	}

	/** Erstellt das Layout des Containers im angegebenen Panel.
	 *   Dabei wird jede Componente mit "setBounds" direkt plaziert.
	 */
	public void layoutContainer(Container parent) {
		main:{
			try{
				//1. HashMap der hinzuzufügenden Sechsecke/Areas/Provinzen
				HexMapTHM objMap = this.getHexMap();
				HashMapMultiZZZ hmCell = objMap.getMapCell();
				if(hmCell.size()==0){
					ExceptionZZZ ez = new ExceptionZZZ("No cell provided by map", iERROR_PROPERTY_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				int iRectWidth = HexCellTHM.getRectWidth(this.getSideLength());
				int iRectWidthHalf = iRectWidth/2;
				int iRectHeight = HexCellTHM.getRectHeight(this.getSideLength());
				int iRowOffset = HexCellTHM.getRowHeightOffset(this.getSideLength());
				int iGapLeft = this.getGapLeft();  //f�r den linken Rand iGapLeft als Bundsteg einf�gen. Dann reagieren die Zellen dort auf das DROP eines von aussen reingezogenen Objekts
				
				HexCellTHM cell = null;
				for(int iY=1; iY <= objMap.getRowMax(); iY++){				
					Integer intY=new Integer(iY);
					String sY = intY.toString();
					
					for(int iX=1; iX <= objMap.getColumnMax(); iX++){
						Integer intX = new Integer(iX);
						String sX = intX.toString();
						
						cell = (HexCellTHM) hmCell.get(sX, sY);
						if(cell!=null){
							if(! MathZZZ.isEven(iY)){
								//!!! damit das auch in der 3., 5., 7. Reihe etc. funktioniert muss der RowOffset ber�cksichtigt werden: (iRowOffset*(iY-1))
								//FGL Vor 20130625: cell.setBounds((iX-1)*iRectWidth,(iY-1)*iRectHeight - (iRowOffset*(iY-1)),iRectWidth+1, iRectHeight); //+1, damit auch der rechte Rand des Sechsecks gezeichnet wird
								
								//FGL 20130625: Weil beim ReinDRAGGEN der linke Rand nicht reagiert...
								//f�r den linken Rand iGapLeft als Bundsteg einf�gen
								cell.setBounds(((iX-1)*iRectWidth)+ iGapLeft ,(iY-1)*iRectHeight - (iRowOffset*(iY-1)),iRectWidth+1, iRectHeight); //, iRectWidth+1, damit auch der rechte Rand des Sechsecks gezeichnet wird
							}else{
								//FGL Vor 20130625: Die zweite Reihe ist um eine halbe L�nge nach rechts verschoben und um das RowOffset (f�r jede Zeile)  nach oben
								//cell.setBounds(((iX-1)*iRectWidth) + iRectWidthHalf,((iY-1)*iRectHeight) - (iRowOffset*(iY-1)),iRectWidth +1, iRectHeight); //+1, damit auch der rechte Rand des Sechsecks gezeichnet wird
								
								//FGL 20130625: Weil beim ReinDRAGGEN der linke Rand nicht reagiert...
								//f�r den linken Rand iGapLeft als Bundsteg einf�gen
								cell.setBounds(((iX-1)*iRectWidth)+ iGapLeft + iRectWidthHalf,((iY-1)*iRectHeight) - (iRowOffset*(iY-1)),iRectWidth +1, iRectHeight); //+1, damit auch der rechte Rand des Sechsecks gezeichnet wird
							}
						}
					}//end for iX
				}//end for iY
			} catch (ExceptionZZZ e) {
				ReportLogZZZ.write(ReportLogZZZ.ERROR, "ExceptionZZZ: " + e.getDetailAllLast());
			}
		}//end main:
	}

	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**Berechnet die bevorzugten Größenmaße f�r das angegebene Panel
	 *  bei den gegebenen Komponenten in dem angegebenen Eltern Container
	 */ 
	public Dimension preferredLayoutSize(Container parent) {
		Dimension dim = new Dimension(0,0);
		
		this.computeSizes(parent);
		
		
		//Always add the container's insets !
		Insets insets = parent.getInsets();
		dim.width = this.getPreferredWidth() + insets.left + insets.right;
		dim.height = this.getPreferredHeight() + insets.top + insets.bottom;
		
		return dim;		
	}

	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub
		
	}
	
	
	//######### GETTER / SETTER
	public int getGapLeft(){
		return this.iGapLeftBase;
	}
	public void setGapLeft(int iGapLeftBase){
		this.iGapLeftBase = iGapLeftBase;
	}
	
	public int getGapDown(){
		//TODO: ggf. Mathematisch ausrechenbar, z.B. auf Basis der Seitenl�nge des Hexecks.
		return this.iGapDownBase;
	}
	public void setGapDown(int iGapDownBase){
		this.iGapDownBase = iGapDownBase;
	}

}
