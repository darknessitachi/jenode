package ENode.Eventing.Impl.SQL;

/** The default implementation of IEventTableNameProvider.
 
*/
public class DefaultEventTableNameProvider implements IEventTableNameProvider
{
	private String _tableName;

	/** Parameterized constructor.
	 
	 @param tableName
	 @exception ArgumentNullException
	*/
	public DefaultEventTableNameProvider(String tableName)
	{
		if (DotNetToJavaStringHelper.isNullOrEmpty(tableName))
		{
			throw new ArgumentNullException("tableName");
		}
		_tableName = tableName;
	}
	/** Get table for a specific aggregate root.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @return 
	*/
	public final String GetTable(String aggregateRootId, java.lang.Class aggregateRootType)
	{
		return _tableName;
	}
	/** Get all the tables of the eventstore.
	 
	 @return 
	*/
	public final Iterable<String> GetAllTables()
	{
		return new String[] { _tableName };
	}
}