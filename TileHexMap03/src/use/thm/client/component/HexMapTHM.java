package use.thm.client.component;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.internal.SessionFactoryImpl;

import use.thm.ApplicationSingletonTHM;
import use.thm.ApplicationTHM;
import use.thm.ITileEventUserTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.client.event.TileMetaEventBrokerTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.persistence.dao.AreaCellDao;
import use.thm.persistence.dao.DefaulttextDao;
import use.thm.persistence.dao.TextDefaulttextDao;
import use.thm.persistence.dao.TileDefaulttextDao;
import use.thm.persistence.dao.TileImmutabletextDao;
import use.thm.persistence.dao.TroopArmyDao;
import use.thm.persistence.dao.TroopArmyVariantDao;
import use.thm.persistence.dao.TroopDao;
import use.thm.persistence.dao.TroopFleetVariantDao;
import use.thm.persistence.dao.TroopVariantDao;
import use.thm.persistence.dao.TroopVariantDaoFactory;
import use.thm.persistence.daoFacade.TroopArmyDaoFacade;
import use.thm.persistence.daoFacade.TroopFleetDaoFacade;
import use.thm.persistence.dto.DtoFactoryGenerator;
import use.thm.persistence.dto.ITileDtoAttribute;
import use.thm.persistence.dto.TileDtoFactory;
import use.thm.persistence.event.VetoFlag4ListenerZZZ;
import use.thm.persistence.hibernate.HibernateContextProviderSingletonTHM;
import use.thm.persistence.interfaces.enums.IEnumSetTextTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellLand;
import use.thm.persistence.model.AreaCellOcean;
import use.thm.persistence.model.AreaCellType;
import use.thm.persistence.model.CellId;
import use.thm.persistence.model.Defaulttext;
import use.thm.persistence.model.HexCell;
import use.thm.persistence.model.Key;
import use.thm.persistence.model.TextDefaulttext;
import use.thm.persistence.model.Tile;
import use.thm.persistence.model.TileDefaulttext;
import use.thm.persistence.model.TileId;
import use.thm.persistence.model.Troop;
import use.thm.persistence.model.TroopArmy;
import use.thm.persistence.model.TroopArmyVariant;
import use.thm.persistence.model.TroopFleet;
import use.thm.persistence.model.TroopFleetVariant;
import basic.persistence.dto.GenericDTO;
import basic.persistence.util.HibernateUtil;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.persistence.SQLiteUtilZZZ;
import basic.zBasic.persistence.interfaces.IDtoFactoryZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.datatype.enums.EnumSetDefaulttextUtilZZZ;
import basic.zBasic.util.datatype.enums.EnumZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
import basic.zKernel.IKernelZZZ;
import basic.zKernel.KernelUseObjectZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJFrameCascadedZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

/**Klasse 
 * - berechnet die Gesamtgröße der Karte/des Panels, das die Karte anzeigt (berechnet die Anzahl der Zeilen)
 * - verwaltet die Sechsecke 
 * @author lindhaueradmin
 *
 */
public class HexMapTHM extends KernelUseObjectZZZ implements ITileEventUserTHM {
	//Die Konstanten dienen als Defaultwerte
	public static int constSideLength = 40;         //Seitenl�nge eines Sechsecks. Hieraus wird alles mathematisch abgeleitet.
	public static int constNumberOfColumn = 20; //Anzahl der Sechsecke in einer Zeile
	public static int constNumberOfRow = 10; //Anzahl der Sechseckzeilen
	
	private KernelJPanelCascadedZZZ panelParent;
	
	//Solange diese Werte <= 0 sind, werden die Konstanten als Defaultwert genommen.
	private int iColumnMax=0; //die maximale Anzahl der Hexagons in einer Zeile
	private int iRowMax=0;    //die maximale Anzahl der Zeilen
	private int iSideLength=0; //die Seitenl�nge eines Hexagons
	
	private HashMapMultiZZZ hmCell = new HashMapMultiZZZ();  //Hashmap mit Hashmap, in der die Sechseckzellen in Form von Koordinaten (z.B. "1","1") abgelegt sind.
	private int iNrOfHexes = 0;
	
	private TileMoveEventBrokerTHM objTileMoveEventBroker = null;
	private TileMetaEventBrokerTHM objTileMetaEventBroker = null; 
		
	public HexMapTHM(IKernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, int iColumnMax, int iRowMax, int iSideLength) throws ExceptionZZZ{
		super(objKernel);
		if(panelParent==null){
			ExceptionZZZ ez = new ExceptionZZZ("No ParentPanel provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		 		
		this.setRowMax(iRowMax); 
		this.setColumnMax(iColumnMax);
		this.iSideLength = iSideLength;
		this.setPanelParent(panelParent);
		
		//20180326	
		//THEMA : Persistierung in einer Datenbank. Falls die Datenbank neu ist, müssen ggfs. noch die GEBIETE und die DEFAULTTRUPPEN erstellt werden.
		//             Die anderen Tabellen (Defaulttexte, ImmutableTexte, Army und Fleetvarianten) wurden schon auf Applikationsebene erstellt.
		//FGL: Nachdem die Überprüfung, ob die Datenbank existiert Aufgabe des Application-Objekt gewerden ist, hier das entsprechende Flag auslesen
		boolean bDbExists = !ApplicationSingletonTHM.getInstance().getFlag(ApplicationTHM.FLAGZ.DATABASE_NEW.name());
		
		
		//Die MapInformationen und die Informationen für Hexfelder sollen aus einer SQL Tabelle kommen. Das legt dann auch die Größe der Karte fest fest....
		//Wenn es schon Mapinformationen gibt (ggf. neu "Map Alias" beachten) dann soll die Karten nicht neu aufgebaut, sondern aus der SQL Datenbank ausgelesen werden.
		boolean bSuccessMap = fillMap(bDbExists);			
	}
	
	/** Anzahl der Hexes in einer Zeile. 
	 * Wird im Konstruktor übergeben
	* @return
	* 
	* lindhaueradmin; 12.09.2008 08:17:33
	 */
	public int getColumnMax(){
		if(this.iColumnMax<=0){
			return HexMapTHM.constNumberOfColumn;
		}else{
			return this.iColumnMax;
		}
	}
	public void setColumnMax(int iColumnMax){
		this.iColumnMax = iColumnMax;
	}
	
	/** Anzahl der Hex-Zeilen. 
	 * Wird im Konstruktor übergeben
	* @return
	* 
	 */
	public int getRowMax(){
		if(this.iRowMax<=0){
			return HexMapTHM.constNumberOfRow;
		}else{
			return this.iRowMax;
		}
	}
	public void setRowMax(int iRowMax){
		this.iRowMax = iRowMax;
	}
	
	/** Seitenlänge der Hexes. Dient als Mathematische Grundlage für alle Berechnungen.
	 * Wird im Konstruktorübergeben.
	* @return
	* 
	* lindhaueradmin; 12.09.2008 08:18:06
	 * @throws ExceptionZZZ 
	 */
	public int getSideLength() throws ExceptionZZZ{
		//20180901: Wg. der Zoombarkeit die  Seitenlänge direkt aus dem Application-Objekt auslesen.
		//               Damit ist sie flexibel und braucht hier nicht mehr gespeichert zu werden.
//		if(this.iSideLength<=0){
//			return HexMapTHM.constSideLength;
//		}else{
//			return this.iSideLength;
//		}
		
		int iSideLength = ApplicationSingletonTHM.getInstance().getHexFieldSideLengthCurrent();
		this.iSideLength = iSideLength;
		return this.iSideLength;
	}
	
	/**Bei fillMap() wird die Anzahl der erzeugten Zellen vermerkt. 
	 *  Hierüber kann sie ausgelesen werden.
	* @return
	* 
	* lindhaueradmin; 12.09.2008 12:18:58
	 */
	public int getTotalNumberOfCell(){
		return this.iNrOfHexes;
	}
	
	/**Hashmap mit Hashmap, in der die Sechseckzellen in Form von Koordinaten (z.B. "1","1") abgelegt sind.
	* @return
	* 
	* lindhaueradmin; 12.09.2008 08:36:03
	 */
	public HashMapMultiZZZ getMapCell(){
		return this.hmCell;
	}
	
	
		
//#########################################

			
	public boolean fillMap() throws ExceptionZZZ{
		return this.fillMap(false);
	}
	
	public boolean fillMap(boolean bDbExists) throws ExceptionZZZ{
		return this.fillMap_(this.getPanelParent(), bDbExists);
	}
	
	private boolean fillMap_(KernelJPanelCascadedZZZ panelMap, boolean bDbExists) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			HashMapMultiZZZ hmCell = this.getMapCell();
			hmCell.clear();
			boolean bFillDatabaseNew = true;
			this.iNrOfHexes = 0;  //Die Gesamtzahl der Hexes wird hochgezählt beim Füllen UND IST DER RÜCKGABEWERT.
			
			//Kernel Objekt
			IKernelZZZ objKernel = this.getKernelObject();
			
			//MoveEventBroker für die Bewegung von einer Zelle zur anderen
			TileMoveEventBrokerTHM objTileMoveEventBroker = new TileMoveEventBrokerTHM(objKernel);
			this.setTileMoveEventBroker(objTileMoveEventBroker);
			
			//20130630: MetaEventBroker für Ereignisse wie Erstellen, Vernichten, etc. am Spielstein
			TileMetaEventBrokerTHM objTileMetaEventBroker = new TileMetaEventBrokerTHM(objKernel);
			this.setTileMetaEventBroker(objTileMetaEventBroker);
				
			//Der HibernateContext ist ein Singleton Objekt, darum braucht man ihn nicht als Parameter im Methodenaufruf weitergeben.
			HibernateContextProviderSingletonTHM objContextHibernate = HibernateContextProviderSingletonTHM.getInstance(this.getKernelObject());			
			if(bDbExists){
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert schon.");
				//Diese Methode hat darüber nicht zu entscheiden.... objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "update");  //! Jetzt erst wird jede Tabelle über den Anwendungsstart hinaus gepseichert.				
							
				//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.
				//EntityManager em = null;
				//Wichtig: Das darf erst NACH dem Überprüfen auf die Datenbankexistenz passieren, da hierdurch die Datei erzeugt wird (wenn auch noch ohne Tabellen) und dies die Prüfung auf Existenz der Datei konterkariert.
				//EntityManager em = objContextHibernate.getEntityManager("c:\\server\\SQLite\\TileHexMap03.sqlite");
				//EntityManager em = objContextHibernate.getEntityManager("jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");				
								
				//Fall: Datenbank existiert. Wenn es einen Fehler gibt, dann wird Sie allerdings neu aufgebaut.
				//boolean bSuccess = fillMap_readCreated_IMPERFORMANT_EXAMPLE(objContextHibernate, panelMap);
				boolean bSuccess = fillMap_readCreated(objContextHibernate, panelMap);
								
				bFillDatabaseNew = !bSuccess;
			}else{
				//Fall: Datenbank existiert noch nicht
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert noch nicht.");
				///Diese Methode hat darüber nicht zu entscheiden... objContextHibernate.getConfiguration().setProperty("hibernate.hbm2ddl.auto", "create");  //! Damit wird die Datenbank und sogar die Tabellen darin automatisch erstellt, aber: Sie wird am Anwendungsende geleert.
			
				bFillDatabaseNew=true;
			}//end if bDbExists
			
			if(bFillDatabaseNew){
				if(bDbExists){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank existiert zwar, es hat aber Probleme beim Einlesen gegeben.");
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Datenbank sollte gelöscht werden, damit der Neuaufbau keine Probleme bekommt.");
					//	TODO ggfs. zur Sicherheit die gesamte Datenbankdatei löschen, was aber nur geht, wenn z.B. kein anderer Client darauf zugreift. Vor dem endgültigen Löschen immer ein Backup machen.
					
				}
				
				//Erzeuge neuen Datenbankinhalt
				//Per Hibernate & Session 
				bReturn = fillMap_createNew(objContextHibernate, panelMap);
				//Per EntityManager, aber das hat Probleme, zumindest mit SQLITE und den @TableGenerator Annotations zum automatischen Erstellen von IDs  
				//bReturn = fillMap_createNew_ENTITYMANAGER_EXAMPLE(objContextHibernate, panelMap);
			}else{
				bReturn = true;
			}
		}//end main:		
		return bReturn;
	}
	
