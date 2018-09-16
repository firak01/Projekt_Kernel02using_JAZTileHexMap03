package use.thm.client.component.menu;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import use.thm.ApplicationSingletonTHM;
import use.thm.client.component.TileTHM;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.zBasicUI.component.UIHelperTHM;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.component.UIHelper;

public class TileCommonComponentHelperUITHM {
	public static void showTileDetailDialog(TileTHM objTile, MouseEvent arg0) throws IOException, ExceptionZZZ{
		
		//TODO: Dies als eigene Dialogbox mit einem Customizbaren Panel, in dem z.B. eine Aktion "Auflösen" eingebaut wird.
		//TODO GOON 20180324: Das wird immer aktueller, zumal jetzt aus dem DTO weitere Angaben aus der Datenbank geholte werden können.
		//++++++++++++++++++++++++++++++++++++++++++++++++++++
		String sMessage = _computeTileDetailString(objTile);
		
		//Leerzeile
		sMessage = sMessage + StringZZZ.crlf();
		
		//Nur m Mouse Event steht der angelickte Punkt zur Verfügung
		Point p = arg0.getPoint();
		sMessage = sMessage + "Point clicked (X/Y) = (" + p.getX() + "/" + p.getY() + ")";
		
		//+++++++++++++++++++++++++++++
		//Das ICON
		//+++++++++++++++++++++++++++++		 					
		ImageIcon objImageIcon = _computeTileDetailImageIcon(objTile);
		
		//+++ AUSGABE +++++++++++++++++++++
		_showTileDetailDialog(objTile, sMessage, objImageIcon);	
		//break;		
	}
	
public static void showTileDetailDialog(TileTHM objTile, ActionEvent arg0) throws ExceptionZZZ{
	//MERKE IN ACTION EVENT IST DIE ANZEIGE DER POINT-KOORDINATEN NICHT MÖGLICH	
	try {
	
		//TODO: Dies als eigene Dialogbox mit einem Customizbaren Panel, in dem z.B. eine Aktion "Auflösen" eingebaut wird.
		//TODO GOON 20180324: Das wird immer aktueller, zumal jetzt aus dem DTO weitere Angaben aus der Datenbank geholte werden können.
		//++++++++++++++++++++++++++++++++++++++++++++++++++++
		String sMessage = _computeTileDetailString(objTile); 
		
		//+++++++++++++++++++++++++++++
		//Das ICON
		//+++++++++++++++++++++++++++++
		ImageIcon objImageIcon = _computeTileDetailImageIcon(objTile);	
		
		//+++ AUSGABE +++++++++++++++++++++
		_showTileDetailDialog(objTile, sMessage, objImageIcon);		
		//break;
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}


/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung passt.
	 * Hier: Übergebener HexMapZoomFactor-ALIAS.
	 * 
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static byte[] getVariantImageUsedInByte(TileTHM objTile, String sZoomFactorAlias) throws ExceptionZZZ{
		GenericDTO<ITileDtoAttribute>objDto = objTile.getDto();
		return UIHelperTHM.getVariantImageUsedInByte(objDto,"IMAGEDIALOG", sZoomFactorAlias);	
	}

private static ImageIcon _computeTileDetailImageIcon(TileTHM objTile) throws ExceptionZZZ, IOException {
	ImageIcon objReturn = null;
	main:{
		//TODO GOON 20180725: Das Bild passend zur Eingestellten Zoom Größe in der Applikation auswählen 
//				GenericDTO<ITileDtoAttribute> objDto = objTile.getDto();
//				byte[] imageInByte = (byte[]) objDto.get(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01); //Das unveränderte Bild aus der Datenbank, wird hier angezeigt.
		
				String sZoomFactorAlias = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasCurrent();
				byte[] imageInByte = TileCommonComponentHelperUITHM.getVariantImageUsedInByte(objTile, sZoomFactorAlias);
				BufferedImage objBufferedImage = UIHelper.toBufferedImage(imageInByte);
				
				//Erst jetzt ein größenverändetes ImageIcon aus dem BufferedImage machen. Merke: Ein Image oder ein BufferedImage funktioniert in der JOptionPane nicht
				objReturn = new ImageIcon(objBufferedImage);
				 
	}//end main
	return objReturn;	
}

private static String _computeTileDetailString(TileTHM objTile) throws ExceptionZZZ {
	String sReturn = new String("");
	main:{
		if(objTile==null) break main;
				
		//Hole weitere Informationen aus dem DTO:
		GenericDTO<ITileDtoAttribute> objDto = objTile.getDto(); //this.getTile().getDto();
		String sShorttext = objDto.get(ITileDtoAttribute.VARIANT_SHORTTEXT);
		Integer intVariantUniquenumber = objDto.get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
		String sMessage = sShorttext + "_" + intVariantUniquenumber.toString(); 
				
		//Anzeige der Koordinaten
		String sCoordinates = "X=" + objTile.getMapX() + "; Y=" + objTile.getMapY(); 
		sMessage = sMessage + StringZZZ.crlf() + sCoordinates;
		
		//Trennzeile
		sMessage = sMessage + StringZZZ.crlf();
		
		//Anzeige der Stärke
		Float fltHealth = objDto.get(ITileDtoAttribute.HEALTH);
		float fHealthNormed = fltHealth.floatValue() * 100;
		Float fltHealthNormed = new Float(fHealthNormed);
		String sHealth = "Health: " + StringZZZ.left(fltHealthNormed.toString(),6)+"%";
		sMessage = sMessage + StringZZZ.crlf() + sHealth;
		
		//Trennzeile
		sMessage = sMessage + StringZZZ.crlf();
		
		//Anzeige des technischen Namens
		String sUniquename = objDto.get(ITileDtoAttribute.UNIQUENAME);
		sMessage = sMessage + StringZZZ.crlf() + sUniquename;
		
		sReturn = sMessage;
	}
	return sReturn;
}

private static void _showTileDetailDialog(TileTHM objTile, String sMessage, ImageIcon objImageIcon) throws ExceptionZZZ{
	
	//Das wäre ohne die Angabe des Fonts JOptionPane.showMessageDialog(objTile, sMessage, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, objImageIcon);
	
	//Merke: Will man den Font in der JOptionPane ändern, muss man dies über ein JLabel Machen.
	Font objFontGui = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
	
	//Merke: Will man Zeilnumbrüche in einem JLabel darstellen, so muss man dies mit HTML machen.
	sMessage = StringZZZ.asHtml(sMessage);	
		
	JLabel label = new JLabel(sMessage);
	label.setFont(objFontGui);
	JOptionPane.showMessageDialog(objTile, label, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, objImageIcon);
	
}
	
	
}
