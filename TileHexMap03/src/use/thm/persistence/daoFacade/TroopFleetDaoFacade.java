package use.thm.persistence.daoFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;

import basic.persistence.daoFacade.GeneralDaoFacadeZZZ;
import basic.persistence.dto.GenericDTO;
import basic.persistence.dto.IDTOAttributeGroup;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.hibernate.DateMapping;
import basic.zBasic.persistence.hibernate.HibernateContextProviderZZZ;
import basic.zBasic.persistence.interfaces.IHibernateContextProviderZZZ;
import basic.zBasic.util.abstractList.VectorExtendedZZZ;
import use.thm.client.component.ArmyTileTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopFleetDao;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import use.thm.persistence.model.TroopFleetVariant;
import use.thm.persistence.model.TroopType;
import use.thm.persistence.model.TroopVariant;
import use.thm.rule.facade.TroopArmyRuleFacade;
import use.thm.rule.facade.TroopFleetRuleFacade;
import use.thm.rule.model.TroopArmyRuleType;
import use.thm.rule.model.TroopFleetRuleType;

/**Soll die notwendigen Schritte für bestimmte Aktionen kapseln. 
 * 
 * Eine Fassade soll das System kapseln, das dahinter steht. (Design Pattern)
 *  
 * @author Fritz Lindhauer
 *
 */
public class TroopFleetDaoFacade extends TileDaoFacade{
	private TroopFleet objTroopFleet = null;

	public TroopFleetDaoFacade(IHibernateContextProviderZZZ objContextHibernate){
		super(objContextHibernate);
	}
	
	public boolean delete(String sUniqueName) throws ExceptionZZZ{
		return deleteTroopFleet(sUniqueName);
	}
	
	@Override
	public boolean delete(Troop objTroop) throws ExceptionZZZ {
		TroopFleet objTroopFleet = (TroopFleet) objTroop;
		return this.deleteTroopFleet(objTroopFleet);
	}
	
