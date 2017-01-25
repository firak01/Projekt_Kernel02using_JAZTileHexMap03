package use.thm.client.event;

import java.util.ArrayList;

/**Dieses Interface enthält Methoden, die von den Klassen implementiert werden müssen, die Bewegung eines Spielsteins (=Tile) verwalten sollen.
 *  (das ist z.B. der TileMoveEventBrokerTHM)
 * @author lindhaueradmin
 *
 */
public interface ISenderTileMovedTHM {
	public abstract void fireEvent(EventCellEnteredTHM  eventEnter);
	public abstract void fireEvent(EventCellLeavedTHM eventLeave);
	public abstract void fireEvent(EventTileDroppedToCellTHM eventDrop);
	
	public abstract void removeListenerTileMoved(IListenerTileMovedTHM InterfaceUser);

	public abstract void addListenerTileMoved(IListenerTileMovedTHM InterfaceUser);
	
	public abstract ArrayList getListenerRegisteredAll();
}
