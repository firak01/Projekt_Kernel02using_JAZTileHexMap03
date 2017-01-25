package use.thm.client.common;

import java.awt.Color;

import basic.zBasic.ReflectCodeZZZ;

/** Klasse bietet nur static Methoden für Effekte in den Area-Feldern an
 *   z.B: Berechnet sie aus der übergebenen Grundfarbe einer Area die veränderte Farbe 
 *          beim Bewegen eines Spielsteins über das Feld (z.B. Anzeige des Pfades, den der Spielstein genommen hat. 
 * @author lindhaueradmin
 *
 */
public class AreaEffectTHM {
	//Helligkeitsgrade als Konstanten, mit denen die Effekte dargestellt werden, 1 = dunkelste Abweichung
	public final static int AREA_LAST_VALID_ENTERED = 1;
	public final static int AREA_ON_PATH = 2;
	public final static int AREA_NORMAL = 3;  //D.h. das ist der Helligkeitsgrad der normalen Area Farbe.
	public final static int AREA_REACHABLE_NEXT = 4;
	public final static int AREA_REACHABLE = 5;
	
	
	private AreaEffectTHM(){
		//Zum Verstecken des Konstruktors "private"
	}
	
	/** Ausgehend von einer Grundfarbe wird eine Farbnuance (heller / dunkler) bereitgestellt, d.h. näher an weiss / schwarz.
	 *   Merke: Color.brighter() kann kann dies nicht leisten.
	 *               Hier muss mit HSB-Werten gearbeite werden.
	 *               
	 * Merke:
	 * int rgb = Color.HSBtoRGB(hue, saturation, brightness);
		red = (rgb>>16)&0xFF;
	    green = (rgb>>8)&0xFF;
	    blue = rgb&0xFF;
	    
	    
	    
	    Merke:
	    Operator >> ist Rechtverschiebung mit Vorzeichen
	    
	    z.B.: Linksverschiebung
	    00000001 << 4
		00010000

		Merke: Integer Werte aus dem HSB - Array Color.RGBtoHSB(r,g,b, HSB[]) bekommt man mit
		HSB[0] *= 360;
		HSB[1] *= 100;
		HSB[2] *= 100;
    * Hue is the actual color in the spectrum. Assume it to be a degree value from 0 to 360
    * Saturation is the amount of that color. Assume values from 0 to 100. Low saturation results in pastel colors, high saturation results in vibrant colors.
    * Brightness is the lightness or darkness of the color. Assume values from 0 to 100. No brightness is black, full brightness is white.



	* @param colorBase, BasisFarbe, die z.B. vom AREA-TYP festgelegt wird.
	* @param iEffect
	* @return
	* 
	* lindhaueradmin; 12.10.2008 13:00:39
	 */
	public static Color getColor4Effect(Color colorBase, int iEffect){
		Color colorReturn = null;
		main:{
			if(colorBase==null) break main;
			
			//In Abhängigkeit des gewünschten Effekts die Werte verändern
			if(iEffect == AreaEffectTHM.AREA_NORMAL){
				colorReturn = colorBase;
			}else if(AreaEffectTHM.isBrighterThanNormal(iEffect)){
				int iDiffer = getDistinguishParameterByColor(colorBase);
				
				
				//heller Werte als Normal				
				//das geht zwar, griffinger ist es jedoch mit Integer Werten zu arbeiten float fDelta = (float) 1/ ((float) (iEffect-AreaEffectTHM.AREA_NORMAL)*(float)4);
				int iDelta =  (iEffect-AreaEffectTHM.AREA_NORMAL)* iDiffer; //Merke: 30 bewirkt einen deutlicheren Uterschied bei der Farbe blau, als z.B. 20
				System.out.println("Effect: " + iEffect + "-- iDelta: " + iDelta);
				
//				Ziel: bei ganz hellen Farben, das umkehren, also gelb soll dunkler werden
				boolean btemp = AreaEffectTHM.isColorBright(colorBase);
				if(btemp){
					//Ausnahme: Helle Farben nicht noch heller machen
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Aushnahme: HELLE Farbe erkannt, mache diese nicht heller, sondern dunkler.");
					colorReturn = AreaEffectTHM.darker(colorBase, iDelta);
				}else{
					colorReturn = AreaEffectTHM.brighter(colorBase, iDelta);
				}
			}else if(!AreaEffectTHM.isBrighterThanNormal(iEffect)){  // ! also Negation
				int iDiffer = getDistinguishParameterByColor(colorBase);
				
				//dunklere Werte als Normal
				//Merke, das Ergebnis ist immer int, da mit int gearbetet wird:    double dDelta = 1/2; //(AreaEffectTHM.AREA_NORMAL- iEffect);
				//das geht zwar, griffinger ist es jedoch mit Integer Werten zu arbeiten float fDelta = (float) 1 /((float) (AreaEffectTHM.AREA_NORMAL- iEffect)*(float)4);
				int iDelta = (AreaEffectTHM.AREA_NORMAL- iEffect)* iDiffer;
				System.out.println("Effect: " + iEffect + "-- iDelta: " + iDelta);
				
//				Ziel: bei ganz Dunklen Farben, das umkehren, also Blau soll aufgehellt werden
				boolean btemp = AreaEffectTHM.isColorDark(colorBase);
				if(btemp){
					//Ausnahme: Dunkle Farben nicht noch dunkler machen
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Aushnahme: DUNKLE Farbe erkannt, mache diese nicht dunkler, sondern heller.");
					colorReturn = AreaEffectTHM.brighter(colorBase, iDelta);
				}else{					
					colorReturn = AreaEffectTHM.darker(colorBase, iDelta);
				}
			}else{
				  colorReturn = colorBase;
			} 
		}
		return colorReturn;
	}
	
