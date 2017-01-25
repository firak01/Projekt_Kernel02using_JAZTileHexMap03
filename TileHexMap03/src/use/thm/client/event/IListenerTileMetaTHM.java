package use.thm.client.event;

import java.util.EventListener;

/**Die Komponente, welche dieses Interface einbindet und sich an den TileMetaEventBroker registriert hat, 
 *  kann mit diesen Methoden auf die Events zugreifen
 * @author lindhaueradmin, 20130630
 *
 */
public interface IListenerTileMetaTHM extends EventListener{
	
	//Merke: Die Rückgabewerte geben an, ob die Komponente damit einverstanden ist oder nicht
	public abstract boolean onTileCreated(EventTileCreatedInCellTHM eventTileCreated);
	
	/**Ziel: Den neuen Event mit dem alten vergleichen zu können. Falls die Events an entscheidender Stelle gleich sind, 
	 *          wird die entsprechende Methode nicht ausgeführt.
	 *          Merke: CellEntered/cellLeaved erben aus CellAffected
	 *          
	* lindhaueradmin; 03.10.2008 10:01:47
	 */
	public abstract EventCellAffectedTHM getEventPrevious();    
	public abstract void setEventPrevious(EventCellAffectedTHM eventCellAffected);
}