	public boolean deleteTroopFleet(String sUniqueName) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
		  System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### DELETE TROOPFLEET ####################");
						
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################											
		  	IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopFleetDao objTroopFleetDao = new TroopFleetDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopFleet objTroopFleet = objTroopFleetDao.searchTroopFleetByUniquename(sUniqueName);
			bReturn = this.deleteTroopFleet(objTroopFleet);
		    if(bReturn){
		    	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Flotte gelöscht '" + sUniqueName + "'");
		    }else{
		    	System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Flotte NICHT gelöscht '" + sUniqueName + "'");
		    }
		  }//end main:
		  return bReturn;
		}
		
		public boolean deleteTroopFleet(TroopFleet objTroopFleet) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				boolean bGoon = false;
				String sMessage = new String("");
			
				//#############################
			
			//#############################
			//2. Hole die Backendentsprechung der Ausgangszelle, daraus muss die TroopArmy entfernt werden.
			//############################# 			
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			AreaCellDao objAreaDaoSource = new AreaCellDao(objContextHibernate);
			int iXStarted = objTroopFleet.getMapX();
			int iYStarted = objTroopFleet.getMapY();
			CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
			AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			
			System.out.println("Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
			Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			//Session session = this.getSessionCurrent();
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			System.out.println("Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
			
			//ALSO: Ich bin mir nicht sicher, ob objTroopArmy in der TileBag gefunden wird.
			//1. VErsuch: Darum nicht so einfach, statt dessen versuchen wieder ein Objekt im persistant state zu bekommen:
			//Merke: session.lock(object, lock_option) ist deprecated... 
			//LockRequest lockRequest = session.buildLockRequest(LockOptions.NONE);//versuch das hinzubekommen. Idee ist, das das Objetk jetzt keine PErsistant State mehr hat.   session2.lock(entity, LockMode.NONE);
			//lockRequest.lock(objTroopArmy);
			//boolean bSuccessfulRemoved = objCellStarted.getTileBag().remove(objTroopArmy);
			
			
			//2. Versuch: Aber damit das funktioniert war eigentlich das Überschreiben der equals() MEthode in Troop - Entity wichtig und notwendig
			PersistentBag pbag = new PersistentBag((SessionImplementor) session, objCellStarted.getTileBag());
			boolean bSuccessfulRemoved = pbag.remove(objTroopFleet);
			System.out.println("Ergebnis des Entfernenversuchs: " + bSuccessfulRemoved);
			
			//sondern als Notlösung: 
			//objCellStarted.getTileBag().clear();
			
//			session.saveOrUpdate(objCellStarted); //Hibernate Interceptor wird aufgerufen
			session.update(objCellStarted); //Hibernate Interceptor wird aufgerufen
			System.out.println("Ausgangszelle. Anzahl Tiles nach UPDATE =" + objCellStarted.getTileBag().size());
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				
				//VErsuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break main;
			}
			
			//############################
			//3. Lösche die eigentliche TroopArmy
			//############################
			
			//+++ Datenbankoperationen		
			session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			//session = this.getSessionCurrent();
			if(session == null) break main;		
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			session.delete(objTroopFleet);
			
			//FLUSH IST HIER NOTWENDIG, UM DIE JOIN-Tabelle HEXCELL_TILE um den Eintrag zu reduzieren, ansonsten habe ich es nicht hinbekommen.
			session.flush(); //Datenbank ynchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
		
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				
				//Versuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break main;
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Flotte gelöscht.");
			
			
			
			/*
			//ABER: ES BLEIBT IMMER ETWAS IN DER HEXCELL_TILE TABELLE ZURUECK...
			//###VERZEWEIFELUNG 
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");		
			session.getTransaction().begin();
			session.flush(); //versuch folgendes zu 
			if (!session.getTransaction().wasCommitted()) {
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			*/
			
			//Falls alles glatt durchgeht....
			bReturn = true;
		}//end main:
		return bReturn;
	}
	
	public boolean fillTroopFleetDto(String sUniqueName, GenericDTO<IDTOAttributeGroup> dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopFleetDto(sUniqeName)  ####################");
						
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################											
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopFleetDao objTroopDao = new TroopFleetDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopFleet objTroop = objTroopDao.searchTroopFleetByUniquename(sUniqueName);
			if(objTroop == null) break main;
			
			bReturn = this.fillTroopFleetDto(objTroop, dto);
			
		}//end main:
		return bReturn;
	}
	
	public boolean fillTroopFleetDto(TroopFleet objTroop, GenericDTO dto) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### fillTroopFleetDto(objTroop)  ####################");
			super.fillTroopDto(objTroop, dto);
			
			if(objTroop == null) break main;
					
			//FRAGE: FUNKTIONIERT HIERBEI CALL BY REFERENCE? JA. Es werden ja Werte in den Objekten gefüllt.					
			if(objTroop.getTroopFleetVariantObject()!=null){
				dto.set(ITileDtoAttribute.VARIANT_IMAGE_URL_STRING,objTroop.getTroopFleetVariantObject().getImageUrlString());
				
				//20180630: Nun das Bild auch direkt als byte[] gespeichert aus der Datenbank holen.
				dto.set(ITileDtoAttribute.VARIANT_IMAGE_IN_BYTE,objTroop.getTroopFleetVariantObject().getImage());
				dto.set(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE_01,objTroop.getTroopFleetVariantObject().getImageCatalog01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE_02,objTroop.getTroopFleetVariantObject().getImageCatalog02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGECATALOG_IN_BYTE_03,objTroop.getTroopFleetVariantObject().getImageCatalog03());
				
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_01,objTroop.getTroopFleetVariantObject().getImageDialog01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_02,objTroop.getTroopFleetVariantObject().getImageDialog02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDIALOG_IN_BYTE_03,objTroop.getTroopFleetVariantObject().getImageDialog03());
				
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_01,objTroop.getTroopFleetVariantObject().getImageHexmapDrag01());							
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_02,objTroop.getTroopFleetVariantObject().getImageHexmapDrag02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_03,objTroop.getTroopFleetVariantObject().getImageHexmapDrag03());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_04,objTroop.getTroopFleetVariantObject().getImageHexmapDrag04());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_05,objTroop.getTroopFleetVariantObject().getImageHexmapDrag05());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEDRAG_IN_BYTE_06,objTroop.getTroopFleetVariantObject().getImageHexmapDrag06());
				
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_01,objTroop.getTroopFleetVariantObject().getImageHexmap01());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_02,objTroop.getTroopFleetVariantObject().getImageHexmap02());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_03,objTroop.getTroopFleetVariantObject().getImageHexmap03());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_04,objTroop.getTroopFleetVariantObject().getImageHexmap04());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_05,objTroop.getTroopFleetVariantObject().getImageHexmap05());
				dto.set(ITileDtoAttribute.VARIANT_IMAGEHEXMAP_IN_BYTE_06,objTroop.getTroopFleetVariantObject().getImageHexmap06());
				
				if(objTroop.getTroopFleetVariantObject().getImmutabletextObject()!=null){
					dto.set(ITileDtoAttribute.VARIANT_SHORTTEXT, objTroop.getTroopFleetVariantObject().getImmutabletextObject().getShorttext());					
				}
			}
			
			bReturn = true;
		}//end main:
		return bReturn;
	}
		
	@Override
	public String insert(TroopVariant objTroopVariant, HexCell objCell) throws ExceptionZZZ {
		String sFacadeType = this.getFacadeType();
		String sUniquename= this.computeUniquename(sFacadeType);
		
		TroopFleetVariant objTroopFleetVariant = (TroopFleetVariant) objTroopVariant;
		AreaCell objArea = (AreaCell) objCell;
		boolean bReturn = this.insertTroopFleet(sUniquename, objTroopFleetVariant, objArea);
		if(bReturn){
			return sUniquename;
		}else{
			return null;
		}
	}
	
	public String insert(TroopFleetVariant objTroopFleetVariant, HexCell objCell) throws ExceptionZZZ {
		String sFacadeType = this.getFacadeType();
		String sUniquename= this.computeUniquename(sFacadeType);
		
		AreaCell objArea = (AreaCell) objCell;
		boolean bReturn = this.insertTroopFleet(sUniquename, objTroopFleetVariant, objArea);
		if(bReturn){
			return sUniquename;
		}else{
			return null;
		}
	}
	
	public boolean insertTroopFleet(String sUniqueName, TroopFleetVariant objTroopFleetVariant, AreaCell objArea) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			check:{
			//Fehler können z.B. auftreten, wenn die Datenbank komplett leer ist... Dies hier abfangen.
			if(objTroopFleetVariant==null){
				String stemp = "Keine TroopFleetVariant übergeben.";
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
				ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName());
				throw ez;		
			}
			if(objArea==null){
				String stemp = "Keine Area übergeben.";
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": " +stemp);
				ExceptionZZZ ez = new ExceptionZZZ(stemp,ExceptionZZZ.iERROR_PARAMETER_MISSING, this,  ReflectCodeZZZ.getMethodCurrentName());
				throw ez;		
			}			
		}//end check:
			Integer intSubtypeUniqueNumberUsed  = null;
			Integer intVariantUniqueNumberUsed  = null;			
			additionalData:{
				//Hole die bisher höchste Zahl der Varianten 				
				IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
				TroopFleetDao objTroopDao = new TroopFleetDao(objContextHibernate);
				
				///!!! Das reicht aus nicht aus... Intern wird wohl Es muss die ZhisId der Variante als WHERE Teil einbezogen werden.
				//Merke: .findColumnValueMax("instanceVariantUniquenumber"); macht nur Unterscheidung zwischen Army und Fleet. Feinere Variantenunterscheidung muss scheitern.
				//TODO GOON 20180322: Integer intVariantUniqueNumber = objTroopDao.findColumnValueMaxForVariant("instanceVariantUniquenumber", intThisIdOfVariantUsed); 
				Integer intVariantUniqueNumber = objTroopDao.findColumnValueMax("instanceSubtypeUniquenumber"); 
				if(intVariantUniqueNumber==null){
					intSubtypeUniqueNumberUsed = new Integer(1);
				}else{
					int itemp = intVariantUniqueNumber.intValue() + 1;
					intSubtypeUniqueNumberUsed = new Integer(itemp);
				}
				
//				try{
				Map<String, Object> whereBy = new HashMap<String, Object>();
				
				//########### FEHLER: could not resolve property: trooparmyvariant_thiskey_id of: use.thm.persistence.model.TroopArmy [select max(instanceVariantUniquenumber) from use.thm.persistence.model.TroopArmy g where g.trooparmyvariant_thiskey_id = :trooparmyvariant_thiskey_id]
				//whereBy.put("trooparmyvariant_thiskey_id",11);
				//Also: Mit demObjekt selbst arbeiten.
				
				TroopFleetVariant objTroopVariantSearchedFor = objTroopFleetVariant;
				whereBy.put("troopFleetVariantObject",objTroopVariantSearchedFor);
				Integer intCategoryUniqueNumber = objTroopDao.findColumnValueMax("instanceVariantUniquenumber",whereBy); 
				if(intCategoryUniqueNumber==null){ //Merke: Wenn es noch keine Variante der Truppe auf der KArte gibt, dann ist der ermittelte Wert 'null'
					intVariantUniqueNumberUsed = new Integer(1);
				}else{
					int itemp = intCategoryUniqueNumber.intValue() + 1;
					intVariantUniqueNumberUsed = new Integer(itemp);
				}
				System.out.println("############ Errechneter neuer max der übergebenen Troopvariant  ist: " + intVariantUniqueNumberUsed);
				
//				}catch(Exception e){
//					System.out.println("########### FEHLER: " + e.getMessage());
//				}
			}
				
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Speicher die TroopFleet neu, füge die Area der TroopFleet hinzu, damit sie weiss in welchem Feld sie steht.
			//####################
			//Session session = this.getSession();//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			Session session = this.getSessionCurrent();
			if(session == null) break main;		
