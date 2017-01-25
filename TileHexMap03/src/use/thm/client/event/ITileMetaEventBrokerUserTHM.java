package use.thm.client.event;

/** Interface muss von den Komponenten implementiert werden, die den Event-Broker verwenden wollen, um einen Event abzufeuern.
 *   Merke: Die Komponenten, die lediglich auf den Event "hören" brauchen dieses Interface nicht !!!
 * @author lindhaueradmin, 20130630
 *
 */
public interface ITileMetaEventBrokerUserTHM {
	//TODO TileMetaEventBrokerTHM durch ein Interface ersetzen
	public abstract TileMetaEventBrokerTHM getSenderUsed();
	public abstract void setSenderUsed(TileMetaEventBrokerTHM objEventSender);
}
