package debug.thm.persistence.dao.facade;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import debug.thm.persistence.dao.tilevariant.DebugTroopArmyVariantDao;
import use.thm.client.component.ArmyTileTHM;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.TroopArmy;
import use.thm.web.webservice.axis2.pojo.TroopArmyPojo;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.math.RandomZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zKernel.KernelZZZ;

public class DebugTroopArmyDaoFacade {
	public static void main(String[] args) {				
		try {
			KernelZZZ objKernel = new KernelZZZ();		
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
			objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
			//##################################
			
			DebugTroopArmyDaoFacade objDebug = new DebugTroopArmyDaoFacade();				
			objDebug.debugFillDto();
					
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
		} 
		
	}
	public DebugTroopArmyDaoFacade(){		
	}
	public boolean debugFillDto(){
		boolean bReturn = false;
		main:{
			try {
				
				KernelZZZ objKernel = new KernelZZZ();		
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
				//Hole eine Army für den Test
				String sMap = "EINS";
				TroopArmy objToFill = null;
				String sUniquename = null;
				
				TroopArmyDao dao = new TroopArmyDao(objContextHibernate);				
				List<TroopArmy>listTroop = dao.searchTroopArmiesAll(sMap);			
				if(listTroop.size()==0){
					System.out.println("Es gibt auf der Karte '" + sMap + " keine platzierte Armeen. Beende die Funktion.");				
					break main;
				}else{
					System.out.println("Es gibt auf der Karte '" + sMap + " platzierte Armeen: " + listTroop.size());				
					
					int iChoose = RandomZZZ.getRandomNumber(0, listTroop.size()-1);
					System.out.println("Verwende eine zufällig ... iChoose="+iChoose);
					
//					for(TroopArmy objTroop : listTroop){
//						TroopArmyPojo objPojo = new TroopArmyPojo();
//						objPojo.setUniquename(objTroop.getUniquename());
//						objPojo.setPlayer(new Integer(objTroop.getPlayer()));
//						objPojo.setType(objTroop.getTroopType());
//						
//						objPojo.setMapAlias(sMap);
//									
//						objPojo.setMapX(new Integer(objTroop.getMapX()));
//						objPojo.setMapY(new Integer(objTroop.getMapY()));
//						
//						listReturn.add(objPojo);
//					}
					
					objToFill = listTroop.get(iChoose);
					sUniquename = objToFill.getUniquename();
				}
				
				
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();			 
				GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
			
				
				TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);
				bReturn = objTroopDaoFacade.fillTroopArmyDto(sUniquename, dto);
                
				//###########################################
				//### DEBUG AUSGABEN ########################
				//###########################################
				//+++ Debug Ausgabe als Printout
				String sShorttext = (String) dto.get(ITileDtoAttribute.VARIANT_SHORTTEXT);
				System.out.println("Dto - Shorttext: " + sShorttext);
				
				Integer intInstanceUniqueNumber = (Integer) dto.get(ITileDtoAttribute.INSTANCE_VARIANT_UNIQUENUMBER);
				System.out.println("Dto - Instance_VARIANT_Uniquenumber: " + intInstanceUniqueNumber.toString());
				
				//+++ Debug Ausgabe der gespeicherten Bilder
				JDialog dialog = new JDialog();     
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setTitle("Image Loaded by DTO from Database - Entiy");
                FlowLayout manager = new FlowLayout();
                dialog.setLayout(manager);
				
				//Das Bild über das DTO holen, gespeichert in der Datenbank
				byte[] byteImage = (byte[]) dto.get(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE); 
				if(byteImage!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImage));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
					//JOptionPane.showMessageDialog(null, "Images saved successfully!","Successfull",JOptionPane.INFORMATION_MESSAGE); 
	                 dialog.add(new JLabel(objImageIconTemp)); //Das wäre, wenn man es direkt aus der Datei liest: new ImageIcon(ImageIO.read(getClass().getResourceAsStream(IMAGE_URL)))));	               
				}
				
				//Das Bild für die Dialoganzeige über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageDialog = (byte[]) dto.get(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE); 
				if(byteImageDialog!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageDialog));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp)); //Für die Kataloganzeige reduzierte Datei
				}
				
				
				//Das Bild für die Kataloganzeige über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageCatalog = (byte[]) dto.get(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE); 
				if(byteImageCatalog!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageCatalog));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp)); //Für die Kataloganzeige reduzierte Datei
				}
				
				//Das Bild für das Ziehen über die Hexmap über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageDrag = (byte[]) dto.get(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE); 
				if(byteImageDrag!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageDrag));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp)); //Für die Kataloganzeige reduzierte Datei
				}
				
				//Das Bild für die Hexmap-Anzeige über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageHexmap = (byte[]) dto.get(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE); 
				if(byteImageHexmap!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageHexmap));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp)); //Für die Kataloganzeige reduzierte Datei
				}
				
	             dialog.pack();
	             dialog.setLocationByPlatform(true);
	             dialog.setVisible(true);
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				bReturn = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//end main:
		return bReturn;
	}
	
}
