package use.thm.client.component;

import java.awt.Color;

import tryout.hibernate.AreaType;
import basic.zKernel.KernelZZZ;

public class AreaTypeOceanTHM extends AreaTypeTHM{

	public AreaTypeOceanTHM(KernelZZZ objKernel) {
		super(objKernel, AreaType.OCEAN);
	}
	public static String getTerm(){
		return "Ozean";
	} 
	public static String getDescription(){
		return "Kann nur von Flotten betreten werden.";
	}
	public static boolean isAccessibleBy(TileTHM objTroop){
		boolean bReturn=false;
		main:{
		if(objTroop==null) break main;
			
		//Je nach Truppentype kann das Feld betreten werden oder nicht
		if(objTroop instanceof ArmyTileTHM){
			bReturn = false;
		}else if(objTroop instanceof FleetTileTHM){
			bReturn = true;
		}
		
		} // end main 
		return bReturn;
	}
	public static Color getColor() {
		Color colorReturn = Color.BLUE;
		return colorReturn;
	}
	
}
