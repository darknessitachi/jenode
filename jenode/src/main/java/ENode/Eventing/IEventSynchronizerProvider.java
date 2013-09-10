package ENode.Eventing;

/** Represents a provider to provide the event synchronizer information.
 
*/
public interface IEventSynchronizerProvider
{
	/** Get all the event synchronizers for the given event type.
	 
	 @param eventType
	 @return 
	*/
	Iterable<IEventSynchronizer> GetSynchronizers(java.lang.Class eventType);
}