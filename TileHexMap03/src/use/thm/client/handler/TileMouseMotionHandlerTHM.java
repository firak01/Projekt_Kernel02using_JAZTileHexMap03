package use.thm.client.handler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

import use.thm.client.component.HexCellTHM;
import use.thm.client.component.TileTHM;
import use.thm.client.event.EventCellEnteredTHM;
import use.thm.client.event.EventCellLeavedTHM;
import use.thm.client.event.EventTileDroppedToCellTHM;
import use.thm.client.event.ITileMoveEventBrokerUserTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;

/** Mit dieser Klasse werden alle Maus-Ereignisse für die Spielsteine zusammengfasst
 * Dadurch können gemeinsam genutzte Eigenschaften (z.B. Die Start-Zelle einer DragDrop-Bewegung oder der Spielstein selbst) einfacher verwendet werden.
 * 
 * Das Interface ITileMoveEventBroker ermöglicht es an die an den EventBroker registrierten Komponenten eine Event zu schicken (Zelle wurde betreten, Zelle wurde verlassen) 
 * @author lindhauer
 *
 */
public class TileMouseMotionHandlerTHM extends MouseAdapter implements MouseMotionListener, ITileMoveEventBrokerUserTHM {
	private TileTHM objTile;
	private HexCellTHM objCellStart;
	
	private ArrayList alCellValidEntered = new ArrayList();  //ArrayListe der in diesem DragDrop betretenen Zellen
	private HexCellTHM objCellValidLast;                         //Die zuletzt betretene, gültige Zelle
	private HexCellTHM objCellCurrent;							 //Die zuletzt betretene Zelle, die aber auch nicht gültig zu sein braucht
	
	private TileMoveEventBrokerTHM objTileMoveEventBroker; //Der EventBroker, an den sich alle Zellen registrieren. Wird eine Zelle verlassen/betreten, dann feuert er den entsprechenden Event ab.
	
	public TileMouseMotionHandlerTHM(TileTHM objTile, TileMoveEventBrokerTHM objTileMoveEventBroker){
		this.objTile = objTile;
		this.setSenderUsed(objTileMoveEventBroker);
	}
	
	//### GETTER /SETTER	
	public TileTHM getTile(){
		return this.objTile;
	}
	
	/** Die Zelle, die unter der Spielfigur liegt, beim Starten der Drag/Drop Bewegung
	* @param objCell
	* 
	* lindhauer; 23.09.2008 09:27:27
	 */
	public void setCellStartedFrom(HexCellTHM objCell){
		this.objCellStart = objCell;
	}
	public HexCellTHM getCellStartedFrom(){
		return this.objCellStart;
	}
	
	public HexCellTHM getCellCurrent(){
		return this.objCellCurrent;
	}
	public void setCellCurrent(HexCellTHM objCell){
		this.objCellCurrent = objCell;
	}
	
	public HexCellTHM getCellValidLast(){
		return this.objCellValidLast;
	}
	
	
	/** Setzt die akuelle Zelle, d.h. die Zelle über die sich der Mauszeiger zuletzt während der drag-Aktion befindet
	 *   Fügt diese Zelle der ArrayListe aller Zellen hinzu.
	*   "Verkürzt" den Weg, d.h. entfernt Zellen aus der Arrayliste, wenn eine Zelle wieder betreten wird, die schon einmal im Zug betreten worden ist.
	* @param objCell
	* 
	* lindhaueradmin; 04.10.2008 10:36:02
	 */
	public void setCellValidLast(HexCellTHM objCell){
		this.objCellValidLast = objCell;
		
		// Hinzufügen in die ArrayListe der vorherigen Zellen
		ArrayList alCellEntered = this.getCellValidPreviousAll();
		
		int iNrOfFieldsSpared=doCutShortToCell(objCell); //Merke: Wird nur gemacht, wenn die neue Zelle schon auf dem Weg liegt
		System.out.println("Anzahl eingesparter Felder durch 'abkürzen': " +  iNrOfFieldsSpared);
		alCellEntered.add(objCell);
	}
	
