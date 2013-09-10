package ENode.Domain;

/** Represents a provider to provide the aggregate root type information.
 
*/
public interface IAggregateRootTypeProvider
{
	/** Get the aggregate root type name by the aggregate root type.
	 
	 @return 
	*/
	String GetAggregateRootTypeName(java.lang.Class aggregateRootType);
	/** Get the aggregate root type by the aggregate root type name.
	 
	 @return 
	*/
	java.lang.Class GetAggregateRootType(String name);
	/** Get all the aggregate root types.
	 
	 @return 
	*/
	Iterable<java.lang.Class> GetAllAggregateRootTypes();
}