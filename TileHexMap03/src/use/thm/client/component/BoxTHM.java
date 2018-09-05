package use.thm.client.component;

import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import custom.zKernel.file.ini.FileIniZZZ;
import use.thm.ApplicationSingletonTHM;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.zBasicUI.component.UIHelperTHM;
import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;
import basic.zBasicUI.component.UIHelper;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostGlassPane;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostMotionAdapter;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostPictureAdapter;

public class BoxTHM extends Box{
	private static final long serialVersionUID = 5244639803783507826L;
	private GenericDTO<IBoxDtoAttribute>objDto = null;
	private String sTileLabel = null;
	public static int iBOX_LAYOUT_AXIS = BoxLayout.Y_AXIS; //Y = von top to bottom
	
	public BoxTHM(){
		super(BoxTHM.iBOX_LAYOUT_AXIS); //Layout Top To Bottom
	}
	
	public BoxTHM(int axis,GenericDTO<IBoxDtoAttribute> objDto, String sTileLabel) throws ExceptionZZZ {
		super(axis);
		
		BoxNew_(objDto, sTileLabel);
	}
	
	public BoxTHM(GenericDTO<IBoxDtoAttribute> objDto, String sTileLabel) throws ExceptionZZZ{
		this(BoxTHM.iBOX_LAYOUT_AXIS,objDto, sTileLabel); 
	}
	