	/** Verkürzt den bisher zurückgelegten Weg bis zu der angegebenen Zelle
	 * 
	* @param objCell, Feld, das auf dem Weg liegen muss
	* @return Anzahl der Felder, die eingespart wurden.
	* 
	* lindhaueradmin; 04.10.2008 10:51:33
	 */
	public int doCutShortToCell(HexCellTHM objCell){
		int iReturn = 0;
		main:{
			ArrayList alCellEntered = this.getCellValidPreviousAll();
			if(! alCellEntered.contains(objCell)) break main;
			
			
	//		+++ TODO als eigenen Methode 
			//In diesem Drag-Drop wurde bereits einmal auf diese Zelle gezogen.
			//==> Die vorher überquerten Felder werden aus der Liste genommen. Diese Züge werden quasi rückgängig gemacht.
			//         Bis zu dem Feld, auf dem man jetzt steht
			
			int iPosLast = alCellEntered.size() - 1; //-1 da der Index mit 0 anfängt
			int iPosNow = alCellEntered.indexOf(objCell); //!!! nicht -1, da es schon ein Index ist
		
			
			//a) erst verbergen
			for(int iCount = iPosLast; iCount >= iPosNow+1; iCount--){
				HexCellTHM objCell2Remove =(HexCellTHM)  alCellEntered.get(iCount);
				objCell2Remove.removeFromPathTile();
			}
			
			//TODO als static Methode in ArrayListExtendedZZZ aufnehmen ArrayListExtendedZZZ.removeLast(ArrayList, iNumberOfElements2Remove);
			//b) nun erst aus der Liste entfernen
			int iNrToRemove = iPosLast - iPosNow;
			for(int iCount = 0; iCount <= iNrToRemove; iCount++){
				int iLast = alCellEntered.size() - 1; //-1 da der Index mit 0 anfängt
				alCellEntered.remove(iLast);
			}
			iReturn = iNrToRemove;
		}//end main:
		return iReturn;
	}
	public ArrayList getCellValidPreviousAll(){
		return alCellValidEntered;
	}
	
	
	//### EVENTS AUS DEM MOUSMOTION LISTENER
	public void mouseDragged(MouseEvent arg0) {
		
		//### Alles was während des Drag/Drop passiert. U.a. beim Wechsel der Zelle feuert der EventBroker seine Benachrichtigung ab
		if(this.getTile().isDragModeStarted()==true){
			//Zugriff auf das MapPanel
			KernelJPanelCascadedZZZ panelMap = this.getTile().getMapPanel();
			
			//TODO ALS EIGENE METHODE  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
//			Zugriff auf die aktuelle Position/Zelle  
			Point pointCursorCurrent = arg0.getPoint();//FGL IDEE: Der Punkt, der hier vorhanden ist, hat die Koordinaten bezogen auf die ZELLE und nicht auf die MAP.
			//===> pointCursorCurrent muss auf MAP-XY Koordinaten umgerechnet werden.
			MouseEvent me = SwingUtilities.convertMouseEvent(this.getTile(), arg0, panelMap);
			pointCursorCurrent = me.getPoint();//FGL IDEE: Der Punkt, der hier vorhanden ist, hat die Koordinaten bezogen auf die ZELLE und nicht auf die MAP.
			
			
			//Nun mit umgerechneten Koordinaten weiterarbeiten
			Component objMapItem = panelMap.getComponentAt(pointCursorCurrent);
			
//			+++++++++++ DEBUG AUSGABE
			String sComponentDetail = "Keine Komponente an der Stelle gefunden.";
			if(objMapItem!=null){
				sComponentDetail = "Klasse: " + objMapItem.getClass().getName();
				//if(objMapItem.getClass().getName().equals(HexCellTHM.class.getName())){
				if(!(objMapItem instanceof HexCellTHM)){
					sComponentDetail = "KEIN GÜLTIGES ZIEL.";
				}else{
					//++++++++++ Aktuelle Zelle setzen
					HexCellTHM objCell = (HexCellTHM) objMapItem;				
					this.setCellCurrent(objCell);
					sComponentDetail = "; X=" + this.getCellCurrent().getMapX() + "/Y=" + this.getCellCurrent().getMapY();
					
					//++++++++++ Falls es eine andere Zelle ist die Events Starten, aber immer das Feedback prüfen
					if(this.getCellValidLast().equals(this.getCellCurrent())){
						
					}else{
						//Erzeuge Events
						EventCellEnteredTHM objEventEntered = new EventCellEnteredTHM(this.getTile(), 1000, this.getCellCurrent().getMapX(), this.getCellCurrent().getMapY());
													
						//EventBroker - Nun hat er über das Verlassen der alten Zelle und das Betreten der neuen Zelle zu informieren
						TileMoveEventBrokerTHM eventBroker = this.getSenderUsed();
						eventBroker.fireEvent(objEventEntered);
						
						//Nun den event - broker fragen, ob der ENTER-Event überall geklappt hat
						if(eventBroker.getContinue()==false){
						}else{
							//Den LEAVE-Event nur, wenn das neue Feld auch betreten werden darf //	Merke: Noch ich die aktuelle Zelle, die alte
							EventCellLeavedTHM objEventLeaved = new EventCellLeavedTHM(this.getTile(), 1001, this.getCellValidLast().getMapX(), this.getCellValidLast().getMapY());
							eventBroker.fireEvent(objEventLeaved);
						
							//Nun den event - broker fragen, ob der LEAVE-Event überall geklappt hat
							if(eventBroker.getContinue()==false){
							}else{
								//Neue aktuelle Zelle setzen (wichtig: das muss hinter den Events passieren)
								this.setCellValidLast(this.getCellCurrent());
							}
						}
					}
				}
			}//end if objMapItem!=null
		}
		
		//### Alles was am Anfang des Drag/Drop passiert
		//Die Startkomponente/Zelle sichern. Sie muss nach dem Ende der Bewegung aktualisiert werden.
		if(this.getTile().isDragModeStarted()==false){ //Dann wurde das Flag noch nicht gesetzt
			
			//Zugriff auf das MapPanel
			KernelJPanelCascadedZZZ panelMap = this.getTile().getMapPanel();

			//TODO ALS EIGENE METHODE  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
			//Zugriff auf die aktuelle Position/Zelle
			Point pointCursorCurrent = arg0.getPoint();//FGL IDEE: Der Punkt, der hier vorhanden ist, hat die Koordinaten bezogen auf die ZELLE und nicht auf die MAP.
			//===> pointCursorCurrent muss auf MAP-XY Koordinaten umgerechnet werden.
			MouseEvent me = SwingUtilities.convertMouseEvent(this.getTile(), arg0, panelMap);
			pointCursorCurrent = me.getPoint();//FGL IDEE: Der Punkt, der hier vorhanden ist, hat die Koordinaten bezogen auf die ZELLE und nicht auf die MAP.
			
			//Nun mit umgerechneten Koordinaten weiterarbeiten
			Component objMapItem = panelMap.getComponentAt(pointCursorCurrent);
			
//			+++++++++++ DEBUG AUSGABE
			String sComponentDetail = "Keine Komponente an der Stelle gefunden.";
			if(objMapItem!=null){
				sComponentDetail = "Klasse: " + objMapItem.getClass().getName();
				if(objMapItem.getClass().getName().equals(HexCellTHM.class.getName())){
					HexCellTHM objCell = (HexCellTHM) objMapItem;
					sComponentDetail = "; X=" + objCell.getMapX() + "/Y=" + objCell.getMapY();
				}else{
					sComponentDetail = "KEIN GÜLTIGES ZIEL.";
				}
			}//end if objMapItem!=null
						
			String sMessage = "Bin über der Komponente: " + sComponentDetail;
			//JOptionPane.showMessageDialog(this.getTile(), sMessage, "Ende der Bewegung...", JOptionPane.INFORMATION_MESSAGE, null);
			System.out.println("Start der Bewegung: " + sMessage);
			//END DEBUG AUSGABE ++++++++++++++++++++++++++++++++++++++
							
			//Die Ausgangszelle speichern
			HexCellTHM objCell = (HexCellTHM) objMapItem;
			this.setCellStartedFrom(objCell);
			
			//Die Ausgangszelle zur letzten gültigen Zelle machen
			this.setCellValidLast(this.getCellStartedFrom());
			
			//Den Modus auf "beim Drag/Drop" ändern.			
			this.getTile().setDragModeStarted(true);  //Dadurch wird beim MaustasteLoslassen - Event alles ausgelöst, was am Ziel passieren soll
		}//end if isDragModeStarted() == false
	}

