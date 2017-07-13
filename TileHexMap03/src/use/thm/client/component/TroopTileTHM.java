package use.thm.client.component;

import use.thm.client.event.TileMoveEventBrokerTHM;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public abstract class TroopTileTHM extends TileTHM {

	public TroopTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker, String sUniquename, String sAliasX, String sAliasY, int iHexSideLength) {
		super(panelMap, objEventBroker, sUniquename, sAliasX, sAliasY, iHexSideLength);
	}

}
