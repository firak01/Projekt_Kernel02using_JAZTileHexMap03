package use.zBasicUI.component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import use.thm.persistence.dto.IBoxDtoAttribute;
import use.thm.persistence.dto.ITileDtoAttribute;
import base.reflection.ReflectionUtil;
import basic.persistence.dto.DTOAttribute;
import basic.persistence.dto.GenericDTO;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.IConstantZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.component.UIHelper;

/** Klasse unterstützt u.a. das Holen der DTO-Werte.
 *   Wird dann z.B. in TileTHM und VariantCatalogTHM verwendet.
 *   Dadurch wird redundanter Code vermieden.
 * @author Fritz Lindhauer
 *
 */
public final class UIHelperTHM implements IConstantZZZ{
	/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung passt.
	 * Hier: Übergebener HexMapZoomFactor-ALIAS.
	 * 
	 * @return
	 * @throws ExceptionZZZ
	 */
	public static byte[] getVariantImageUsedInByte(GenericDTO<ITileDtoAttribute>objDto, String sAttributeSuffix, String sZoomFactorAlias) throws ExceptionZZZ{
		if(objDto==null){
			ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		if(StringZZZ.isEmpty(sAttributeSuffix)){
			ExceptionZZZ ez = new ExceptionZZZ("AttributeSuffix",iERROR_PARAMETER_MISSING, UIHelper.class,  ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
					
		if(StringZZZ.isEmpty(sZoomFactorAlias)){
			return UIHelperTHM.getVariantImageUsedInByteDefault(objDto);
		}
		
		//+++ Hole aus dem Dto das Attribut mit dem erechneten Namen	
		Class<ITileDtoAttribute> c = ITileDtoAttribute.class;
		String sPrefix = "VARIANT_";
											
		//Hole aus dem Dto-Objekt das Ausgangsbild
		for(Field f : c.getDeclaredFields() ){
			int mod = f.getModifiers();
			if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
//					try{
					//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
					String s = f.getName();
					if(StringZZZ.contains(s, sPrefix + sAttributeSuffix + "_IN_BYTE_", true)){
						if(s.endsWith(sZoomFactorAlias)){
							
							//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
							//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
							DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>										
							return (byte[]) objDto.get(objDtoAttribute);	
						}
					}
				}
			}//end for
	
			//+++ Wurde nichts gefunden, vieleicht einen Defaultwert zurückgeben
			return UIHelperTHM.getVariantImageUsedInByteDefault(objDto);
		}
			
		
			public static byte[] getVariantImageUsedInByteDefault(GenericDTO<ITileDtoAttribute>objDto) throws ExceptionZZZ{
				if(objDto==null){
					ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
								
				Class<ITileDtoAttribute> c = ITileDtoAttribute.class;
				String sAttributename = "VARIANT_CATALOGIMAGE_IN_BYTE";
				//return UIHelperTHM.getAttributeFromDtoInByte(objDto, sAttributename);
				return UIHelperTHM.getAttributeFromDtoInByte(c, objDto, sAttributename);
			}
							
//			public static byte[] getAttributeFromDtoInByte(GenericDTO<ITileDtoAttribute>objDto, String sAttributename) throws ExceptionZZZ{
//				if(objDto==null){
//					ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
//				if(StringZZZ.isEmpty(sAttributename)){
//					ExceptionZZZ ez = new ExceptionZZZ("AttributeName",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
//					throw ez;
//				}
//				
//				Class<ITileDtoAttribute> c = ITileDtoAttribute.class;
//				
//				for(Field f : c.getDeclaredFields() ){
//					int mod = f.getModifiers();
//					if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
//							//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
//							String s = f.getName();
//							if(StringZZZ.contains(s, sAttributename, true)){								
//									//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
//									//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
//									DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>										
//									return (byte[]) objDto.get(objDtoAttribute);	
//								}
//					}
//				}//End for
//				
//				return null;
//			}
			
			//###############################
			/**Hole per Reflection aus der DTO-Attribut Klasse das Bild, welches zur Auflösung passt.
			 * Hier: Übergebener HexMapZoomFactor-ALIAS.
			 * 
			 * @return
			 * @throws ExceptionZZZ
			 */
			public static byte[] getVariantCatalogImageUsedInByte(GenericDTO<IBoxDtoAttribute>objDto, String sAttributeSuffix, String sZoomFactorAlias) throws ExceptionZZZ{
				if(objDto==null){
					ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
				if(StringZZZ.isEmpty(sAttributeSuffix)){
					ExceptionZZZ ez = new ExceptionZZZ("AttributeSuffix",iERROR_PARAMETER_MISSING, UIHelper.class,  ReflectCodeZZZ.getMethodCurrentName());
					throw ez;
				}
							
				if(StringZZZ.isEmpty(sZoomFactorAlias)){
					return UIHelperTHM.getVariantCatalogImageUsedInByteDefault(objDto);
				}
				
				//+++ Hole aus dem Dto das Attribut mit dem erechneten Namen	
				Class<IBoxDtoAttribute> c = IBoxDtoAttribute.class;
				String sPrefix = "VARIANT_";
													
				//Hole aus dem Dto-Objekt das Ausgangsbild
				for(Field f : c.getDeclaredFields() ){
					int mod = f.getModifiers();
					if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
//							try{
							//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
							String s = f.getName();
							if(StringZZZ.contains(s, sPrefix + sAttributeSuffix + "_IN_BYTE_", true)){
								if(s.endsWith(sZoomFactorAlias)){
									
									//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
									//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
									DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>										
									return (byte[]) objDto.get(objDtoAttribute);	
								}
							}
						}
					}//end for
			
					//+++ Wurde nichts gefunden, vieleicht einen Defaultwert zurückgeben
					return UIHelperTHM.getVariantCatalogImageUsedInByteDefault(objDto);
				}
					
				
					public static byte[] getVariantCatalogImageUsedInByteDefault(GenericDTO<IBoxDtoAttribute>objDto) throws ExceptionZZZ{
						if(objDto==null){
							ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
										
						Class<IBoxDtoAttribute> c = IBoxDtoAttribute.class;
						String sAttributename = "VARIANT_IMAGE_IN_BYTE";
						//return UIHelperTHM.getAttributeFromDtoInByte(objDto, sAttributename);
						return UIHelperTHM.getAttributeFromDtoInByte(c, objDto, sAttributename);
					}
	
//					public static byte[] getAttributeFromDtoInByte(GenericDTO<IBoxDtoAttribute>objDto, String sAttributename) throws ExceptionZZZ{
//						if(objDto==null){
//							ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
//							throw ez;
//						}
//						if(StringZZZ.isEmpty(sAttributename)){
//							ExceptionZZZ ez = new ExceptionZZZ("AttributeName",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
//							throw ez;
//						}
//						
//						Class<IBoxDtoAttribute> c = IBoxDtoAttribute.class;
//						
//						for(Field f : c.getDeclaredFields() ){
//							int mod = f.getModifiers();
//							if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
//									//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
//									String s = f.getName();
//									if(StringZZZ.contains(s, sAttributename, true)){								
//											//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
//											//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
//											DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>										
//											return (byte[]) objDto.get(objDtoAttribute);	
//										}
//							}
//						}//End for
//						
//						return null;
//					}
					
					public static byte[] getAttributeFromDtoInByte(Class interfaceClassOfDtoAttribute, GenericDTO<?>objDto, String sAttributename) throws ExceptionZZZ{
						if(objDto==null){
							ExceptionZZZ ez = new ExceptionZZZ("Dto-Object",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
						if(StringZZZ.isEmpty(sAttributename)){
							ExceptionZZZ ez = new ExceptionZZZ("AttributeName",iERROR_PARAMETER_MISSING, UIHelperTHM.class,  ReflectCodeZZZ.getMethodCurrentName());
							throw ez;
						}
						
						Class<?> c = IBoxDtoAttribute.class;
						
						for(Field f : c.getDeclaredFields() ){
							int mod = f.getModifiers();
							if(Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod)){
									//System.out.printf("%s = %d%n",  f.getName(), f.get(null));// f.get(null) wirkt wohl nur bei Konstanten, die im Interface so defineirt sind: public static final int CONST_1 = 9;
									String s = f.getName();
									if(StringZZZ.contains(s, sAttributename, true)){								
											//Erzeuge eine DTOAttribut Instanz, die dem aktuell gefundenen Namen der Konstante entspricht.
											//Merke: DTOAttribute braucht eine überschreibene equals() und hashCode() Methode, damit der gespeichert Wert mit einer erzeugten Instanz verglichen werden kann.
											DTOAttribute objDtoAttribute = DTOAttribute.getInstance(s); //<IDTOAttributeGroup, T>										
											return (byte[]) objDto.get(objDtoAttribute);	
										}
							}
						}//End for
						
						return null;
					}
}
