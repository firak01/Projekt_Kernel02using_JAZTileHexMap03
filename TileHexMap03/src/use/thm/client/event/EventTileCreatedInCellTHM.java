package use.thm.client.event;

import use.thm.client.component.TileTHM;

/** Verwendet von TileMetaEventBroker. Ist also einer der Events, die mit einem Spielstein auf höherer Ebene passieren können.
 * hier:  - Erstellen 
 * 
 * @author lindhaueradmin
 *
 */
public class EventTileCreatedInCellTHM extends EventCellAffectedTHM{
	
	public EventTileCreatedInCellTHM(TileTHM objTile, int iID, String sMapX, String sMapY) {
		super(objTile, iID, sMapX, sMapY);
	}

}
