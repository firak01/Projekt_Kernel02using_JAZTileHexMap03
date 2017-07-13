package use.thm.client.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import use.thm.IMapFixedTHM;
import use.thm.IMapPositionableTHM;
import use.thm.client.event.EventCellAffectedTHM;
import use.thm.client.event.EventCellEnteredTHM;
import use.thm.client.event.EventCellLeavedTHM;
import use.thm.client.event.EventTileDroppedToCellTHM;
import use.thm.client.event.IListenerTileMovedTHM;
import use.thm.persistence.model.HexCell;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zBasic.util.math.MathZZZ;
import basic.zKernel.KernelZZZ;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

/** Klasse für eine Sechseck-Zelle. Definiert als Swing Komponente
 *  20131210: Nun mit JPA-Annotations versehen, da die Informationen in einer SQL Datenbank gespeichert werden sollen.
 *  
 * Interface IMapPositionable sorgt dafür, dass X/Y Koordinaten vorhanden sind
 * Interface IListenerTileMovedTHM sogt daf�r, dass eine Zelle auf die Bewegung der Spielfigur (Tile) reagieren kann.  (Die eigentlichen Event werden von einem EventBroker verschickt, an dem sich die Zelle anmelden muss)
 * @author lindhaueradmin
 *
 */

//TODO GOON: Die für die HexCelle als POJO Objekt benötigten Felder in einer eigenen Klasse auslagern und das Objekt hier übenehmen!!!
//Fehlermeldung, wenn nicht alle Eigenschaften einer Java Klasse mit JPA-Annotations versehen sind:
//Exception in thread "main" org.hibernate.MappingException: Could not determine type for: use.thm.client.component.HexMapTHM, at table: HEXCELL, for columns: [org.hibernate.mapping.Column(mapParent)]

public class HexCellTHM extends KernelJPanelCascadedZZZ implements IMapFixedTHM, IListenerTileMovedTHM{  //Probleme mit dem Ziehen des ganzen Frames, wenn hier der FramePArent nicht bekannt ist.
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Folgendes Objekt wird in der SQL Datenbank persistiert
	private HexCell objHexCell;
	
	 //Folgende Properties sind nicht für das Persistieren in einer Datenbank relevant
	 private HexMapTHM mapParent = null;     //Die Karte, in welche diese Zelle eingebettet ist. Dar�ber bekommt man dann das panelMap (d.h. das KernelJPanelCascadedZZZ, in das diese Zelle engebettet ist.)
	 private Point[] paNeighbourCellAll = null;     //Die Koordianten aller unmittelbaren Nachbarzellen
	
	 
	 private int iSideLength = 0;  //Das ist die Kantenl�nge des gleichseitigen Sechsecks

	 private int iXLeftUpperCorner = 0; //Wichtig f�r das Zeichnen des Sechsecks
	 private int iYLeftUpperCorner = 0;
	 private Polygon po = null;    //Das, was als Sechseck gezeichnet wird
	 
	 //Event-Interface
	 EventCellAffectedTHM objEventPrevious;
	 
	 //Effekte werden beim Zeichnen der Komponente ber�cksichtig
	 //boolean bFlagEffectDragTile=false; //beim Drag Drop ins aktuelle Feld (das zuletzt gültige Feld)
	 //boolean bFlagEffectPathTile=false; //Falls Feld im Pfad der Drag Droop Bewegung ist
	//20130721: Flags werden nun als Enum hier hinterlegt.
		public enum FLAGZ{
			EFFECT_DRAG_TILE, EFFECT_PATH_TILE;
		}
		
	/**Default Konstruktor um per Reflection API - objClass.newInstance() machen zu k�nnen.
	 * 
	 * lindhaueradmin, 23.07.2013
	 */
	public HexCellTHM(){		
	}
	 
