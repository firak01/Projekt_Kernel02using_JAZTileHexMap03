package use.zBasicUI.glassPane.dragDropTranslucent;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.SwingUtilities;

import use.thm.client.component.BoxTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;

public class GhostPictureAdapter4BoxTHM extends GhostPictureAdapter {
	
	public GhostPictureAdapter4BoxTHM(GhostGlassPane glassPane, String action){
		super(glassPane, action);
	}
	
	/* 20180905: Überschreibe die Methode der Elternklasse: Hier mit der Besonderheit, dass das Bild "frisch" geholt wird und deshalb dem ZoomLevel der Karte entspricht */
	 public void mousePressed(MouseEvent e)
	    {
	    	/* FGL 20130627: Klasse GhostMotionAdapter verwendet den GlassPane beim DRAGGEN.
	    	 * Aber: Wenn man einen JScrollPane verwendet, funktioniert der alte GlassPane nicht mehr richtig.
	    	 * 
	    	 * Darum im ersten Wurf beim Clicken der Mouse-Taste den glassPane neu erstellen.*/
			try {
	    	
	        Component c = e.getComponent();
	        
	        System.out.println("GhostPictureAdapter: MousePressed on component using GhostPictureAdapter");
	        //Scheint so nicht zu funktionieren... this.refreshGhostGlassPane(); //FGL 20130627 neu, aktualisiere den GlassPane, weil beim Verschieben eines Panels mit ScrollBars es sonst nicht mehr funktioniert.
	        glassPane.setVisible(true);

	        Point p = (Point) e.getPoint().clone();
	        SwingUtilities.convertPointToScreen(p, c);
	        SwingUtilities.convertPointFromScreen(p, glassPane);
	        glassPane.setPoint(p);
	        
	        BoxTHM objBox = (BoxTHM) c;
	        byte[] imageInByte = objBox.getVariantCatalogImageDragUsedInByte();
	        BufferedImage image = UIHelper.toBufferedImage(imageInByte);			
	        if(image!=null) glassPane.setImage(image); //Verwende das hier gefundene Bild. Darum heisst es ja auch "Picture Adapter".	      
	        glassPane.validate();
	        glassPane.repaint();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ExceptionZZZ e1) {
				e1.printStackTrace();
			}
	    }
}
