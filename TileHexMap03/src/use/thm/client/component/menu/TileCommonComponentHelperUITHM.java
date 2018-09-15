package use.thm.client.component.menu;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import use.thm.client.component.TileTHM;
import use.thm.persistence.dto.ITileDtoAttribute;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.component.UIHelper;

public class TileCommonComponentHelperUITHM {
	public static void showTileDetailDialog(TileTHM objTile, MouseEvent arg0) throws IOException, ExceptionZZZ{
		
		//TODO: Dies als eigene Dialogbox mit einem Customizbaren Panel, in dem z.B. eine Aktion "Auflösen" eingebaut wird.
		//TODO GOON 20180324: Das wird immer aktueller, zumal jetzt aus dem DTO weitere Angaben aus der Datenbank geholte werden können.
		//++++++++++++++++++++++++++++++++++++++++++++++++++++
		String sMessage = new String();
		
		//Hole weitere Informationen aus dem DTO:
		GenericDTO<ITileDtoAttribute> objDto = objTile.getDto(); //this.getTile().getDto();
		String sShorttext = objDto.get(ITileDtoAttribute.VARIANT_SHORTTEXT);
		Integer intVariantUniquenumber = objDto.get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
		sMessage = sShorttext + "_" + intVariantUniquenumber.toString(); 
				
		//Anzeige der Koordinaten
		Point p = arg0.getPoint();
		String sCoordinates = "X=" + objTile.getMapX() + "; Y=" + objTile.getMapY()  + " (" + p.getX() + "/" + p.getY() + ")";
		sMessage = sMessage + StringZZZ.crlf() + sCoordinates;
		//Trennzeile
		sMessage = sMessage + StringZZZ.crlf();
		Float fltHealth = objDto.get(ITileDtoAttribute.HEALTH);
		float fHealthNormed = fltHealth.floatValue() * 100;
		Float fltHealthNormed = new Float(fHealthNormed);
		String sHealth = "Health: " + StringZZZ.left(fltHealthNormed.toString(),6)+"%";
		sMessage = sMessage + StringZZZ.crlf() + sHealth;
		
		//Trennzeile
		sMessage = sMessage + StringZZZ.crlf();
					
		String sUniquename = objDto.get(ITileDtoAttribute.UNIQUENAME);
		sMessage = sMessage + StringZZZ.crlf() + sUniquename;
		
		//+++++++++++++++++++++++++++++
		//Das ICON
		//+++++++++++++++++++++++++++++
		//TODO GOON 20180725: Das Bild passend zur Eingestellten Zoom Größe in der Applikation auswählen 
		byte[] imageInByte = (byte[]) objDto.get(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01); //Das unveränderte Bild aus der Datenbank, wird hier angezeigt.
		BufferedImage objBufferedImage = UIHelper.toBufferedImage(imageInByte);
		
		//Erst jetzt ein größenverändetes ImageIcon aus dem BufferedImage machen. Merke: Ein Image oder ein BufferedImage funktioniert in der JOptionPane nicht
		ImageIcon objImageIcon = new ImageIcon(objBufferedImage);
		 					
		//+++ AUSGABE +++++++++++++++++++++
		JOptionPane.showMessageDialog(objTile, sMessage, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, objImageIcon);		
		//break;		
	}
	
public static void showTileDetailDialog(TileTHM objTile, ActionEvent arg0) throws ExceptionZZZ{
	//MERKE IN ACTION EVENT IST DIE ANZEIGE DER KOORDINATEN NICHT MÖGLICH	
	try {
	
		//TODO: Dies als eigene Dialogbox mit einem Customizbaren Panel, in dem z.B. eine Aktion "Auflösen" eingebaut wird.
		//TODO GOON 20180324: Das wird immer aktueller, zumal jetzt aus dem DTO weitere Angaben aus der Datenbank geholte werden können.
		//++++++++++++++++++++++++++++++++++++++++++++++++++++
		String sMessage = new String();
		
		//Hole weitere Informationen aus dem DTO:
		GenericDTO<ITileDtoAttribute> objDto = objTile.getDto(); //this.getTile().getDto();
		String sShorttext = objDto.get(ITileDtoAttribute.VARIANT_SHORTTEXT);
		Integer intVariantUniquenumber = objDto.get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
		sMessage = sShorttext + "_" + intVariantUniquenumber.toString(); 
				
		//Anzeige der Koordinaten
//		Point p = arg0.getPoint();
//		String sCoordinates = "X=" + objTile.getMapX() + "; Y=" + objTile.getMapY()  + " (" + p.getX() + "/" + p.getY() + ")";
//		sMessage = sMessage + StringZZZ.crlf() + sCoordinates;
		//Trennzeile
		sMessage = sMessage + StringZZZ.crlf();
		Float fltHealth = objDto.get(ITileDtoAttribute.HEALTH);
		float fHealthNormed = fltHealth.floatValue() * 100;
		Float fltHealthNormed = new Float(fHealthNormed);
		String sHealth = "Health: " + StringZZZ.left(fltHealthNormed.toString(),6)+"%";
		sMessage = sMessage + StringZZZ.crlf() + sHealth;
		
		//Trennzeile
		sMessage = sMessage + StringZZZ.crlf();
					
		String sUniquename = objDto.get(ITileDtoAttribute.UNIQUENAME);
		sMessage = sMessage + StringZZZ.crlf() + sUniquename;
		
		//+++++++++++++++++++++++++++++
		//Das ICON
		//+++++++++++++++++++++++++++++
		//TODO GOON 20180725: Das Bild passend zur Eingestellten Zoom Größe in der Applikation auswählen 
		byte[] imageInByte = (byte[]) objDto.get(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01); //Das unveränderte Bild aus der Datenbank, wird hier angezeigt.
		BufferedImage objBufferedImage = UIHelper.toBufferedImage(imageInByte);
		
		
		//Erst jetzt ein größenverändetes ImageIcon aus dem BufferedImage machen. Merke: Ein Image oder ein BufferedImage funktioniert in der JOptionPane nicht
		ImageIcon objImageIcon = new ImageIcon(objBufferedImage);
		 					
		//+++ AUSGABE +++++++++++++++++++++
		JOptionPane.showMessageDialog(objTile, sMessage, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, objImageIcon);		
		//break;
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
}