		/** Konstruktor
		* lindhaueradmin; 11.09.2008 09:20:22
		 * @param iSideLength: Die Seitenlänge des Hexagons. Sie ist grundlage für alle anderen mathematisch zu berechneneden Punkte.
		 * @throws ExceptionZZZ 
		 */
	 public HexCellTHM(KernelZZZ objKernel, HexMapTHM mapParent, HexCell objHexCell, int iSideLength) throws ExceptionZZZ{
			//super(objKernel, frameParent);
			 super(objKernel, mapParent.getPanelParent());
			
			 this.setHexCellObject(objHexCell);
			this.mapParent = mapParent;
			
			//Die Seitenlänge als Grundlage aller Berechnungen
			this.iSideLength = iSideLength;
			
			//Die Koordinaten, mit denen das Hexagon in die Komponente gezeichnet wird
			this.iXLeftUpperCorner = 0;		this.iYLeftUpperCorner = HexCellTHM.getYUpperLeftCorner(iSideLength);
			
			//Bevorzugte Größe der Komponente. Wird vom Layout-Manager benötigt, um das Panel richtig größenmäßig zu gestalten
			int iWidth = HexCellTHM.getRectWidth(iSideLength) + 1;
			int iHeigth = HexCellTHM.getRectHeight(iSideLength)+1;
			Dimension dim = new Dimension(iWidth, iHeigth);
			this.setPreferredSize(dim);
		}  

	//### FLAGS ####################	
		@Override
		public Class getClassFlagZ() {
			return FLAGZ.class;
		}

	
	/** Die Seitenlänge des Hexagons. Wird im Konstruktor übergeben.
	* @return
	* 
	* lindhaueradmin; 11.09.2008 09:21:12
	 */
	public int getSideLength(){
		return this.iSideLength;
	}
	
	 public Polygon DrawHex(Graphics g)
	 {
		 	int[] xEcke=new int[6];
		 	int[] yEcke=new int[6];
		 	
		 	int x0 = this.iXLeftUpperCorner;
		 	int y0 = this.iYLeftUpperCorner;
		 	xEcke[0] = x0;
		 	yEcke[0] = y0;
		 	
		 	
		 	double xKoord= this.getSideLength()*(Math.cos(Math.PI/6));
		 	double yKoord=this.getSideLength()*(Math.sin(Math.PI/6));
		 	int x1=(int)xKoord;
		 	int y1 =(int)yKoord;
		 	
		 	xEcke[1] = x0+x1-2;//-1 feinsteuerung
		 	yEcke[1] = y0-y1-1; //minus, weil y=0 auf dem Bildschirm nach oben ist //-1 feinsteuereung
		 	
		 	//die Koordinaten der �brigen Ecken sind:
		 	xEcke[2] = x0+2*x1;
		 	yEcke[2] = y0;
		 	
		 	xEcke[3] = x0+2*x1;
		 	yEcke[3] = y0+2*y1;
		 	
		 	xEcke[4] = x0+x1;
		 	yEcke[4] = y0+y1+this.getSideLength();//-1;//-1 feinsteuerung
		 	
		 	xEcke[5] = x0; 
		 	yEcke[5] = y0+this.getSideLength();
		 	
		 	//zeichne das Sechseck
		 	g.setColor(Color.black);
		 	
		 	this.po = new Polygon(xEcke, yEcke, 6);
		 	g.drawPolygon(po);
		 	return po;   //Damit kann dann weiteres gemacht werden, gef�llt etc.
		 	
		 	
		 	
		 	//zu Testzwecken ein Rechteck zeichnen, das angibt wie groß die JComponent ist.
		 	/*
		 	this.setOpaque(true);
		 	Rectangle re = po.getBounds();
		 	g.drawRect((int)re.getX(), (int) re.getY(), re.width, re.height);
		 	*/
		 	// nicht sofrot f�llen, dies soll in den Unterklassen geschehen, wenn z.B. die Geländeart bekannt ist fillPolygon(xEcke, yEcke, 6);
	 }
	 
