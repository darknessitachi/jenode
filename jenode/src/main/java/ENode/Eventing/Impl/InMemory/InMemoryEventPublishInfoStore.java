package ENode.Eventing.Impl.InMemory;

/** Represents a storage to store the event publish information of aggregate.
 
*/
public class InMemoryEventPublishInfoStore implements IEventPublishInfoStore
{
	private final ConcurrentDictionary<String, Long> _versionDict = new ConcurrentDictionary<String, Long>();

	/** Insert the first published event version of aggregate.
	 
	 @param aggregateRootId
	*/
	public final void InsertFirstPublishedVersion(String aggregateRootId)
	{
		_versionDict.TryAdd(aggregateRootId, 1);
	}

	/** Update the published event version of aggregate.
	 
	 @param aggregateRootId
	 @param version
	*/
	public final void UpdatePublishedVersion(String aggregateRootId, long version)
	{
		_versionDict[aggregateRootId] = version;
	}

	/** Get the current event published version for the specified aggregate.
	 
	 @param aggregateRootId
	 @return 
	*/
	public final long GetEventPublishedVersion(String aggregateRootId)
	{
		return _versionDict[aggregateRootId];
	}
}