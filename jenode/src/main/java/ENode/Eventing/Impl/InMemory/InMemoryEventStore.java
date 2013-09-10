package ENode.Eventing.Impl.InMemory;

import ENode.Infrastructure.Concurrent.*;

/** In-memory concurrent dictionary based event store implementation.
 
*/
public class InMemoryEventStore implements IEventStore
{
	private final ConcurrentDictionary<EventKey, EventStream> _eventDict = new ConcurrentDictionary<EventKey, EventStream>();

	/** Append the event stream to the event store.
	 
	 @param stream
	*/
	public final void Append(EventStream stream)
	{
		if (stream == null)
		{
			return;
		}
		if (!_eventDict.TryAdd(new EventKey(stream.getAggregateRootId(), stream.getVersion()), stream))
		{
			throw new ConcurrentException("");
		}
	}
	/** Check whether an event stream is exist in the event store.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @param minStreamVersion
	 @param maxStreamVersion
	 @return 
	*/
	public final Iterable<EventStream> Query(String aggregateRootId, java.lang.Class aggregateRootType, long minStreamVersion, long maxStreamVersion)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _eventDict.Values.Where(x => aggregateRootId.equals(x.AggregateRootId) && x.Version >= minStreamVersion && x.Version <= maxStreamVersion).OrderBy(x => x.Version);
	}
	/** Query event streams from event store.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @param id
	 @return 
	*/
	public final boolean IsEventStreamExist(String aggregateRootId, java.lang.Class aggregateRootType, Guid id)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _eventDict.Values.Any(x => aggregateRootId.equals(x.AggregateRootId) && id.equals(x.Id));
	}
	/** Query all the event streams from the event store.
	 
	 @return 
	*/
	public final Iterable<EventStream> QueryAll()
	{
		return _eventDict.Values;
	}

	private static class EventKey
	{
		private String privateAggregateRootId;
		private String getAggregateRootId()
		{
			return privateAggregateRootId;
		}
		private void setAggregateRootId(String value)
		{
			privateAggregateRootId = value;
		}
		private long privateStreamVersion;
		private long getStreamVersion()
		{
			return privateStreamVersion;
		}
		private void setStreamVersion(long value)
		{
			privateStreamVersion = value;
		}

		public EventKey(String aggregateRootId, long streamVersion)
		{
			setAggregateRootId(aggregateRootId);
			setStreamVersion(streamVersion);
		}

		@Override
		public boolean equals(Object obj)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var eventKey = (EventKey)((obj instanceof EventKey) ? obj : null);

			if (eventKey == null)
			{
				return false;
			}
			if (eventKey == this)
			{
				return true;
			}

			return getAggregateRootId().equals(eventKey.AggregateRootId) && getStreamVersion() == eventKey.StreamVersion;
		}
		@Override
		public int hashCode()
		{
			return getAggregateRootId().hashCode() + (new Long(getStreamVersion())).hashCode();
		}
	}
}