	//20170316: Steuere über die DAO-Klassen
	private boolean fillMap_readCreated(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
							
			//ACHTUNG NICHT LOESCHEN: Diese DAO Methoden funktionieren. und es war kompliziert genug hinzubekommen.
			//20170316: Steuere das über die DAO-Klassen
			//WENN MAN über die Liste der AreaCells geht (wg. Performance) dann braucht man dies nicht.
	//		AreaCellDao daoAreaCell = new AreaCellDao(objContextHibernate);			
	//		int iMaxMapX = daoAreaCell.computeMaxMapX(); 
	//		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": iMaxMapX = " + iMaxMapX);
	//				
	//		int iMaxMapY = daoAreaCell.computeMaxMapY(); 
	//		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": iMaxMapY = " + iMaxMapY);
	//		
	//		
	//		//WENN Die Anzahl der Zellen in der Datenbank leer ist, dann diese neu aufbauen/füllen
	//		if(iMaxMapX<=0 || iMaxMapY <=0){
	//			bBuildNew = true;
	//		}else{
	//			bBuildNew = false;
	//			this.setColumnMax(iMaxMapX);
	//			this.setRowMax(iMaxMapY);
	//		}
			
			
			/* ACHTUNG PERFORMANCEPROBLEM
			 * LÖSUNGSANSATZ ALLES AUF EINMAL HOLEN....
			 * STATT HIER DIE CellID aufzubauen und dann Zelle für Zelle zu suchen......
			 * 
			 * The most inefficient way is to do it by accessing the corresponding property and trigger the lazy loading. There are some really big drawbacks:
	
	    Imagine what happen if you need to retrieve multiple level of data.
	    If the result set is going to be big, then you are issuing n+1 SQLs to DB.
	
	The more proper way is to try to fetch all related data in one query (or a few).
	
	Just give an example using Spring-data like syntax (should be intuitive enough to port to handcraft Hibernate Repository/DAO):
	
	interface GroupRepository {
	    @Query("from Group")
	    List<Group> findAll();
	
	    @Query("from Group g left join fetch g.users")
	    List<Group> findAllWithUsers();
	}
	
	Join fetching is equally simple in Criteria API (though seems only left join is available), quoted from Hibernate doc:
	
	List cats = session.createCriteria(Cat.class)
	    .add( Restrictions.like("name", "Fritz%") )
	    .setFetchMode("mate", FetchMode.EAGER)
	    .setFetchMode("kittens", FetchMode.EAGER)
	    .list();
			 */
			
		//Steuerung über DAO - Klassen
		AreaCellDao daoAreaCell = new AreaCellDao(objContextHibernate);	
			
		//Es soll performanter sein erst die ganze Liste zu holen (wg. Lazy), statt Über die ID jede Zelle einzeln.
		List<AreaCell>listAreaCell = daoAreaCell.findLazyAll(0, -1);
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Anzahl gefundener HexCells = " + listAreaCell.size());
		
		AreaCellTHM objCellThmTemp; //Die in der Datenbank gefundenen Zelle in das UI bringen
		int iNrOfHexes=0;
		int iNrOfHexesX=-1;
		int iNrOfHexesY=-1;
		for(AreaCell objCell : listAreaCell){
			//+++ Ohne eine weitere Abfrage "zählen" was die maximale Anzahl Zeilen / Spalten ist			
			int iX = objCell.getMapX();
			Integer intX = new Integer(iX);			
			String sX = intX.toString();
			if(iX>iNrOfHexesX) iNrOfHexesX = iX; //Auch ein Weg die Kartenbreite zu ermitteln, ohne eine zusätzliche Abfrage zu starten.
						
			int iY = objCell.getMapY();
			Integer intY = new Integer(iY);
			String sY = intY.toString();
			if(iY>iNrOfHexesY) iNrOfHexesY = iY; //Auch ein Weg die Kartenhöhe zu ermitteln, ohne eine zusätzliche Abfrage zu starten.
									
			//+++ Das Passende "Panel" erzeugen und in die Karte packen.
			String sHexTypeSub = objCell.getAreaType(); //LA= LAND, OC=OCEAN       //AR=Area objCell.getHexType();
			if(sHexTypeSub.equalsIgnoreCase("OC")){
				AreaCellOcean objCellTemp = (AreaCellOcean) objCell;			
				objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Das sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Gebäudeinfos/Armeeinfos
	
			/*TODO Hintergrundbild
			 *  ImageIcon background = new ImageIcon("Water.png");
				objCellTemp.setIcon(background);
			 */
				
				//TODO: Die Zelle soll ein eigenes Layout bekommen, das die Spielsteine automatisch anordnet.
				//objCellThmTemp.setLayout(null);
				//20180903 Das Layout ist nun wichtig, damit die Spielsteine beim Zoomen nicht in ihrer Position verrutschen.
				//BoxLayout layoutBox = new BoxLayout(objCellThmTemp, BoxLayout.X_AXIS);
				//objCellThmTemp.setLayout(layoutBox);
				
				//TODO GOON 2018-09-03: Versuch mit einem eigenen CellLayout die Spielsteine auch richtig zu setzen, wenn gezoomt wird.
				HexagonalCellLayoutTHM layoutHexCell = new HexagonalCellLayoutTHM(this.getKernelObject(), objCellThmTemp);
				objCellThmTemp.setLayout(layoutHexCell);
				
				//Am MoveEventBroker registrieren
				objTileMoveEventBroker.addListenerTileMoved(objCellThmTemp);
				
				//20130630: Hier am MetaEventBroker registrieren
				objTileMetaEventBroker.addListenerTileMeta(objCellThmTemp);
				
				//###############
				//Die Zelle in eine HashMap packen, die für´s UI verwendet wird				
				hmCell.put(sX, sY, objCellThmTemp);
	
				iNrOfHexes++; //Zelle zur Summe hinzufügen		
			}else if(sHexTypeSub.equalsIgnoreCase("LA")){
				AreaCellLand objCellTemp = (AreaCellLand) objCell;			
				objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
				
				/*TODO Hintergrundbild. Das muss natürlich abhängt vom Typ der Zelle sein.
				 *  ImageIcon background = new ImageIcon("Grass.png");
					objCeThmlTemp.setIcon(background);
				 */
				
				//20180903: Die Zelle bekommt ein eigenes Layout, das die Spielsteine automatisch anordnet.
				//                 Damit wird der Spielstein auch richtig in der Karte gesetzt zu setzen, wenn durch Zoomen sie Areas kleiner werden.
				//objCellThmTemp.setLayout(null);
				HexagonalCellLayoutTHM layoutHexCell = new HexagonalCellLayoutTHM(this.getKernelObject(), objCellThmTemp);
				objCellThmTemp.setLayout(layoutHexCell);
							
				//Am MoveEventBroker registrieren
				objTileMoveEventBroker.addListenerTileMoved(objCellThmTemp);
				
				//20130630: Hier am MetaEventBroker registrieren
				objTileMetaEventBroker.addListenerTileMeta(objCellThmTemp);
				
				//###############
				//Die Zelle in eine HashMap packen, die für´s UI verwendet wird		
				hmCell.put(sX, sY, objCellThmTemp);
	
				iNrOfHexes++; //Zelle zur Summe hinzufügen		
			}else{
				String sError = "Unbekanntes Geländeobjekt. '" + sHexTypeSub + "'";
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": " +sError);
				ExceptionZZZ ez = new ExceptionZZZ(sError, iERROR_PARAMETER_VALUE, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
				throw ez;
			}		
		}// end for AREA
		this.iNrOfHexes = iNrOfHexes;  //Angabe ist wichtig für das Layout. s. HexagonalLayoutTHM.preferredLayoutSize(...) -> ...computeSize(...). Ohne das gibt es keine Scrolleisten.
		this.setColumnMax(iNrOfHexesX);
		this.setRowMax(iNrOfHexesY);
		
		//TODO GOON: nun Liste aller Tiles (FLEET / ARMY) holen. Die Idee ist, das dies schneller ist, als bei jeder Zelle abzufragen "gibt es eine Armee/Flotte".
		//Dann ausgehend von der Armee/Flotte die Zelle ermitteln.
		/*
		 * TroopFleet objTroopTemp = new TroopFleet(new TileId("EINS", "1", "FLEET UNIQUE " + sY));
						//momentan wird noch ein BLOB gespeichert. ERst mal die LID in der HEXCell generieren lassen objTroopTemp.setHexCell(objCellTemp); //wg. 1:1 Beziehung
						//TODO GOON: Die Validierung auf ein gültiges Feld in einen Event der Persistierung packen, siehe Buch..... Dannn kann man auch wieer richtig das falsche Hinzufügen testen 
						
						FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
						
						EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
						objTileMetaEventBroker.fireEvent(objEventTileCreated);
						
		 */
		
		//Steuerung über DAO - Klassen
		TroopDao daoTroop = new TroopDao(objContextHibernate);	
			
		//Es soll performanter sein erst die ganze Liste zu holen (wg. Lazy), statt Über die ID jede Zelle einzeln.
		List<Troop>listTroop = daoTroop.findLazyAll(0, -1);
		System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Anzahl gefundener Truppen = " + listTroop.size());
	
		for(Troop objTroop : listTroop){
			String sTroopTypeSub = objTroop.getTroopType();
			if(sTroopTypeSub.equalsIgnoreCase("AR")){
				TroopArmy objTroopTemp = (TroopArmy) objTroop;	
				
				int iX = objTroopTemp.getMapX();
				Integer intX = new Integer(iX);			
				String sX = intX.toString();
				
				int iY = objTroopTemp.getMapY();
				Integer intY = new Integer(iY);
				String sY = intY.toString();	
				
				String sUniquename = objTroopTemp.getUniquename();//Die Backend-Funkion heisst genauso wie die UI-Funktion, die verwndet wird, um das UI-Objekt mit dem Backend zu identifizieren.
				
				
				//+++ UI Operationen & die TroopArmy noch an das UI-verwendete Objekt weitergeben	
				//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
				//FGL 20171011: Ersetzt durch eine Factory - Klasse
				//TileDtoFactory factoryTile = new TileDtoFactory();
				
				//FGL: 20171012: Nun hole die Factory "generisch"
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
				GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
				
				TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);
				objTroopDaoFacade.fillTroopArmyDto((TroopArmy) objTroop, dto);				
				//dto.set(ITileDtoAttribute.UNIQUENAME, sUniquename);				
				//dto.set(ITileDtoAttribute.VARIANT_SHORTTEXT, "NEUTEST");
				
				ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
				EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objArmyTemp, 1, sX, sY);
				objTileMetaEventBroker.fireEvent(objEventTileCreated);
	
			}else if(sTroopTypeSub.equalsIgnoreCase("FL")){
				TroopFleet objTroopTemp = (TroopFleet) objTroop;			
				
				int iX = objTroopTemp.getMapX();
				Integer intX = new Integer(iX);			
				String sX = intX.toString();
				
				int iY = objTroopTemp.getMapY();
				Integer intY = new Integer(iY);
				String sY = intY.toString();	
				
				String sUniquename = objTroopTemp.getUniquename();//Die Backend-Funkion heisst genauso wie die UI-Funktion, die verwndet wird, um das UI-Objekt mit dem Backend zu identifizieren.
				
				
				//+++ UI Operationen & die TroopFleet noch an das UI-verwendete Objekt weitergeben	
				//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
				//FGL 20171011: Ersetzt durch eine Factory - Klasse
//				TileDtoFactory factoryTile = new TileDtoFactory();				
//				GenericDTO dto = factoryTile.createDTO();	
				
				//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
				DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
				GenericDTO dto = objFactoryGenerator.createDtoForClass(FleetTileTHM.class);
							
				TroopFleetDaoFacade objTroopDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
				objTroopDaoFacade.fillTroopFleetDto((TroopFleet) objTroop, dto);				
				//dto.set(ITileDtoAttribute.UNIQUENAME, sUniquename);
				//dto.set(ITileDtoAttribute.VARIANT_SHORTTEXT, "NEUTEST");
				
				FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
				EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
				objTileMetaEventBroker.fireEvent(objEventTileCreated);
				
			} else{
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Unbekannter Truppentyp = " + sTroopTypeSub);				
			}
		} // end for TROOP (FLEET / ARMY)
		
		//################################################################################################
		if(iNrOfHexes<=0){
			bReturn = false;// ist ja noch nix ausgelesen worden, warum auch immer a) Datenbank existiert ohne Inhalt, b) Keine Objekte für den Schlüssel gefunden.
		}else{
			bReturn = true;
		}
	}//main:
	return bReturn;
}
	
	
	
	//20170316: Steuere über die DAO-Klassen
		private boolean fillMap_readCreated_IMPERFORMANT_EXAMPLE(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				int iNrOfHexes = 0;
				int iNrOfHexesX = 0;	
				int iNrOfHexesY = 0;
				boolean bBuildNew = true;
							
			//ACHTUNG NICHT LOESCHEN: Diese DAO Methoden funktionieren. und es war kompliziert genug hinzubekommen.
			//20170316: Steuere das über die DAO-Klassen
			//WENN MAN über die Liste der AreaCells geht (wg. Performance) dann braucht man dies nicht.
			AreaCellDao daoAreaCell = new AreaCellDao(objContextHibernate);			
			int iMaxMapX = daoAreaCell.computeMaxMapX(); 
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": iMaxMapX = " + iMaxMapX);
					
			int iMaxMapY = daoAreaCell.computeMaxMapY(); 
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": iMaxMapY = " + iMaxMapY);
						
			//WENN Die Anzahl der Zellen in der Datenbank leer ist, dann diese neu aufbauen/füllen
			if(iMaxMapX<=0 || iMaxMapY <=0){
				bBuildNew = true;
			}else{
				bBuildNew = false;
				this.setColumnMax(iMaxMapX);
				this.setRowMax(iMaxMapY);
			}
			
			/* ACHTUNG PERFORMANCEPROBLEM
			 * LÖSUNGSANSATZ ALLES AUF EINMAL HOLEN....
			 * STATT HIER DIE CellID aufzubauen und dann Zelle für Zelle zu suchen......
			 * 
			 * The most inefficient way is to do it by accessing the corresponding property and trigger the lazy loading. There are some really big drawbacks:

	    Imagine what happen if you need to retrieve multiple level of data.
	    If the result set is going to be big, then you are issuing n+1 SQLs to DB.

	The more proper way is to try to fetch all related data in one query (or a few).

	Just give an example using Spring-data like syntax (should be intuitive enough to port to handcraft Hibernate Repository/DAO):

	interface GroupRepository {
	    @Query("from Group")
	    List<Group> findAll();

	    @Query("from Group g left join fetch g.users")
	    List<Group> findAllWithUsers();
	}

	Join fetching is equally simple in Criteria API (though seems only left join is available), quoted from Hibernate doc:

	List cats = session.createCriteria(Cat.class)
	    .add( Restrictions.like("name", "Fritz%") )
	    .setFetchMode("mate", FetchMode.EAGER)
	    .setFetchMode("kittens", FetchMode.EAGER)
	    .list();
			 */
		
			
			if(!bBuildNew){
			//Zwei verschachtelte Schleifen, Aussen: Solange wie es "Provinzen" gibt...
			//                                                  Innen:   von 1 bis maximaleSpaltenanzahl...
			int iY = 0;
			do{//die maximale Zeilenzahl ist noch hart verdrahtet, soll sich aber später automatisch ergeben.....
			iY++;
			Integer intY = new Integer(iY);
			String sY = intY.toString();
			
			for(int iX=1; iX <= this.getColumnMax(); iX++){
				Integer intX = new Integer(iX);				
				String sX = intX.toString();
				
				//################			
				//20170319: Nun wird eine "Landschaft ausgelesen und anschliessend gebaut"   HexCellTHM objCellTemp = new HexCellTHM(this.getKernelObject(), panelMap,  sX, sY, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
				AreaCellTHM objCellThmTemp; //Die Zelle für das UI
				
				CellId primaryKey = new CellId("EINS", sX, sY);//Die vorhandenen Schlüssel Klasse
				AreaCell objCellTemp = daoAreaCell.findByKey(primaryKey);			
				if(objCellTemp==null){
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Zelle mit x/Y in Tabelle EINS NICHT gefunden (" + sX + "/" + sY + ")");					
				}else{
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Zelle mit x/Y in Tabelle EINS GEFUNDEN (" + sX + "/" + sY + ")");
					
					//Die in der Datenbank gefundenen Zelle in das UI bringen
					objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Das sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Gebäudeinfos/Armeeinfos
					/*TODO Hintergrundbild. Das muss natürlich abhängt vom Typ der Zelle sein.
					 *  ImageIcon background = new ImageIcon("Grass.png");
	    				objCellTemp.setIcon(background);
					 */
					
					
					//TODO: Die Zelle soll ein eigenes Layout bekommen, das die Spielsteine automatisch anordnet.
					objCellThmTemp.setLayout(null);
					//20180903 Das Layout ist nun wichtig, damit die Spielsteine beim Zoomen nicht in ihrer Position verrutschen.
//					BoxLayout layoutBox = new BoxLayout(objCellThmTemp, BoxLayout.X_AXIS);
//					objCellThmTemp.setLayout(layoutBox);
					
//					HexagonalCellLayoutTHM layoutHexCell = new HexagonalCellLayoutTHM(this.getKernelObject(), objCellThmTemp);
//					objCellThmTemp.setLayout(layoutHexCell);
					
					//Am MoveEventBroker registrieren
					objTileMoveEventBroker.addListenerTileMoved(objCellThmTemp);
					
					//20130630: Hier am MetaEventBroker registrieren
					objTileMetaEventBroker.addListenerTileMeta(objCellThmTemp);
					
					//###############
					  //Die Zelle in eine HashMap packen, die für´s UI verwendet wird				
					  hmCell.put(sX, sY, objCellThmTemp);
					  
					  iNrOfHexesX++;
					  iNrOfHexes++; //Zelle zur Summe hinzufügen		
					
				}
						
				
			}//End for iX
			iNrOfHexesY++;
			}while(iY< this.getRowMax());
			
			
			this.setColumnMax(iNrOfHexesX);
			this.setRowMax(iNrOfHexesY);
			}//end if !bBuildNew
			
			if(iNrOfHexes<=0){
				bReturn = false;// ist ja noch nix ausgelesen worden, warum auch immer a) Datenbank existiert ohne Inhalt, b) Keine Objekte für den Schlüssel gefunden.
			}else{
				bReturn = true;
			}
			}//main:
			return bReturn;
	}
	
	private boolean fillMap_readCreated_ENTITYMANAGER_EXAMPLE_IMPERFORMANTER_ANSATZ(HibernateContextProviderSingletonTHM objContextHibernate, EntityManager em, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			int iNrOfHexes = 0;	
			
			
		//BEACHTE: VERWENDE HIER DEN ENTITY MANAGER
		String sQueryTemp = "SELECT MAX(c.mapX) FROM HexCell c";
		Query objQuery = em.createQuery(sQueryTemp);
		Object objSingle =objQuery.getSingleResult();
		if(objSingle!=null){
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Objekt als Single Result der Query " + objSingle.hashCode());
		}else{
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": NULL Objekt als Single Result der Query " + sQueryTemp);
		}
		
		List objResult = objQuery.getResultList();
		
		//TODO: WENN Die Anzahl der Zellen in der Datenbank leer ist, dann diese neu aufbauen/füllen
		for(Object obj : objResult){
			if(obj==null){										
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": NULL Objekt in ResultList der Query " + sQueryTemp);
				bReturn = false;
			}else{					
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Gefundenes Objekt obj.class= " + obj.getClass().getName());
				System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Wert im Objekt ist = " + obj.toString());
				bReturn = false;
			}
		}
		
		
		
		
		//Zwei verschachtelte Schleifen, Aussen: Solange wie es "Provinzen" gibt...
		//                                                  Innen:   von 1 bis maximaleSpaltenanzahl...
		int iY = 0;
		do{//die maximale Zeilenzahl ist noch hart verdrahtet, soll sich aber später automatisch ergeben.....
		iY++;
		Integer intY = new Integer(iY);
		String sY = intY.toString();
		
		for(int iX=1; iX <= this.getColumnMax(); iX++){
			Integer intX = new Integer(iX);				
			String sX = intX.toString();
			
		}//End for iX
		}while(iY< this.getRowMax());
		
		bReturn = false;// ist ja noch nix ausgelesen worden
		
		}//main:
		return bReturn;
}
	
	private boolean fillMap_createNew(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			/*++++++++++++++
			//Hibernate Beispiel für einfaches Erzeugen der Entities
			SessionFactory sf = HibernateUtil.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();

			Person person = new Person("Steve", "Balmer");
			session.save(person);

			Employee employee = new Employee("James", "Gosling", "Marketing", new Date());
			session.save(employee);

			Owner owner = new Owner("Bill", "Gates", 300L, 20L);
			session.save(owner);
					
			session.getTransaction().commit();
			session.close();
			*/			
			
			
			
			bReturn = fillMap_createNewAreas(objContextHibernate, panelMap);
			if(bReturn){
				bReturn = fillMap_createNewTiles(objContextHibernate, panelMap);
			}
		}//end main
		return bReturn;		
	}
	private boolean fillMap_createNewAreas(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{
			int iNrOfHexes = 0;
			
			Session session = objContextHibernate.getSession(); 
									
			//Zwei verschachtelte Schleifen, Aussen: Solange wie es "Provinzen" gibt...
			//                                                  Innen:   von 1 bis maximaleSpaltenanzahl...
			int iY = 0;
			int iRowMax = this.getRowMax();
			int iColumnMax = this.getColumnMax(); 
			do{
			iY++;
			Integer intY = new Integer(iY);
			String sY = intY.toString();
			
			for(int iX=1; iX <= iColumnMax; iX++){
				Integer intX = new Integer(iX);				
				String sX = intX.toString();
				
				session.beginTransaction(); //Ein zu persistierendes Objekt ==> eine Transaction, auch wenn mehrere in einer Transaction abzuhandeln wären, aber besser um Fehler abfangen zu können.
				
				
				//FGL 20081004 nun wird eine "Landschaft gebaut"   HexCellTHM objCellTemp = new HexCellTHM(this.getKernelObject(), panelMap,  sX, sY, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
				AreaCellTHM objCellThmTemp;
				AreaCell objCellTemp;
				if((sX.equals("5") & sY.equals("5")) | (sX.equals("4") & sY.equals("5")) | (sX.equals("4") & sY.equals("6")) | (sX.equals("4") & sY.equals("7"))  ){
					//OZEAN					
					//Aretype nun als ENUMERATION objCellTemp = new AreaCell(new CellId("EINS", sX, sY), AreaTypeTHM.OZEAN);
					objCellTemp = new AreaCellOcean(new CellId("EINS", sX, sY));
					
					//TODO: Streng genommen müsste die Erstellung des UI hiervon getrennt werden.... So wie ich es wg. der falschen Platzierung von Armeen/Flotten im UI vorhabe
					objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Das sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Gebäudeinfos/Armeeinfos
					/*TODO Hintergrundbild
					 *  ImageIcon background = new ImageIcon("Water.png");
        				objCellTemp.setIcon(background);
					 */
				}else{
					//LAND
					objCellTemp = new AreaCellLand(new CellId("EINS", sX, sY));
					
					//TODO: Streng genommen müsste die Erstellung des UI hiervon getrennt werden.... So wie ich es wg. der falschen Platzierung von Armeen/Flotten im UI vorhabe
					objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
					/*TODO Hintergrundbild
					 *  ImageIcon background = new ImageIcon("Grass.png");
        				objCellTemp.setIcon(background);
					 */					
				}
				
				//TODO: Die Zelle soll ein eigenes Layout bekommen, das die Spielsteine automatisch anordnet.
				objCellThmTemp.setLayout(null);
				//20180903 Das Layout ist nun wichtig, damit die Spielsteine beim Zoomen nicht in ihrer Position verrutschen.
//				BoxLayout layoutBox = new BoxLayout(objCellThmTemp, BoxLayout.X_AXIS);
//				objCellThmTemp.setLayout(layoutBox);
				
				HexagonalCellLayoutTHM layoutHexCell = new HexagonalCellLayoutTHM(this.getKernelObject(), objCellThmTemp);
				objCellThmTemp.setLayout(layoutHexCell);
				
				//Am MoveEventBroker registrieren
				this.getTileMoveEventBroker().addListenerTileMoved(objCellThmTemp);
				
				//20130630: Hier am MetaEventBroker registrieren
				this.getTileMetaEventBroker().addListenerTileMeta(objCellThmTemp);
				

				//TEST: Ausgabe eines zusammengesetzten Wertes, der wg @Transient NICHT in der Datenbank persistiert werden soll
				//System.out.println("Erstellt wurde ein Feld mit dem Alias: " + objCellTemp.getFieldAlias());
				//Liefert folgende Losausgabe auf der Console: DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: component[sMapAlias,sMapX,sMapY]{sMapX=30, sMapY=20, sMapAlias=EINS}, using strategy: org.hibernate.id.CompositeNestedGeneratedValueGenerator
			
				//Hibernate: Die Zelle in die Datenbank packen, in der sie persistiert wird
				session.save(objCellTemp);
					
                //Die Zelle in eine HashMap packen, die für´s UI verwendet wird				
				hmCell.put(sX, sY, objCellThmTemp);
				iNrOfHexes++; //Zelle zur Summe hinzufügen
					
				
				//TODO TEST: FALSCH ist es eine Zelle zu erzeugen, die es schon gibt.
//					AreaCell objCellTest = new AreaCell(new CellId("EINS", "5", "5"), AreaType.LAND);
//					AreaCellTHM objAreaTest = new  AreaCellTHM(this.getKernelObject(), this,  objCellTest, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
//					session.save(objCellTest); 
					//Erfolgreich ist der Test, wenn er folgende Fehlermelung liefert: 
					//Exception in thread "main" org.hibernate.NonUniqueObjectException: a different object with the same identifier value was already associated with the session: [tryout.hibernate.AreaCell#tryout.hibernate.CellId@2079d3]
					
					//Werte endgültig in die Datenbank übernehmen, per Hibernate
					//Merke: Fehler "Caused by: java.sql.SQLException: [SQLITE_CONSTRAINT]  Abort due to constraint violation (columns mapAlias, mapX, mapY are not unique)"
					//           Hier wird versucht ein NEUES Objekt mit dem gleichen Schlüsselwerten in die Tabelle zu schreiben. 
					//           Daher sollte man vorher prüfen, ob es nicht schon solch ein Objekt gibt.
					session.getTransaction().commit();										
				}//End for iX
			}while(iY< iRowMax);
			
			//FGL 20170415: Die Session nicht schliessen, da man auch noch die Spielsteine platzieren muss
			//session.close();	
			this.iNrOfHexes = iNrOfHexes;//Angabe ist wichtig für das Layout. s. HexagonalLayoutTHM.preferredLayoutSize(...) -> ...computeSize(...). Ohne das gibt es keine Scrolleisten.
			if(iNrOfHexes >= 1) bReturn = true;
		}//end main
		return bReturn;
	}
	private boolean fillMap_createNewTiles(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		boolean bReturn = false;
		main:{			
			//###################
			//Hole das passende TroopVariant-Objekt
			//###################
		   			
			//TODO GOON 20181103:
			//20181103: Verwende eine Factory, um die passende Dao-Klasse zu holen und daraus dann das passende Varianten-Objekt.
		    TroopVariantDaoFactory objDaoVariantFactory = TroopVariantDaoFactory.getInstance();
		    
		    long lngTroopArmyVariant_Thiskeyid = 11; //"Infanterie". TODO GOON 20180311: Aus dem GhostDropEvent (via GhostpictureAdapter) die im PANEL_WEST ausgewählte Variante holen.						
//		    TroopArmyVariantDao daoKeyArmy = new TroopArmyVariantDao(objContextHibernate);
//			TroopArmyVariant objTroopArmyVariant = (TroopArmyVariant) daoKeyArmy.searchKey("TROOPARMYVARIANT", lngTroopArmyVariant_Thiskeyid );					   
			TroopVariantDao daoKey = objDaoVariantFactory.createDaoVariant(lngTroopArmyVariant_Thiskeyid);
			TroopArmyVariant objTroopArmyVariant = (TroopArmyVariant) daoKey.searchKey();
			
			 long lngTroopFleetVariant_Thiskeyid = 21; //"Destroyer". TODO GOON 20180311: Aus dem GhostDropEvent (via GhostpictureAdapter) die im PANEL_WEST ausgewählte Variante holen.			
//			TroopFleetVariantDao daoKeyFleet = new TroopFleetVariantDao(objContextHibernate);
//			TroopFleetVariant objTroopFleetVariant = (TroopFleetVariant) daoKeyFleet.searchKey("TROOPFLEETVARIANT", lngTroopFleetVariant_Thiskeyid );
			daoKey = objDaoVariantFactory.createDaoVariant(lngTroopFleetVariant_Thiskeyid);			
			TroopFleetVariant objTroopFleetVariant = (TroopFleetVariant) daoKey.searchKey();
					
			
			//##################
			//Hole die Areas
			//###################
			
			//Nun hatte es sich gezeigt, dass es wesentlich schneller ist, alle Provinzen auf einen Schlag abzufragen, statt jede Provinz per CellId zu suchen.
			//Steuerung über DAO - Klassen
			AreaCellDao daoAreaCell = new AreaCellDao(objContextHibernate);	
				
			//Es soll performanter sein erst die ganze Liste zu holen (wg. Lazy), statt Über die ID jede Zelle einzeln.
			List<AreaCell>listAreaCell = daoAreaCell.findLazyAll(0, -1);
			System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Anzahl gefundener HexCells = " + listAreaCell.size());
			//vielleicht so die initaialisierung der "bag" möglich .... Das war ein Versuch wg. "Database is locked" Fehler... daoAreaCell.setSession(null);
			
			
			/* !!! Alte Version, nicht Löschen: So werden Truppen ohne einen Event hinzugefügt	
			//TEST: TRUPPEN Komponente(n) in eine bestimmte Zelle hinzufügen
			if(sX.equals("1") && sY.equals("1")){
				objCellTemp.setOpaque(false);
				//TEST, MUSS DAS SEIN ? Nein, nicht notwendig. Die Steuerung/Auswahl der Komponentent funktioniert reibungslos, auch wenn ander Komponenten dar�ber liegen.  objCellTemp.setJComponentContentDraggable(false);  //Also: Wenn andere "draggable Componenten" darin sind, dann hiermit das Fenster nicht ziehen.
				
				
				//TileTHM objTileTemp= new TileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
				//FGL 2008-10-04: Nun Truppen positionieren
				
				ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
				
				objCellTemp.add(objArmyTemp);
			}else if(sX.equals("5")&& sY.equals("5")){
				objCellTemp.setOpaque(false);
				
				FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
				
				objCellTemp.add(objFleetTemp);
			}*/
			
			Session session = null; 
			int iNrOfTiles = 0;
			
			//Für die Tests auf falsche Platzierung der Spielsteine untenstehende Variablen auf true setzen
			boolean bUseTestArea = true;
			boolean bUseTestOccupied = true;
		
			for(AreaCell objCellTemp : listAreaCell){			
				CellId objId = objCellTemp.getId();			
				String sX = objId.getMapX();
				String sY = objId.getMapY();
								
				//Anfangsaufstellung: TRUPPEN Komponente in einer Datenbank persistieren und in einer bestimmten Zelle per Event hinzufügen 			
				if((sX.equals("1") && sY.equals("2"))  | (sX.equals("1") && sY.equals("3")) ){
						boolean bGoon = false;
						//20170629: Dies in eine Methode kapseln (DAO Klasse?). Ziel: Dies ohne Redundanz beim Einsetzen eines neuen Spielsteins verwenden.
						TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);
						String sUniquename = objTroopDaoFacade.computeUniquename();//Merke: Zur Berechnung des uniquename wird hier ein Zeitstempel verwendet
																							
						bGoon = objTroopDaoFacade.insertTroopArmy(sUniquename, objTroopArmyVariant, objCellTemp);
						if(bGoon){
														
							//+++ UI Operationen & die TroopArmy noch an das UI-verwendete Objekt weitergeben
							//20170711: Nimm hier die UniqueId des Backends mit auf....
							//20170711: Der Uniquename muss der im UI-verwendeten Objekt übergeben werden. Das ist dann die Verbindung zwischen UI und Backend.
							//               Diese Verbindung zwischen UI und Backend wird wichtig, wenn der Spielstein weiterbewegt wird und diese Bewegung im Backend verzeichnet werden soll,
							//                sprich ein Update gemacht werden soll.
							
							//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
							//FGL 20171011: Ersetzt durch eine Factory - Klasse
							//TileDtoFactory factoryTile = new TileDtoFactory();

							//20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
							objTroopDaoFacade.fillTroopArmyDto(sUniquename, dto);
							
							ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
							
							EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objArmyTemp, 1, sX, sY);
							this.getTileMetaEventBroker().fireEvent(objEventTileCreated);						
							
							iNrOfTiles++;
						}else{
							String sMessage = "UNERWARTET: HIER SOLLTE EINE ARMEE ERZEUGT WERDEN.... .";		
							sMessage = sMessage + StringZZZ.crlf() + objTroopDaoFacade.getFacadeResult().getMessage(); //Hole die Meldung ab.																
							JOptionPane.showMessageDialog (panelMap, sMessage);//TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
						}
				}else if((sX.equals("5") && sY.equals("5")) | (sX.equals("4") && sY.equals("6"))){
					boolean bGoon = false;
					//20170629: Dies in eine Methode kapseln (DAO Klasse?). Ziel: Dies ohne Redundanz beim Einsetzen eines neuen Spielsteins verwenden.
					TroopFleetDaoFacade objFleetDaoFacade = new TroopFleetDaoFacade(objContextHibernate);					
					String sUniquename = objFleetDaoFacade.computeUniquename();//Merke: Zur Berechnung des uniquename wird hier ein Zeitstempel verwendet
					bGoon = objFleetDaoFacade.insertTroopFleet(sUniquename, objTroopFleetVariant, objCellTemp);
					if(bGoon){
						
						//20170711: Nimm hier die UniqueId des Backends mit auf....
						//20170711: Der Uniquename muss der im UI-verwendeten Objekt übergeben werden. Das ist dann die Verbindung zwischen UI und Backend.
						//               Diese Verbindung zwischen UI und Backend wird wichtig, wenn der Spielstein weiterbewegt wird und diese Bewegung im Backend verzeichnet werden soll,
						//               sprich ein Update gemacht werden soll.
						
						
						//+++ UI Operationen & die TroopFleet noch an das UI-verwendete Objekt weitergeben	
						//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
						//FGL 20171011: Ersetzt durch eine Factory - Klasse
						//TileDtoFactory factoryTile = new TileDtoFactory();
						//GenericDTO dto = factoryTile.createDTO();
						
						//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
						DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
						GenericDTO dto = objFactoryGenerator.createDtoForClass(FleetTileTHM.class);
						objFleetDaoFacade.fillTroopFleetDto(sUniquename, dto);

						//FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sUniquename, sX, sY, this.getSideLength());
						FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
						
						EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
						this.getTileMetaEventBroker().fireEvent(objEventTileCreated);	
						
						iNrOfTiles++;						
					}else{
						//TEST: DIESER CODE DARF NICHT AUSGEFÜHRT WERDEN, DER onPreInsert-Listener sollte die Erzeugung erlauben.
						String sMessage = "UNERWARTET: HIER SOLLTE EINE FLOTTE ERZEUGT WERDEN.... .";		
						sMessage = sMessage + StringZZZ.crlf() + objFleetDaoFacade.getFacadeResult().getMessage(); //Hole die Meldung ab.						
						JOptionPane.showMessageDialog (panelMap, sMessage); //TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
					}
																														
				}
				
				if (bUseTestArea && sX.equals("1") && sY.equals("5")){	//Das nicht in einen else---Fall packen, sonst wird dieser Fall nicht ausgeführt.
					//TEST: FALSCHES PLATZIEREN DER FLOTTEN Komponente in eine bestimmte LAND Zelle per Event hinzufügen					
					boolean bGoon = false;			
					
					//20170629: Dies in eine Methode kapseln (DAO Klasse?). Ziel: Dies ohne Redundanz beim Einsetzen eines neuen Spielsteins verwenden.
					TroopFleetDaoFacade objFleetDaoFacade = new TroopFleetDaoFacade(objContextHibernate);
					String sUniquename = "FLEET TEST PLACE ON LAND " + sY;//TODO GOON : sY als Uniquename zu verwenden ist nur heuristisch und nicht wirklich UNIQUE
					bGoon = objFleetDaoFacade.insertTroopFleet(sUniquename, objTroopFleetVariant, objCellTemp);										
					if(bGoon){
						//TEST: DIESER CODE DARF NICHT AUSGEFÜHRT WERDEN, DER onPreInsert-Listener muss das verhindert haben und zurückgeliefert haben.
						String sMessage = "UNERWARTETES TESTERGEBNIS: HIER DARF NIX ERZEUGT WERDEN, WG. PASSENDES GEBIET REGEL. SOLLTE ABER IM BACKEND SCHON ABGEFANGEN WRODEN SEIN!";			
						JOptionPane.showMessageDialog (panelMap, sMessage);
							
					}else{					
						//Mache nun eine Ausgabe, wie sonst in AreaCellTHM.onTileCreated(EventTileCreatedInCellTHM) 		
						String sMessage = "ERWARTETES TESTERGEBNIS: " + objFleetDaoFacade.getFacadeResult().getMessage(); //Hole die Meldung ab.						
						JOptionPane.showMessageDialog (panelMap, sMessage);//TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
					}					
				}																
				
				if(bUseTestOccupied && sX.equals("1") && sY.equals("3")){	//Das nicht in einen else---Fall packen, sonst wird dieser Fall ggfs. nicht ausgeführt.					
					//TEST: FALSCHES PLATZIEREN DER TRUPPEN Komponente in einer bestimmten Zelle (Vom richtigen Geländetyp), die aber schon BESETZT ist per Event hinzufügen
					boolean bGoon = false;	

					//20170629: Dies in eine Methode kapseln (DAO Klasse?). Ziel: Dies ohne Redundanz beim Einsetzen eines neuen Spielsteins verwenden.
					TroopArmyDaoFacade objTroopDaoFacade = new TroopArmyDaoFacade(objContextHibernate);
					String sUniquename = "ARMY TEST OCCUPIED FIELD " + sY;//TODO GOON : sY als Uniquename zu verwenden ist nur heuristisch und nicht wirklich UNIQUE																				
					bGoon = objTroopDaoFacade.insertTroopArmy(sUniquename, objTroopArmyVariant, objCellTemp);
					if(bGoon){
						//TEST: DIESER CODE DARF NICHT AUSGEFÜHRT WERDEN, DER onPreInsert-Listener muss das verhindern.
						String sMessage = "FALSCHES TESTERGEBNIS: HIER DARF NIX ERZEUGT WERDEN, WG. STACKING-LIMIT REGEL.";		
						JOptionPane.showMessageDialog (panelMap, sMessage);
																		
					}else{						
						
						//Mache nun eine Ausgabe, wie sonst in AreaCellTHM.onTileCreated(EventTileCreatedInCellTHM)
						String sMessage = "ERWARTETES TESTERGEBNIS: " + objTroopDaoFacade.getFacadeResult().getMessage(); //Hole die Meldung ab.
						JOptionPane.showMessageDialog (panelMap, sMessage);//TODO GOON: Eigentlich hier nicht ausgeben, sondern das Ergebnis für irgendwelche Frontend-Klassen zur Verfügung stellen, die dann ggfs. auch eine UI Komponente haben.
					}											
				}//end iif
			}//end for
			if(session!= null) session.close();	
			if(iNrOfTiles >= 1) bReturn = true;
		}//end main:
		return bReturn;
	}
	
	/** Anhand der in fillMap() hinzugefügten Zellen und der Anzahl der Zellen pro Zeile, kann die Gesamtanzahl der Zeilen berechnet werden.
	* @return
	* 
	* lindhaueradmin; 12.09.2008 12:21:46
	 */
	public int getRowTotal(){
		int iReturn = 0;
		int iNumberOfCellsTotal = this.getTotalNumberOfCell();
		int iNumberOfCellsInRow = this.getColumnMax();
		iReturn = HexMapTHM.getRowTotal(iNumberOfCellsTotal, iNumberOfCellsInRow);
		return iReturn;
	}
	
	/** Berechnet die Gesamtzahl der Zeilen.
	* @param iNumberOfCellsTotal
	* @param iNumberOfCellsInRow
	* @return
	* 
	* lindhaueradmin; 12.09.2008 12:22:54
	 */
	public static int getRowTotal(int iNumberOfCellsTotal, int iNumberOfCellsInRow){
		int iReturn = 0;
		if(iNumberOfCellsTotal<=iNumberOfCellsInRow){
			iReturn = 1;
		}else{
			double dtemp = iNumberOfCellsTotal/iNumberOfCellsInRow;
			iReturn = (int) dtemp;
		}
		return iReturn;
	}
	
	
	//#### GETTER / SETTER
	public void setPanelParent(KernelJPanelCascadedZZZ panelParent){
		this.panelParent = panelParent;
	}
	public KernelJPanelCascadedZZZ getPanelParent(){
		return this.panelParent;
	}

	public TileMetaEventBrokerTHM getTileMetaEventBroker() {
		return this.objTileMetaEventBroker;
	}

	public void setTileMetaEventBroker(
			TileMetaEventBrokerTHM objTileMetaEventBroker) {
		this.objTileMetaEventBroker = objTileMetaEventBroker;	
	}

	public TileMoveEventBrokerTHM getTileMoveEventBroker() {
		return this.objTileMoveEventBroker;
	}

	public void setTileMoveEventBroker(
			TileMoveEventBrokerTHM objTileMoveEventBroker) {
		this.objTileMoveEventBroker = objTileMoveEventBroker;
	}

	private boolean fillMap_createNew_ENTITYMANAGER_EXAMPLE(HibernateContextProviderSingletonTHM objContextHibernate, KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
			boolean bReturn = false;
			main:{
				int iNrOfHexes = 0;
				
				//+++ Vorbereiten der Wertübergabe an die Datenbank
				
				//Hibernate Weg: 
				//Session session = objContextHibernate.getSession();			
				//session.beginTransaction();
				
				//JPA Weg über den EntityManager
				String sSchemaName = "TileHexMap03";//das kommt aus META-INF\persistence.xml
				EntityManager em = objContextHibernate.getEntityManager(sSchemaName);
				em.getTransaction().begin();
						
//			    ABER: Es scheint mit JPA Probleme mit den Table-Generatoren zu geben.
//			    Fehlermeldung beim em.persist(...) ist: SQL error or missing database (no such table: COMMON_FUER_IDGENERATOR_ASSOCIATION)
//				
				
				//Für die Tests auf falsche Platzierung der Spielsteine untenstehende Variablen auf true setzen
				boolean bUseTestArea = true;
				boolean bUseTestOccupied = true;
				
				//Zwei verschachtelte Schleifen, Aussen: Solange wie es "Provinzen" gibt...
				//                                                  Innen:   von 1 bis maximaleSpaltenanzahl...
				int iY = 0;
				do{//die maximale Zeilenzahl ist noch hart verdrahtet, soll sich aber später automatisch ergeben.....
				iY++;
				Integer intY = new Integer(iY);
				String sY = intY.toString();
				
				for(int iX=1; iX <= this.getColumnMax(); iX++){
					Integer intX = new Integer(iX);				
					String sX = intX.toString();
					
					//FGL 20081004 nun wird eine "Landschaft gebaut"   HexCellTHM objCellTemp = new HexCellTHM(this.getKernelObject(), panelMap,  sX, sY, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
					AreaCellTHM objCellThmTemp;
					AreaCell objCellTemp;
					if((sX.equals("5") & sY.equals("5")) | (sX.equals("4") & sY.equals("5")) | (sX.equals("4") & sY.equals("6")) | (sX.equals("4") & sY.equals("7"))  ){
						//OZEAN					
						//Aretype nun als ENUMERATION objCellTemp = new AreaCell(new CellId("EINS", sX, sY), AreaTypeTHM.OZEAN);
						objCellTemp = new AreaCellOcean(new CellId("EINS", sX, sY));
						objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Das sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Gebäudeinfos/Armeeinfos
						/*TODO Hintergrundbild
						 *  ImageIcon background = new ImageIcon("Water.png");
	        				objCellTemp.setIcon(background);
						 */
					}else{
						//LAND
						objCellTemp = new AreaCellLand(new CellId("EINS", sX, sY));
						objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
						/*TODO Hintergrundbild
						 *  ImageIcon background = new ImageIcon("Grass.png");
	        				objCellTemp.setIcon(background);
						 */
						
	
					}
					
					//TODO: Die Zelle soll ein eigenes Layout bekommen, das die Spielsteine automatisch anordnet.
					objCellThmTemp.setLayout(null);
					//20180903 Das Layout ist nun wichtig, damit die Spielsteine beim Zoomen nicht in ihrer Position verrutschen.
//					BoxLayout layoutBox = new BoxLayout(objCellThmTemp, BoxLayout.X_AXIS);
//					objCellThmTemp.setLayout(layoutBox);
					
//					HexagonalCellLayoutTHM layoutHexCell = new HexagonalCellLayoutTHM(this.getKernelObject(), objCellThmTemp);
//					objCellThmTemp.setLayout(layoutHexCell);
					
					//Am MoveEventBroker registrieren
					objTileMoveEventBroker.addListenerTileMoved(objCellThmTemp);
					
					//20130630: Hier am MetaEventBroker registrieren
					objTileMetaEventBroker.addListenerTileMeta(objCellThmTemp);
					
				
					
				/* !!! Alte Version, nicht Löschen: So werden Truppen ohne einen Event hinzugefügt	
				//TEST: TRUPPEN Komponente(n) in eine bestimmte Zelle hinzufügen
				if(sX.equals("1") && sY.equals("1")){
					objCellTemp.setOpaque(false);
					//TEST, MUSS DAS SEIN ? Nein, nicht notwendig. Die Steuerung/Auswahl der Komponentent funktioniert reibungslos, auch wenn ander Komponenten dar�ber liegen.  objCellTemp.setJComponentContentDraggable(false);  //Also: Wenn andere "draggable Componenten" darin sind, dann hiermit das Fenster nicht ziehen.
					
					
					//TileTHM objTileTemp= new TileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
					//FGL 2008-10-04: Nun Truppen positionieren
					
					ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
					
					objCellTemp.add(objArmyTemp);
				}else if(sX.equals("5")&& sY.equals("5")){
					objCellTemp.setOpaque(false);
					
					FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
					
					objCellTemp.add(objFleetTemp);
				}*/
				
					//TEST: Ausgabe eines zusammengesetzten Wertes, der wg @Transient NICHT in der Datenbank persistiert werden soll
					//System.out.println("Erstellt wurde ein Feld mit dem Alias: " + objCellTemp.getFieldAlias());
					//Liefert folgende Losausgabe auf der Console: DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: component[sMapAlias,sMapX,sMapY]{sMapX=30, sMapY=20, sMapAlias=EINS}, using strategy: org.hibernate.id.CompositeNestedGeneratedValueGenerator
				
						//JPA: Die Zelle in die Datenbank packen, in der sie persistiert wird
						//Hibernate, mit Session session.save(objCellTemp);
					   //JPA:
					   em.persist(objCellTemp);
						
	                    //Die Zelle in eine HashMap packen, die für´s UI verwendet wird				
						hmCell.put(sX, sY, objCellThmTemp);
						iNrOfHexes++; //Zelle zur Summe hinzufügen
						
						//TEST: FALSCHES PLATZIEREN DER TRUPPEN Komponente in einer bestimmten Zelle per Event hinzufügen
						if(bUseTestArea && sX.equals("1") && sY.equals("2")){
							String sUniquename ="FLEET UNIQUE " + sY;
							
							//+++ Datenbankoperationen
							TroopFleet objTroopTemp = new TroopFleet(new TileId( "EINS", "1", sUniquename));					
							//TODO GOON 20170407: Die Validierung auf ein gültiges Feld in einen Event der Persistierung packen, siehe Buch..... Dannn kann man auch wieer richtig das falsche Hinzufügen testen 
							objTroopTemp.setHexCell(objCellTemp); //wg. 1:1 Beziehung
							//TODO GOON 20170407: Hier müsste dann ein Fehler kommen, damit der folgende Code nicht ausgeführt wird......
							objCellTemp.getTileBag().add(objTroopTemp);//Füge diese Flotte der HexCell hinzu //wg. 1:n Beziehung
						
							//Hibernate: session.save(objTroopTemp);
							//JPA:
							em.persist(objTroopTemp);		
							
							//+++ UI Operationen & die TroopFleet noch an das UI-verwendete Objekt weitergeben	
							//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
							//FGL 20171011: Ersetzt durch eine Factory - Klasse
//							TileDtoFactory factoryTile = new TileDtoFactory();
//							GenericDTO dto = factoryTile.createDTO();	
							
							//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator - Komfortfunktion
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
							
							dto.set(ITileDtoAttribute.UNIQUENAME, sUniquename);
							//FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sUniquename, sX, sY, this.getSideLength());
							FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
							
							EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
							objTileMetaEventBroker.fireEvent(objEventTileCreated);
							
							
						}
						
						//Anfangsaufstellung: TRUPPEN Komponente in einer Datenbank persistieren und in einer bestimmten Zelle per Event hinzufügen 
						if(sX.equals("1") && sY.equals("2")  | (sX.equals("1") & sY.equals("3")) | (sX.equals("1") & sY.equals("4"))){
							String sUniquename = "ARMY UNIQUE " + sY;
							
							//+++ Datenbankoperationen
							TroopArmy objTroopTemp = new TroopArmy(new TileId("EINS", "1", sUniquename));//TODO GOON : sY als Uniquename zu verwenden ist nur heuristisch und nicht wirklich UNIQUE
							objTroopTemp.setHexCell(objCellTemp); //Füge Zelle der Trupppe hinzu, wg. 1:1 Beziehung
							objCellTemp.getTileBag().add(objTroopTemp); //Füge diese Army der HexCell hinzu //wg. 1:n Beziehung
							
							//Hibernate: session.save(objTroopTemp);
							//JPA:
							em.persist(objTroopTemp);
							
							//+++ UI Operationen & die TroopArmy noch an das UI-verwendete Objekt weitergeben
							//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
							//FGL 20171011: Ersetzt durch eine Factory - Klasse
//							TileDtoFactory factoryTile = new TileDtoFactory();
//							GenericDTO dto = factoryTile.createDTO();	
							
							//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
							
							dto.set(ITileDtoAttribute.UNIQUENAME, sUniquename);
							//ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, sUniquename, sX, sY, this.getSideLength());
							ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
							
							EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objArmyTemp, 1, sX, sY);
							objTileMetaEventBroker.fireEvent(objEventTileCreated);
							
						
							
						}else if(sX.equals("5")&& sY.equals("5")){
							String sUniquename = "FLEET UNIQUE " + sY;
							
							//+++ Datenbankoperationen
							TroopFleet objTroopTemp = new TroopFleet(new TileId("EINS", "1", sUniquename));
							objTroopTemp.setHexCell(objCellTemp); //wg. 1:1 Beziehung					
							objCellTemp.getTileBag().add(objTroopTemp);//Füge diese Flotte der HexCell hinzu //wg. 1:n Beziehung
							
							//Hibernate: session.save(objTroopTemp);
							em.persist(objTroopTemp);
							
							//+++ UI Operationen & die TroopFleet noch an das UI-verwendete Objekt weitergeben	
							//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
							//FGL 20171011: Ersetzt durch eine Factory - Klasse
//							TileDtoFactory factoryTile = new TileDtoFactory();
//							GenericDTO dto = factoryTile.createDTO();	
//							
							//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
							
							dto.set(ITileDtoAttribute.UNIQUENAME, sUniquename);
							//FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sUniquename, sX, sY, this.getSideLength());
							FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
							
							EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
							objTileMetaEventBroker.fireEvent(objEventTileCreated);
							
							
						}
						
						//TEST: FALSCHES PLATZIEREN DER TRUPPEN Komponente in einer bestimmten Zelle, die schon besetzt ist per Event hinzufügen
						if(bUseTestOccupied && sX.equals("1") && sY.equals("2")){
							String sUniquename = "ARMY UNIQUE " + sY;											
							
							//+++ Datenbankoperationen
							TroopArmy objTroopTemp = new TroopArmy(new TileId("EINS", "1",sUniquename));
							//TODO GOON 20170407: Die Validierung auf ein gültiges Feld in einen Event der Persistierung packen, siehe Buch..... Dannn kann man auch wieder richtig das falsche Hinzufügen testen 
							objTroopTemp.setHexCell(objCellTemp); //wg. 1:1 Beziehung	
							//TODO GOON 20170407: Hier müsste dann ein Fehler kommen, damit der folgende Code nicht ausgeführt wird......
							objCellTemp.getTileBag().add(objTroopTemp);//Füge diese Flotte der HexCell hinzu //wg. 1:n Beziehung
							
							//Hibernate: session.save(objTroopTemp);
							/*versuch, ob bei anderer Persistierung der Listener ausgeführt wird.*/
							em.persist(objCellTemp);
							em.persist(objTroopTemp);											
							
							
			
							
							//+++ UI Operationen & die TroopArmy noch an das UI-verwendete Objekt weitergeben
							//GenericDTO dto = GenericDTO.getInstance(ITileDtoAttribute.class);
							//FGL 20171011: Ersetzt durch eine Factory - Klasse
//							TileDtoFactory factoryTile = new TileDtoFactory();
//							GenericDTO dto = factoryTile.createDTO();
							
							//FGL 20171112: Hole die Factory - Klasse generisch per FactoryGenerator:
							DtoFactoryGenerator objFactoryGenerator = DtoFactoryGenerator.getInstance();
							GenericDTO dto = objFactoryGenerator.createDtoForClass(ArmyTileTHM.class);
							
							dto.set(ITileDtoAttribute.UNIQUENAME, sUniquename);
							//ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, sUniquename, sX, sY, this.getSideLength());
							ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, dto, sX, sY, this.getSideLength());
							
							EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objArmyTemp, 1, sX, sY);
							objTileMetaEventBroker.fireEvent(objEventTileCreated);
							
							
						}
						
						
					}//End for iX
				}while(iY< this.getRowMax());
				
				
				//TEST: FALSCH ist es eine Zelle zu erzeugen, die es schon gibt.
	//			AreaCell objCellTest = new AreaCell(new CellId("EINS", "5", "5"), AreaType.LAND);
	//			AreaCellTHM objAreaTest = new  AreaCellTHM(this.getKernelObject(), this,  objCellTest, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
	//			session.save(objCellTest); 
				//Erfolgreich ist der Test, wenn er folgende Fehlermelung liefert: 
				//Exception in thread "main" org.hibernate.NonUniqueObjectException: a different object with the same identifier value was already associated with the session: [tryout.hibernate.AreaCell#tryout.hibernate.CellId@2079d3]
				
				//Werte endgültig in die Datenbank übernehmen, per Hibernate
				//Merke: Fehler "Caused by: java.sql.SQLException: [SQLITE_CONSTRAINT]  Abort due to constraint violation (columns mapAlias, mapX, mapY are not unique)"
				//           Hier wird versucht ein NEUES Objekt mit dem gleichen Schlüsselwerten in die Tabelle zu schreiben. 
				//           Daher sollte man vorher prüfen, ob es nicht schon solch ein Objekt gibt.
				
				//HIBERNATE
				//session.getTransaction().commit();
				//session.close();
				
				//JPA:
				em.getTransaction().commit();
				em.close();
				
				this.iNrOfHexes = iNrOfHexes;
			}//end main:
			return bReturn;
		}
}
