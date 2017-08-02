package use.thm.persistence.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;

import custom.zKernel.LogZZZ;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import use.thm.rule.facade.AreaCellRuleFacade;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.facade.TroopFleetRuleFacade;
import use.thm.rule.model.TroopArmyRuleType;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;

/** Rückgabewert ist ein "Veto", also:
 * Damit wird nix eingefügt... return true;
*  Damit wird etwas eingefügt return false;
*  
*  Das Problem ist nun, dass ein Veto zwar das Einfügen verhindert, aber dies beispielsweise nicht Abrufbar ist.
*  Vairante  über Transaction.wasCommitted() abzufragen funktioniert nicht, da immer "false" zurückgegeben wird.
 * @author Fritz Lindhauer
 *
 */
public class PreInsertListenerTHM implements PreInsertEventListener,IKernelUserZZZ, IVetoFlagZZZ {
	private static final long serialVersionUID = 1L;
	private KernelZZZ objKernel;
	private LogZZZ objLog; 
	
	private VetoFlag4ListenerZZZ objLastResult=new VetoFlag4ListenerZZZ();

	@Override
	public boolean onPreInsert(PreInsertEvent event) {
//		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " onPreInsert   Hibernate-Event 02...");		
		boolean bReturn = false;
		String sReturnMessage = new String("");
		
		try {
		this.resetVeto();
		
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
		
				
		//Versuch nun mehr über den Event herauszubekommen....
		Object obj = event.getEntity(); 
		if(obj instanceof TroopArmy){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Armee soll eingefügt werden.");
			TroopArmy troop = (TroopArmy) obj;
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": UNIQUENAME ="+troop.getUniquename());

			//Hole das Hexfeld
			HexCell hex = troop.getHexCell();
			String sType = hex.getHexType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HexFeld vom Typ="+sType);
			
			//RuleFacade: Mache darin auch die Überprüfung, ob es noch einen anderen Spielstein in dem Feld gibt, also das StckingLimit überschritten wird, ob auf ein betretbares Feld gesetzt wird, etc.
			TroopArmyRuleFacade objRuleFacade = new TroopArmyRuleFacade(objHibernateContext, troop);
			boolean bValid	 = objRuleFacade.onUpdateTroopArmyPosition(hex,"PREINSERT");//false bedeutet, dass einer Regel verletzt wurde.	//Die PREINSERT ANGABE ist notwendig, wg. anderen STACKING_LIMITS.		
			bReturn = !bValid;
			if(!bValid){
				//Hole die Meldungen aus dem Regelwerk ab.			
				sReturnMessage = objRuleFacade.getMessagesAsString();
			}			
		}else if(obj instanceof TroopFleet){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Flotte soll eingefügt werden.");
			TroopFleet troop = (TroopFleet) obj;
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": UNIQUENAME ="+troop.getUniquename());
			
			//Hole das Hexfeld
			HexCell hex = troop.getHexCell();
			String sType = hex.getHexType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": HexFeld vom Typ="+sType);
			
			TroopFleetRuleFacade objRuleFacade = new TroopFleetRuleFacade(objHibernateContext, troop);
			boolean bValid	 = objRuleFacade.onUpdateTroopFleetPosition(hex,"PREINSERT");//false bedeutet, dass einer Regel verletzt wurde.	//Die PREINSERT ANGABE ist notwendig, wg. anderen STACKING_LIMITS.		
			bReturn = !bValid;
			if(!bValid){
				//Hole die Meldungen aus dem Regelwerk ab.			
				sReturnMessage = objRuleFacade.getMessagesAsString();
			}	
		}else if(obj instanceof AreaCellLand || obj instanceof AreaCellOcean || obj instanceof AreaCell){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Area soll eingefügt werden.");
			AreaCell area = (AreaCell) obj;
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FIELDALIAS ="+area.getFieldAlias());
			
			AreaCellRuleFacade objRuleFacade = new AreaCellRuleFacade(objHibernateContext, area);
			boolean bValid	 = objRuleFacade.onUpdateAreaCell(area,"PREINSERT");//false bedeutet, dass einer Regel verletzt wurde.	//Die PREINSERT ANGABE ist notwendig, wg. anderen STACKING_LIMITS.		
			bReturn = !bValid;
			if(!bValid){
				//Hole die Meldungen aus dem Regelwerk ab.			
				sReturnMessage = objRuleFacade.getMessagesAsString();
			}	
			
		}else{
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": eingefügt wird ein Objekt der Klasse: " + obj.getClass().getName());
			bReturn = false; //Der Returnwert true bedeutet "VETO"
			
			//ACHTUNG: DAS NICHT MACHEN... 
			//Erstens wird dadurch die LAZY Erstellung unterlaufen... es dauert also länger.
			//Zweitens existieren beim Aufbau der Karte (also beim INSERT der Hexfelder) die Tiles noch nicht. Hier ist also nix zu prüfen.
			//Daher diese Überprüfung beim Speichern machen... Welcher Event????
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
//				
//			}
		}				
		this.veto(bReturn, sReturnMessage);
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen eines Spielsteins / einer Area.");
			this.veto(true);
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
		
		
		public boolean isVeto(){
			return this.objLastResult.isVeto();				
		}
		public void veto(boolean bResult){
			this.objLastResult.veto(bResult);			
		}
		public void veto(boolean bResult, String sResultMessage){
			this.objLastResult.veto(bResult, sResultMessage);
		}
		public void resetVeto(){
			this.objLastResult.resetVeto();
		}
		public Calendar getVetoDate(){
			return this.objLastResult.getVetoDate();
		}
		public VetoFlag4ListenerZZZ getCommitResult(){
			return this.objLastResult;
		}
}
