package use.thm.client.component;

import java.awt.Color;

import use.thm.persistence.model.AreaCellType;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;

/** Detailinformationen über eine Area (das ist quasi das, was eine HexCell zu einer Area macht)
 *  Wird dann von der eigentlichen Area überschrieben. Merke: Das sind nicht die in der SQL Datenbank gespeicherten POJOS.
 *  
 * @author lindhaueradmin
 *
 */
public abstract class AreaTypeTHM extends KernelUseObjectZZZ{
	//public static int OZEAN=1;
	//public static int LAND = 1024; //Durch enum ersetzt
	private Enum enumAreaType = null;
	public AreaTypeTHM(KernelZZZ objKernel, AreaCellType objAreaTypeEnum ){
		super(objKernel);
		this.enumAreaType = objAreaTypeEnum;
	}
	public static  String getDescription(){
		return "Eine Area ohne Geländebeschreibung";
	}
	public static boolean  isAccessibleBy(TroopTileTHM objTroop){
		return false;
	}
	public  static Color getColor(){
		return Color.BLACK;
	}
	
	//### Getter / Setter
	public String getAreaType(){
		return this.enumAreaType.name();
	}
}
