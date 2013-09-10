package ENode.Infrastructure.Sql;

/** The default implementation of ISqlQueryDbConnectionFactory.
 
*/
public class DefaultSqlQueryDbConnectionFactory implements ISqlQueryDbConnectionFactory
{
	private String _connectionString;

	/** Parameterized constructor.
	 
	 @param connectionString
	 @exception ArgumentNullException
	*/
	public DefaultSqlQueryDbConnectionFactory(String connectionString)
	{
		if (connectionString == null)
		{
			throw new ArgumentNullException("connectionString");
		}
		_connectionString = connectionString;
	}

	/** Create a db connection instance.
	 
	 @return 
	*/
	public final IDbConnection CreateConnection()
	{
		return new SqlConnection(_connectionString);
	}
}