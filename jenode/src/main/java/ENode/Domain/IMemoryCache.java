package ENode.Domain;

/** Represents a high speed memory cache to get or set aggregate.
 
*/
public interface IMemoryCache
{
	/** Get an aggregate from memory cache.
	 
	 @param id
	 @return 
	*/
	AggregateRoot Get(Object id);
	/** Get a strong type aggregate from memory cache.
	 
	 <typeparam name="T"></typeparam>
	 @param id
	 @return 
	*/
	<T extends AggregateRoot> T Get(Object id);
	/** Set an aggregate to memory cache.
	 
	 @param aggregateRoot
	*/
	void Set(AggregateRoot aggregateRoot);
}