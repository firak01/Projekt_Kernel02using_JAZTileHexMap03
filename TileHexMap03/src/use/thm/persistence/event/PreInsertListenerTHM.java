package use.thm.persistence.event;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

import custom.zKernel.LogZZZ;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;

/**
 * Damit wird nix eingefügt... return true;
*  Damit wird etwas eingefügt return false;
 * @author Fritz Lindhauer
 *
 */
public class PreInsertListenerTHM implements PreInsertEventListener,IKernelUserZZZ {
	private static final long serialVersionUID = 1L;
	private KernelZZZ objKernel;
	private LogZZZ objLog; 

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
//		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPreInsert   Hibernate-Event 02...");		
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
			
			//Aber, das das mit den DAO-Klassen hier nicht klappt (wg. Sessionproblemen: 1. Lock Database oder 2. Nested Transaction not allowed),
			//versuchen doch direkt die AreaCell zu bekommen, um den AreaTyp zu bekommen.
			AreaCell area = (AreaCell) troop.getHexCell();
			String sTypeArea = area.getAreaType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
			
			if(!sTypeArea.equalsIgnoreCase("LA")) bReturn = true; //Der Returnwert true bedeutet "VETO"
			
			//Merke: 20170415: Hier hatte ich zuerst versuch über ein DAO Objekt die notwendigen Informationen zu bekommen. daoArea.findByKey(cellid);
			//                           Aber, zumindest mit SQLite bekommt man dann Probleme, wenn man
			//                           A) Eine zweite Session erstellt (Database locked)
			//                           B) In ein und derselben Session versucht eine zweite Transaktion zu starten, bevor die andere Transaktion beendet ist (Nested Transaction not allowed).			
			//                               In der DAO wird aber eine neue Transaction gemact....
			
		}else if(obj instanceof TroopFleet){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Flotte soll eingefügt werden.");
			TroopFleet troop = (TroopFleet) obj;
			
			//Hole das Hexfeld
			HexCell hex = troop.getHexCell();
			String sType = hex.getHexType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HexFeld vom Typ="+sType);
			
			//Aber, das das mit den DAO-Klassen hier nicht klappt (wg. Sessionproblemen: 1. Lock Database oder 2. Nested Transaction not allowed),
			//versuchen doch direkt die AreaCell zu bekommen, um den AreaTyp zu bekommen.
			AreaCell area = (AreaCell) troop.getHexCell();
			String sTypeArea = area.getAreaType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
			
			if(!sTypeArea.equalsIgnoreCase("OC")) bReturn = true; //Der Returnwert true bedeutet "VETO"
			
		}else{
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": eingefügt wird ein Objekt der Klasse: " + obj.getClass().getName());
			bReturn = false; //Der Returnwert true bedeutet "VETO"
		}		
		return bReturn;
	}

	//#######################################
		//Methods implemented by Interface
		public KernelZZZ getKernelObject() {
			return this.objKernel;
		}
		public void setKernelObject(KernelZZZ objKernel) {
			this.objKernel = objKernel;
		}
			
		public LogZZZ getLogObject() {
			return this.objLog;
		}
		public void setLogObject(LogZZZ objLog) {
			this.objLog = objLog;
		}
}
