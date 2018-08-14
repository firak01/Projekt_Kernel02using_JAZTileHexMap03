package use.thm.client.component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;

import use.thm.ApplicationSingletonTHM;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.zBasicUI.component.UIHelperTHM;
import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.log.ReportLogZZZ;

public class BoxTHM extends Box{
	private GenericDTO<IBoxDtoAttribute>objDto = null;
	
	public BoxTHM(){
		super(BoxLayout.Y_AXIS);
	}
	
	public BoxTHM(int axis,GenericDTO<IBoxDtoAttribute> objDto) throws ExceptionZZZ {
		super(axis);
		
		BoxNew_(objDto);
	}
	
	public BoxTHM(GenericDTO<IBoxDtoAttribute> objDto) throws ExceptionZZZ{
		this(BoxLayout.Y_AXIS,objDto); //Box.createVerticalBox()
	}
	
	private boolean BoxNew_(GenericDTO<IBoxDtoAttribute> objDto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{			
			this.setDto(objDto);						
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
	 * @return
	 * @throws ExceptionZZZ
	 */
	public byte[] getVariantCatalogImageDragUsedInByte(String sZoomFactorAlias) throws ExceptionZZZ{
		GenericDTO<IBoxDtoAttribute>objDto = this.getDto();
		byte[] objaReturn =   UIHelperTHM.getVariantCatalogImageDragUsedInByte(objDto,"IMAGEDRAG", sZoomFactorAlias);	
		return objaReturn;
	}

	
}
