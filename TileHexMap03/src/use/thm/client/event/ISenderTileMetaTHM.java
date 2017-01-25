package use.thm.client.event;

import java.util.ArrayList;
/**Dieses Interface enthält Methoden, die von den Klassen implementiert werden müssen, die die MetaAktionen an einem Spielstein (=Tile) verwalten sollen.
 *  (das ist z.B. der TileMetaEventBrokerTHM). MetaAktionen sind z.B. Erstellen des Spielsteins.
 * @author lindhaueradmin
 *
 */
public interface ISenderTileMetaTHM {
	public abstract void fireEvent(EventTileCreatedInCellTHM  eventEnter);
	
	public abstract void removeListenerTileMeta(IListenerTileMetaTHM InterfaceUser);

	public abstract void addListenerTileMeta(IListenerTileMetaTHM InterfaceUser);
	
	public abstract ArrayList getListenerRegisteredAll();
}
