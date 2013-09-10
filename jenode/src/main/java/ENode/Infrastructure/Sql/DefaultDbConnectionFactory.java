package ENode.Infrastructure.Sql;

/** The default implementation of IDbConnectionFactory.
 
*/
public class DefaultDbConnectionFactory implements IDbConnectionFactory
{
	/** Create a db connection instance with the given connectionString.
	 
	 @param connectionString
	 @return 
	*/
	public final IDbConnection CreateConnection(String connectionString)
	{
		return new SqlConnection(connectionString);
	}
}