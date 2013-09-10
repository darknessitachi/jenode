package ENode.Domain.Impl;

import ENode.Eventing.*;
import ENode.Infrastructure.*;
import ENode.Snapshoting.*;

/** An repository implementation with the event sourcing pattern.
 
*/
public class EventSourcingRepository implements IRepository
{
	private IAggregateRootFactory _aggregateRootFactory;
	private IMemoryCache _memoryCache;
	private IEventStore _eventStore;
	private ISnapshotStore _snapshotStore;

	/** Parameterized constructor.
	 
	 @param aggregateRootFactory
	 @param memoryCache
	 @param eventStore
	 @param snapshotStore
	*/
	public EventSourcingRepository(IAggregateRootFactory aggregateRootFactory, IMemoryCache memoryCache, IEventStore eventStore, ISnapshotStore snapshotStore)
	{
		_aggregateRootFactory = aggregateRootFactory;
		_memoryCache = memoryCache;
		_eventStore = eventStore;
		_snapshotStore = snapshotStore;
	}

	/** Get an aggregate from memory cache, if not exist, get it from event store.
	 
	 @param id
	 <typeparam name="T"></typeparam>
	 @return 
	*/
	public final <T extends AggregateRoot> T Get(Object id)
	{
		T tempVar = Get(T.class, id);
		return (T)((tempVar instanceof T) ? tempVar : null);
	}
	/** Get an aggregate from memory cache, if not exist, get it from event store.
	 
	 @param type
	 @param id
	 @return 
	*/
	public final AggregateRoot Get(java.lang.Class type, Object id)
	{
		if (id == null)
		{
			throw new ArgumentNullException("id");
		}
		AggregateRoot tempVar = _memoryCache.Get(id);
		return (tempVar != null) ? tempVar : GetFromStorage(type, id.toString());
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Helper Methods

	/** Get aggregate root from event store.
	 
	*/
	private AggregateRoot GetFromStorage(java.lang.Class aggregateRootType, String aggregateRootId)
	{
		AggregateRoot aggregateRoot = null;
		final long minStreamVersion = 1;
		final long maxStreamVersion = Long.MAX_VALUE;

		RefObject<AggregateRoot> tempRef_aggregateRoot = new RefObject<AggregateRoot>(aggregateRoot);
		boolean tempVar = TryGetFromSnapshot(aggregateRootId, aggregateRootType, tempRef_aggregateRoot);
			aggregateRoot = tempRef_aggregateRoot.argvalue;
		if (tempVar)
		{
			return aggregateRoot;
		}

		Iterable<EventStream> streams = _eventStore.Query(aggregateRootId, aggregateRootType, minStreamVersion, maxStreamVersion);
		aggregateRoot = BuildAggregateRoot(aggregateRootType, streams);

		return aggregateRoot;
	}
	/** Try to get an aggregate root from snapshot store.
	 
	*/
	private boolean TryGetFromSnapshot(String aggregateRootId, java.lang.Class aggregateRootType, RefObject<AggregateRoot> aggregateRoot)
	{
		aggregateRoot.argvalue = null;

		Snapshot snapshot = _snapshotStore.GetLastestSnapshot(aggregateRootId, aggregateRootType);
		if (snapshot == null)
		{
			return false;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var aggregateRootFromSnapshot = ObjectContainer.<ISnapshotter>Resolve().RestoreFromSnapshot(snapshot);
		if (aggregateRootFromSnapshot == null)
		{
			return false;
		}

		if (!aggregateRootId.equals(aggregateRootFromSnapshot.UniqueId))
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var message = String.format("Aggregate root restored from snapshot not valid as the aggregate root id not matched. Snapshot aggregate root id:%1$s, required aggregate root id:%2$s", aggregateRootFromSnapshot.UniqueId, aggregateRootId);
			throw new RuntimeException(message);
		}

		Iterable<EventStream> eventsAfterSnapshot = _eventStore.Query(aggregateRootId, aggregateRootType, snapshot.getVersion() + 1, Long.MAX_VALUE);
		aggregateRootFromSnapshot.ReplayEventStreams(eventsAfterSnapshot);
		aggregateRoot.argvalue = aggregateRootFromSnapshot;
		return true;
	}
	/** Rebuild the aggregate root using the event sourcing pattern.
	 
	*/
	private AggregateRoot BuildAggregateRoot(java.lang.Class aggregateRootType, Iterable<EventStream> streams)
	{
		java.util.ArrayList<Object> eventStreams = streams.ToList();
		if (streams == null || !eventStreams.Any())
		{
			return null;
		}

		AggregateRoot aggregateRoot = _aggregateRootFactory.CreateAggregateRoot(aggregateRootType);
		aggregateRoot.ReplayEventStreams(eventStreams);

		return aggregateRoot;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}