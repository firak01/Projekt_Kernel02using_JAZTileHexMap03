package use.thm.client.event;

import java.util.EventObject;

import use.thm.client.component.TileTHM;

public class EventCellEnteredTHM extends EventCellAffectedTHM{
	
	public EventCellEnteredTHM(TileTHM source, int iID,  String sMapX, String sMapY) {
		super(source, iID, sMapX, sMapY);
	}
}