	/** Wird ein Effekt eine hellere Farbe erzeugen als die normale AreaFarbe
	* @param iEffect
	* @return
	* 
	* lindhaueradmin; 12.10.2008 13:43:37
	 */
	public static boolean isBrighterThanNormal(int iEffect){
		boolean bReturn;
		main:{
			bReturn = AreaEffectTHM.isBrighter(iEffect, AreaEffectTHM.AREA_NORMAL);
		}
		return bReturn;
	}
	
	/** Wird ein Effekt eine hellere Farbe erzeugen als der andere Effekt
	* @param iEffect
	* @param iEffectCompare
	* @return
	* 
	* lindhaueradmin; 12.10.2008 13:41:55
	 */
	public static boolean isBrighter(int iEffect, int iEffectCompare){
			if(iEffect > iEffectCompare){
				return true;
			}else{
				return false;
			}
	}
	
	/** Merke: Im Gegensatz zu den Methoden, die mit Int  arbeiten werden hier keine weiteren Feinheiten (z.B. distinguish Parameter) berücksichtigt.
	 *               Sollte man diese Methode noch mal verwenden, kann das ja (an float angepasst) alles auch eingebaut werden.
	 * @deprecated
	* @param colorBase
	* @param fDelta
	* @return
	* 
	* lindhaueradmin; 12.10.2008 18:36:40
	 */
	public static Color brighter(Color colorBase, float fDelta){
		Color colorReturn = null;
		main:{
			if(colorBase==null) break main;
			
//			H=hue, S=Saturation, B=brightness
			float[] hsbvals = Color.RGBtoHSB(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), null);
			float hue = hsbvals[0];
			float saturation = hsbvals[1];
			float brightness = hsbvals[2];
			System.out.println("Ziel: heller - Brightness vor Delta: " + brightness);
			System.out.println("Ziel: heller - Brightness vor Delta (als int): " + (brightness*= 100));
			brightness = hsbvals[2] + fDelta;
			int iRGB = Color.HSBtoRGB(hue, saturation, brightness);
			System.out.println("Ziel: heller - Brightness nach Delta: " + brightness);
			System.out.println("Ziel: heller - Brightness nach Delta (als int): " + (brightness*= 100));
			/*
			HSB[0] *= 360;
			HSB[1] *= 100;
			HSB[2] *= 100;
			*/
			
			
//			wieder als eine Color zurückgeben
			
				/* Merke:
		red = (rgb>>16)&0xFF;
	    green = (rgb>>8)&0xFF;
	    blue = rgb&0xFF;
				 */
			  colorReturn = new Color(iRGB);
		}
		  return colorReturn;
	}
	
	/** Merke: Im Gegensatz zu den Methoden, die mit Int  arbeiten werden hier keine weiteren Feinheiten (z.B. distinguish Parameter) berücksichtigt.
	 *               Sollte man diese MEthode noch mal verwenden, kann das ja (an float angepasst) alles auch eingebaut werden.
	 *@deprecated
	* @param colorBase
	* @param fDelta
	* @return
	* 
	* lindhaueradmin; 12.10.2008 18:34:32
	 */
	public static Color darker(Color colorBase, float fDelta){
		Color colorReturn = null;
		main:{
			if(colorBase==null) break main;
			
//			H=hue, S=Saturation, B=brightness
			float[] hsbvals = Color.RGBtoHSB(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), null);
			float hue = hsbvals[0];
			float saturation = hsbvals[1];
			float brightness = hsbvals[2];
			System.out.println("Ziel: dunkler - Brightness vor Delta: " + brightness);
			System.out.println("Ziel: dunkler - Brightness vor Delta (als int): " + (brightness*= 100));
			brightness = hsbvals[2] - fDelta;
//			wieder als eine Color zurückgeben
			int iRGB = Color.HSBtoRGB(hue, saturation, brightness);
			System.out.println("Ziel: dunkler - Brightness nach Delta: " + brightness);
			System.out.println("Ziel: dunkler - Brightness nach Delta (als int): " + (brightness*= 100));
			

				/* Merke:
		red = (rgb>>16)&0xFF;
	    green = (rgb>>8)&0xFF;
	    blue = rgb&0xFF;
				 */
			  colorReturn = new Color(iRGB);
		}
		  return colorReturn;
	}
	
	public static Color brighter(Color colorBase, int iDelta){
		Color colorReturn = null;
		main:{
			if(colorBase==null) break main;
			
//			H=hue, S=Saturation, B=brightness
			float[] hsbvals = Color.RGBtoHSB(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), null);
			float hue = hsbvals[0];
			float saturation = hsbvals[1];
			float brightness = hsbvals[2];
			System.out.println("Ziel: heller - HSB vor Delta: " + hue + "/" + saturation + "/" + brightness);
			System.out.println("Ziel: heller - HSB vor Delta (als int): " + (int)(hue*=360) + "/" + (int)(saturation*=100)+ "/" + (int)(brightness*= 100));
			
			float fDelta =(float) iDelta/ (float)100;  //Den Integer BrightnessWert in das entsprechende float umwandeln
			hue = hsbvals[0];
			saturation = hsbvals[1];
			brightness = hsbvals[2] + fDelta;
//			wieder als eine Color zurückgeben
			int iRGB = Color.HSBtoRGB(hue, saturation, brightness);
			System.out.println("Ziel: heller - HSB nach Delta: " + hue + "/" + saturation + "/" + brightness);
			System.out.println("Ziel: heller - HSB nach Delta (als int): " + (int)(hue*=360) + "/" + (int)(saturation*=100)+ "/" + (int)(brightness*= 100));
			/*
			HSB[0] *= 360;
			HSB[1] *= 100;
			HSB[2] *= 100;
			*/
			
			

				/* Merke:
		red = (rgb>>16)&0xFF;
	    green = (rgb>>8)&0xFF;
	    blue = rgb&0xFF;
				 */
			  colorReturn = new Color(iRGB);
		}
		  return colorReturn;
	}
	
	public static Color darker(Color colorBase, int iDelta){
		Color colorReturn = null;
		main:{
			if(colorBase==null) break main;
			
//			H=hue, S=Saturation, B=brightness
			float[] hsbvals = Color.RGBtoHSB(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), null);
			float hue = hsbvals[0];
			float saturation = hsbvals[1];
			float brightness = hsbvals[2];
			System.out.println("Ziel: dunkler - HSB vor Delta: " + hue + "/" + saturation + "/" + brightness);
			System.out.println("Ziel: dunkler - HSB vor Delta (als int): " + (int)(hue*=360) + "/" + (int)(saturation*=100)+ "/" + (int)(brightness*= 100));
			float fDelta =(float) iDelta/ (float)100;  //Den Integer BrightnessWert in das entsprechende float umwandeln
			hue = hsbvals[0];
			 saturation = hsbvals[1];
			brightness = hsbvals[2] - fDelta;
//			wieder als eine Color zurückgeben
			int iRGB = Color.HSBtoRGB(hue, saturation, brightness);
			System.out.println("Ziel: dunkler - HSB nach Delta: " + hue + "/" + saturation + "/" + brightness);
			System.out.println("Ziel: dunkler - HSB nach Delta (als int): " + (int)(hue*=360) + "/" + (int)(saturation*=100)+ "/" + (int)(brightness*= 100));

				/* Merke:
		red = (rgb>>16)&0xFF;
	    green = (rgb>>8)&0xFF;
	    blue = rgb&0xFF;
				 */
			  colorReturn = new Color(iRGB);
		}
		  return colorReturn;
	}
	
	/** Als "dunkle" Farbe wird definiert, jede Farbe, die dunkler als 30 (HSB - Wert mit B in Integer umgerechent) ist.
	* @param colorBase
	* @return
	* 
	* lindhaueradmin; 12.10.2008 17:40:20
	 */
	public static boolean isColorDark(Color colorBase){
		boolean bReturn = false;
		main:{
//			H=hue, S=Saturation, B=brightness
			float[] hsbvals = Color.RGBtoHSB(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), null);
			//float hue = hsbvals[0];
			//float saturation = hsbvals[1];
			float brightness = hsbvals[2];
			//System.out.println("Ziel: dunkler - Brightness vor Delta: " + brightness);
			
			 brightness*= 100;
			//System.out.println("Ziel: dunkler - Brightness vor Delta (als int): " +brightness);
			 
			 if(brightness <= 30){
				 bReturn = true;
			 }else{
				 bReturn = false;
			 }
		}
		return bReturn;
	}
	
	/** Als "helle" Farbe wird definiert, jede Farbe, die Heller als 80 (HSB - Wert mit B in Integer umgerechent) ist.
	* @param colorBase
	* @return
	* 
	* lindhaueradmin; 12.10.2008 17:40:20
	 */
	public static boolean isColorBright(Color colorBase){
		boolean bReturn = false;
		main:{
//			H=hue, S=Saturation, B=brightness
			float[] hsbvals = Color.RGBtoHSB(colorBase.getRed(), colorBase.getGreen(), colorBase.getBlue(), null);
			//float hue = hsbvals[0];
			//float saturation = hsbvals[1];
			float brightness = hsbvals[2];
			//System.out.println("Ziel: dunkler - Brightness vor Delta: " + brightness);
			
			 brightness*= 100;
			//System.out.println("Ziel: dunkler - Brightness vor Delta (als int): " +brightness);
			 
			 if(brightness >= 200){
				 bReturn = true;
			 }else{
				 bReturn = false;
			 }
		}
		return bReturn;
	}
	
	/**Bei manchen Farben merkt man den Unteschied kaum, wenn man die Helligkeit um den gleichen Wert verändert. 
	 *  Durch diesen Multiplikator wird der Effekt bei dunkleren Farben (blau) z.B. verstärkt, 
	 *  bei helleren Farben braucht er nicht so stark zu sein.
	 *  (wird angewendet in .getColor4Effect(Color, iEffect)   )
	 *  
	* @param c
	* @return
	* 
	* lindhaueradmin; 12.10.2008 18:33:05
	 */
	public static int getDistinguishParameterByColor(Color c){
		int iReturn = 20;
		if(c.equals(Color.BLUE)){
			iReturn = 30;
		}else if(c.equals(Color.ORANGE)){
			iReturn = 10;
		}
		return iReturn;
	}
	
}
