package use.thm.client.event;

import java.util.EventObject;

import use.thm.client.component.TileTHM;

public class EventCellAffectedTHM extends EventObject{
	private TileTHM objTileAsSource;
	private String sMapX;
	private String sMapY;
	private int iId;
	
	/** Event Elternklasse für EventCellEntered/EventCellLeaved, die ausgelöst werden, wenn ein Spielstein über die Zellen der Karte bewegt wird 
	* lindhaueradmin; 03.10.2008 09:53:41
	 * @param source, der Spielstein
	 * @param iID
	 * @param sMapX, X-Koordinate (also die x-Koordinate der Zelle, in die sich hineinbewegt, bzw. die verlassen wird)
	 * @param sMapY, Y-Koordinate (also die y-Koordinate der Zelle, in die sich hineinbewegt, bzw. die verlassen wird)
	 */
	public EventCellAffectedTHM(TileTHM objTile , int iID,  String sMapX, String sMapY) {
		super((Object)objTile);
		this.objTileAsSource = objTile;
		this.sMapX = sMapX;
		this.sMapY = sMapY;
		this.iId = iID;
	}
	public int getID(){
		return this.iId;
	}
	public String getMapX(){
		return this.sMapX;
	}
	public String getMapY(){
		return this.sMapY;
	}
	public TileTHM getTile(){
		return this.objTileAsSource;
	}
}