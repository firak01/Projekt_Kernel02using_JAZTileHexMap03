package use.thm;

import use.thm.client.event.TileMetaEventBrokerTHM;
import use.thm.client.event.TileMoveEventBrokerTHM;

public interface ITileEventUserTHM {
	public TileMetaEventBrokerTHM getTileMetaEventBroker();
	public void setTileMetaEventBroker(TileMetaEventBrokerTHM objTileMetaEventBroker);
	
	public TileMoveEventBrokerTHM getTileMoveEventBroker();
	public void setTileMoveEventBroker(TileMoveEventBrokerTHM objTileMoveEventBroker);
	
}
