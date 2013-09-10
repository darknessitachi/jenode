package ENode.Commanding;

import ENode.Domain.*;

/** Represents a context environment for command handler handling command.
 
*/
public interface ICommandContext
{
	/** Add a new aggregate into the current context.
	 
	 @param aggregateRoot
	*/
	void Add(AggregateRoot aggregateRoot);
	/** Get an aggregate from the current context.
	 
	 1. If the aggregate already exist in the current context, then return it directly;
	 2. If not exist then try to get it from memory cache;
	 3. If still not exist then try to get it from event store;
	 Finally, if the specified aggregate not found, then AggregateRootNotFoundException will be raised; otherwise, return the found aggregate.
	 
	 
	 <typeparam name="T"></typeparam>
	 @param id
	 @return 
	*/
	<T extends AggregateRoot> T Get(Object id);
	/** Get an aggregate from the current context.
	 
	 1. If the aggregate already exist in the current context, then return it directly;
	 2. If not exist then try to get it from memory cache;
	 3. If still not exist then try to get it from event store;
	 Finally, if the specified aggregate not found, return null; otherwise, return the found aggregate.
	 
	 
	 <typeparam name="T"></typeparam>
	 @param id
	 @return 
	*/
	<T extends AggregateRoot> T GetOrDefault(Object id);
}