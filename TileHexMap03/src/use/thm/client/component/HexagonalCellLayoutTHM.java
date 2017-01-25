package use.thm.client.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zKernel.KernelZZZ;

/**Klasse steuert das Layout der einzelnen Sechseckzellen.
 *  Dies ist wichtig um zusätzliche Komponenten (z.B. Spielfiguren) hinzuzufügen.
 * @author lindhaueradmin
 *
 */
public class HexagonalCellLayoutTHM extends HexagonalLayoutTHM {

	public HexagonalCellLayoutTHM(KernelZZZ objKernel, HexMapTHM map) {
		super(objKernel, map);
	}


	/** Erstellt das Layout des Containers im angegebenen Panel.
	 *   Dabei wird jede Componente mit "setBounds" direkt plaziert.
	 */
	public void layoutContainer(Container parent) {
		//TODO: ALLES NOCH SO ERSTELLEN; DAS BESTIMMTE SPIELS TEINE IN BESTIMMTE BEREICHE GEZEICHNET WERDEN.
		main:{
			try{
				//1. HashMap der hinzuzufügenden Sechsecke/Areas/Provinzen
				HexMapTHM objMap = this.getHexMap();
				HashMapMultiZZZ hmCell = objMap.getMapCell();
				if(hmCell.size()==0){
					ExceptionZZZ ez = new ExceptionZZZ("No cell provided by map", iERROR_PROPERTY_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				
				//Mathematische Grundlagen
				int iRectWidth = HexCellTHM.getRectWidth(this.getSideLength());
				int iRectWidthHalf = iRectWidth/2;
				int iRectHeight = HexCellTHM.getRectHeight(this.getSideLength());
				int iRowOffset = HexCellTHM.getRowHeightOffset(this.getSideLength());
				
				//Alle Zellen hinzufügen
				HexCellTHM cell = null;
				int iY=0;
				do{
				iY++;
				
				Integer intY=new Integer(iY);
				String sY = intY.toString();
				
				for(int iX=1; iX <= objMap.getColumnMax(); iX++){
					Integer intX = new Integer(iX);
					String sX = intX.toString();
					
					cell = (HexCellTHM) hmCell.get(sX, sY);
					if(cell!=null){
						//Merke: Spielsteine etc. weden beim Laden/füllen der Zellen als Unterpanel hinzugefügt und müssen nicht extra gezeichnet werden
						if(! MathZZZ.isEven(iY)){
							//!!! damit das auch in der 3., 5., 7. Reihe etc. funktioniert muss der RowOffset berücksichtigt werden: (iRowOffset*(iY-1))
							cell.setBounds((iX-1)*iRectWidth,(iY-1)*iRectHeight - (iRowOffset*(iY-1)),iRectWidth+1, iRectHeight); //+1, damit auch der rechte Rand des Sechsecks gezeichnet wird
							
						}else{
							//Die zweite Reihe ist um eine halbe Länge nach rechts verschoben und um das RowOffset (für jede Zeile)  nach oben
							cell.setBounds(((iX-1)*iRectWidth) + iRectWidthHalf,((iY-1)*iRectHeight) - (iRowOffset*(iY-1)),iRectWidth +1, iRectHeight); //+1, damit auch der rechte Rand des Sechsecks gezeichnet wird
						}
						
					}else{
						break;
					}
				}
				}while(cell !=null);
				
				
				
			} catch (ExceptionZZZ e) {
				ReportLogZZZ.write(ReportLogZZZ.ERROR, "ExceptionZZZ: " + e.getDetailAllLast());
			}
		}//end main:
	}
}
