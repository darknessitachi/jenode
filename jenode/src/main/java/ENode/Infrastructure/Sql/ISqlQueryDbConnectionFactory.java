package ENode.Infrastructure.Sql;

/** Represents a factory to create sql query db connection.
 
*/
public interface ISqlQueryDbConnectionFactory
{
	/** Create a new sql query db connection.
	 
	 @return 
	*/
	IDbConnection CreateConnection();
}