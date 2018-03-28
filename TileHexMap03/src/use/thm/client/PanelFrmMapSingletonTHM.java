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
import basic.zBasicUI.glassPane.dragDropTranslucent.IGhostGlassPanePanelUser;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class PanelFrmMapSingletonTHM  extends KernelJPanelCascadedZZZ implements IGhostGlassPanePanel, IGhostGlassPanePanelUser{
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
		
		//### PANEL WEST - GRUNDLAGEN
		//Merke: Man kann das Panel-West noch nicht erstellen, da der Listener an pictureAdapter.addGostDropListener(listener) erst nach der PANEL_CENTRAL Erstellung existiert.
		//TODO GOON: FGL 20180311
				//HIER MUSSES EINE "LISTE" ÜBER ALLE TROOP- / FLEETVARIANTEN geben, die dann erstellt wird.
				//FÜR JEDE VARIANTE WIRD EIN EIGNER GHOST PICURE ADAPTER ERSTELLT
				//- neben der aciton "new_sale" 
				//- muss es hier auch die THISKEY_ID der VARIANTE als Argument im Konstruktor geben. Diese erhällt man aus der "LISTE".
				
											
		//### PANEL CENTER
		//Merke: Das Panel Center muss erst definiert werden, damit das Panel West dies anschliessend schon als DROP-Ziel verwenden kann.
		HashMap<String, Boolean> hmFlag = new HashMap<String, Boolean>();
		//hmFlag.put("isKernelProgram", true); //2013-07-08: Damit wird angezeigt, das es in der Kernel .ini - Konfigurationsdatei einen entsprechenden Abschnitt gibt, in dem Parameter hierf�r hinterlegt sind.
		hmFlag.put(FLAGZ.COMPONENT_KERNEL_PROGRAM.name(), true);
		PanelMain_CENTERTHM objPanelCenter = new PanelMain_CENTERTHM(objKernel, this, hmFlag);  
		this.setPanelSub("CENTER", objPanelCenter);//Backend Hashtable hinzufügen
		
		//FGL 20130627: Das Panel nicht mehr direkt, sondern über den JScrollPane einbinden this.add(objPanelCenter, BorderLayout.CENTER); //Frontend hinzuf�gen
		JScrollPane scrollPaneCenter = new JScrollPane(objPanelCenter); //Frontend hinzufügen
		this.add(scrollPaneCenter, BorderLayout.CENTER);
		
		//FGL 20130627: Als Drop Ziel nun den JScrollPane. Das Panel als Drop Ziel funktioniert nicht immer, wenn gescrollt wurde.
		//GhostDropListener listener = new GhostDropManagerHexMapPanelTHM(objPanelCenter); //Funktioniert nicht, wenn mit den ScrollBars gescrollt worden ist.
		//Probleme bei zu großen Hexes GhostDropListener listener = new GhostDropManagerHexMapPanelTHM(this.getKernelObject(), scrollPaneCenter, objPanelCenter.getHexMap());  
		GhostDropListener listenerForDropToHexMap = new GhostDropManagerHexMapPanelTHM(this.getKernelObject(), scrollPaneCenter, objPanelCenter.getHexMap());		

		//### PANEL WEST - FÜLLEN				
		//20180326 den Listener an die PictureAdapter der "Box"-Objekte übergeben
		//PanelMain_WESTTHM objPanelWest = new PanelMain_WESTTHM(objKernel, this, pictureAdapter);				
		PanelMain_WESTTHM objPanelWest = new PanelMain_WESTTHM(objKernel, this, listenerForDropToHexMap);
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
