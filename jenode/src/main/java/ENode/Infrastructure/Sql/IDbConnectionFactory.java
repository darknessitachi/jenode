package ENode.Infrastructure.Sql;

/** Represents a factory to create db connection.
 
*/
public interface IDbConnectionFactory
{
	/** Create a new db connection with the given connection string.
	 
	 @return 
	*/
	IDbConnection CreateConnection(String connectionString);
}