	public void mouseMoved(MouseEvent arg0) {
		//MERKE DER EVENT WIRD IMMER AUGEFÜHRT, SOBALD SICH DER CURSOR ÜBER DER COMPONENTE BEFINDET....
		System.out.println("ÜBER DER COMPONENTE "+this.getTile().getMapX() + "/" + this.getTile().getMapY());
		Cursor objCursor = new Cursor(Cursor.HAND_CURSOR);		
		this.getTile().setCursor(objCursor);  //Den Cursor zum "IsMovable setzen"
		/*
		// Mauszeiger zum Fadenkreuz ändern, wenn er sich innerhalb eines Rechtecks befindet.
		Rectangle2D objRect = panelCurrent.findSquare(arg0.getPoint());
		if (objRect == null){
			panelCurrent.setCursor((Cursor.getDefaultCursor()));
		}else{
			panelCurrent.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
		*/
	}
	
	

	
//	 ###########Die Events aus dem MOUSEADAPTER #######################
	public void mouseClicked(MouseEvent arg0) {
		
		//TODO: Vom objTile aus kann man auf den TileStore zugreifen.
		//             Daraus können dann Informationen entnommen werden, Truppenstärke, etc.
		
		//Momentan beim Doppelclick nur die Position anzeigen lassen
		int iCount =arg0.getClickCount();
		int iErg = 0; 
		switch(iCount){
		case 1:
			break;
		case 2:
			//TODO: Dies als eigene Dialogbox mit einem Customizbaren Panel, in dem z.B. eine Aktion "Auflösen" eingebaut wird.
			/*
			// +++ Nicht einfach Löschen, sondern mal abfragen
			iErg = JOptionPane.showConfirmDialog(this.getTile(), "Would you realy like to remove this square ?", "Remove square ?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(iErg == JOptionPane.YES_OPTION){
				// Löschen
				panelCurrent.removeSquare(objRect2D);
			}
			*/
			
			//Anzeige der Koordinaten
			Point p = arg0.getPoint();
			String sMessage = "X=" + this.getTile().getMapX() + "; Y=" + this.getTile().getMapY()  + " (" + p.getX() + "/" + p.getY() + ")";
			JOptionPane.showMessageDialog(this.getTile(), sMessage, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, null);
			
			break;
		} //END Swithc
		
		/*
		//aktuelles Quadrat entfernen, wenn Doppelclick
		Rectangle2D objRect2D = panelCurrent.findSquare(arg0.getPoint());
				
		if(objRect2D != null){
			
		
		}
			*/
	}

