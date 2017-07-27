package use.thm.persistence.event;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;

import custom.zKernel.LogZZZ;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.model.TroopArmyRuleType;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.KernelSingletonTHM;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.ExtendedVectorZZZ;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;
import basic.zKernel.IKernelUserZZZ;
import basic.zKernel.KernelZZZ;

/**
 * Wird beim commit ausgeführt.
 * @author Fritz Lindhauer
 *
 */
public class SaveOrUpdateListenerTHM extends DefaultSaveOrUpdateEventListener implements IKernelUserZZZ, IVetoFlagZZZ{ //das klappt nicht  implements SaveOrUpdateEventListener,IKernelUserZZZ {
//public class SaveOrUpdateListenerTHM  implements SaveOrUpdateEventListener, IKernelUserZZZ, IVetoFlagZZZ{ //das klappt nicht  implements SaveOrUpdateEventListener,IKernelUserZZZ {
	  
	private static final long serialVersionUID = 1L;
	private KernelZZZ objKernel;
	private LogZZZ objLog; 
	
	private VetoFlag4ListenerZZZ objLastResult=new VetoFlag4ListenerZZZ();

	//@Override
	 public void onSaveOrUpdate_NurTest(SaveOrUpdateEvent event)   throws HibernateException {
		 System.out.println(ReflectCodeZZZ.getPositionCurrent() + " Hibernate-Event 02...");	
		 
		 Object obj = event.getObject();
		 System.out.println("TEST Object:" + obj.getClass().getName());
		 
		 Object objEntity =  event.getEntity();
		 if(objEntity!=null){
			 System.out.println("TEST Entity:" + objEntity.getClass().getName());
		}else{
			System.out.println("TEXT Entity: NULL");
		}
		 
		 //ABER: WARUM BEKOMME ICH HIER NIE EIN UPDATE DER AREACELL ANGEZEIGT?
		 this.veto(false);
	 }

	@Override
	public void onSaveOrUpdate(SaveOrUpdateEvent event) throws HibernateException {
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + " Hibernate-Event 03...");		
		boolean bHasVeto = true;//Erst mal auf true setzen. Falls einer der Fälle nicht greift, dann kommt eine leere dialogbox hoch, um hier ein Problem anzuzeigen.
		try {
		this.resetVeto();

			
		//Versuch nun mehr über den Event herauszubekommen....
//		Object obj = event.getEntity();   //NULL, zumindest beim SAVE - Fall
//		EntityEntry entry = event.getEntry(); //NULL zumindest beim SAVE - Fall
		//Also hier nur
		Object obj = event.getObject();
		String sTypeArea = null;
		if(obj instanceof TroopArmy){			
			TroopArmy troop = (TroopArmy) obj;
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Armee soll committed werden. Uniquename = '" + troop.getUniquename() + "'");
            bHasVeto = saveOrUpdate_TroopVeto(troop);        					
		}else if(obj instanceof TroopFleet){			
			TroopFleet troop = (TroopFleet) obj;
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Eine Flotte soll committed werden. Uniquename = '" + troop.getUniquename() + "'");
			bHasVeto = saveOrUpdate_FleetVeto(troop);
		}else if(obj instanceof AreaCell){
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": Committed wird ein Objekt der Klasse: " + obj.getClass().getName());
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": Aufruf beim expliziten SaveOrUpdate einer AreaCell. z.B. um die Anzahl darin gespeicherter Spielsteine (i TileBag) zu reduzieren. Ansosnten wurde DAS NOCH NIE AUSGEFÜHRT... WARUM JETZT ? ... ");
			
			AreaCell area = (AreaCell) obj;
			sTypeArea = area.getAreaType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
			
