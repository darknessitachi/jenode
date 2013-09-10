package ENode.Domain.Impl;

import ENode.Eventing.*;

/** Default implementation of IMemoryCacheRebuilder.
 
*/
public class DefaultMemoryCacheRebuilder implements IMemoryCacheRebuilder
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private IAggregateRootFactory _aggregateRootFactory;
	private IAggregateRootTypeProvider _aggregateRootTypeProvider;
	private IEventStore _eventStore;
	private IMemoryCache _memoryCache;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param aggregateRootFactory
	 @param aggregateRootTypeProvider
	 @param eventStore
	 @param memoryCache
	*/
	public DefaultMemoryCacheRebuilder(IAggregateRootFactory aggregateRootFactory, IAggregateRootTypeProvider aggregateRootTypeProvider, IEventStore eventStore, IMemoryCache memoryCache)
	{
		_aggregateRootFactory = aggregateRootFactory;
		_aggregateRootTypeProvider = aggregateRootTypeProvider;
		_eventStore = eventStore;
		_memoryCache = memoryCache;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Using event sourcing pattern to rebuild the whole domain by replaying all the domain events from the eventstore.
	 
	*/
	public final void RebuildMemoryCache()
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var groups = _eventStore.QueryAll().GroupBy(x => x.AggregateRootId);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var group : groups)
		{
			if (!group.Any())
			{
				continue;
			}

			java.lang.Class aggregateRootType = _aggregateRootTypeProvider.GetAggregateRootType(group.First().AggregateRootName);
			AggregateRoot aggregateRoot = _aggregateRootFactory.CreateAggregateRoot(aggregateRootType);

			aggregateRoot.ReplayEventStreams(group);

			_memoryCache.Set(aggregateRoot);
		}
	}
}