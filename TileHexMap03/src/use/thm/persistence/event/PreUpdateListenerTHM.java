package use.thm.persistence.event;

import java.util.Calendar;
import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import custom.zKernel.LogZZZ;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import use.thm.rule.facade.AreaCellRuleFacade;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.facade.TroopFleetRuleFacade;
import use.thm.rule.model.AreaCellRuleType;
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
public class PreUpdateListenerTHM  implements PreUpdateEventListener,IKernelUserZZZ, IVetoFlagZZZ {
//DAS geht nicht public class PreUpdateListenerTHM extends DefaultPreUpdateEventListener implements PreUpdateEventListener,IKernelUserZZZ, IVetoFlagZZZ {
	private static final long serialVersionUID = 1L;
	private KernelZZZ objKernel;
	private LogZZZ objLog; 
	
	private VetoFlag4ListenerZZZ objLastResult=new VetoFlag4ListenerZZZ();

	public boolean onPreUpdate(PreUpdateEvent event) {
		//20170802: Ich glaube, das wird gar nicht aufgerufen.
		//                Falls doch, dann auf das RuleFacade-System umstellen.
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPreUpdate   Hibernate-Event 0X...");
		boolean bReturn = false; //Erst mal false setzen.
		boolean bHasVeto = true;//Erst mal auf true setzen. Falls einer der Fälle nicht greift, dann kommt eine leere dialogbox hoch, um hier ein Problem anzuzeigen.
		try {
		this.resetVeto();

		//Merke: 20170415: Hier hatte ich zuerst versuch über ein DAO Objekt die notwendigen Informationen zu bekommen. daoArea.findByKey(cellid);
		//                           Aber, zumindest mit SQLite bekommt man dann Probleme, wenn man
		//                           A) Eine zweite Session erstellt (Database locked)
		//                           B) In ein und derselben Session versucht eine zweite Transaktion zu starten, bevor die andere Transaktion beendet ist (Nested Transaction not allowed).			
		//                               In der DAO wird aber eine neue Transaction gemacht....
		
		//Versuch nun mehr über den Event herauszubekommen....
		Object obj = event.getEntity(); 
		String sTypeArea = null;
		if(obj instanceof TroopArmy){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Armee soll eingefügt werden.");
			TroopArmy troop = (TroopArmy) obj;
			bHasVeto = preUpdate_ArmyVeto(troop);  
			
		}else if(obj instanceof TroopFleet){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Flotte soll eingefügt werden.");
			TroopFleet troop = (TroopFleet) obj;
			bHasVeto = preUpdate_FleetVeto(troop);  
					
		}else  if(obj instanceof AreaCell){
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": eingefügt wird ein Objekt der Klasse: " + obj.getClass().getName());
			AreaCell area = (AreaCell) obj;
			bHasVeto = preUpdate_AreaVeto(area);  
//			
//			bReturn = false; //Der Returnwert true bedeutet "VETO"
//			
//			//ACTUNG: DAS NICHT MACHEN... 
//			//Erstens wird dadurch die LAZY Erstellung unterlaufen... es dauert also länger.
//			//Zweitens existieren beim Aufbau der KArete (also beim INSERT der Hexfelder) die Tiles noch nicht. Hier ist also nix zu prüfen.
//			//Daher diese Überprüfung beim Speichern machen... Welcher Event????
//			AreaCell area = (AreaCell) obj;
//			String sTypeArea = area.getAreaType();
//			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
//			
//			Collection<Tile> colTile = area.getTileBag();
//			for(Tile objTile : colTile){
//				String sTileType = objTile.getTileType();
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Enthält Spielstein vom Typ="+sTileType);
//				
//				if(sTypeArea.equalsIgnoreCase("OC") & sTileType.equalsIgnoreCase("AR")){
//					bReturn = true; // also VETO
//				}else if(sTypeArea.equalsIgnoreCase( "LA") & sTileType.equalsIgnoreCase("FL")){
//					bReturn = true;
//				}else{
//					bReturn = false;
//				}
//				
				
		}else{
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": preUpdate für ein Objekt der Klasse: " + obj.getClass().getName());
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": DIESE KLASSE WIRD NOCH NICHT BEHANDELT");
		}
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen eines Spielsteins / einer Area.");
			this.veto(true);
		}		
		bReturn = !bHasVeto;
		return bReturn;
	}
	
	private boolean preUpdate_AreaVeto(AreaCell area) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			String sReturnMessage = new String("");
			
			//Aber, das das mit den DAO-Klassen hier nicht klappt (wg. Sessionproblemen: 1. Lock Database oder 2. Nested Transaction not allowed),
			//versuchen doch direkt die AreaCell zu bekommen, um den AreaTyp zu bekommen.
			//AreaCell area = (AreaCell) troop.getHexCell(); //TODO GOON 20170630: DIES STELLE WIRFT EINEN FEHLER, BEIM TESTEN "EINFUEGEN" IN EIN SCHON BESETZTES FELD
			
			
			//Merke: 20170415: Hier hatte ich zuerst versuch über ein DAO Objekt die notwendigen Informationen zu bekommen. daoArea.findByKey(cellid);
			//                           Aber, zumindest mit SQLite bekommt man dann Probleme, wenn man
			//                           A) Eine zweite Session erstellt (Database locked)
			//                           B) In ein und derselben Session versucht eine zweite Transaktion zu starten, bevor die andere Transaktion beendet ist (Nested Transaction not allowed).			
			//                               In der DAO wird aber eine neue Transaction gemact....
			
			//Die Bemerkung vom 20170415 hat dann zur Folge: Wenn man an dieser Stelle eine neue Session aufmacht, dann gibt es in der aufrufenden Methode den Fehler, dass die Session closed sei.
			HibernateContextProviderSingletonTHM objHibernateContext = HibernateContextProviderSingletonTHM.getInstance();			
			//			AreaCellDao areaDao = new AreaCellDao(objHibernateContext);
			//			AreaCell area = areaDao.findByKey(hex.getId());
									
			//####################
			//### BACKEND Validierung
			//####################
			//Merke: 
			//AreaCellTHM.onTileDrop(event) und darin .isAcessibleBy(.... ) erst nach der BackendValidierung aufrufen. 
			//Grund: Dadurch, dass die Area schon vom UI nicht betreten werden kann, wird alles abgebrochen und diese Backend-Validierung passiert nicht.
				
			AreaCellRuleFacade objRuleFacade = new AreaCellRuleFacade(objHibernateContext, area);
			boolean bValid = objRuleFacade.onUpdateAreaCell(area,"UPDATE");//Die UPDATE Angaebe ist notwednig wg. anderen Stacking Limits als z.B. im PREINSERT.
			bReturn = !bValid;
			if(!bValid){
				//Hole die Meldungen aus dem Regelwerk ab.			
				sReturnMessage = objRuleFacade.getMessagesAsString();		       
			}
			 this.veto(bReturn, sReturnMessage);
		}
		return bReturn;
	}
	
		private boolean preUpdate_ArmyVeto(TroopArmy troop) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				String sReturnMessage = new String("");
				
				//Hole das Hexfeld
				HexCell hex = troop.getHexCell();
				if(hex==null) break main; //Das ist ggfs. beim Löschen der TroopArmy möglich, wenn die 1:1 Beziehungen gelöst/gelöscht werden. 
				String sType = hex.getHexType();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HexFeld vom Typ="+sType);
				
				//Aber, das das mit den DAO-Klassen hier nicht klappt (wg. Sessionproblemen: 1. Lock Database oder 2. Nested Transaction not allowed),
				//versuchen doch direkt die AreaCell zu bekommen, um den AreaTyp zu bekommen.
				//AreaCell area = (AreaCell) troop.getHexCell(); //TODO GOON 20170630: DIES STELLE WIRFT EINEN FEHLER, BEIM TESTEN "EINFUEGEN" IN EIN SCHON BESETZTES FELD
				
				
				//Merke: 20170415: Hier hatte ich zuerst versuch über ein DAO Objekt die notwendigen Informationen zu bekommen. daoArea.findByKey(cellid);
				//                           Aber, zumindest mit SQLite bekommt man dann Probleme, wenn man
				//                           A) Eine zweite Session erstellt (Database locked)
				//                           B) In ein und derselben Session versucht eine zweite Transaktion zu starten, bevor die andere Transaktion beendet ist (Nested Transaction not allowed).			
				//                               In der DAO wird aber eine neue Transaction gemact....
				
				//Die Bemerkung vom 20170415 hat dann zur Folge: Wenn man an dieser Stelle eine neue Session aufmacht, dann gibt es in der aufrufenden Methode den Fehler, dass die Session closed sei.
				HibernateContextProviderSingletonTHM objHibernateContext = HibernateContextProviderSingletonTHM.getInstance();			
				//			AreaCellDao areaDao = new AreaCellDao(objHibernateContext);
				//			AreaCell area = areaDao.findByKey(hex.getId());
				
							
				//####################
				//### BACKEND Validierung
				//####################
				//Merke: 
				//AreaCellTHM.onTileDrop(event) und darin .isAcessibleBy(.... ) erst nach der BackendValidierung aufrufen. 
				//Grund: Dadurch, dass die Area schon vom UI nicht betreten werden kann, wird alles abgebrochen und diese Backend-Validierung passiert nicht.
					
				TroopArmyRuleFacade objRuleFacade = new TroopArmyRuleFacade(objHibernateContext, troop);
				boolean bValid = objRuleFacade.onUpdateTroopArmyPosition(hex,"UPDATE");//Die UPDATE Angaebe ist notwednig wg. anderen Stacking Limits als z.B. im PREINSERT.
				bReturn = !bValid;
				if(!bValid){
					//Hole die Meldungen aus dem Regelwerk ab.			
					sReturnMessage = objRuleFacade.getMessagesAsString();		        
				}
				this.veto(bReturn, sReturnMessage);
			}
			return bReturn;
		}
		private boolean preUpdate_FleetVeto(TroopFleet troop) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				
				String sReturnMessage = new String("");
				
					
				//Hole das Hexfeld
				HexCell hex = troop.getHexCell();
				if(hex==null) break main; //Das ist ggfs. beim Löschen der TroopArmy möglich, wenn die 1:1 Beziehungen gelöst/gelöscht werden. 
				String sType = hex.getHexType();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HexFeld vom Typ="+sType);
				
				//Aber, das das mit den DAO-Klassen hier nicht klappt (wg. Sessionproblemen: 1. Lock Database oder 2. Nested Transaction not allowed),
				//versuchen doch direkt die AreaCell zu bekommen, um den AreaTyp zu bekommen.
				//AreaCell area = (AreaCell) troop.getHexCell(); //TODO GOON 20170630: DIES STELLE WIRFT EINEN FEHLER, BEIM TESTEN "EINFUEGEN" IN EIN SCHON BESETZTES FELD
				
				
				//Merke: 20170415: Hier hatte ich zuerst versuch über ein DAO Objekt die notwendigen Informationen zu bekommen. daoArea.findByKey(cellid);
				//                           Aber, zumindest mit SQLite bekommt man dann Probleme, wenn man
				//                           A) Eine zweite Session erstellt (Database locked)
				//                           B) In ein und derselben Session versucht eine zweite Transaktion zu starten, bevor die andere Transaktion beendet ist (Nested Transaction not allowed).			
				//                               In der DAO wird aber eine neue Transaction gemact....
				
				//Die Bemerkung vom 20170415 hat dann zur Folge: Wenn man an dieser Stelle eine neue Session aufmacht, dann gibt es in der aufrufenden Methode den Fehler, dass die Session closed sei.
				HibernateContextProviderSingletonTHM objHibernateContext = HibernateContextProviderSingletonTHM.getInstance();			
				//			AreaCellDao areaDao = new AreaCellDao(objHibernateContext);
				//			AreaCell area = areaDao.findByKey(hex.getId());
				
							
				//####################
				//### BACKEND Validierung
				//####################
				//Merke: 
				//AreaCellTHM.onTileDrop(event) und darin .isAcessibleBy(.... ) erst nach der BackendValidierung aufrufen. 
				//Grund: Dadurch, dass die Area schon vom UI nicht betreten werden kann, wird alles abgebrochen und diese Backend-Validierung passiert nicht.
					
				TroopFleetRuleFacade objRuleFacade = new TroopFleetRuleFacade(objHibernateContext, troop);
				boolean bValid = objRuleFacade.onUpdateTroopFleetPosition(hex,"UPDATE");//Die UPDATE Angaebe ist notwednig wg. anderen Stacking Limits als z.B. im PREINSERT.
				bReturn = !bValid;
				if(!bValid){
					//Hole die Meldungen aus dem Regelwerk ab.			
					sReturnMessage = objRuleFacade.getMessagesAsString();		       
				}
				this.veto(bReturn, sReturnMessage);
			}
			
			return bReturn;
		}

	//#######################################
		//Methods implemented by additional Interface
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

		public boolean isVeto() {
			return this.objLastResult.isVeto();
		}
		
		public void veto(boolean bResult) {
			this.objLastResult.veto(bResult);			
		}
		
		public void veto(boolean bResult, String sResultMessage) {
			this.objLastResult.veto(bResult, sResultMessage); 
		}
				
		public void resetVeto() {
			this.objLastResult.resetVeto();
		}

		public Calendar getVetoDate() {
			return this.objLastResult.getVetoDate();
		}

		public VetoFlag4ListenerZZZ getCommitResult(){
			return this.objLastResult;
		}
		
}
