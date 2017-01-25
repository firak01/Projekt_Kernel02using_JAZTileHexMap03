package use.thm.client.component;

import use.thm.client.event.TileMoveEventBrokerTHM;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class FleetTileTHM extends TroopTileTHM {

	public FleetTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker, String sAliasX, String sAliasY, int iHexSideLength) {
		super(panelMap, objEventBroker, sAliasX, sAliasY, iHexSideLength);
		this.setName("Flotte");
	}
}
