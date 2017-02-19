package use.thm.client.component;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.hibernate.Session;

import tryout.hibernate.AreaCell;
import tryout.hibernate.AreaType;
import tryout.hibernate.CellId;
import use.thm.ITileEventUserTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.client.event.TileMetaEventBrokerTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.client.hibernate.HibernateContextProviderTHM;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.abstractList.HashMapMultiZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasicUI.glassPane.dragDropTranslucent.GhostDropListener;
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
		
	public HexMapTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, int iColumnMax, int iRowMax, int iSideLength) throws ExceptionZZZ{
		super(objKernel);
		if(panelParent==null){
			ExceptionZZZ ez = new ExceptionZZZ("No ParentPanel provided", iERROR_PARAMETER_MISSING, this.getClass().getName(), ReflectCodeZZZ.getMethodCurrentName());
			throw ez;
		}
		 		
		this.iRowMax = iRowMax;
		this.iColumnMax = iColumnMax;
		this.iSideLength = iSideLength;
		this.setPanelParent(panelParent);
		fillMap();
	}
	
	/** Anzahl der Hexes in einer Zeile. 
	 * Wird im Konstruktor �bergeben
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
	
	/** Seitenlänge der Hexes. Dient als Mathematische Grundlage für alle Berechnungen.
	 * Wird im Konstruktorübergeben.
	* @return
	* 
	* lindhaueradmin; 12.09.2008 08:18:06
	 */
	public int getSideLength(){
		if(this.iSideLength<=0){
			return HexMapTHM.constSideLength;
		}else{
			return this.iSideLength;
		}
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
	
	public int fillMap() throws ExceptionZZZ{
		return this.fillMap_(this.getPanelParent());
	}
	
	private int fillMap_(KernelJPanelCascadedZZZ panelMap) throws ExceptionZZZ{
		int iReturn = 0;
		main:{
			HashMapMultiZZZ hmCell = this.getMapCell();
			hmCell.clear();
			this.iNrOfHexes = 0;  //Die gasamtzahl der Hexes wird hochgez�hlt beim F�llen UND IST DER R�CKGABEWERT.
			
			//Kernel Objekt
			KernelZZZ objKernel = this.getKernelObject();
			
			//MoveEventBroker für die Bewegung von einer Zelle zur anderen
			TileMoveEventBrokerTHM objTileMoveEventBroker = new TileMoveEventBrokerTHM(objKernel);
			this.setTileMoveEventBroker(objTileMoveEventBroker);
			
			//20130630: MetaEventBroker f�r Ereignisse wie Erstellen, Vernichten, etc. am Spielstein
			TileMetaEventBrokerTHM objTileMetaEventBroker = new TileMetaEventBrokerTHM(objKernel);
			this.setTileMetaEventBroker(objTileMetaEventBroker);
			
			//TODO GOON: Die MapInformationen und die Informationen für Hexfelder sollen aus einer SQL Tabelle kommen. Das legt dann auch die Größe der Karte fest fest....
			//TODO GOON: Wenn es schon Mapinformationen gibt (ggf. neu "Map Alias" beachten) dann soll die Karten nicht neu aufgebaut, sondern aus der SQL Datenbank ausgelesen werden.
			//Verwende eine Komfortklasse:
			HibernateContextProviderTHM objContextHibernate = new HibernateContextProviderTHM(this.getKernelObject());
			
			//Erzeuge den Entity Manager als Ausgangspunkt für die Abfragen. !!! Damit Hibernate mit JPA funktioniert, braucht man die Datei META-INF\persistence.xml. Darin wird die persistence-unit angegeben.		
			//TODO GOON: Den Namen der Datenbank/des Schemas aus der Kernelkonfiguration holen.
			//EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
			//EntityManager em = objContextHibernate.getEntityManager("c:\\server\\SQLite\\TileHexMap03.sqlite");
			//EntityManager em = objContextHibernate.getEntityManager("jdbc:sqlite:c:\\server\\SQLite\\TileHexMap03.sqlite");
			//EntityManager em = objContextHibernate.getEntityManager("TileHexMap03"); 
			EntityManager em = objContextHibernate.getEntityManager("TileHexMap03");
			//Query objQuery = em.createQuery("SELECT MAX(c.sMapX) FROM HexCell c");//Fehler: could not resolve property: sMapX of: tryout.hibernate.HexCell 
			//Query objQuery = em.createQuery("SELECT MAX(c.MapX) FROM HexCell c");//Fehler: could not resolve property: MapX of: tryout.hibernate.HexCell
			//Query objQuery = em.createQuery("SELECT MAX(c.x) FROM HexCell c");//Fehler: could not resolve property: x of: tryout.hibernate.HexCell
			//Query objQuery = em.createQuery("SELECT MAX(c.X) FROM HexCell c");//Fehler: could not resolve property: X of: tryout.hibernate.HexCell
			
			//TODO: Prüfe die Existenz der Datenbank ab. Ohne die erstellte Datenbank und die Erstellte Datenbanktabelle kommt es hier zu einem Fehler.
			//           Darum muss ich den Code immer erst auskommentieren, nachdem ich die Datenbank gelöscht habe.
			
			//TODO: Mache ein DAO Objekt und dort diesen HQL String hinterlegen.
			//TODO: Anzahl der echten Elemente aus einer noch zu erstellenden Hibernate-ZKernelUtility-Methode holen, sowie eine ResultList OHNE NULL Objekte.
			//String sQueryTemp = "SELECT MAX(c.id.sMapX) FROM HexCell c";
			//um einen Integer Wert zu bekommen die Propert naxh HexCell geholt und nicht mehr über id gehen.
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
				}else{					
					System.out.println(ReflectCodeZZZ.getPositionCurrent() + ": Gefundenes Objekt obj.class= " + obj.getClass().getName());
				}
			}
			
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
			Session session = objContextHibernate.getSession();
			
			//Vorbereiten der Wertübergabe an die Datenbank
			session.beginTransaction();
			
			
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
					objCellTemp = new AreaCell(new CellId("EINS", sX, sY), AreaType.OCEAN);
					objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Das sollen dann erst "Areas" werden, dh. mit Geländeinformationen, Danach "Provinzen" mit Gebäudeinfos/Armeeinfos
					/*TODO Hintergrundbild
					 *  ImageIcon background = new ImageIcon("Water.png");
        				objCellTemp.setIcon(background);
					 */
				}else{
					//LAND
					objCellTemp = new AreaCell(new CellId("EINS", sX, sY), AreaType.LAND);
					objCellThmTemp = new  AreaCellTHM(this.getKernelObject(), this,  objCellTemp, this.getSideLength());  //ToDo: Dass sollen dann erst "Areas" werden, dh. mit Gel�ndeinformationen, Danach "Provinzen" mit Geb�udeinfos/Armeeinfos
					/*TODO Hintergrundbild
					 *  ImageIcon background = new ImageIcon("Grass.png");
        				objCellTemp.setIcon(background);
					 */
					

				}
				
				//TODO: Die Zelle soll ein eigenes Layout bekommen, das die Spielsteine automatisch anordnet.
				objCellThmTemp.setLayout(null);
				
				//Am MoveEventBroker registrieren
				objTileMoveEventBroker.addListenerTileMoved(objCellThmTemp);
				
				//20130630: Hier am MetaEventBroker registrieren
				objTileMetaEventBroker.addListenerTileMeta(objCellThmTemp);
				
			
				
			/* !!! Alte Version, nicht Löschen: So werden Truppen ohne einen Event hinzugef�gt	
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
					session.save(objCellTemp);
					
                    //Die Zelle in eine HashMap packen, die für´s UI verwendet wird				
					hmCell.put(sX, sY, objCellThmTemp);
					iReturn++; //Zelle zur Summe hinzufügen
					
					//TEST: FALSCHES PLATZIEREN DER TRUPPEN Komponente in einer bestimmten Zelle per Event hinzufügen
					boolean bUseTestArea = false;
					if(bUseTestArea && sX.equals("1") && sY.equals("2")){
						FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
						
						EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
						objTileMetaEventBroker.fireEvent(objEventTileCreated);
					}
					
					//Anfangsaufstellung: TRUPPEN Komponente in einer bestimmten Zelle per Event hinzufügen
					//TODO: Die Truppenaufstellung soll wie die Karte auch in einer Tabelle hinterlegt werden. 
					if(sX.equals("1") && sY.equals("2")){
						ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
						
						EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objArmyTemp, 1, sX, sY);
						objTileMetaEventBroker.fireEvent(objEventTileCreated);
					}else if(sX.equals("5")&& sY.equals("5")){
						FleetTileTHM objFleetTemp = new FleetTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
						
						EventTileCreatedInCellTHM objEventTileCreated = new EventTileCreatedInCellTHM(objFleetTemp, 1, sX, sY);
						objTileMetaEventBroker.fireEvent(objEventTileCreated);
					}
					
					//TEST: FALSCHES PLATZIEREN DER TRUPPEN Komponente in einer bestimmten Zelle, die schon besetzt ist per Event hinzuf�gen
					boolean bUseTestOccupied = false;
					if(bUseTestOccupied && sX.equals("1") && sY.equals("2")){
						ArmyTileTHM objArmyTemp = new ArmyTileTHM(panelMap, objTileMoveEventBroker, sX, sY, this.getSideLength());
						
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
			session.getTransaction().commit();
			session.close();
		}
		this.iNrOfHexes = iReturn;
		return iReturn;
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
}
