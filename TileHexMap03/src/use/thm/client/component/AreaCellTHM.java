package use.thm.client.component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.swing.JOptionPane;

import use.thm.client.common.AreaEffectTHM;
import use.thm.client.event.EventCellEnteredTHM;
import use.thm.client.event.EventCellLeavedTHM;
import use.thm.client.event.EventTileCreatedInCellTHM;
import use.thm.client.event.EventTileDroppedToCellTHM;
import use.thm.client.event.IListenerTileMetaTHM;
import use.thm.persistence.model.AreaCell;
import use.thm.persistence.model.AreaCellType;
import basic.zBasic.ExceptionZZZ;
import basic.zBasic.ReflectCodeZZZ;
import basic.zBasic.util.datatype.string.StringZZZ;
import basic.zKernel.KernelZZZ;

/**Eine Zelle, die weitere Spezialisierte Eigenschaften hat, die beim Setzen der Spielsteine zu ber�cksichtigen sind.
 * Z.B. Kann ein Spielstein aufgrund des Gel�ndetyps hier erstellt werden (siehe Interface IListenerTileMetaTHM)
 *      muss hier gepr�ft werden, well die Klasse HexCellTHM diese Eigenschaften noch nicht kennt.
 * @author lindhaueradmin
 *
 */
public class AreaCellTHM  extends HexCellTHM implements IListenerTileMetaTHM{
	private AreaCell objAreaCell=null;
	
	
	 //Effekte werden beim Zeichnen der Komponente ber�cksichtig
	 //boolean bFlagEffectReachable=false; //bem Drag Dro: Ist ein Feld in diesem Zug �berhaupt erreichbar
	 //boolean bFlagEffectReachableNext=false; //bem Drag Drop: Ist ein Feld in diesem Zug als n�chstes erreichbar
	//20130721: Flags werden nun als Enum hier hinterlegt.
	public enum FLAGZ{
		EFFECT_REACHABLE, EFFECT_REACHABLE_NEXT;
	}
	
	/** Default Konsturktor um per Refelction API - objClass.newInstance() machen zu k�nnen.
	 * 
	 * lindhaueradmin, 23.07.2013
	 */
	public AreaCellTHM(){	
	}
	public AreaCellTHM(KernelZZZ objKernel, HexMapTHM mapParent, AreaCell objAreaCell, int iSideLength) throws ExceptionZZZ {
		super(objKernel, mapParent, objAreaCell, iSideLength);
		this.setAreaCellObject(objAreaCell);
	} 
	
	private void setAreaCellObject(AreaCell objAreaCell2) {
		this.objAreaCell = objAreaCell2;
	}
	public AreaCell getAreaCellObject(){
		return this.objAreaCell;
	}
	
	/* 20130701: Über die mapParent ist das panelParent zu erreichen
	public AreaCellTHM(KernelZZZ objKernel, KernelJPanelCascadedZZZ panelParent, String sMapX, String sMapY, int iSideLength, int iAreaType) {
		super(objKernel, panelParent, sMapX, sMapY, iSideLength);
		this.setAreaType(iAreaType);
	}*/
//	public void setAreaType(String sAreaType){
//		this.getAreaCellObject().setAreaType(sAreaType);
//	}
	public String getAreaType(){
		return this.getAreaCellObject().getAreaTypeObject().name();
	}
	/** Darf der Spielstein die Area betreten.
	 *  Voraussetzungen sind momentan:
	 *  a) dass der Spielstein den Geländetyp betreten darf. Intern wird die gleichnamige statische Methode der AreaType-Klasse abgefragt
	 *   
	* @param tileTHM
	* @return
	* 
	* lindhaueradmin; 04.10.2008 14:47:08
	 */
	public boolean isAccessableBy(TileTHM tileTHM){
		boolean bReturn = true;
		main:{
			String sAreaType = this.getAreaType();
			if(sAreaType.equals(AreaCellType.OCEAN.name())){
				bReturn = AreaTypeOceanTHM.isAccessibleBy(tileTHM);
			}else if(sAreaType.equals(AreaCellType.LAND.name())){
				bReturn = AreaTypeLandTHM.isAccessibleBy(tileTHM);
			}
		}
		return bReturn;
	}
	
