package use.thm.rule.facade;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopFleet;
import use.thm.rule.model.AreaCellRuleType;
import use.thm.rule.model.TroopArmyRuleType;
import use.thm.rule.model.TroopFleetRuleType;
import basic.rule.facade.GeneralRuleHibernateFacadeZZZ;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;

public class AreaCellRuleFacade  extends GeneralRuleFacadeTHM{
	private AreaCell objArea;
	
	private AreaCellRuleFacade(){
		super();
	}
	public AreaCellRuleFacade(IHibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public AreaCellRuleFacade(AreaCell area){
		this();
		this.setArea(area);
	}
	
	public AreaCellRuleFacade(IHibernateContextProviderZZZ objContextHibernate, AreaCell area){
		this(objContextHibernate);
		this.setArea(area);
	}
	
	public boolean onUpdateAreaCell(AreaCell area, String sCallingFlag) throws ExceptionZZZ{
		boolean bReturn = true;// true=alles o.k., false=Regelverletzung schlägt zu. Daher wird später, d.h. beim Persistieren ein Veto eingelegt.
		main:{
			//DAS IST NICHT NOTWENDIG, ERST WENN AREAS AUCH PER DRAG AND DROP AUF DIE KARTE GEZOGEN WERDEN KÖNNEN.
			if(sCallingFlag.equals("xix")){ //so umständlich weil sonst der Compiler wg. "unreachable Code" meckert.
			}else{
			break main;
			}
			

			boolean bHasVeto = false;
			String sTypeArea = area.getAreaType();
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Area vom Typ="+sTypeArea);
			
			Collection<Tile> colTile = area.getTileBag();
			if(colTile.size() == 0){
				break main;
			}else{	
				//#####################################################
				//Spielsteintypen hinsichtlich der Gebietsart prüfen
				for(Tile objTile : colTile){
					String sTileType = objTile.getTileType();
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Enthaelt Spielstein vom Typ="+sTileType);
					
					if(sTypeArea.equalsIgnoreCase("OC") & sTileType.equalsIgnoreCase("AR")){
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen einer Area wg. Armee Spielsteins");
						this.getFacadeRuleResult().addMessage(AreaCellRuleType.TILETYPE_ARMY);
						bHasVeto = true;
					}else if(sTypeArea.equalsIgnoreCase( "LA") & sTileType.equalsIgnoreCase("FL")){				
						System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim Committen einer Area wg. Flotten Spielsteins");
						this.getFacadeRuleResult().addMessage(AreaCellRuleType.TILETYPE_FLEET);
						bHasVeto = true;
					}else{
						bHasVeto = false;
					}						
				}//end for	
				
				//#####################################################
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
				//Schliesse die neu geholte Session...
				//wg. der Collection in der Zelle, die ja wieder beim Aktualisieren geholt werden muss. Ohne das Schliessen gibt es folgende Fehlermeldung: Exception in thread "AWT-EventQueue-0" org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
//				session.getTransaction().commit();
				//session.clear();
				//session.close();				
			
			//ABER: später trotzdem: Database is locked()-Fehler
			
			//4. Ansatz
			//VERSUCH AUF ANDEREM WEG EINE SESSION ZU BEKOMMEN OHNE DIE AKTUELLE ZU SCHLIESEN:						
			//VERSUCH DIE AKTUELLE SESSION WEITERZUVERWENDEN & DIE AKTUELLE TRANSACTION. ICH FÜRCHTE DAS IST ABER NUR MÖGLICH BEI REINEN LESEOPERATIONEN!!!
				
				
			
				Session session = this.getSessionCurrent();
				if(session == null) break main;			
				//session.getTransaction();//NEIN, FEHLERMEDUNG: NESTED TRANSACTION NOT ALLOWED, also mit der aktuellen Session und der aktuellen Transaction weiterarbeiten.
				                                      //Daher nur zum Lesen zu gebrauchen!!!

				//Update, d.h. Initialisierung innerhalb dieser Session ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
				session.update(area);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
				PersistentBag pbag = new PersistentBag((SessionImplementor) session, area.getTileBag());
				System.out.println("Zielzelle. Anzahl Tiles=" + area.getTileBag().size() + " / PersistentBag. Anzahl Tiles= " + pbag.size());
					
				//Hole die Obergrenze								
				int iStackingLimitUsed = this.computeStackingLimit(sCallingFlag);
				if(pbag.size()>=iStackingLimitUsed){ 
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": FEHLER beim committen einer Area");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Maximum erlaubtes StackingLimit im Gebiet überschritten: " + area.getMapX() + "/" + area.getMapY() );
					this.getFacadeRuleResult().addMessage(AreaCellRuleType.STACKING_LIMIT_MAX);
					bReturn = false;
				}
									
			
				}		//col.size()=0
		bReturn = !bHasVeto;
	}//end main:
	return bReturn;
	}
		
	public void setArea(AreaCell area){
		this.objArea = area;
	}
	public AreaCell getArea(){
		return this.objArea;
	}
	
}
