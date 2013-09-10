package ENode.Eventing;

/** Represents a event store to store event streams of aggregate.
 
*/
public interface IEventStore
{
	/** Append the event stream to the event store.
	 
	*/
	void Append(EventStream stream);
	/** Check whether an event stream is exist in the event store.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @param id
	 @return 
	*/
	boolean IsEventStreamExist(String aggregateRootId, java.lang.Class aggregateRootType, Guid id);
	/** Query event streams from event store.
	 
	*/
	Iterable<EventStream> Query(String aggregateRootId, java.lang.Class aggregateRootType, long minStreamVersion, long maxStreamVersion);
	/** Query all the event streams from the event store.
	 
	 @return 
	*/
	Iterable<EventStream> QueryAll();
}