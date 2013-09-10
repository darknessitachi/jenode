package ENode.Messaging.Impl.SQL;

/** The default implementation of IQueueTableNameProvider.
 
*/
public class DefaultQueueTableNameProvider implements IQueueTableNameProvider
{
	private String _tableNameFormat;

	/** Parameterized constructor.
	 
	 @param tableNameFormat
	*/
	public DefaultQueueTableNameProvider(String tableNameFormat)
	{
		_tableNameFormat = tableNameFormat;
	}
	/** Get the formatted table name by the given queue name and the current table name format.
	 
	 @param queueName
	 @return 
	*/
	public final String GetTable(String queueName)
	{
		return !DotNetToJavaStringHelper.isNullOrEmpty(_tableNameFormat) ? String.format(_tableNameFormat, queueName) : queueName;
	}
}