	public void mouseEntered(MouseEvent arg0) {
		//TODO: In einer Statuszeile die XY-Koordinaten ausgeben
		
		// TODO Auto-generated method stub
		super.mouseEntered(arg0);
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		super.mouseExited(arg0);
	}

	public void mousePressed(MouseEvent arg0){	
		super.mousePressed(arg0);
	
		
		
		/*
		//neues Quadrat hinzufügen, wenn Zeiger nicht in einem Quadrat
		Rectangle2D objRect2D = panelCurrent.findSquare(arg0.getPoint());
		if(objRect2D == null){
			//Das Panel ein neues Square-Objekt hinzufügen lassen
			panelCurrent.addSquare(arg0.getPoint());
		}else{
			//Das "getroffene" Square-Objekt als aktuelles setzen
			panelCurrent.setSquareCurrent(objRect2D);
		}	
		*/	
	}

	public void mouseReleased(MouseEvent arg0) {
		super.mouseReleased(arg0);
				
		//Im MouseDrag des MouseMotionHandlers wird ein Flag gesetzt.
		//Hierüber bekommt man mit, ob die Maustaste losgelassen worden ist "AM ENDE DER DRAG - BEWEGUNG"
		if(this.getTile().isDragModeStarted()){
			this.getTile().setDragModeStarted(false);
			
			//### Spielstein der neuen Komponente hinzufügen und von der alten entfernen #############################
			//Zugriff auf das MapPanel
			KernelJPanelCascadedZZZ panelMap = this.getTile().getMapPanel();
			
			//Zugriff auf die aktuelle Position/Zelle
			Point pointCursorCurrent = arg0.getPoint();//FGL IDEE: Der Punkt, der hier vorhanden ist, hat die Koordinaten bezogen auf die ZELLE und nicht auf die MAP.
			String sMessage4OptionPane = "Start X=" + this.getTile().getMapX() + "; Start Y=" + this.getTile().getMapY()  + " (" + pointCursorCurrent.getX() + "/" + pointCursorCurrent.getY() + ")";
			//JOptionPane.showMessageDialog(this.getTile(), sMessage4OptionPane, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, null);
			
			
			//===> pointCursorCurrent muss auf MAP-XY Koordinaten umgerechnet werden.
			MouseEvent me = SwingUtilities.convertMouseEvent(this.getTile(), arg0, panelMap);
			pointCursorCurrent = me.getPoint();//FGL IDEE: Der Punkt, der hier vorhanden ist, hat die Koordinaten bezogen auf die ZELLE und nicht auf die MAP.
			String sMessage4OptionPane2 = "Start X=" + this.getTile().getMapX() + "; .convertierted MouseEvent... Start Y=" + this.getTile().getMapY()  + " (" + pointCursorCurrent.getX() + "/" + pointCursorCurrent.getY() + ")";
			//JOptionPane.showMessageDialog(this.getTile(), sMessage4OptionPane2, "Detailangaben....", JOptionPane.INFORMATION_MESSAGE, null);
			
			//Nun weiterarbeiten mit umgerechneten Koordinaten
			Component objMapItem = panelMap.getComponentAt(pointCursorCurrent);
		
			
			String sComponentDetail = "Keine Komponente an der Stelle gefunden.";
			if(objMapItem!=null){
				sComponentDetail = "Klasse: " + objMapItem.getClass().getName();
				//if(objMapItem.getClass().getName().equals(HexCellTHM.class.getName())){
				if(!(objMapItem  instanceof HexCellTHM)){
					//+++ Falls es sich um keine Zelle handelt (z.B. den Spielfeldrand)
					sComponentDetail = "KEIN GÜLTIGES ZIEL.";
				}else{
					//+++ Fall: Es handelt sich um eine Zelle, ist sie aber auch für den Spielstein gültig ?
					
					
//					TODO: Für den Spielstein eine "Undo-Möglichkeit anbieten, die durch den Doppelclick auf den Spielstein als Button sichtbar wird. Falls man den Stein versehentlich zu früh losgelassen hat.
					//### Event losschicken, dass der Zug beendet ist
					HexCellTHM objCellCur = this.getCellCurrent();
					EventTileDroppedToCellTHM objEventDropped = new EventTileDroppedToCellTHM(this.getTile(), 1003, objCellCur.getMapX(), objCellCur.getMapY());

					//EventBroker - Nun hat er über das Beenden des Zuges zu informieren
					TileMoveEventBrokerTHM eventBroker = this.getSenderUsed();
					eventBroker.fireEvent(objEventDropped);
					boolean bContinue = eventBroker.getContinue(); //Abfrage, ob eine Komponente etwas dagegen hat.
					//#############################################
					if( !bContinue){ //Also einer Zelle hinzufügen, aber nur, wenn der aufgeworfene Event o.k. war.
						System.out.println("Keine gültiges Zielkomponente, hier darf dieser Spielstein nicht abgesetzt werden.");
					}else{
						//TODO: Als eigene Methode extrahieren
						sComponentDetail += "; X=" + objCellCur.getMapX() + "/Y=" + objCellCur.getMapY();
						
						this.getTile().setMapX(objCellCur.getMapX());
						this.getTile().setMapY(objCellCur.getMapY());
						objCellCur.add(this.getTile());
						
						//Alte komponente neu zeichnen, dort ist ja der Stein weg
						HexCellTHM objCellPrevious = this.getCellStartedFrom();
						if(objCellPrevious!=null){
							objCellPrevious.repaint();
						}
						
						
						
						
						//Neue Komponente neu zeichnen
						//objCellCur.repaint();
						
						
						
						
						//Feedback String "Im Ziel angekommen			
						String sMessage = "Bin über der Komponente: " + sComponentDetail;
						//JOptionPane.showMessageDialog(this.getTile(), sMessage, "Ende der Bewegung...", JOptionPane.INFORMATION_MESSAGE, null);
						System.out.println("Ende der Bewegung: " + sMessage);
					} //bContinue == false
				} //end if objMapItem instanceof HexCellTHM
			}//end if objMapItem!=null
			
//			### Pfad der Felder, die überquert worden sind löschen (d.h. wählt man ein ungültiges Ziel aus, so ist auch die ganze Bewegung hinfällig)
			ArrayList alCellEntered = this.getCellValidPreviousAll();
			for(int iCount =this.getCellValidPreviousAll().size()-1; iCount >= 0; iCount--){
				HexCellTHM objCell2Remove =(HexCellTHM)  alCellEntered.get(iCount);
				objCell2Remove.removeFromPathTile();
			}
			
			//TODO: Für die Undo-Funktionalität an dieser Stelle die Werte in einer anderen ArrayList speichern (falls man einzelne Schritte rückgängig machen will).
			this.getCellValidPreviousAll().clear(); //sonst wird beim Folgezug ggf. eine docCutShort(...) durchgeführt, nur weil hier mal drübergezogen worden ist.
		}
	}
	
