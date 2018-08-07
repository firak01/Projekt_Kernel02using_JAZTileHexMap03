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
			
		/* Nachstehenden Code nach VariantCatalogDaoFacade.fillVariantCatalogDto() verschieben
			//TODO GOON 20180725: Lies das Bild gemäß des in der Applikation eingestellten ZOOMFaktors für GUI aus.
			//byte[] imageInByte = objEntity.getImageCatalog01(); //Diese Catalog Bilder sind in der Größe reduziert.
			//FGL 20180803: DAS IST HIER ZU HOCH. ES SOLLTE IM BOX-Objkekt (boxTHM?) ein Dto-Objekt geben, dass dann gefüllt wird. U.a. mit Bildern auf verschiedenen Zoomstufen!!!!
			//String sGuiZoomFactorAliasCurrent = ApplicationSingletonTHM.getInstance().getGuiZoomFactorAliasCurrent();
			//byte[] imageInByte = this.getVariantCatalogImageUsedInByte(sGuiZoomFactorAliasCurrent);
			
			//Hole alle ZoomFaktoren als Alias und gehe diese durch 
			HashMap<String,String> hmZoomFactor = ApplicationSingletonTHM.getInstance().getHashMapGuiZoomFactorAlias();
			Set<String> setZoomFactorAliasKey = hmZoomFactor.keySet();
			for(String sGuiZoomFactorAliasCurrent : setZoomFactorAliasKey){
				
				//TODO GOON: ALSO HIER DAS DTO ERST EINMAL FÜLLEN UND NICHT AUSLESEN !!!
				//D.H. imageInByte "errechnen"!!!!
				
				byte[] imageInByte = this.getVariantCatalogImageUsedInByte(sGuiZoomFactorAliasCurrent);
			   
				
			Class<ITileDtoAttribute> c = ITileDtoAttribute.class;
			for(Field f : c.getDeclaredFields() ){
				int mod = f.getModifiers();
				if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
//					try{
						//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
						String s = f.getName();
						if(StringZZZ.contains(s, "IMAGECATALOG", true)){
							if(s.endsWith(sGuiZoomFactorAliasCurrent)){
								DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>		
								this.getDto().set(objDtoAttribute, imageInByte);								
							}
						}
				}
			}//end for Field
			}//end for Zoomfactor alias
		*/
			
		}//end main:
		return bReturn;
	}
	
	public void setDto(GenericDTO<IBoxDtoAttribute> objDto){
		this.objDto = objDto;
	}
	public GenericDTO<IBoxDtoAttribute> getDto() {
		if(this.objDto==null){
			//this.objDto =GenericDTO.getInstance(ITileDtoAttribute.class); //ITileDtoAttribute bestimmt also welche Properties in der DTO-Klasse gespeicehrt sind.
			
			//FGL 20171011: Ersetzt durch eine Factory - Klasse
//			TileDtoFactory factoryTile = new TileDtoFactory();
//			GenericDTO dto = factoryTile.createDTO();	
			
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
		//das wäre das Bild in normaler Größe return (byte[]) this.getDto().get(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE); //es müsste kliner gerechnet werden
		//das kleiner und transparent gerechnete Bild
		
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
		//das wäre das Bild in normaler Größe   this.getDto().set(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE, imageInByte); //es müsste kliner gerechnet werden					
		//this.getDto().set(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE, imageInByte);
		
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
//					try{
						//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
						String s = f.getName();
						if(StringZZZ.contains(s, "IMAGE", true)){
							if(s.endsWith(sZoomFactorAlias)){
								
								//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
								//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
								DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>		
								
//								Object obj = ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_04;//Die Gleichheit und den HashCode mit "hart verdrahteten Werten" entwickeln/überprüfen.						
//								if(obj.equals(objDtoAttribute)){
//									System.out.println("GLEICH");
//									System.out.println("Hashcode: " + obj.hashCode());
//								}else{
//									System.out.println("UNGLEICH");
//								}
								this.getDto().set(objDtoAttribute, imageInByte);	
							}
						}
//					}catch(IllegalAccessException e){
//						e.printStackTrace();
//					}
				}
			}
		}//end main
	}

	
}