	/** Darf der Spielstein in dem Gebiet erzeugt werden.
	 *   Intern wird die gleichnamige statische Methode der AreaType-Klasse abgefragt.
	 *   
	 *   Voraussetzungen sind:
	 *   a) dass der Spielstein den Gel�ndetyp betreten darf. (.isAccessableBy)
	 *   b) dass kein anderer Spielstein in dem Gebiet steht.
	 * @param tileTHM
	 * @return
	 */
	public boolean isCreationPlaceFor(TileTHM objTile){
		boolean bReturn = true;
		main:{
			boolean bIsAccessable = this.isAccessableBy(objTile);
			if(!bIsAccessable){
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! Darf das Feld nicht betreten " +  this.getMapX() + "/" + this.getMapY());
				bReturn = false;
				break main;
			}
			
			boolean bIsOccupiedByTile = this.isOccupiedByAnyTile();
			if(bIsOccupiedByTile){
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! Feld ist schon von anderem Spielstein besetzt." +  this.getMapX() + "/" + this.getMapY());
				bReturn = false;
				break main;
			}
		}
		return bReturn;
	}
	
	/** Farbe der Area, abh�ngig vom AreaType
	* @return, Farbobject
	* 
	* lindhaueradmin; 04.10.2008 13:33:38
	 */
	public Color getAreaColor(){
		Color colorReturn = null;
		main:{ 
		    String sAreaType = this.getAreaType();
			if(sAreaType.equals(AreaCellType.OCEAN.name())){
				colorReturn = AreaTypeOceanTHM.getColor();
			}else if(sAreaType.equals(AreaCellType.LAND.name())){
				colorReturn = AreaTypeLandTHM.getColor();
			}
		}
		return colorReturn;
	}
	
	/* (non-Javadoc)
	 * @see use.thm.client.component.HexCellTHM#fillDetail(java.awt.Graphics, java.awt.Polygon)
	 * 
	 * Hier werden anders als in der Zelle für jeden Areatyp andere Farben verwendet
	 * Merke: po ist das Sechseck.
	 */
	public void fillDetail(Graphics g, Polygon po){
		Color c = this.getAreaColor();
		
		//Falls per DragDrop eine Spielfigur auf die Zelle gezogen wird
	 	//if (this.getFlag("effectDragTile")){
		if (this.getFlagZ(HexCellTHM.FLAGZ.EFFECT_DRAG_TILE.name())){
	 		//Erst den zugehörenden Effekt holen
	 		//int iEffect = this.getEffectByFlag("effectDragTile");
	 		int iEffect = this.getEffectByFlag(HexCellTHM.FLAGZ.EFFECT_DRAG_TILE.name());
	 		
	 		//Dann darauf basierend einen helleren / dunkleren Wert errechnen
	 		Color cEffect = AreaEffectTHM.getColor4Effect(c, iEffect);
	 		g.setColor(cEffect);
	 		//g.setColor(c.darker());
	 	//}else if(this.getFlag("effectPathTile")){
		}else if(this.getFlagZ(HexCellTHM.FLAGZ.EFFECT_PATH_TILE.name())){
//	 		Erst den zugehörenden Effekt holen
	 		//int iEffect = this.getEffectByFlag("effectPathTile");
			int iEffect = this.getEffectByFlag(HexCellTHM.FLAGZ.EFFECT_PATH_TILE.name());
	 		
	 		//Dann darauf basierend einen helleren / dunkleren Wert errechnen
	 		Color cEffect = AreaEffectTHM.getColor4Effect(c, iEffect);
	 		g.setColor(cEffect);
	 		//g.setColor(c.brighter());
	 	}else{
	 		g.setColor(c);
	 	}
		g.fillPolygon(po);
	}
	

