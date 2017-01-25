package use.thm.client.event;

import java.util.EventListener;

import basic.zKernelUI.component.model.EventComponentSelectionResetZZZ;

/**Die Komponente, welche dieses Interface einbindet und sich an den TileMoveEventBroker registriert hat, 
 *  kann mit diesen Methoden auf die Events zugreifen
* @param eventSelectionResetNew
* 
* lindhaueradmin; 08.02.2007 10:07:59
 */
public interface IListenerTileMovedTHM extends EventListener{
	
	//Merke: Die Rückgabewerte geben an, ob die Komponente damit einverstanden ist oder nicht
	public abstract boolean onTileEnter(EventCellEnteredTHM eventCellEntered);
	public abstract boolean onTileLeave(EventCellLeavedTHM eventCellLeaved);
	public abstract boolean onTileDrop(EventTileDroppedToCellTHM eventTileDropped);
	
	/**Ziel: Den neuen Event mit dem alten vergleichen zu können. Falls die Events an entscheidender Stelle gleich sind, 
	 *          wird die entsprechende Methode nicht ausgeführt.
	 *          Merke: CellEntered/cellLeaved erben aus CellAffected
	 *          
	* lindhaueradmin; 03.10.2008 10:01:47
	 */
	public abstract EventCellAffectedTHM getEventPrevious();    
	public abstract void setEventPrevious(EventCellAffectedTHM eventCellAffected);
}