//			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
//			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			//+++ DAS ENTITY UND VERBUNDENEN OBJEKTE
			TroopFleet objTroopTemp = new TroopFleet(new TileId("EINS", "1", sUniqueName));//TODO GOON : sY als Uniquename zu verwenden ist nur heuristisch und nicht wirklich UNIQUE						
//			session.update(objArea);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
//			session.getTransaction().commit();	
			objTroopTemp.setHexCell(objArea); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
			
			//Füge Variante der Truppe hinzu wg n:1 Beziehung. ABER: Diese muss schon zuvor geholt worden sein, sonst überschneiden sich die Transaktionen.
			objTroopTemp.setTroopFleetVariantObject(objTroopFleetVariant);
			objTroopTemp.setInstanceVariantUniquenumber(intVariantUniqueNumberUsed); //die muss zuvor ausgerechnet worden sein.
			objTroopTemp.setInstanceSubtypeUniquenumber(intSubtypeUniqueNumberUsed); //die muss zuvor ausgerechnet worden sein.
			
			//Füge die Initialwerte der Variante hinzu.
			Float fltHealthInitial = objTroopFleetVariant.getHealthInitial();
			objTroopTemp.setHealth(fltHealthInitial.floatValue());
			
			//+++ DAS ERSTELLDATUM ++++++++++++++++++++++++++++++++++++
			this.makeCreatedDates(objTroopTemp);
			
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			session.save(objTroopTemp); //Hibernate Interceptor wird aufgerufen	: HibernateInterceptorTHM.java	

			//			session.update(objTroopTemp); //Hibernate Interceptor wird aufgerufen
			//          FEHLER: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): [use.thm.persistence.model.TroopFleet#0]