	 /* Finds the component corresponding to the given SCREEN-coordinates.
		 We use this method instead of the Swing methods because they do not
always returns the correct component. We use the screen coordinates
to avoid 'rounding' errors 
@author Erwin Wernsen, 
Turnkiek Technical Systems B.V, 
Amersfoort, Holland*/
	public static Component findComponentAt( Container c, Point sp ) {
		 Point cp = new Point( sp.x, sp.y );
	SwingUtilities.convertPointFromScreen( cp, c );
	System.out.println("xScreen=" + sp.x  +"; yScreen=" + sp.y);
	 if (!c.contains( cp.x, cp.y)) {
 		 return c;
}
int ncomponents = c.getComponentCount();
Component component[] = c.getComponents();
for (int i = 0 ; i < ncomponents ; i++) {
 		 Component comp = component[i];
		   Point loc = comp.getLocation();
 		 if ((comp != null) && (comp.contains(cp.x - loc.x, cp.y - loc.y))&&(comp.getPeer() instanceof java.awt.peer.LightweightPeer) && (comp.isVisible() == true)) {
		     // found a component that intersects the point, see if there
		     // is a deeper possibility.
 			 
 		
   		 if (comp instanceof Container) {
		       Container child = (Container) comp;
 		     Component deeper = findComponentAt(child,		 sp );
		       if (deeper != null) {
		 		 return deeper;
		       } 		       
   		 }
   		
else {
     		 return comp;
		     }
 			 return comp;
		   }
}
return c;
}

	
	//### Interface ITileMoveEventBrokerUser
	public TileMoveEventBrokerTHM getSenderUsed() {
		return this.objTileMoveEventBroker;
	}

	public void setSenderUsed(TileMoveEventBrokerTHM objEventSender) {
		this.objTileMoveEventBroker = objEventSender;
	}
	
	//##########################################

	
}//end class
