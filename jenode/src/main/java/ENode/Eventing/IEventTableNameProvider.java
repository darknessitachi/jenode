package ENode.Eventing;

/** Represents a provider to provide the eventstore event table name.
 
*/
public interface IEventTableNameProvider
{
	/** Get table for a specific aggregate root.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @return 
	*/
	String GetTable(String aggregateRootId, java.lang.Class aggregateRootType);
	/** Get all the tables of the eventstore.
	 
	 @return 
	*/
	Iterable<String> GetAllTables();
}