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
import use.thm.client.component.BoxTHM;
import use.thm.persistence.dao.TileDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopVariantDao;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.daoFacade.TroopArmyVariantDaoFacade;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.web.webservice.axis2.pojo.TroopArmyPojo;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.math.RandomZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zKernel.KernelZZZ;

public class DebugCatalogDaoFacade {
	public static void main(String[] args) {				
		try {
			KernelZZZ objKernel = new KernelZZZ();		
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);					
			objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gespeichert UND auch wiedergeholt.				
			
			//##################################
			
			DebugCatalogDaoFacade objDebug = new DebugCatalogDaoFacade();				
			objDebug.debugFillDto();
					
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
		} 
		
	}
	public DebugCatalogDaoFacade(){		
	}
	public boolean debugFillDto(){
		boolean bReturn = false;
		main:{
			try {
				
				KernelZZZ objKernel = new KernelZZZ();		
				HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(objKernel);
				
				//Hole eine Variante für den Test
				TroopArmyVariant objToFill = null;
				String sUniquename = null;
				Long lngThiskey = null;
				
				TroopArmyVariantDao dao = new TroopArmyVariantDao(objContextHibernate);				
				List<TroopArmyVariant>listTroopVariant = dao.searchTroopArmyVariantsAll();			
				if(listTroopVariant.size()==0){
					System.out.println("Es gibt im Katalog keine Armee-Varianten. Beende die Funktion.");				
					break main;
				}else{
					System.out.println("Es gibt im Katalog Armee-Varianten: " + listTroopVariant.size());				
					
					int iChoose = RandomZZZ.getRandomNumber(0, listTroopVariant.size()-1);
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
					
					objToFill = listTroopVariant.get(iChoose);
					sUniquename = objToFill.getSubtype();
					lngThiskey = objToFill.getThiskey();
				}
				
				
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();			 
				GenericDTO dto = objFactoryGenerator.createDtoForClass(BoxTHM.class);
			
				
				TroopArmyVariantDaoFacade objTroopVariantDaoFacade = new TroopArmyVariantDaoFacade(objContextHibernate);
				bReturn = objTroopVariantDaoFacade.fillTroopArmyVariantDto(lngThiskey, dto);
                
				//###########################################
				//### DEBUG AUSGABEN ########################
				//###########################################
				//+++ Debug Ausgabe als Printout
				String sSubtype = (String) dto.get(IBoxDtoAttribute.SUBTYPE);
				System.out.println("Dto - Subtype: " + sSubtype);
				
				String sUniqueName = (String) dto.get(IBoxDtoAttribute.UNIQUENAME);
				System.out.println("Dto - Uniqename: " +sUniqueName);
				
				//+++ Debug Ausgabe der gespeicherten Bilder
				JDialog dialog = new JDialog();     
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setTitle("Image Loaded by DTO from Database - Entiy");
                FlowLayout manager = new FlowLayout();
                dialog.setLayout(manager);
				
				//Das Bild über das DTO holen, gespeichert in der Datenbank
				byte[] byteImage = (byte[]) dto.get(IBoxDtoAttribute.VARIANT_IMAGE_IN_BYTE); 
				if(byteImage!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImage));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
					//JOptionPane.showMessageDialog(null, "Images saved successfully!","Successfull",JOptionPane.INFORMATION_MESSAGE); 
	                 dialog.add(new JLabel(objImageIconTemp)); //Das wäre, wenn man es direkt aus der Datei liest: new ImageIcon(ImageIO.read(getClass().getResourceAsStream(IMAGE_URL)))));	               
				}
				
				//TODO GOON: 200180725 - Über alle 3 Dialobilder _02 _03 in einer Schleife gehen
				//Das Bild für die Dialoganzeige über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageDialog = (byte[]) dto.get(IBoxDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01); 
				if(byteImageDialog!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageDialog));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp)); 
				}
				
				//TODO GOON: 200180725 - Über alle 3 Katalogbilder _02 _03 in einer Schleife gehen
				//Das Bild für die Kataloganzeige über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageCatalog = (byte[]) dto.get(IBoxDtoAttribute.VARIANT_IMAGE_IN_BYTE_01); 
				if(byteImageCatalog!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageCatalog));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp));
				}
				
				//TODO GOON: 200180725 - Über alle 3 Zoombilder _02 .. _03 der GUI Zoomstufen in Schleife gehen
				//Das Bild für das Ziehen über die Hexmap über das DTO holen, gespeichert in der Datenbank
				byte[] byteImageDrag = (byte[]) dto.get(IBoxDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_01); 
				if(byteImageDrag!=null) {
					BufferedImage objBufferedImageTemp = ImageIO.read(new ByteArrayInputStream(byteImageDrag));
					ImageIcon objImageIconTemp = new ImageIcon(objBufferedImageTemp);
				
					dialog.add(new JLabel(objImageIconTemp)); 
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
