package use.thm.client.component;

import java.io.File;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import use.thm.ApplicationSingletonTHM;
import use.thm.ApplicationTHM;
import use.thm.ITileEventUserTHM;
import use.thm.IVariantCatalogUserTHM;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostMotionAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class VariantCatalogTHM  extends KernelUseObjectZZZ{
	private HashMapMultiZZZ hmCatalog = new HashMapMultiZZZ();  //Hashmap mit Hashmap, in der die Sechseckzellen in Form von Koordinaten (z.B. "1","1") abgelegt sind.
	
	public VariantCatalogTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, GhostGlassPane glassPane, GhostDropListener listenerForDropToHexMap) throws ExceptionZZZ{
		super(objKernel);
		if(panelParent==null){
			ExceptionZZZ ez = new ExceptionZZZ("No ParentPanel provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		 		
		//20180326	
		//THEMA : Persistierung in einer Datenbank. Falls die Datenbank neu ist, müssen ggfs. noch die GEBIETE und die DEFAULTTRUPPEN erstellt werden.
		//             Die anderen Tabellen (Defaulttexte, ImmutableTexte, Army und Fleetvarianten) wurden schon auf Applikationsebene erstellt.
		//FGL: Nachdem die Überprüfung, ob die Datenbank existiert Aufgabe des Application-Objekt gewerden ist, hier das entsprechende Flag auslesen
		boolean bDbExists = !ApplicationSingletonTHM.getInstance().getFlag(ApplicationTHM.FLAGZ.DATABASE_NEW.name());
		
	    //Also: Die Varianten-Objekte werden schon vorher auf Applikationsebene erzeugt.
		//Hier also alle auslesen....... TODO GOON 20180326
		
		//Tja und dann müssen daraus "Box"-Objekte erstellt werden, die in die HashMapMultiZZZ abgelegt werden.
		//!!! nicht die Entities direkt irgendwo abspeichern.
		//
		
		//TODO GOON 20180326: Solche Box-Objekte im Konstruktor des VariantCatalogTHM-Objekts erzeugen.
	     Box box = Box.createVerticalBox();
	     box.setBorder(new EmptyBorder(0, 0, 0, 20));
	     
	   //Eclipse Workspace
		File f = new File("");
	    String sPathEclipse = f.getAbsolutePath();
	    ReportLogZZZ.write(ReportLogZZZ.DEBUG, "Eclipse absolut path: " + sPathEclipse);
       //String sPathParent = sPathEclipse.substring(0, sPathEclipse.lastIndexOf(System.getProperty("file.separator")));
       String sBaseDirectory = sPathEclipse + File.separator + "images";
       String sFile = sBaseDirectory + File.separator + "new_sale.png";
	     
	    //Ein Label hinzufügen mit dem entsprechenden Bild
       JLabel label = UIHelper.createLabelWithIcon("NEW ARMY", sFile); 
	    box.add(label);
		
		GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, "new_sale", sFile);
	    pictureAdapter.addGhostDropListener(listenerForDropToHexMap);
						
	    //Es muss das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
    
	     
	     //wird nun von außen übergeben, muss der gleiche sein, der den DROP-Event abfängt.		     GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, "new_sale", sFile); //Das wird immer und �berall redundant gemacht, da es ja mehrere Pictures gibt. Es wird auch redundant gemacht beim DROPP-EVENT abfangen.
	     label.addMouseListener(pictureAdapter); //Beim Clicken wird das Bild vom pictureAdapter an die passende Stelle im glassPane gesetzt.

	     //Das DRAGGEN 
	     label.addMouseMotionListener(new GhostMotionAdapter(glassPane));
	     
	     
	     //Nun diese Box-Objekte wegsichern...
//	     HashMap<String, Box> hmBox = new HashMap<String, Box>();
//	     hmBox.put("new_sale", box);
	     
	     
//	     
	     this.getMapCatalog().put("ARMY", "new_sale", box);
	     
	     
	     
	     //############################################################
	   //TODO GOON 20180326: Solche Box-Objekte im Konstruktor des VariantCatalogTHM-Objekts erzeugen.
	     //ABER: NUN GIBT ES PROBLEME, WEIL DIE PERSISTIERUNG SCHEINBAR IMMER 2x AUSGEFÜHRT WIRD.
	     //      DANACH GIBT ES NÄMLICHE DEN FEHLER, DASS SCHON EIN TILE IM FELD IST.....
	     
	     
	     Box box02 = Box.createVerticalBox();
	     box02.setBorder(new EmptyBorder(0, 0, 0, 20));
	     
	     String sFile02 = sBaseDirectory + File.separator + "new_sale.png";
	     
	    //Ein Label hinzufügen mit dem entsprechenden Bild
	     JLabel label02 = UIHelper.createLabelWithIcon("NEW FLEET", sFile); 
	    box02.add(label02);
		
		GhostPictureAdapter pictureAdapter02 = new GhostPictureAdapter(glassPane, "new_sale02", sFile);
	    pictureAdapter02.addGhostDropListener(listenerForDropToHexMap);
	    //Merke: Verwendet man hier den bisherigen Picture Adapter und hängt noch einen weitern dropListener an, 
	    //       dann wird 2x ein drop durchgeführt. D.h. es wird 2x ein Entity erzeugt. Beim 2. Mal gibt es dann die Fehlermeldung:
	    //       'Maximale Anzahl der Amreen / Flotten im Feld erreicht'. Darum ist es wichtig hier immer einen NEUEN picture Adapter zu erzeugen, pro Variante.
	    
	    				
	    //Es muss das pictureAdapter-Objekt der gleich sein, der das DRAGGEN bereitstellt, wie auch das DROPPEN!!!!
    
	     
	     //wird nun von außen übergeben, muss der gleiche sein, der den DROP-Event abfängt.		     GhostPictureAdapter pictureAdapter = new GhostPictureAdapter(glassPane, "new_sale", sFile); //Das wird immer und �berall redundant gemacht, da es ja mehrere Pictures gibt. Es wird auch redundant gemacht beim DROPP-EVENT abfangen.
	     label02.addMouseListener(pictureAdapter02); //Beim Clicken wird das Bild vom pictureAdapter an die passende Stelle im glassPane gesetzt.

	     //Das DRAGGEN 
	     label02.addMouseMotionListener(new GhostMotionAdapter(glassPane));
	     
	     
	     
	     this.getMapCatalog().put("FLEET", "new_sale02", box02);
	}
	
	/**Hashmap mit Hashmap, in der die Box-Objekte in Form von Variantenklasse und Typ (z.B. "FLEET","1") abgelegt sind.
	 */
	public HashMapMultiZZZ getMapCatalog(){
		return this.hmCatalog;
	}
}