	private boolean BoxNew_(GenericDTO<IBoxDtoAttribute> objDto, String sTileLabel) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{			
			this.setDto(objDto);	
			this.setTileLabel(sTileLabel);
						
			//++++ TESTTEST GOON WIRJKT DAS HIER?
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Versuch das Erzeugen der Box zu überschreiben. Z.B. Label unerhalb.");			
			if(this.getDto() == null) break main;
			
			try{
				KernelSingletonTHM objKernel = KernelSingletonTHM.getInstance();
				FileIniZZZ objFileConfig = objKernel.getFileConfigIni();
			
				//0. Hole den gerade in der Applikation für das GUI eingestellten ZoomFaktor. Diesen als Variable für die INI-Berechnungen zur Verfügung stellen
				String sGuiZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasCurrent();
				String sGuiZoomFactorCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactor(sGuiZoomFactorAliasCurrent);							
				objFileConfig.setVariable("GuiZoomFactorUsed", sGuiZoomFactorCurrent);
			
				//0. Hole den gerade in der Applikation für die Karte eingestellten ZoomFaktor. Diesen als Variable für die INI-Berechnungen zur Verfügung stellen
				String sHexZoomFactorCurrent = ApplicationSingletonTHM.getInstance().getHexZoomFactorCurrent();							
				objFileConfig.setVariable("HexZoomFactorUsed", sHexZoomFactorCurrent);
									
				//###################################################################################################################
							
		    	this.setBorder(new EmptyBorder(0, 0, 0, 20)); //TODO: Größe gemäß Zoomfaktor
		    	this.setOpaque(true);//Schaltet den default Hintergrund aus (normalerweise grau). // Dies auf false gesetzt "opaque heisst 'undurchsichtig' ").
		    	
		        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
				//Nun Graphics-Objekt holen, zum Font holen und Größe des Fonts einstellen.
				//KernelJPanelCascadedZZZ objPanel = this.getPanelParent();				
				//Graphics g = objPanel.getGraphics(); //!!! DAS GIBT IMMER NULL. Graphics Objekt steht nur in paint() Methode zur Verfügung.
	
		    	 //Merke: Das BoxTHM Objekt hat noch keine paint()-Methode. Also wird eine Schriftgröße/Font nur im übergeordneten PanelObjekt machbar sein		    
				Font objFont = ApplicationSingletonTHM.getInstance().getGuiFontCurrent();
																
				//++++ IDEE 20180827: ALLES IN paintComponent() ???? KLAPPT ABER NOCH NICHT
				//20180807: Verwende zum Konkreten Erzeugen des Bildes das, welches dem aktuell eingestellten ZoomFaktor entspricht
			    //Merke: Das muss zuvor in ein Dto-Objekt gefüllt worden sein, durch VariantCatalogDaoFacade.fillVariantCatalogDto(....)
				//Die Anordnung der Box sorgt nun dafür, dass der Text unter dem Bild steht.
				byte[] imageInByteUsed = this.getVariantCatalogImageUsedInByte();						
				if(imageInByteUsed!=null){
					JLabel labelWithIcon = UIHelper.createLabelWithIcon("", objFont,  imageInByteUsed);
					this.add(labelWithIcon);		
				}
				JLabel labelWithoutIcon = UIHelper.createLabel(sTileLabel, objFont);	
				this.add(labelWithoutIcon);					
				//++++++++++++++++
			} catch (ExceptionZZZ e) {
				e.printStackTrace();
			}  
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	public void setDto(GenericDTO<IBoxDtoAttribute> objDto){
		this.objDto = objDto;
	}
	public GenericDTO<IBoxDtoAttribute> getDto() {
		if(this.objDto==null){
		
			//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator, die als Singleton umgebaut wurde:
			try {
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
				GenericDTO dto = objFactoryGenerator.createDtoForClass(this.getClass());										
				this.objDto = dto;				
			} catch (ExceptionZZZ e) {
				e.printStackTrace();
				System.out.println("Ein Fehler ist aufgetreten: " + e.getDetailAllLast());
				ReportLogZZZ.write(ReportLogZZZ.ERROR, e.getDetailAllLast());
			}						
		}
		return this.objDto;
	}
	
	/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung passt.
	 * Hier: Initialer HexMapZoomFactor-ALIAS.
	 * 
	 * @return
	 * @throws ExceptionZZZ
	 */
	public byte[] getVariantCatalogImageUsedInByte() throws ExceptionZZZ{
		String sZoomFactorAlias = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasCurrent();
		return this.getVariantCatalogImageUsedInByte(sZoomFactorAlias);
	}
	
	/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung passt.
	 * Hier: Übergebener HexMapZoomFactor-ALIAS.
	 * 
	 * @return
	 * @throws ExceptionZZZ
	 */
	public byte[] getVariantCatalogImageUsedInByte(String sZoomFactorAlias) throws ExceptionZZZ{
		GenericDTO<IBoxDtoAttribute>objDto = this.getDto();
		return UIHelperTHM.getVariantCatalogImageUsedInByte(objDto,"IMAGE", sZoomFactorAlias);	
	}
	
	
	protected void setVariantCatalogImageUsedInByte(byte[] imageInByte) throws ExceptionZZZ{
		String sZoomFactorAlias = ApplicationSingletonTHM.getInstance().getHexZoomFactorAliasCurrent();
		this.setVariantCatalogImageUsedInByte(imageInByte, sZoomFactorAlias);
	}
	
	protected void setVariantCatalogImageUsedInByte(byte[] imageInByte, String sZoomFactorAlias) throws ExceptionZZZ{
		main:{
			if(StringZZZ.isEmpty(sZoomFactorAlias)){
				this.setVariantCatalogImageUsedInByte(imageInByte);
				break main;
			}
			
			Class<IBoxDtoAttribute> c = IBoxDtoAttribute.class;
			for(Field f : c.getDeclaredFields() ){
				int mod = f.getModifiers();
				if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
						String s = f.getName();
						if(StringZZZ.contains(s, "IMAGE", true)){
							if(s.endsWith(sZoomFactorAlias)){
								
								//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
								//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
								DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>		
								this.getDto().set(objDtoAttribute, imageInByte);	
							}
						}
				}
			}
		}//end main
	}
	
	
	/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung (der HexMap) passt.
	 * Hier: Initialer HexMapZoomFactor-ALIAS.
	 * 
	 * @return
	 * @throws ExceptionZZZ
	 */
	public byte[] getVariantCatalogImageDragUsedInByte() throws ExceptionZZZ{
		//das wäre das Bild in normaler Größe return (byte[]) this.getDto().get(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE); //es müsste kleiner gerechnet werden
		//das kleiner und transparent gerechnete Bild
		
		String sZoomFactorAlias = ApplicationSingletonTHM.getInstance().getHexZoomFactorAliasCurrent();
		byte[] objaReturn =  this.getVariantCatalogImageDragUsedInByte(sZoomFactorAlias);
		return objaReturn;
	}
	
	/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung BEIM ZIEHEN AUF DIE KARTE passt.
	 * Hier: Übergebener HexMapZoomFactor-ALIAS.
	 * 
	 * @throws ExceptionZZZ
	 * @return
	 */
	public byte[] getVariantCatalogImageDragUsedInByte(String sZoomFactorAlias) throws ExceptionZZZ{
		GenericDTO<IBoxDtoAttribute>objDto = this.getDto();
		byte[] objaReturn =   UIHelperTHM.getVariantCatalogImageDragUsedInByte(objDto,"IMAGEDRAG", sZoomFactorAlias);	
		return objaReturn;
	}
	
	//### Einfache Getter / Setter
	public void setTileLabel(String sTileLabel){
		this.sTileLabel = sTileLabel;
	}
	public String getTileLabel(){
		return this.sTileLabel;		
	}
}