	/* (non-Javadoc)
	 * @see use.thm.client.component.HexCellTHM#onTileEnter(use.thm.client.event.EventCellEnteredTHM)
	 * 
	 *  Falls ein nicht gültiges Feld betreten wird, wird der Cursor zum DEFAULT_CURSOR
	 */
	public boolean onTileEnter(EventCellEnteredTHM eventCellEntered) {
		boolean bReturn = true;
		Cursor objCursor=null;
		main:{
//			TODO dafür eine eigene Methode //Erst mal herausfinden, ob das die eigene Area ist
			if(eventCellEntered.getMapX().equals(this.getMapX()) && eventCellEntered.getMapY().equals(this.getMapY())){
				
				//+++ Ist das Neue-Feld vom letzten g�ltigen Feld aus erreichbar ?
				HexCellTHM objCellValidLast = eventCellEntered.getTile().getMouseMotionHandler().getCellValidLast();	
				
				String sX = objCellValidLast.getMapX();
				String sY = objCellValidLast.getMapY();
				bReturn = this.isNeighbourImmediateOf(sX,sY);
				if(!bReturn){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! AREA ist kein Nachbarfeld vom letzten betretbaren Feld [" + objCellValidLast.getMapX() + "/" + objCellValidLast.getMapY() +"]: " +  this.getMapX() + "/" + this.getMapY() );
					objCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					break main;
				}
			
				bReturn = this.isAccessableBy(eventCellEntered.getTile());
				if(!bReturn){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! Darf das Feld nicht betreten " +  this.getMapX() + "/" + this.getMapY());
					objCursor = new Cursor(Cursor.DEFAULT_CURSOR);
					break main;
				}
				
				bReturn = super.onTileEnter(eventCellEntered); //Merke: Hier wird der CURSOR zum CROSSHAIR Cursor
			}
			
		}//end main
		if(objCursor!=null) this.setCursor(objCursor);
		return bReturn;
	}

	public boolean onTileLeave(EventCellLeavedTHM eventCellLeaved) {
		boolean bReturn = true;
		main:{
			super.onTileLeave(eventCellLeaved);
			
			//TODO: Eigentlich muss hier �berpr�ft werden, ob die alte Zelle verlassen werden kann
			/* so nicht
	//		Erst mal herausfinden, ob das die eigene Zelle ist
			if(eventCellLeaved.getMapX().equals(this.getMapX()) && eventCellLeaved.getMapY().equals(this.getMapY())){
				bReturn = this.isAccessableBy(eventCellLeaved.getTile());
				if(bReturn == true){
					super.onTileLeave(eventCellLeaved);
				}else{
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! Darf das Feld nicht verlassen " +  this.getMapX() + "/" + this.getMapY());
				}
			}*/
			}//end main
		return bReturn;
	}


