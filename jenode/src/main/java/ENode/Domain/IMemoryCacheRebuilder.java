package ENode.Domain;

/** An interface to rebuild the whole domain by using event sourcing pattern.
 
*/
public interface IMemoryCacheRebuilder
{
	/** Using event sourcing pattern to rebuild the domain by replaying all the domain events from the eventstore.
	 
	*/
	void RebuildMemoryCache();
}