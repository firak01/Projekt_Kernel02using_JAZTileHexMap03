package use.thm.client;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.File;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.HexagonalLayoutTHM;
import use.thm.client.dragDropTranslucent.GhostDropManagerHexMapPanelTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;

import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostMotionAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPaneFrame;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanel;

import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class PanelMain_WESTTHM extends KernelJPanelCascadedZZZ implements IGhostGlassPanePanel{
	//GhostDragDrop Interface
		private GhostGlassPane glassPane; //etwas, das per Drag/Drop bewegt wird, wird dorthin als Bild kopiert.

	//Default Konstruktor, damit die Klasse per Refelction einfachmit newInstance erzeugt werden kann.
	public PanelMain_WESTTHM(){		
	}	
	public PanelMain_WESTTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, GhostPictureAdapter pictureAdapter) throws ExceptionZZZ  {
		super(objKernel, panelParent);
		this.enableGhostGlassPane((FrmMapSingletonTHM)this.getFrameParent());
		try{	
			//Das Draggen als Bild über den GlassPane
		    //GhostGlassPane glassPane = ((FrmMapSingletonTHM)this.getFrameParent()).getGhostGlassPane();
			GhostGlassPane glassPane = this.getGhostGlassPane();
			if(glassPane==null) throw new ExceptionZZZ("Kein GhostGlassPane im FrameParent vorhanden");
		     
			this.setJComponentContentDraggable(false); //Nur die einzelnen Labels ziehbar machen
			
			JLabel label;
		     Box box = Box.createVerticalBox();
		     box.setBorder(new EmptyBorder(0, 0, 0, 20));
		     
		   //Eclipse Workspace
			File f = new File("");
		    String sPathEclipse = f.getAbsolutePath();
		    ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Eclipse absolut path: " + sPathEclipse);
	        //String sPathParent = sPathEclipse.substring(0, sPathEclipse.lastIndexOf(System.getProperty("file.separator")));
	        String sBaseDirectory = sPathEclipse + File.separator + "images";
	        String sFile = sBaseDirectory + File.separator + "new_sale.png";
		     
		     //Ein Label hinzuf�gen mit dem entsprechenden Bild
		     box.add(label = UIHelper.createLabelWithIcon("Test Drag", sFile));
		     //++++++++++++++++++++++++++++++++++++++
		     		     
		     //wird nun von au�en übergeben, muss der gleiche sein, der den DROP-Event abf�ngt.		     GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, "new_sale", sFile); //Das wird immer und �berall redundant gemacht, da es ja mehrere Pictures gibt. Es wird auch redundant gemacht beim DROPP-EVENT abfangen.
		     label.addMouseListener(pictureAdapter); //Beim Clicken wird das Bild vom pictureAdapter an die passende Stelle im glassPane gesetzt.

		     //Das DRAGGEN 
		     label.addMouseMotionListener(new GhostMotionAdapter(glassPane));
		     
		     
		     /* FGL 20130627: Nach Einführen des JScrollPanes funktioniert das Droppen nicht mehr 100%ig wenn gescrollt wurde.
		      * Daher versuchen den JScrollPane als Dropp-Ziel einzubinden, was in PanelFrmMapSingletonTHM passiert
		      *
		     //!!! Das DROPPEN wird NICHT in dem darübergeordneten Panel definiert, da der pictureAdapter nur hier an das Label.addMouseListener() übergeben werden kann.
		     //Das DROPPEN: Merke Die TileHexMap ist keine JComponent. Das sind nur die einzelnen Zellen. 
		     //Die einzelnen Zellen zu verwenden ist aber hier noch zuviel und zue tief verschachtelt. Darum das Panel als Drop-Ziel. 
		     KernelJPanelCascadedZZZ panelCenter = this.getPanelNeighbour("CENTER");
		     GhostDropListener listener = new GhostDropManagerHexMapPanelTHM(panelCenter);
		     pictureAdapter.addGhostDropListener(listener);
		     */

		     //+++++++++++++++++++++++++++++++++++++++
		     this.setLayout(new BorderLayout());
			 this.add(BorderLayout.CENTER, box);
		        
			
		} catch (ExceptionZZZ ez) {				
			this.getLogObject().WriteLineDate(ez.getDetailAllLast());
			ReportLogZZZ.write(ReportLogZZZ.ERROR, ez.getDetailAllLast());
		}
}
	
	//#### Interface Methoden #####################
		public GhostGlassPane getGhostGlassPane() {
			return this.glassPane;
		}

		public void setGhostGlassPane(GhostGlassPane glassPane) {
			this.glassPane = glassPane;
		}
		
		public boolean enableGhostGlassPane(IGhostGlassPaneFrame frameParentGhostGlassPaneEnabled) {
			//GhostGlassPane glassPane = ((FrmMapSingletonTHM)this.getFrameParent()).getGhostGlassPane();
			GhostGlassPane glassPane = frameParentGhostGlassPaneEnabled.getGhostGlassPane();
			this.setGhostGlassPane(glassPane);
			return true;
		}
}
