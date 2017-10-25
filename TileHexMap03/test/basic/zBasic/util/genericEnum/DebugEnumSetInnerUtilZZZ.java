package basic.zBasic.util.genericEnum;

import use.thm.persistence.model.TileDefaulttextValue;
import basic.zBasic.util.genericEnum.EnumSetInnerUtilZZZ;

/** Klasse zum Ausprobieren und Testen, der "EnumSetInnerUtilZZZ" Klasse.
 *  Hiermit soll dann auf enums zugegriffen werden, die innerhalb einer Klasse 
 *  bereitgestellt werden. 
 *  
 *  TODO GOON 20171023
 *  
 *  Merke: Im Original lautet die verwendete Klasse HisKeyUtility.
 *  
 *  
 * @author Fritz Lindhauer
 *
 */
public class DebugEnumSetInnerUtilZZZ {

	public static void main(String[] args) {
		DebugEnumSetInnerUtilZZZ objDebug = new DebugEnumSetInnerUtilZZZ();
		objDebug.debugIt();
	}
	
	public boolean debugIt(){
			try{
				Long lngObject = new Long(1); //Merke: Aufgrund der "Generik" muss hier ein Objekt verwendet werden. Eine 1 als fester Wert reicht für lngObject nicht aus. 
				ObjectTestValue.EnumSetInnerTestTypeZZZ x = EnumSetInnerUtilZZZ.getThiskeyEnum(ObjectTestValue.EnumSetInnerTestTypeZZZ.class, lngObject);   //das mit der festen 1 klappt nicht ... getHisKeyEnum(TileDefaulttext.TileDefaulttextValueEnum.class, 1);
				System.out.println("Gefundener Wert: '" + x.toString() + "'");
				
				//ABER: Nur den Enum-Namen auszugeben reicht mir natürlich nicht. Dies wird kombiniert mit der Lösung aus basic.zBasic.util.abstractEnum.
				//          Das Ziel ist es hier auch Kurztexte, Langtexte, etc. im Enum zur Verfügung zu stellen.
				Long lngObjectMapped = new Long(1); //Merke: Aufgrund der "Generik" muss hier ein Objekt verwendet werden. Eine 1 als fester Wert reicht für lngObject nicht aus. 
				ObjectTestMappedValue.EnumSetInnerMappedTestTypeZZZ xMapped = EnumSetInnerUtilZZZ.getThiskeyEnum(ObjectTestMappedValue.EnumSetInnerMappedTestTypeZZZ.class, lngObjectMapped);   //das mit der festen 1 klappt nicht ... getHisKeyEnum(TileDefaulttext.TileDefaulttextValueEnum.class, 1);
				System.out.println("Gefundener mapped NAME: '" + xMapped.name() + "'");
				System.out.println("Gefundener mapped toString: '" + xMapped.toString() + "'");
				System.out.println("Gefundener mapped getAbbreviation: '" + xMapped.getAbbreviation() + "'");
				System.out.println("Gefundener mapped getDescription: '" + xMapped.getDescription() + "'");
			}catch(Exception e){
				System.out.println("Es ist ein Fehler aufgetreten: "+ e.getMessage());
				e.printStackTrace();
			}
	
				
		return true;
		}

}
