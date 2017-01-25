package use.thm.client.component;

import use.thm.client.event.TileMoveEventBrokerTHM;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class ArmyTileTHM extends TroopTileTHM {

	public ArmyTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker, String sAliasX, String sAliasY, int iHexSideLength) {
		super(panelMap, objEventBroker, sAliasX, sAliasY, iHexSideLength);
		this.setName("Armee");
	}

}
