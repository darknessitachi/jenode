package ENode.Domain;

/** Represents a repository of the building block of Eric Evans's DDD.
 
*/
public interface IRepository
{
	/** Get an aggregate from memory cache, if not exist, get it from event store.
	 
	 <typeparam name="T"></typeparam>
	 @param id
	 @return 
	*/
	<T extends AggregateRoot> T Get(Object id);
	/** Get an aggregate from memory cache, if not exist, get it from event store.
	 
	 @param type
	 @param id
	 @return 
	*/
	AggregateRoot Get(java.lang.Class type, Object id);
}