	 /** Die errechnete horizontale Breite eines auf einer Spitze stehenden gleichseitigen Sechsecks (also von Seite zu Seite).
	* @param iLength
	* @return
	* 
	* lindhaueradmin; 12.09.2008 10:57:27
	 */
	public static int getRectWidth(int iLength){
		 double dtemp =iLength *(Math.cos(Math.PI/6));
		 int iReturn = ((int) dtemp) *2;
		 return iReturn;
	 }
	
	 /** Die errechnete vertikale Breite eines auf einer Spitze stehenden gleichseitigen Sechsecks (also von Spitze zu Spitze).
	* @param iLength
	* @return
	* 
	* lindhaueradmin; 12.09.2008 10:58:16
	 */
	public static int getRectHeight(int iLength){
		 double dtemp = (2* iLength * Math.sin(Math.PI/6));
		 int iReturn = ((int) dtemp ) + iLength;
		 return iReturn;
	 }
	
	
	 /** Um eine Reihe Sechsecke direkt unter eine andere Reihe Sechsecke direct unter eine ander Reihe zu bringen ist ein Versatz notwendig.
	  *   Hier wird ausgerechnet wie gro� der vertikale Versatz ist.
	* @param iLength
	* @return
	* 
	* lindhaueradmin; 12.09.2008 10:59:31
	 */
	public static int getRowHeightOffset(int iLength){
		 double dtemp = iLength * Math.sin(Math.PI/6);
		 int iReturn = (int) dtemp;
		 return iReturn;
	 }
	
	 /** Um eine Reihe Sechsecke direkt unter eine andere Reihe Sechsecke direct unter eine ander Reihe zu bringen ist ein Versatz notwendig.
	  *   Hier wird ausgerechnet wie gro� der horizontale Versatz ist (das ist die halbe Breite des Rechtecks von Seite zu Seite).
	* @param iLength
	* @return
	* 
	* lindhaueradmin; 12.09.2008 10:59:31
	 */
	public static int getRowWidthOffset(int iLength){
		double dtemp = iLength *(Math.cos(Math.PI/6));
		int iReturn = (int) dtemp;
		return iReturn;
	}
	
	 /**Will man ein auf einer Spitze stehenden gleichseitigen Sechseck in ein Quadrat einzeichnen (Wie es z.B. jede JComponent ist),
	  *  so ist das die linke obere Ecke. Ein idealer Startpunkt, um das Polygon zu zeichnen.
	* @param iLength
	* @return
	* 
	* lindhaueradmin; 12.09.2008 11:04:30
	 */
	public static int getYUpperLeftCorner(int iLength){
		 double dtemp = iLength*(Math.sin(Math.PI/6));
		 int iReturn = (int) dtemp;
		 return iReturn;
	 }
	 
	 /** Das Polygon, als graphische Grundlage für das Sechseck.
	  *   In dieser Klasse wird es nur gezeichnet, d.h. mit einem Schwarzen Rand versehen. 
	  *   In Unterklassen soll das wieder aufgeriffen werden k�nnen und so z.B. mit einer anderen Farbe gef�llt werden. 
	* @return
	* lindhaueradmin; 11.09.2008 08:18:51
	 */
	public Polygon getPolygon(){
		 return this.po;
	 }
	
	public void paintComponent(Graphics g){
		Polygon po = DrawHex(g);
		fillDetail(g, po);
	 
		
		setVisible(true);
		setOpaque(false);
	}
	
