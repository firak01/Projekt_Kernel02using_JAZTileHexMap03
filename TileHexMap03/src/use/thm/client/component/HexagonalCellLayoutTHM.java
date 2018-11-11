package use.thm.client.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import use.thm.ApplicationSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

/**Klasse steuert das Layout der einzelnen Sechseckzellen.
 *  Dies ist wichtig um zus�tzliche Komponenten (z.B. Spielfiguren) hinzuzuf�gen.
 * @author lindhaueradmin
 *
 */
public class HexagonalCellLayoutTHM extends KernelUseObjectZZZ implements LayoutManager{
	private HexCellTHM objHexCell = null;
	
	public HexagonalCellLayoutTHM(IKernelZZZ objKernel, HexCellTHM cell) {
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
//			try {
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
				Component objComponent = objaComponent[0];				
				stemp = ReflectCodeZZZ.getMethodCurrentName() + ": Positioniere Komponenten innerhalb der Zelle  Map (X/Y): " + cell.getMapX() + "/ " + cell.getMapY() + "| Pixel (X/Y): " + cell.getX() + "/" + cell.getY();
				System.out.println(stemp);
				ReportLogZZZ.write(ReportLogZZZ.DEBUG, stemp);
				
//				int iComponentWidth = objComponent.getWidth(); //Die ursprüngliche Größe der Component bleibt auch beim Zoomen immer gleich.... 
//				int iComponentHeight = objComponent.getHeight();
				Dimension dim = objComponent.getPreferredSize();
				int iComponentPreferredWidth = (int) dim.getWidth();
				int iComponentPreferredHeight = (int) dim.getHeight();
				
//				String sZoomFactorAlias = ApplicationSingletonTHM.getInstance().getHexZoomFactorAliasCurrent();
//				String sZoomFactor = ApplicationSingletonTHM.getInstance().getHexZoomFactorCurrent();
				
				int iCellWidth = parent.getWidth(); //Die Größe des Parents bleibt beim Zoomen nicht gleich, sie wird angepasst.
				int iCellHeight = parent.getHeight();
				
				int iComponentWidth = iComponentPreferredWidth; //objComponent.getWidth(); //Die ursprüngliche Größe der Component bleibt auch beim Zoomen immer gleich.... 
				int iComponentHeight = iComponentPreferredHeight; //objComponent.getHeight();
				int iPositionHeight = (iCellHeight - iComponentHeight) / 2;
				int iPositionWidth = (iCellWidth - iComponentWidth) / 2;
				objaComponent[0].setLocation(iPositionWidth, iPositionHeight);//TODO GOON 20180903: Hier irgendwie die Mitte der Zelle ausrechnen... dies muss auch beim Zoomen verändert werden.
				//... Hier gäbe es dann die Möglichkeit weitere Komponenten in der Zelle an einer anderen Stellen zu positionieren.
				
			}
//			} catch (ExceptionZZZ e) {				
//				e.printStackTrace();
//			}
		}//end main:
	}

	@Override
	public void addLayoutComponent(String arg0, Component arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub
		
	}
}
