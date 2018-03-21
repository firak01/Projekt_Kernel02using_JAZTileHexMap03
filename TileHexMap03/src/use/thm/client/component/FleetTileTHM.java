package use.thm.client.component;

import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.persistence.dto.ITileDtoAttribute;
import basic.persistence.dto.GenericDTO;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class FleetTileTHM extends TroopTileTHM {

//	public FleetTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker, String sUniquename, String sAliasX, String sAliasY, int iHexSideLength) {
//		super(panelMap, objEventBroker, sUniquename, sAliasX, sAliasY, iHexSideLength);
//		this.setName("Flotte");
//	}	
	public FleetTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker,GenericDTO<ITileDtoAttribute> objDto, String sAliasX, String sAliasY, int iHexSideLength) {
		super(panelMap, objEventBroker, objDto, sAliasX, sAliasY, iHexSideLength);
		//this.setName("Flotte");
		this.setName(this.getUniquename());
	}
}
