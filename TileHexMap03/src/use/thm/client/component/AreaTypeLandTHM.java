package use.thm.client.component;

import java.awt.Color;

import use.thm.persistence.model.AreaType;
import basic.zKernel.KernelZZZ;

public class AreaTypeLandTHM extends AreaTypeTHM {

	public AreaTypeLandTHM(KernelZZZ objKernel) {
		super(objKernel, AreaType.LAND);
	}
	public static String getTerm(){
		return "Land";
	}
	public static String getDescription(){
		return "Kann nur von Armeen betreten werden.";
	}
	public static boolean isAccessibleBy(TileTHM objTroop){
		boolean bReturn=false;
		main:{
		if(objTroop==null) break main;
			
		//Je nach Truppentype kann das Feld betreten werden oder nicht
		if(objTroop instanceof ArmyTileTHM){
			bReturn = true;
		}else if(objTroop instanceof FleetTileTHM){
			bReturn = false;
		}
		
		} // end main
		return bReturn;
	}
	public static  Color getColor() {
		Color colorReturn = Color.orange;
		return colorReturn;
	}
}
