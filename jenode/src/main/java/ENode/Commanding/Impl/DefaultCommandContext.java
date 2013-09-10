package ENode.Commanding.Impl;

import ENode.Domain.*;

/** The default implementation of command context interface and tracking context interface.
 
*/
public class DefaultCommandContext implements ICommandContext, ITrackingContext
{
	private java.util.List<AggregateRoot> _trackingAggregateRoots;
	private IRepository _repository;

	/** Parameterized constructor.
	 
	 @param repository
	*/
	public DefaultCommandContext(IRepository repository)
	{
		_trackingAggregateRoots = new java.util.ArrayList<AggregateRoot>();
		_repository = repository;
	}

	/** Add an aggregate root to the context.
	 
	 @param aggregateRoot The aggregate root to add.
	 @exception ArgumentNullException Throwed when the aggregate root is null.
	*/
	public final void Add(AggregateRoot aggregateRoot)
	{
		if (aggregateRoot == null)
		{
			throw new ArgumentNullException("aggregateRoot");
		}

		_trackingAggregateRoots.add(aggregateRoot);
	}
	/** Get the aggregate from the context.
	 
	 @param id The id of the aggregate root.
	 <typeparam name="T">The type of the aggregate root.</typeparam>
	 @return The found aggregate root.
	 @exception ArgumentNullException Throwed when the id is null.
	 @exception AggregateRootNotFoundException Throwed when the aggregate root not found.
	*/
	public final <T extends AggregateRoot> T Get(Object id)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var aggregateRoot = GetOrDefault<T>(id);

		if (aggregateRoot == null)
		{
			throw new AggregateRootNotFoundException(id.toString(), T.class);
		}

		return aggregateRoot;
	}
	/** Get the aggregate from the context, if the aggregate root not exist, returns null.
	 
	 @param id The id of the aggregate root.
	 <typeparam name="T">The type of the aggregate root.</typeparam>
	 @return If the aggregate root was found, then returns it; otherwise, returns null.
	 @exception ArgumentNullException Throwed when the id is null.
	*/
	public final <T extends AggregateRoot> T GetOrDefault(Object id)
	{
		if (id == null)
		{
			throw new ArgumentNullException("id");
		}

		String aggregateRootId = id.toString();

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var aggregateRoot = _trackingAggregateRoots.SingleOrDefault(x => aggregateRootId.equals(x.UniqueId));
		if (aggregateRoot != null)
		{
			return (T)((aggregateRoot instanceof T) ? aggregateRoot : null);
		}

		aggregateRoot = _repository.getGet()<T>(aggregateRootId);

		if (aggregateRoot != null)
		{
			_trackingAggregateRoots.add(aggregateRoot);
		}

		return (T)((aggregateRoot instanceof T) ? aggregateRoot : null);
	}
	/** Returns all the tracked aggregate roots of the current context.
	 
	 @return 
	*/
	public final Iterable<AggregateRoot> GetTrackedAggregateRoots()
	{
		return _trackingAggregateRoots;
	}
	/** Clear all the tracked aggregate roots of the current context.
	 
	*/
	public final void Clear()
	{
		_trackingAggregateRoots.clear();
	}
}