	public boolean onTileDrop(EventTileDroppedToCellTHM eventTileDropped) {
		boolean bReturn = true;
		main:{
			//TODO dafür eine eigene Methode //Erst mal herausfinden, ob das die eigene Area ist
			String sX = this.getMapX();
			String sY = this.getMapY();
			
			String sXDropped = eventTileDropped.getMapX();
			String sYDropped = eventTileDropped.getMapY();
			if(sXDropped.equals(sX) && sYDropped.equals(sY)){
		
//			 	+++++ ist dies das gleiche Feld wie das zuletzt gültige ?	
				//Merke: Wenn "von Aussen" ein Spielstein eingesetzt wurde, dann gibt es keine "vorherige" Zelle.
				HexCellTHM objCellValidLast = eventTileDropped.getTile().getMouseMotionHandler().getCellValidLast();
				if(objCellValidLast!=null){
					String sXValid = objCellValidLast.getMapX();
					String sYValid = objCellValidLast.getMapY();
					if(!(sXValid.equals(sX) && sYValid.equals(sY))){
						bReturn = false;
						System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! AREA darf nicht Ende der Bewegung sein: " +  this.getMapX() + "/" + this.getMapY());
						break main;
					}
				}
				
				//++++ Gibt es bereits einen Spielstein in der Zelle?
				boolean bIsOccupiedByTile = this.isOccupiedByAnyTile();
				if(bIsOccupiedByTile){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! Feld ist schon von anderem Spielstein besetzt." +  this.getMapX() + "/" + this.getMapY());
					bReturn = false;
					break main;
				}
				
			
				// TODO: Zusätzlich muss gefragt werden, ob der Spielstein einer ist, den diese Zelle "beobachtet". 
				bReturn = super.onTileDrop(eventTileDropped); //Das sorgt z.B. dafür, dass alle Zellen die "Hervorhebung" verlieren.
				if(!bReturn){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + "!!! CELL darf nicht Ende der Bewegung sein: " +  this.getMapX() + "/" + this.getMapY());
					break main;
				} 
				
				//Merke 20170711: Das Arbeiten mit der Component, die dann andere Koordinaten bekommt und einer anderen Zelle zugeordnet wird,
				//                        passiert in TileMouseMotionHandlerTHM.mouseReleased(). Dort dann ggfs. auch die Persistierung in der Datenbank ändern.
				
				//Merke 20170711: Falls weiterer Überprüfungen passieren sollten (z.B. gegnerische Spielsteine angrenzend, etc.) dann müsste hierzu an dieser Stelle die Datenbank abgefragt werden
				
				
			}
		}//end main
		return bReturn;
	}
	
	//### Direkter FLAG Bezug ###########
	public int getEffectByFlag(String sFlagName){
		int iReturn = AreaEffectTHM.AREA_NORMAL;
		main:{
			if(StringZZZ.isEmpty(sFlagName)) break main;
			
			//String stemp = sFlagName.toLowerCase();
			String stemp = sFlagName;
			//if(stemp.equals("effectreachable")){
			if(stemp.equalsIgnoreCase(FLAGZ.EFFECT_REACHABLE.name())){
				iReturn = AreaEffectTHM.AREA_REACHABLE;
			//}else if(stemp.equals("effectreachablenext")){
			}else if(stemp.equalsIgnoreCase(FLAGZ.EFFECT_REACHABLE_NEXT.name())){
				iReturn = AreaEffectTHM.AREA_REACHABLE_NEXT;
			//}else if(stemp.equals("effectdragtile")){  //Flag kommt aus HexCell
			}else if(stemp.equalsIgnoreCase(HexCellTHM.FLAGZ.EFFECT_DRAG_TILE.name())){
				iReturn = AreaEffectTHM.AREA_LAST_VALID_ENTERED;
			//}else if(stemp.equals("effectpathtile")){ //Flag kommt aus HexCell
			}else if(stemp.equalsIgnoreCase(HexCellTHM.FLAGZ.EFFECT_PATH_TILE.name())){
				iReturn = AreaEffectTHM.AREA_ON_PATH;
			}	
		}
		return iReturn;
	}
	
	//### FLAGS ####################
	@Override
	public Class getClassFlagZ() {
		return FLAGZ.class;
	}
	
	
	/* Hinzugef�gt am 20130701: Sowohl beim Start als auch per Drag & Drop "von aussen" sollen so Spielsteine in der Karte neu gesetzt werden. 
	 * (non-Javadoc)
	 * @see use.thm.client.event.IListenerTileMetaTHM#onTileCreated(use.thm.client.event.EventTileCreatedInCellTHM)
	 */
	public boolean onTileCreated(EventTileCreatedInCellTHM eventTileCreated) {
		boolean bReturn = false;
		main:{
			//TODO daf�r eine eigene Methode //Erst mal herausfinden, ob das die eigene Area ist
			String sX = this.getMapX();
			String sY = this.getMapY();
			if(eventTileCreated.getMapX().equals(sX) && eventTileCreated.getMapY().equals(sY)){
		
				//20170705: Merke: Durch die Verwendung von PreInsertListenerTHM oder SaveOrUpdateListenerTHM und der Überprüfung im Backend, sollte es an dieser Stelle keine Ausgabe mehr geben.
				//                         Die JOptionPane.showMessageDialog Ausgabe wird in den TroopArmyDaoFacade bzw. TroopFleetDaoFacade gemacht.
				System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " ##### Pr�fe ob der Spielstein (TILE) '" + eventTileCreated.getTile().getName() + "' hier (" + sX + "/" +sY +") erzeugt werden kann. #######");
				boolean bIsCreationPlaceFor = this.isCreationPlaceFor(eventTileCreated.getTile());
				if(bIsCreationPlaceFor){
					System.out.println(ReflectCodeZZZ.getMethodCurrentName() + " Pr�fung erfolgreich - Erzeuge hier dann das TILE #######");
					bReturn = true;
				}else{
					//Fehlerhinweis ausgeben
					JOptionPane.showMessageDialog(this, "Spielstein '" + eventTileCreated.getTile().getName() + "' kann hier (" + sX + "/" +sY +") nicht erzeugt werden.");
					bReturn = false;
					break main;
				}
			}
			
			//F�ge den neuen Spielstein in diese Zelle ein.	
			if(bReturn){
				this.add(eventTileCreated.getTile());
				this.repaint();
			}
		}
		return bReturn;
	}
	
	
	//#### GETTER / SETTER
//	public AreaCell getAreaCellObject(){
//		return this.objAreaCell;
//	}
//	public void setAreaCellObject(AreaCell objAreaCell){
//		this.objAreaCell = objAreaCell;
//	}
}
