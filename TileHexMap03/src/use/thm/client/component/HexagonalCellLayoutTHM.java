package use.thm.client.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

/**Klasse steuert das Layout der einzelnen Sechseckzellen.
 *  Dies ist wichtig um zus�tzliche Komponenten (z.B. Spielfiguren) hinzuzuf�gen.
 * @author lindhaueradmin
 *
 */
public class HexagonalCellLayoutTHM extends KernelUseObjectZZZ implements LayoutManager{
	private HexCellTHM objHexCell = null;
	
	public HexagonalCellLayoutTHM(KernelZZZ objKernel, HexCellTHM cell) {
		super(objKernel);
		this.objHexCell = cell;
	}
	
	public HexCellTHM getHexCell(){
		return this.objHexCell;
	}


	/** Erstellt das Layout des Containers im angegebenen Panel.
	 *   Genauer: In den HexMapTHM.fillMap() Methoden...
	 */
	public void layoutContainer(Container parent) {
		main:{
			String stemp = ReflectCodeZZZ.getMethodCurrentName() + ": Zeichne alle Komponenten INNERHALB DER ZELLE erneut.";
			System.out.println(stemp);
			ReportLogZZZ.write(ReportLogZZZ.DEBUG, stemp);
			
			HexCellTHM cell = this.getHexCell();
			//Component[] objaComponent = (Component[]) cell.getComponents(); //NIX ENTHALTEN...
			//20180903: Die Zelle hat scheinbar keine Components. Beim Erstellen der Spielsteine werden Sie auch nicht Swing-mäßig hinzugefügt.
			//                 Wie wird denn dann die X/Y Position des Spielsteins "errechnet"?
			//                 Das ist nicht korrekt... Die Component wird swingmäßig so hinzugefügt:
			//                 AreaCellTHM.onTileCreated(...) enthält folgende Zeile: this.add(eventTileCreated.getTile());

			Component[] objaComponent = (Component[]) parent.getComponents();
			if(objaComponent.length>=1){
				stemp = ReflectCodeZZZ.getMethodCurrentName() + ": Positioniere Komponenten innerhalb der Zelle  Map (X/Y): " + cell.getMapX() + "/ " + cell.getMapY() + "| Pixel (X/Y): " + cell.getX() + "/" + cell.getY();
				System.out.println(stemp);
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, stemp);
				objaComponent[0].setLocation(50, 50);//TODO GOON 20180903: Hier irgendwie die Mitte der Zelle ausrechnen... dies muss auch beim Zoomen verändert werden.
				//... Hier gäbe es dann die Möglichkeit weitere Komponenten in der Zelle an einer anderen Stellen zu positionieren.
			}
		}//end main:
	}


	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Dimension minimumLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Dimension preferredLayoutSize(Container arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void removeLayoutComponent(Component arg0) {
		// TODO Auto-generated method stub
		
	}
}
