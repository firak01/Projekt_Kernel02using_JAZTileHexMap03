package use.thm.persistence.event;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import basic.zBasic.ReflectCodeZZZ;

public class PreInsertListenerTHM implements PreInsertEventListener {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
//		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPreInsert   Hibernate-Event 02...");
		//Damit wird nix eingefügt... return true;
		//Damit wird etwas eingefügt return false;
		boolean bReturn = false;
		
		//Versuch nun mehr über den Event herauszubekommen....
		Object obj = event.getEntity(); 
		if(obj instanceof TroopArmy){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Armee soll eingefügt werden.");
			TroopArmy troop = (TroopArmy) obj;

			//Hole das Hexfeld
			HexCell hex = troop.getHexCell();
			String sType = hex.getHexType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HexFeld vom Typ="+sType);
			
			//Hole aus dem Hexfeld die Area-Informationen.
			//// TODO [14.04.2017 10:17:59, Fritz Lindhauer]:  AreaCell area = hex.
					
			
		}else if(obj instanceof TroopFleet){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Flotte soll eingefügt werden.");
			TroopFleet troop = (TroopFleet) obj;
			
		}else{
			
		}		
		return bReturn;
	}
}
