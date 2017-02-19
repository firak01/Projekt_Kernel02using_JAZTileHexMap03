package use.thm.client;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashMap;

import javax.swing.JScrollPane;

import use.thm.client.component.HexMapTHM;
import use.thm.client.dragDropTranslucent.GhostDropManagerHexMapPanelTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPaneFrame;
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanel;

import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class PanelFrmMapSingletonTHM  extends KernelJPanelCascadedZZZ implements IGhostGlassPanePanel{
	//GhostDragDrop Interface
	private GhostGlassPane glassPane; //etwas, das per Drag/Drop bewegt wird, wird dorthin als Bild kopiert.

	public PanelFrmMapSingletonTHM(KernelZZZ objKernel, KernelJFrameCascadedZZZ frameParent) throws ExceptionZZZ{
		super(objKernel, frameParent);
		this.enableGhostGlassPane((FrmMapSingletonTHM)frameParent);
		
     	//GhostGlassPane glassPane = ((FrmMapSingletonTHM)this.getFrameParent()).getGhostGlassPane();
		GhostGlassPane glassPane = this.getGhostGlassPane();
		if(glassPane==null) throw new ExceptionZZZ("Kein GhostGlassPane im FrameParent vorhanden");
		
		//Wird normalerweise im KernelJFrameCascaded.lauchchDoing() gemacht, aber bei der Konfiguration der Panels muss schon auf ein Nachbarpanel zugegriffen werden.
		//frameParent.setPanelContent(this); //Ist das oberste Panel, wichtig wenn man aus einem Panel nach den Nachbarpanels sucht.
		
		//### Layout Manager
		this.setLayout(new BorderLayout());
			 
		//### PANEL CENTER
		//Merke: Das Panel Center muss erst definiert werden, da das Panel West dies anschliessend schon als DROP-Ziel verwendet.
		HashMap<String, Boolean> hmFlag = new HashMap<String, Boolean>();
		//hmFlag.put("isKernelProgram", true); //2013-07-08: Damit wird angezeigt, das es in der Kernel .ini - Konfigurationsdatei einen entsprechenden Abschnitt gibt, in dem Parameter hierf�r hinterlegt sind.
		hmFlag.put(FLAGZ.COMPONENT_KERNEL_PROGRAM.name(), true);
		PanelMain_CENTERTHM objPanelCenter = new PanelMain_CENTERTHM(objKernel, this, hmFlag);  
		this.setPanelSub("CENTER", objPanelCenter);//Backend Hashtable hinzuf�gen
		
		//FGL 20130627: Das Panel nicht mehr direkt, sondern �ber den JScrollPane einbinden this.add(objPanelCenter, BorderLayout.CENTER); //Frontend hinzuf�gen
		JScrollPane scrollPaneCenter = new JScrollPane(objPanelCenter); //Frontend hinzuf�gen
		this.add(scrollPaneCenter, BorderLayout.CENTER);
		
		//FGL 20130627: Als Drop Ziel nun den JScrollPane. Das Panel als Drop Ziel funktioniert nicht immer, wenn gescrollt wurde.
		//GhostDropListener listener = new GhostDropManagerHexMapPanelTHM(objPanelCenter); //Funktioniert nicht, wenn mit den ScrollBars gescrollt worden ist.
		//Probleme bei zu gro�en Hexes GhostDropListener listener = new GhostDropManagerHexMapPanelTHM(this.getKernelObject(), scrollPaneCenter, objPanelCenter.getHexMap());  
		GhostDropListener listener = new GhostDropManagerHexMapPanelTHM(this.getKernelObject(), scrollPaneCenter, objPanelCenter.getHexMap());

		 
	    //Es muss das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
		//TODO GOON: Diese pictureAdapter-Objekte in einer HashMap verwalten, so dass über ein Schlüsselwort der korrekte Picture Adapter
		//           sowohl für die zu DRAGGENDE Komponente als auch für die den DROP empfangende Komponente 
		//           geholt werden kann.
		 //Eclipse Worspace
		 File f = new File("");
		 String sPathEclipse = f.getAbsolutePath();
		 String sBaseDirectory = sPathEclipse + File.separator + "images";
		 String sFile = sBaseDirectory + File.separator + "new_sale.png";
		 GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, "new_sale", sFile);
	     pictureAdapter.addGhostDropListener(listener);
		
		//### PANEL WEST
	    //Es muss das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
		PanelMain_WESTTHM objPanelWest = new PanelMain_WESTTHM(objKernel, this, pictureAdapter);				
		this.setPanelSub("WEST", objPanelWest);       //Backend Hashtable hinzuf�gen
		this.add(objPanelWest, BorderLayout.WEST); //Frontend hinzuf�gen
	}
	
	//#### Interface Methoden #####################
			public GhostGlassPane getGhostGlassPane() {
				return this.glassPane;
			}

			public void setGhostGlassPane(GhostGlassPane glassPane) {
				this.glassPane = glassPane;
			}

			public boolean enableGhostGlassPane(IGhostGlassPaneFrame frameParentGlassPaneEnabled) {
				//GhostGlassPane glassPane = ((FrmMapSingletonTHM)this.getFrameParent()).getGhostGlassPane();
				GhostGlassPane glassPane = frameParentGlassPaneEnabled.getGhostGlassPane();
				this.setGhostGlassPane(glassPane);
				return true;
			}
}
