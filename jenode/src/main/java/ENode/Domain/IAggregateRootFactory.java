package ENode.Domain;

/** Defines a factory to create empty aggregate root.
 
*/
public interface IAggregateRootFactory
{
	/** Create an empty aggregate root with the given type.
	 
	*/
	AggregateRoot CreateAggregateRoot(java.lang.Class aggregateRootType);
}