package ENode.Eventing;

/** Represents a storage to store the event publish information of aggregate.
 
*/
public interface IEventPublishInfoStore
{
	/** Insert the first published event version of aggregate.
	 
	*/
	void InsertFirstPublishedVersion(String aggregateRootId);
	/** Update the published event version of aggregate.
	 
	*/
	void UpdatePublishedVersion(String aggregateRootId, long version);
	/** Get the current event published version for the specified aggregate.
	 
	*/
	long GetEventPublishedVersion(String aggregateRootId);
}