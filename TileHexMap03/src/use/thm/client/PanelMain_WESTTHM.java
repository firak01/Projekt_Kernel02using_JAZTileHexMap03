package use.thm.client;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.LayoutManager;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import use.thm.IVariantCatalogUserTHM;
import use.thm.client.component.BoxTHM;
import use.thm.client.component.HexCellTHM;
import use.thm.client.component.HexMapTHM;
import use.thm.client.component.HexagonaMapLayoutTHM;
import use.thm.client.component.VariantCatalogTHM;
import use.thm.client.dragDropTranslucent.GhostDropManagerHexMapPanelTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropEvent;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostMotionAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPaneFrame;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanel;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class PanelMain_WESTTHM extends KernelJPanelCascadedZZZ implements IGhostGlassPanePanel, IVariantCatalogUserTHM{
	//GhostDragDrop Interface
		private GhostGlassPane glassPane; //etwas, das per Drag/Drop bewegt wird, wird dorthin als Bild kopiert.
		
	//IVariantCatalogTHM Interface
		private VariantCatalogTHM objCatalog=null;
		
		
	//Default Konstruktor, damit die Klasse per Refelction einfachmit newInstance erzeugt werden kann.
	public PanelMain_WESTTHM(){		
	}	
	public PanelMain_WESTTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, GhostDropListener listenerForDropToHexMap,HashMap<String, Boolean>hmFlag) throws ExceptionZZZ  {
		super(objKernel, panelParent,hmFlag);
		this.enableGhostGlassPane((FrmMapSingletonTHM)this.getFrameParent()); //Das Draggen als Bild über den GlassPane. Der wird dann bei der BOX-ERSTELLUNG gebraucht.
		try{				
			this.setJComponentContentDraggable(false); //Nur die einzelnen Labels ziehbar machen
									
		     //++++++++++++++++++++++++++++++++++++++		     
		    //Es muss das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
	        //Angedacht war dafür früher:
	        //           Diese pictureAdapter-Objekte in einer HashMap verwalten, so dass über ein Schlüsselwort der korrekte Picture Adapter
			//           sowohl für die zu DRAGGENDE Komponente als auch für die den DROP empfangende Komponente 
			//           geholt werden kann.
            //20180336: Mit dem Datenbankbackend gilt: Das Bild (den Pfad\\Namen) aus der jeweiligen Variante holen.
	     
	        //TODO GOON 20180326:
	        //Der übergebene String wird beim Drop-Event ausgewertet.
	        //Nur darüber kann dem Backend mitgeteilt werden von welcher "Variante" das neu zu erstellende Entity sein soll.
		    //Das passiert in: GhostDropManagerHexMapPanelTHM.ghostDropped(GhostDropEvent e)
	
			
			//NEU 20180326: Die Box-Objekte im Konstruktor der VariantCatalog-Klasse erzeugen.
			VariantCatalogTHM objCatalog = new VariantCatalogTHM(objKernel, panelParent, glassPane, listenerForDropToHexMap);
			this.setVariantCatalog(objCatalog);
             
			
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
			this.setLayout((LayoutManager) new BoxLayout( this, BoxLayout.Y_AXIS ) );
		     
		     //Nun die HashMapMultiZZZ durchgehen und die Box-Elemente auslesen und hier mit .add(...,box) hinzufügen
		     HashMapMultiZZZ hmCatalog = this.getVariantCatalog().getMapCatalog();
		     for (Iterator<String> iteratorVariantTypes = hmCatalog.getOuterKeySetIterator(); iteratorVariantTypes.hasNext();) {
		    	 String sVariantType = (String) iteratorVariantTypes.next();
		    	 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariantType + "'");

		    	 for (Iterator<String> iteratorVariant = hmCatalog.getInnerKeySetIterator(sVariantType); iteratorVariant.hasNext();) {
		    		 String sVariant = (String) iteratorVariant.next();
		    		 System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX String: '" + sVariant + "'");

		    		 BoxTHM boxTemp = (BoxTHM) hmCatalog.get(sVariantType, sVariant);//z.B.: Box boxTemp = (Box) hmCatalog.get("ARMY","new_sale");		    		 		    		 		     
				     //this.add(BorderLayout.CENTER, boxTemp);
		    		//boxTemp.repaint();
		    		 this.add(boxTemp);
		    	 }		    	 		    	
		     }
		     
		        
			
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
		
		
		@Override
		public VariantCatalogTHM getVariantCatalog() {
			return this.objCatalog;
		}

		@Override
		public void setVariantCatalog(VariantCatalogTHM objCatalog) {
			this.objCatalog = objCatalog;
		}

}