	/** F�lle Details in die Zelle, 
	 *   dies wird beim paintComponent() aufgerufen.
	 *   
	 *   F�r Drag/Drop Effecte werden dann andere Farben verwendet
	* @param g
	* @param po
	* 
	* lindhaueradmin; 04.10.2008 13:56:32
	 */
	public void fillDetail(Graphics g, Polygon po){
		//Falls per DragDrop eine Spielfigur auf die Zelle gezogen wird
		//Merke: Bei Areas wird mit der AreaEffectTHM-Klasse und deren static - Methoden gearbeitet
	 	//if (this.getFlag("effectDragTile")){
		if (this.getFlagZ(FLAGZ.EFFECT_DRAG_TILE.name())){
	 		g.setColor(Color.LIGHT_GRAY);
	 		g.fillPolygon(po);
	 	}
	 	//if(this.getFlag("effectPathTile")){
		if(this.getFlagZ(FLAGZ.EFFECT_PATH_TILE.name())){
	 		g.setColor(Color.GRAY);
	 		g.fillPolygon(po);
	 	}
	}
	
	
	
	
	//### Interface: IMapPositionable ####################################
	public String getMapX(){
		int itemp = this.getHexCellObject().getMapX();
		return new Integer(itemp).toString();
	}
	public String getMapY(){
		int itemp =  this.getHexCellObject().getMapY();
		return new Integer(itemp).toString();
	}
	
	public KernelJPanelCascadedZZZ getMapPanel() {
		HexMapTHM mapParent = this.getMapParent();
		return mapParent.getPanelParent();
	}

	//### Interface IListenerTileMovedTHM ###############################################
	public EventCellAffectedTHM getEventPrevious() {
		return objEventPrevious;
	}
	public void setEventPrevious(EventCellAffectedTHM eventCellAffected) {
		this.objEventPrevious = eventCellAffected;
	}


