package use.thm.rule.facade;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.TroopArmy;
import use.thm.rule.model.TroopArmyRuleType;
import basic.rule.facade.GeneralRuleHibernateFacadeZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

public class TroopArmyRuleFacade  extends GeneralRuleHibernateFacadeZZZ{
	private TroopArmy objTroopArmy = null;
	
	private TroopArmyRuleFacade(){
		super();
	}
	public TroopArmyRuleFacade(IHibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public TroopArmyRuleFacade(TroopArmy troop){
		this();
		this.setTroop(troop);
	}
	
	public TroopArmyRuleFacade(IHibernateContextProviderZZZ objContextHibernate, TroopArmy troop){
		this(objContextHibernate);
		this.setTroop(troop);
	}
	
	public boolean onUpdateTroopArmyPosition(HexCell objPersistedHexTarget, String sCallingFlag) throws ExceptionZZZ{
		boolean bReturn = true;// true=alles o.k., false=Regel schlägt zu, wird also verletzte. Daher wird später, d.h. beim Persistieren ein Veto eingelegt.
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Hexcell Objekt von der Klasse: " + objPersistedHexTarget.getClass().getName());
			if(objPersistedHexTarget instanceof use.thm.persistence.model.AreaCellOcean){
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (1)");
				this.getFacadeRuleResult().addMessage(TroopArmyRuleType.AREATYPE);
				bReturn = false;
				
			}else if(objPersistedHexTarget instanceof use.thm.persistence.model.AreaCellLand){
				
				//TODO GOON 20170726: Stacking Limit prüfen
				//MERKE: Lass CODE stehen. Nur zur Erinnerung wie (ggfs.) andere Zellen gefunden werden könnten
				//CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
				//AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
								
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				//VERSUCH AUF ANDEREM WEG EINE SESSION ZU BEKOMMEN OHNE DIE AKTUELLE ZU SCHLIESEN
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
				//Session session = objContextHibernate.getSessionFactory().getCurrentSession();
				Session session = objContextHibernate.getSessionFactory().openSession();
				
				/*Zum Erstellen einer neuen SessionFactory.....
				 *  Configuration cfg = this.getConfiguration();
		      ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
		      SessionFactory sf = cfg.buildSessionFactory(sr);
				 */
				if(session == null) break main;			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				PersistentBag pbag = new PersistentBag((SessionImplementor) session, objPersistedHexTarget.getTileBag());
				System.out.println("Zielzelle. Anzahl Tiles=" + objPersistedHexTarget.getTileBag().size() + " / PersistentBag. Anzahl Tiles= " + pbag.size());
				
				//TODO: Hole ggfs. pro Gelädetyp das Stackinglimit
				int iStackingLimitUsed = 0;
				int iStackingLimitUsedDefault = 1;
				if(StringZZZ.isEmpty(sCallingFlag)){
					iStackingLimitUsed = iStackingLimitUsedDefault;
				}else if(sCallingFlag.equalsIgnoreCase("PREINSERT")){
					iStackingLimitUsed = iStackingLimitUsedDefault;
				}else if(sCallingFlag.equalsIgnoreCase("UPDATE")){
					iStackingLimitUsed = iStackingLimitUsedDefault +1;
				}
				if(pbag.size()>=iStackingLimitUsed){ //STACKING LIMIT von 1 beim  PRE_INSERT, aber die Armee ist schon in der HEXCelle. Das sollte nur noch nicht committed sein...
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (1.2)");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": (Maximum erlaubt ist: " + iStackingLimitUsedDefault + " im Gebiet " + objPersistedHexTarget.getMapX() + "/" + objPersistedHexTarget.getMapY());
					this.getFacadeRuleResult().addMessage(TroopArmyRuleType.STACKING_LIMIT_MAX);
					bReturn = false;
				}
				
				
			}else if (objPersistedHexTarget instanceof use.thm.persistence.model.AreaCell){
				
				AreaCell area = (AreaCell) this.getTroop().getHexCell(); //TODO GOON 20170630: DIES STELLE WIRFT EINEN FEHLER, BEIM TESTEN "EINFUEGEN" IN EIN SCHON BESETZTES FELD
				String sTypeArea = area.getAreaType();
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
				
				if(!sTypeArea.equalsIgnoreCase("LA")){						
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (2)");
					this.getFacadeRuleResult().addMessage(TroopArmyRuleType.AREATYPE);
					bReturn = false;
				}else{
										
					//TODO GOON 20170726: Stacking Limit prüfen
					Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
					if(session == null) break main;			
					session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
					
					PersistentBag pbag = new PersistentBag((SessionImplementor) session, objPersistedHexTarget.getTileBag());
					System.out.println("Zielzelle. Anzahl Tiles=" + objPersistedHexTarget.getTileBag().size() + " / PersistentBag. Anzahl Tiles= " + pbag.size());
					
					if(pbag.size()>=1){ //STACKING LIMIT von 1
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (2.2)");
						this.getFacadeRuleResult().addMessage(TroopArmyRuleType.STACKING_LIMIT_MAX);
						bReturn = false;
					}
					
				}		
			}else{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": UNERWARTETER FALL. Hexcell Objekt von der Klasse: " + objPersistedHexTarget.getClass().getName());
				this.getFacadeRuleResult().addMessage(TroopArmyRuleType.AREATYPE);
				bReturn = false;
			}//end if hex instanceoff			
		}
		return bReturn;
	}
	
	public void setTroop(TroopArmy troop){
		this.objTroopArmy = troop;
	}
	public TroopArmy getTroop(){
		return this.objTroopArmy;
	}
	
}
