package use.thm.client.component;

import use.thm.client.event.TileMoveEventBrokerTHM;
import use.thm.persistence.dto.ITileDtoAttribute;
import basic.persistence.dto.GenericDTO;
import basic.zKernelUI.component.KernelJPanelCascadedZZZ;

public class ArmyTileTHM extends TroopTileTHM {

//	public ArmyTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker, String sUniquename, String sAliasX, String sAliasY, int iHexSideLength) {
//		super(panelMap, objEventBroker, sUniquename, sAliasX, sAliasY, iHexSideLength);
//		this.setName("Armee");
//	}
	
	public ArmyTileTHM(KernelJPanelCascadedZZZ panelMap, TileMoveEventBrokerTHM objEventBroker, GenericDTO<ITileDtoAttribute> objDto, String sAliasX, String sAliasY, int iHexSideLength) {
		super(panelMap, objEventBroker, objDto, sAliasX, sAliasY, iHexSideLength);
		//FGL 20180321... aber jetzt kann man den Namen doch eindeutig machen...this.setName("Armee");
		this.setName(this.getUniquename());
	}
}
