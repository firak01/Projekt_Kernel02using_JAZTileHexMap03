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
import use.thm.rule.model.TroopFleetRuleType;
import basic.rule.facade.GeneralRuleHibernateFacadeZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

public class TroopArmyRuleFacade  extends GeneralRuleFacadeTHM{
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
		//!!! Weil dies während eines .save() Aufrufs durchgeführt wird, dürfen hier keine weiteren Transactions aufgemacht werden.
		//    Ansonsten gibt es Fehlermelund bzgl. NESTED TRANSACTIONS
		boolean bReturn = true;// true=alles o.k., false=Regelverletzung schlägt zu. Daher wird später, d.h. beim Persistieren ein Veto eingelegt.
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Hexcell Objekt von der Klasse: " + objPersistedHexTarget.getClass().getName() + " | CallingFlag: '" + sCallingFlag + "'");
			if(objPersistedHexTarget instanceof use.thm.persistence.model.AreaCellOcean){
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (1)");
				this.getFacadeRuleResult().addMessage(TroopArmyRuleType.AREATYPE);
				bReturn = false;
				
			}else if(objPersistedHexTarget instanceof use.thm.persistence.model.AreaCellLand){
				//################################
				//### Stacking Limit prüfen
				//#################################
				//MERKE: Lass CODE stehen. Nur zur Erinnerung wie (ggfs.) andere Zellen gefunden werden könnten
				//CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
				//AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
								
				//Die bisherigen Ansätze eine Session zu holen scheiterten. Wie auch schon zuvor in den DAO-Klassen.
				//1. Ansatz:
				//Session session = this.getSession(); //Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				//ABER: Später: session isClosed() Fehler
				
				//ALSO: GANZ neue Session aufmachen
				//2. Ansatz
				/*Zum Erstellen einer neuen SessionFactory.....
				 *  Configuration cfg = this.getConfiguration();
		             ServiceRegistry sr = new ServiceRegistryBuilder().applySettings(cfg.getProperties()).buildServiceRegistry();		    
		             SessionFactory sf = cfg.buildSessionFactory(sr);
				 */
				//Session session = objContextHibernate.getSessionFactory().openSession(); 

				//ABER: Fehlermeldung  Exception in thread "AWT-EventQueue-0" org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
				//womit die Collection der TileBag in den Area-Objekten gemeint sind.
				
				//3. Ansatz
				//also versuch am Ende diese Session wieder zu schliessen				
//				session.getTransaction().commit();
//				session.clear();
//				session.close();				
				
				//ABER: später trotzdem: Database is locked()-Fehler
				
				//4. Ansatz
				//VERSUCH AUF ANDEREM WEG EINE SESSION ZU BEKOMMEN OHNE DIE AKTUELLE ZU SCHLIESEN:						
				//VERSUCH DIE AKTUELLE SESSION WEITERZUVERWENDEN & DIE AKTUELLE TRANSACTION. ICH FÜRCHTE DAS IST ABER NUR MÖGLICH BEI REINEN LESEOPERATIONEN!!!
				Session session = this.getSessionOpen();
				if(session == null) break main;
//				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Starte Transaction:....");
//				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				//Update, d.h. Initialisierung innerhalb dieser Session ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
//				session.update(objPersistedHexTarget);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
				PersistentBag pbag = new PersistentBag((SessionImplementor) session, objPersistedHexTarget.getTileBag());
				System.out.println("Zielzelle. Anzahl Tiles=" + objPersistedHexTarget.getTileBag().size() + " / PersistentBag. Anzahl Tiles= " + pbag.size());
				
				int iStackingLimitUsed = this.computeStackingLimit(sCallingFlag);
				if(pbag.size()>=iStackingLimitUsed){ //STACKING LIMIT von 1 beim  PRE_INSERT, aber die Armee ist schon in der HEXCelle. Das sollte nur noch nicht committed sein...
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (1.2)");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Maximum erlaubtes StackingLimit im Gebiet überschritten: " + objPersistedHexTarget.getMapX() + "/" + objPersistedHexTarget.getMapY() );
					this.getFacadeRuleResult().addMessage(TroopArmyRuleType.STACKING_LIMIT_MAX);
					bReturn = false;
				}
				
//				session.getTransaction().commit();
				
				
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
					Session session = this.getSessionOpen();
					if(session == null) break main;		
//					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
//					session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
					
					//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
//					session.update(objPersistedHexTarget);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
					PersistentBag pbag = new PersistentBag((SessionImplementor) session, objPersistedHexTarget.getTileBag());
					System.out.println("Zielzelle. Anzahl Tiles=" + objPersistedHexTarget.getTileBag().size() + " / PersistentBag. Anzahl Tiles= " + pbag.size());
					
					if(pbag.size()>=1){ //STACKING LIMIT von 1
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen eines Armee Spielsteins (2.2)");
						this.getFacadeRuleResult().addMessage(TroopArmyRuleType.STACKING_LIMIT_MAX);
						bReturn = false;
					}
					
//					session.getTransaction().commit();
					
					//Schliesse die neu geholte Session...
					//wg. der Collection in der Zelle, die ja wieder beim Aktualisieren geholt werden muss. Ohne das Schliessen gibt es folgende Fehlermeldung: Exception in thread "AWT-EventQueue-0" org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
//					session.getTransaction().commit();
					//session.clear();
					//session.close();					
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
