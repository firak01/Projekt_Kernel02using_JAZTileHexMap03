package use.thm.client.event;

import use.thm.client.component.TileTHM;

public class EventTileDroppedToCellTHM extends EventCellAffectedTHM{
	
	public EventTileDroppedToCellTHM(TileTHM source, int iID,  String sMapX, String sMapY) {
		super(source, iID, sMapX, sMapY);
	}
}
