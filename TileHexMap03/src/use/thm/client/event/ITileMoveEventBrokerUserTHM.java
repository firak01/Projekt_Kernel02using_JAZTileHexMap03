package use.thm.client.event;

import basic.zKernelUI.component.model.KernelSenderComponentSelectionResetZZZ;

/** Interface muss von den Komponenten implementiert werden, die den Event-Broker verwenden wollen, um einen Event abzufeuern.
 *   Merke: Die Komponenten, die lediglich auf den Event "hören" brauchen dieses Interface nicht !!!
 *    
 * @author lindhaueradmin
 *
 */
public interface ITileMoveEventBrokerUserTHM {
	//TODO TileMoveEventBrokerTHM durch ein Interface ersetzen
	public abstract TileMoveEventBrokerTHM getSenderUsed();
	public abstract void setSenderUsed(TileMoveEventBrokerTHM objEventSender);
}