	/* (non-Javadoc)
	 * @see use.thm.client.event.IListenerTileMovedTHM#onTileEnter(use.thm.client.event.EventCellEnteredTHM)
	 * 
	 * Setze Flag, mit dem markiert wird, dass der Effekt f�r "aktuelles, g�ltiges Feld" eintreten soll.
	 * Setze den Cursor auf CROSSHAIR
	 */
	public boolean onTileEnter(EventCellEnteredTHM eventCellEntered) {
		//Erst mal herausfinden, ob das die eigene Zelle ist
		if(eventCellEntered.getMapX().equals(this.getMapX()) && eventCellEntered.getMapY().equals(this.getMapY())){
			//this.setFlag("effectpathtile", false);
			try {
				this.setFlagZ(FLAGZ.EFFECT_PATH_TILE.name(), false);
				this.setFlagZ(FLAGZ.EFFECT_DRAG_TILE.name(), true);
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.repaint();
			
			Cursor objCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
			this.setCursor(objCursor);
		}
		return true;
	}

	public boolean onTileLeave(EventCellLeavedTHM eventCellLeaved) {
//		Erst mal herausfinden, ob das die eigene Zelle ist
		if(eventCellLeaved.getMapX().equals(this.getMapX()) && eventCellLeaved.getMapY().equals(this.getMapY())){
			
			try {
				this.setFlagZ(FLAGZ.EFFECT_DRAG_TILE.name(), false);			
				this.setFlagZ(FLAGZ.EFFECT_PATH_TILE.name(), true);
			} catch (ExceptionZZZ e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.repaint();
		}
		return true;
	}


	public boolean onTileDrop(EventTileDroppedToCellTHM eventTileDropped) {
		//Falls das diese Zelle betrifft & Falls es einen Spielstein betrifft, den diese Zelle beobachtet
		boolean bRepaint = false;
		if(this.getFlagZ(FLAGZ.EFFECT_DRAG_TILE.name())==true){
			try {
				this.setFlagZ(FLAGZ.EFFECT_DRAG_TILE.name(), false);
			} catch (ExceptionZZZ e) {				
				e.printStackTrace();
			}
			bRepaint = true;
		}
		if(this.getFlagZ(FLAGZ.EFFECT_PATH_TILE.name()) == true){
			try {
				this.setFlagZ(FLAGZ.EFFECT_PATH_TILE.name(), false);
			} catch (ExceptionZZZ e) {				
				e.printStackTrace();
			}
			bRepaint = true;
		}
		if(bRepaint==true){
			this.repaint();
		}
		return true;
	}
	
	
	/**Setzt das Flag um, das daf�r sorgt, das die Zelle als "Im Drag/Drop(?Bewegung) des Spielsteins �berquert" markiert ist.
	 *  Das passiert z.B. wenn im Ziehen erneut �ber die "Spur des Spielsteins" (TilePath) gezogen wird und so das zuletzt betretene Feld wieder zur�ckgenommen wird. 
	* 
	*  Setze den Cursor wieder auf den DEFAULT_CURSOR
	* lindhaueradmin; 04.10.2008 09:36:41
	 */
	public void removeFromPathTile(){
		try {
			this.setFlagZ(FLAGZ.EFFECT_PATH_TILE.name(), false);
			this.setFlagZ(FLAGZ.EFFECT_DRAG_TILE.name(), false);//Der darf nat�rlich auch nicht auftreten, wenn die Zelle ganz vom Pfad entfernt worden ist.
		} catch (ExceptionZZZ e) {			
			e.printStackTrace();
		}
		this.repaint(); //da diese Zelle ggf. weder eine Zelle ist, die gerade verlassen oder betreten wird, sorgt kein Event daf�r, das neu gezeichnet wird. Also hier neu zeichnen.
		
		Cursor objCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		this.setCursor(objCursor);
	}
	
	public boolean isNeighbourImmediateOf(String sAliasX, String sAliasY){
		boolean bReturn=false;
		main:{
			if(StringZZZ.isEmpty(sAliasX) || StringZZZ.isEmpty(sAliasY)) break main;
			
			
			//###	Gleichstand testen:
			String sX = this.getMapX();
			String sY = this.getMapY();
			if(sX.equals(sAliasX) && sY.equals(sAliasY)) break main;  //Gleichstand. D.h. Kein Nachbarfeld
			
			//### Rechnen
			//Momentan funktioniert das nur mit integer-Alias-Werten. Ansonsten muss man mit den Aliasnamen rechnen k�nnen. (Aliasname also einem Wert zuordnen)
			if(!StringZZZ.isNumeric(sAliasX) | !StringZZZ.isNumeric(sAliasY)) break main;

			Integer intX = new Integer(sAliasX);
			int iX = intX.intValue();
			
			Integer intY = new Integer(sAliasY);
			int iY = intY.intValue();
		
			//###################################
			//Hole ein Array aller Nachbarpunkte
			Point p = new Point(iX, iY);
			Point[] paNeighbour = this.getNeighbourAll();
			if(paNeighbour==null)break main;
			
			for(int icount=0; icount<= paNeighbour.length-1; icount++){
				if(paNeighbour[icount].equals(p)){
					bReturn = true;
					break main;
				}
			}
		}//end main
		return bReturn;
	}
	
	/** Gibt - bezogen auf die HexMap - die Koordinaten der Nachbarzellen zur�ck.
	 *   Dabei wird ber�cksichtigt, das es in einer Sechseck-Karte einen Versatz zwischen gerader und ungerager Zeile gibt.
	* @return Point-Array
	* 
	* lindhaueradmin; 05.10.2008 12:32:07
	 */
	public Point[] getNeighbourAll(){
			//Einmal ausrechnen wenn es gebraucht wird und immer wieder verwenden
			if(this.paNeighbourCellAll!=null){
				return this.paNeighbourCellAll;
			}else{
				Point[] pointaReturn = null;
				main:{
					Integer intX = new Integer(this.getMapX());
					int iX = intX.intValue();
					
					Integer intY = new Integer(this.getMapY());
					int iY = intY.intValue();
	
					Point p = new Point(iX, iY);
					Point[]paDelta = HexCellTHM.getNeighbourDeltaAll(p);	//Merke: Die DELTA-Werte unterscheiden sich wegen des Versatzes im Sechseck. Es wird also auf gerade/ungerade Reihe gepr�ft.
					if(paDelta==null) break main;
					
					//Den akutellen Punkt immer um die Delta Werte ver�ndern.
					pointaReturn = 	new Point[paDelta.length];
					for(int icount=0; icount <= paDelta.length-1; icount++){
						pointaReturn[icount] = new Point(iX + (int)paDelta[icount].getX(), iY+ (int)paDelta[icount].getY()) ;
					}
					this.paNeighbourCellAll = pointaReturn;
				}//end main:
			return pointaReturn;
		}
	}
	
	/** Gibt die Delta Wert zur�ck, die zur Ermittlung der Nachbarn verwendet werden.
	 *    Hintergrund ist die Verschiebung der Zellen in einer Sechseckkarte.
	 *    Dabei ist zu beachten, dass sich die Delta-Werte unterscheiden, in Abh�ngigkeit der Reihe, die man betrachtet (gerade oder ungerade Reihennummerierung) 
	* @return
	* 
	* lindhaueradmin; 05.10.2008 12:15:16
	 */
	public static Point[] getNeighbourDeltaAll(Point p){
		Point[] pointaReturn = null;
		main:{
			if(p == null) break main;
			
			pointaReturn = new Point[6];
			int iBaseX;
			if(HexCellTHM.isInRowEven(p)){
				iBaseX=0;
			}else{
				iBaseX=-1; //Die Verschiebung
			}
			pointaReturn[0] = new Point(iBaseX, -1);
			pointaReturn[1] = new Point(iBaseX+1, -1);
			pointaReturn[2] = new Point(-1, 0);
			pointaReturn[3] = new Point(1, 0);
			pointaReturn[4] = new Point(iBaseX, 1);
			pointaReturn[5] = new Point(iBaseX+1, 1);
		}//end main
		return pointaReturn;
	}
	
	/** Enthält diese Zelle schon einen Spielstein?
	 * 
	 */
	public boolean isOccupiedByAnyTile(){
		boolean bReturn = false;
		main:{
			Component[] objaComponent = this.getComponents();
			System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " #### Die Zelle enthält " + objaComponent.length + " Objekte. ");
			if(objaComponent.length>=1){
				int icount = 0;
				for(Component objComponent : objaComponent){
					icount++;
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " #### " + icount +". Name: " + objComponent.getName());
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " #### " + icount +". Klasse: " + objComponent.getClass().getName());
				}
				bReturn = true;
				break main;
				
			}
		}
		return bReturn;
	}
	
	/** Gerade Zeile oder nicht (bezogen auf Integer-Alias-Wert).
	 *    Diese Untersuchung ist z.B. wichtig f�r die Ermittlung der Nachbarzellen.
	* @return
	* 
	* lindhaueradmin; 05.10.2008 12:07:56
	 */
	public boolean isInRowEven(){
		boolean bReturn;
		main:{
			Integer intY = new Integer(this.getMapY());
			int iY = intY.intValue();
			
			bReturn = MathZZZ.isEven(iY);
		}
		return bReturn;
	}
	
	/** Ist die durch den Punkt angegebene Zelle (der HexMap-Koordinaten) in einer geraden oder in einer ungeraden Reihe.
	 *	   Diese Untersuchung ist z.B. wichtig für die Ermittlung der Nachbarzellen.
	* @param p
	* @return
	* 
	* lindhaueradmin; 05.10.2008 12:21:38
	 */
	public static boolean isInRowEven(Point p){
		boolean bReturn=false;
		main:{
			if(p == null) break main;
			
			int iY = (int) p.getY();
			bReturn = MathZZZ.isEven(iY);
		}
		return bReturn;
	}

	//#### GETTER / SETTER
	public HexMapTHM getMapParent(){
		return this.mapParent;
	}
	public void setMapParent(HexMapTHM mapParent){
		this.mapParent = mapParent;
	}
	
	public HexCell getHexCellObject(){
		return this.objHexCell;
	}
	public void setHexCellObject(HexCell objHexCell){
		this.objHexCell = objHexCell;
	}
}
