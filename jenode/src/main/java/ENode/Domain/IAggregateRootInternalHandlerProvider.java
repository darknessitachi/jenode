package ENode.Domain;

/** Defines a provider interface to provide the aggregate root internal handler.
 
*/
public interface IAggregateRootInternalHandlerProvider
{
	/** Get the internal event handler within the aggregate.
	 
	*/
	Action<AggregateRoot, Object> GetInternalEventHandler(java.lang.Class aggregateRootType, java.lang.Class eventType);
}