package ENode.Eventing.Impl.SQL;

import ENode.Domain.*;

/** Default implementation of IEventTableNameProvider.
 
*/
public class AggregatePerEventTableNameProvider implements IEventTableNameProvider
{
	private IAggregateRootTypeProvider _aggregateRootTypeProvider;

	/** Parameterized constructor.
	 
	 @param aggregateRootTypeProvider
	*/
	public AggregatePerEventTableNameProvider(IAggregateRootTypeProvider aggregateRootTypeProvider)
	{
		_aggregateRootTypeProvider = aggregateRootTypeProvider;
	}

	/** Get table for a specific aggregate root.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @return 
	*/
	public final String GetTable(String aggregateRootId, java.lang.Class aggregateRootType)
	{
		return aggregateRootType.getName();
	}

	/** Get all the tables of the eventstore.
	 
	 @return 
	*/
	public final Iterable<String> GetAllTables()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _aggregateRootTypeProvider.GetAllAggregateRootTypes().Select(x => x.getName());
	}
}