			Collection<Tile> colTile = area.getTileBag();
			if(colTile.size() == 0){
				bHasVeto = false;
			}else{
				for(Tile objTile : colTile){
					String sTileType = objTile.getTileType();
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Enthaelt Spielstein vom Typ="+sTileType);
					
					if(sTypeArea.equalsIgnoreCase("OC") & sTileType.equalsIgnoreCase("AR")){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen einer Area wg. Armee Spielsteins");
						bHasVeto = true;
					}else if(sTypeArea.equalsIgnoreCase( "LA") & sTileType.equalsIgnoreCase("FL")){				
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen einer Area wg. Flotten Spielsteins");
						bHasVeto = true;
					}else{
						bHasVeto = false;
					}						
				}//end for
			}
		}else{
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": Committed wird ein Objekt der Klasse: " + obj.getClass().getName());
			System.out.println(ReflectCodeZZZ.getPositionCurrent()+": DIESE KLASSE WIRD NOCH NICHT BEHANDELT");					
		}
		this.veto(bHasVeto);	
						
		} catch (ExceptionZZZ e) {
			e.printStackTrace();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen eines Spielsteins / einer Area.");
			this.veto(true);
		}		
	}
	
	private boolean saveOrUpdate_TroopVeto(TroopArmy troop) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
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
			
			
			
			//TODO GOON AreaCellTHM.onTileDrop(event) und darin .isAcessibleBy(.... ) wird vorher aufgerufen. 
			//                    Dadurch, dass die Area schon vom UI nicht betreten werden kann, wird alles abgebrochen und diese Validierung passiert nicht.
			//                    Statt dessen PreInsertListener.onPreInsert() zum Teseten des TroopArmyRuleFacade verwenden. Dieser greift beim Ziehen des Spielsteins von Aussen in die Karte. 
			//TODO GOON 20170724: Teste auch hier die Verwendung hier die RuleFacade.....
			TroopArmyRuleFacade objRuleFacade = new TroopArmyRuleFacade(objHibernateContext, troop);
			boolean bValid = objRuleFacade.onUpdateTroopArmyPosition(hex,"UPDATE");//Die UPDATE Angaebe ist notwednig wg. anderen Stacking Limits als z.B. im PREINSERT.
			bReturn = !bValid;
			if(!bValid){
				String sResultMessage = new String("");
			
				VectorExtendedZZZ<Enum<?>> vecMessage = objRuleFacade.getFacadeRuleResult().getMessageVector();				
				for(Object obj :  vecMessage){
					@SuppressWarnings("unchecked")
					Enum<TroopArmyRuleType> rule = (Enum<TroopArmyRuleType>) obj;					
					String sMessage = rule.toString();
					if(sResultMessage.length()==0){
						sResultMessage = sMessage;
					}else{
						sResultMessage += "\n" + sMessage; 
					}
				}//end for
		        this.veto(bReturn, sResultMessage);
			}
		}
		return bReturn;
	}
	
	private boolean saveOrUpdate_FleetVeto(TroopFleet troop){
		boolean bReturn = false;
		main:{
			//Hole das Hexfeld
			HexCell hex = troop.getHexCell();
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
			//			HibernateContextProviderSingletonTHM objHibernateContext = HibernateContextProviderSingletonTHM.getInstance();			
			//			AreaCellDao areaDao = new AreaCellDao(objHibernateContext);
			//			AreaCell area = areaDao.findByKey(hex.getId());
				
			System.out.println("Hexcell Objekt von der Klasse: " + hex.getClass().getName());
			if(hex instanceof use.thm.persistence.model.AreaCellOcean){
				
				AreaCell area = (AreaCell) troop.getHexCell(); //TODO GOON 20170630: DIES STELLE WIRFT EINEN FEHLER, BEIM TESTEN "EINFUEGEN" IN EIN SCHON BESETZTES FELD
				String sTypeArea = area.getAreaType();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
				
				if(!sTypeArea.equalsIgnoreCase("OC")) {				
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Flotten Spielsteins");
					bReturn = true;
				}else{
					bReturn = false;
				}
			}else{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": UNERWARTETER FALL. Hexcell Objekt von der Klasse: " + hex.getClass().getName());
			}//end if hex instanceoff			
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
		@Override
		public boolean isVeto() {
			return this.objLastResult.isVeto();
		}
		@Override
		public void veto(boolean bResult) {
			this.objLastResult.veto(bResult);			
		}
		@Override
		public void veto(boolean bResult, String sResultMessage) {
			this.objLastResult.veto(bResult, sResultMessage); 
		}
		
		@Override
		public void resetVeto() {
			this.objLastResult.resetVeto();
		}

		@Override
		public Calendar getVetoDate() {
			return this.objLastResult.getVetoDate();
		}

		@Override
		public VetoFlag4ListenerZZZ getCommitResult(){
			return this.objLastResult;
		}

	
}