//			session.flush();
//			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(this.getHibernateContext(),"save",session.getTransaction());
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"save",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung.
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}

			/* WASSERFELDER GEHOEREN KEINEM SPIELER darum hier nicht relevant
			if(objArea instanceof AreaCell){				
				session = this.getSession();//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");				
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.					
			
				AreaCellOcean objAreaTemp = (AreaCellOcean) objArea;
				
				//!!! WASSERFELDER GEHOEREN KEINEM SPIELER darum hier nicht relevant
//				int iPlayer = objTroopTemp.getPlayer();
//				bjAreaTemp.setPlayerOwner(iPlayer);							

				//session.save(objAreaTemp); //constraint violation, so als ob ein neues Objekt gepseichert werden würde .... insert ...
				session.update(objAreaTemp); //SaveAndUpdate-Listener wird NICHT(!) ausgeführt, aber es gibt keine constraint verletzung ... update
				if (!session.getTransaction().wasCommitted()) {
					//session.flush();								
					session.getTransaction().commit();///SaveAndUpdate-Listener NICHT ausgeführt. //TODO GOON: Probiere ob 'AbstractFlushingEventListener' ausgeführt würde.
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung.
				this.getFacadeResult().setMessage(sMessage);			
				break validEntry;
			}
			*/
			
			//###################
			//2. Aktualisiere die Hex-Zelle, füge die TroopFleet der Liste hinzu, damit die Hex-Zelle weiss welche TroopFleets in ihr stehen.
			//####################		
			//session = this.getSession();//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			session = this.getSessionCurrent();
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();
			
			//Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session
			//http://stackoverflow.com/questions/18956825/hibernate-collection-is-not-associated-with-any-session
			//session.refresh(objCellTemp);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			session.refresh(objArea);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			
			//weil Lazy - Loading eingestellt ist an dieser Stelle erst mal sehn, die Collection zu bekommen
			//objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
			//Collection<Tile>colTile = objCellTemp.getTileBag();
			Collection<Tile>colTile = objArea.getTileBag();
						
			Hibernate.initialize(colTile);
			if(colTile!=null){
				colTile.add(objTroopTemp);
			}
			
			session.update(objArea); //SaveAndUpdate-Listener wird nicht ausgeführt
			if (!session.getTransaction().wasCommitted()) {
				//session.flush();								
				session.getTransaction().commit();///SaveAndUpdate-Listener wir ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());				
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"save",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			
			//Falls alles glatt durchgeht....
			bReturn = true;
		}//end validEntry:
								
		}//end main:
		return bReturn;
		
		
		//#### Aus Dokumentationsgründen mal den ursprungsCode hier erhalten. Das ist dann alles schicker gemacht worden.
		//TODO GOON 20170419: 
//		if(objCellTemp instanceof AreaCell){
//			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
//			session = objContextHibernate.getSession();
//			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
//			session.getTransaction().begin();					
//			//session.beginTransaction(); //Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
//			//Das aktiviert nicht den PreUpdateListener und zu diesem Zeitpunkt auch nicht mehr den PreInsertListener 
//			
//			AreaCellLand objAreaTemp = (AreaCellLand) objCellTemp;
//			int iPlayer = objTroopTemp.getPlayer();
//			objAreaTemp.setPlayerOwner(iPlayer);
//			
//			session.update(objCellTemp); //SaveAndUpdate-Listener wird nicht ausgeführt
//			if (!session.getTransaction().wasCommitted()) {
//				//session.flush();								
//				session.getTransaction().commit();///SaveAndUpdate-Listener wir ausgeführt, aber FÜR EIN TROOPARMY OBJEKT!!!
//			}
//		}
//		
//		//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
//		session = objContextHibernate.getSession();
//		System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
//		session.getTransaction().begin();	
//		
//		//Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session
//		//http://stackoverflow.com/questions/18956825/hibernate-collection-is-not-associated-with-any-session
//		session.refresh(objCellTemp);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
//											
//		//weil Lazy - Loading eingestellt ist an dieser Stelle erst mal sehn, die Collection zu bekommen
//		//objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
//		Collection<Tile>colTile = objCellTemp.getTileBag();
//		
//		
//		Hibernate.initialize(colTile);
//		if(colTile!=null){
//			colTile.add(objTroopTemp);
//		}
//		session.update(objCellTemp);
//		if (!session.getTransaction().wasCommitted()) {
//			//session.flush();
//			session.getTransaction().commit();//SaveAndUpdate-Listener wir NICHT ausgeführt, aber PreInsert-Listener wird ausgeführt.
//		}
	}
	

	@Override
	public boolean move(Troop objTroop, HexCell objCell) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean updateTroopFleetPosition(String sUniqueName, AreaCell objAreaTarget) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": START #### UPDATE TROOPFLEETPOSITION ####################");
			
			validEntry:{
			boolean bGoon = false;
			String sMessage = new String("");
			
			//###################
			//1. Hole die TroopArmy, füge die neue Area der TroopArmy hinzu, damit sie weiss in welchem neuen Feld sie steht.
			//####################											
			IHibernateContextProviderZZZ objContextHibernate = this.getHibernateContext();
			TroopFleetDao objTroopFleetDao = new TroopFleetDao(objContextHibernate);

			//HQL verwenden, um die TroopArmy anhand des Uniquename zu bekommen. 
			TroopFleet objTroopFleet = objTroopFleetDao.searchTroopFleetByUniquename(sUniqueName);

			//#############################
			//2. Mache die vorgeschaltete Validierung
			//############################# 
			//Wichtig: Vor dem ganzen Ablauf die Validierung durchführen, sonst muss man nach der Validierung wieder alles rückgängig machen.
			TroopFleetRuleFacade objRuleFacade = new TroopFleetRuleFacade(getHibernateContext(), objTroopFleet);
			bGoon = objRuleFacade.onUpdateTroopFleetPosition(objAreaTarget,"PREINSERT");//Die PREINSERT  Angaebe ist notwendig wg. anderen Stacking Limits. Mögliche Werte UPDATE / PREINSERT.
			if(!bGoon){	
				//NEGATIVES Ergebnis der vorgeschalteten Validierung
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vorgeschaltete Validierung NICHT erfolgreich");
				
				//Hole die Meldungen aus dem Regelwerk ab.			
				sMessage = objRuleFacade.getMessagesAsString();

				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}//Vorgeschaltetet Validierung
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Vorgeschaltete Validierung erfolgreich");
			
			//#############################
			//3. Hole die Backendentsprechung der Ausgangszelle, daraus muss die TroopArmy entfernt werden.
			//############################# 
			AreaCellDao objAreaDaoSource = new AreaCellDao(objContextHibernate);
			int iXStarted = objTroopFleet.getMapX();
			int iYStarted = objTroopFleet.getMapY();
			CellId primaryKeyCellStarted = new CellId("EINS", Integer.toString(iXStarted), Integer.toString(iYStarted));
			AreaCell objCellStarted = objAreaDaoSource.findByKey(primaryKeyCellStarted);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			
			//#############################
			//4. Mache die Datenbankoperationen
			//############################# 
			
				//+++ 4.1 Datenbankoperationen: Entferne aus der Ausgangszelle
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
				//Session session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				Session session = this.getSessionCurrent();
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
					
				//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten/genutzt worden ist.
				//session.update(objCellStarted);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
				PersistentBag pbag = new PersistentBag((SessionImplementor) session, objCellStarted.getTileBag());
				boolean bSuccessfulRemoved = pbag.remove(objTroopFleet);//Aber damit das funktioniert war eigentlich das Überschreiben der equals() Methode in Troop - Entity wichtig und notwendig
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ergebnis des Entfernenversuchs: " + bSuccessfulRemoved);
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Ausgangszelle. Anzahl Tiles=" + objCellStarted.getTileBag().size());
				//session.saveOrUpdate(objCellStarted); //Hibernate Interceptor wird aufgerufen
				session.update(objCellStarted); //Hibernate Interceptor wird aufgerufen			
				session.flush();
				session.getTransaction().commit();
				if (!session.getTransaction().wasCommitted()) {
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					
					//VErsuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
					//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
					//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
					
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
					if(objResult!=null){
						sMessage = objResult.getVetoMessage();
						bGoon = !objResult.isVeto();
					}else{
						//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
						bGoon = true;
					}
				}			
				if(!bGoon){
					//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
					this.getFacadeResult().setMessage(sMessage);
					break validEntry;
				}
			
			
			//#############################
			//Hole die Backendentsprechung der Zielzelle
			//#############################
			/*nicht notwendig, wird von aussen übergeben, LASS DEN CODE ABER STEHEN, ZUR ANSCHAUUNG
			AreaCellDao objAreaDao = new AreaCellDao(objContextHibernate);
			int iXDropped = objArea.getMapX();
			int iYDropped = objArea.getMapY();
			CellId primaryKeyCell = new CellId("EINS", Integer.toString(iXDropped), Integer.toString(iYDropped));
			AreaCell objCellTarget = objAreaDao.findByKey(primaryKeyCell);//Spannend. Eine Transaction = Eine Session, d.h. es müsste dann wieder eine neue Session gemacht werden, beim zweiten DAO Aufruf.
			*/ 
			   
			//+++ 4.2: Datenbankoperation: Aktualisiere die Troop mit der neuen Position/dem neuien HexFeld.		
			session = this.getSession();	//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session			
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
			
			//Update, d.h. Initialisierung ist wichtig, weil die Zelle ggfs. noch nie zuvor betreten worden ist.
			session.update(objAreaTarget);//20170703: GROSSE PROBLEME WG. LAZY INITIALISIERUNG DES PERSISTENTBAG in dem area-Objekt. Versuche damit das zu inisiteliesen.
			objTroopFleet.setHexCell(objAreaTarget); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
		
			//Merke: EINE TRANSACTION = EINE SESSION ==>  neue session von der SessionFactory holen
			//wenn man .save() aufruft, wird immer eine neue Zeile mit dem Objekt erzeugt, auch wenn man ein bestehendes aktualisieren möchte session.save(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			//Alternative: Siehe https://stackoverflow.com/questions/30473707/hibernate-creates-new-row-on-save-and-not-updates
			//session.saveOrUpdate(objTroopArmy); //Hibernate Interceptor wird aufgerufen
			session.update(objTroopFleet); //Hibernate Interceptor wird aufgerufen
			session.flush();
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				
				//Versuch die Werte der neuen xSpalte yZeile in der Datenbank zu aktualisiere, mit flush()... 
				//session.flush(); //Datenbank synchronisation, d.h. Inserts und Updates werden gemacht. ABER es wird noch nix committed.
				//session.getTransaction().commit(); //onPreInsertListener wird ausgeführt   //!!! TODO: WARUM WIRD wg. des FLUSH NIX MEHR AUSGEFÜHRT AN LISTENERN, ETC ???
				
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"save",session.getTransaction());//EventType.PRE_INSERT
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Aktuelle Position der Army (X/Y): " + objTroopFleet.getMapX() + "/" + objTroopFleet.getMapY());
			
			
			/* ########## TODO GOON: DAS ERST MAL NOCH AUSKLAMMERN. ERST MUSS DIE ARMY ECHT DIE NEUE POSITION HABEN 
			//###################
			//2. Aktualisiere die Area-AUSGANGS-Zelle, Besitzer bleibt, aber die TroopArmy muss aus der Collection rausgenommen werden.
			//###################
			//TODO GOON 20170713
			
			//###################
			//2. Aktualisiere die Area-ZIEL-Zelle, setze den "Besitzer" in das Gebiet.
			//####################			
			if(objArea instanceof AreaCell){
				session = this.getSession();		//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
				if(session == null) break main;	
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");			
				session.getTransaction().begin();//Ein zu persistierendes Objekt - eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.					 
				
				AreaCellLand objAreaTemp = (AreaCellLand) objArea;
				int iPlayer = objTroopArmy.getPlayer();
				objAreaTemp.setPlayerOwner(iPlayer);							

				//Die Area wurde ja schon persistiert, also hier update und nicht save.
				session.update(objAreaTemp); //SaveAndUpdate-Listener wird NICHT(!) ausgeführt, aber es gibt keine constraint verletzung ... update
				if (!session.getTransaction().wasCommitted()) {
					//session.flush();								
					session.getTransaction().commit();///SaveAndUpdate-Listener NICHT ausgeführt. //TODO GOON: Probiere ob 'AbstractFlushingEventListener' ausgeführt würde.
					//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
					VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			*/
			
			//++++++++ 4.3 Datanbankoperationen Aktualisiere die Hex-Zelle, füge die TroopArmy der Liste hinzu, damit die Hex-Zelle weiss welche TroopArmies in ihr stehen.		
			//session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			session = this.getSessionCurrent();
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");
			session.getTransaction().begin();
			
			//Exception in thread "main" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session
			//http://stackoverflow.com/questions/18956825/hibernate-collection-is-not-associated-with-any-session
			//session.refresh(objCellTemp);//liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			session.refresh(objAreaTarget);///wg. Exception in thread "AWT-EventQueue-0" org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: use.thm.persistence.model.HexCell.objbagTile, could not initialize proxy - no Session //liefert im Log... Collection fully initialized: [use.thm.persistence.model.HexCell.objbagTile#component[mapAlias,mapX,mapY]{mapY=2, mapX=1, mapAlias=EINS}]
			
			//weil Lazy - Loading eingestellt ist an dieser Stelle erst mal sehn, die Collection zu bekommen
			//objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
			//Collection<Tile>colTile = objCellTemp.getTileBag();
			Collection<Tile>colTile = objAreaTarget.getTileBag();				
			Hibernate.initialize(colTile);//Exception in thread "AWT-EventQueue-0" org.hibernate.HibernateException: collection is not associated with any session
			if(colTile!=null){
				colTile.add(objTroopFleet);
			}
	
			//session.saveOrUpdate(objAreaTarget); //SaveAndUpdate-Listener wird nicht ausgeführt
			session.update(objAreaTarget); //SaveAndUpdate-Listener wird nicht ausgeführt
			session.flush();
			session.getTransaction().commit();
			if (!session.getTransaction().wasCommitted()) {
				
				//session.getTransaction().commit();///SaveOrUpdate-Listener wird ausgeführt, FÜR EIN TROOPARMY OBJEKT!!!
				//session.flush(); //versuch folgendes zu 
				//bGoon = HibernateUtil.wasCommitSuccessful(objContextHibernate,"update",session.getTransaction());
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				if(objResult!=null){
					sMessage = objResult.getVetoMessage();
					bGoon = !objResult.isVeto();
				}else{
					//also... wenn kein Veto, dann immer true, auch wenn nicht committed!
					bGoon = true;
				}
			}
			
			
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}

			/*
			//###VERZEWEIFELUNG 
			session = this.getSession();			//Vesuch eine neue Session zu bekommen. Merke: Die Session wird hier nicht gespeichert! Wg. 1 Transaktion ==> 1 Session
			if(session == null) break main;	
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + ": Starte Transaction:....");		
			session.getTransaction().begin();
			session.flush(); //versuch folgendes zu 
			if (!session.getTransaction().wasCommitted()) {
				VetoFlag4ListenerZZZ objResult = HibernateUtil.getCommitResult(this.getHibernateContext(),"update",session.getTransaction());					
				sMessage = objResult.getVetoMessage();
				bGoon = !objResult.isVeto();
			}
			if(!bGoon){
				//Mache die Ausgabe im UI nicht selbst, sondern stelle lediglich die Daten zur Verfügung. Grund: Hier stehen u.a. die UI Komponenten nicht zur Verfügung
				this.getFacadeResult().setMessage(sMessage);
				break validEntry;
			}
			*/
			
			//Falls alles glatt durchgeht....
			bReturn = true;
		}//end validEntry:		
		}//end main:
		return bReturn;
	}
	
	

	@Override
	public String getFacadeType() {
		return TroopType.FLEET.name();
	}
	
	@Override
	public TroopFleet getEntityUsed() {
		return this.objTroopFleet;
	}
	
	@Override
	public void setEntityUsed(Troop objTroopFleet){
		this.objTroopFleet =  (TroopFleet) objTroopFleet;
	}

	
}
