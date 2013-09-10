package ENode.Eventing.Impl.SQL;

import ENode.Infrastructure.*;
import ENode.Infrastructure.Dapper.*;
import ENode.Infrastructure.Sql.*;

/** The SQL implementation of IEventPublishInfoStore.
 
*/
public class SqlEventPublishInfoStore implements IEventPublishInfoStore
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private String _connectionString;
	private String _tableName;
	private IDbConnectionFactory _connectionFactory;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param connectionString
	 @param tableName
	 @exception ArgumentNullException
	*/
	public SqlEventPublishInfoStore(String connectionString, String tableName)
	{
		if (DotNetToJavaStringHelper.isNullOrEmpty(connectionString))
		{
			throw new ArgumentNullException("connectionString");
		}
		if (DotNetToJavaStringHelper.isNullOrEmpty(tableName))
		{
			throw new ArgumentNullException("tableName");
		}

		_connectionString = connectionString;
		_tableName = tableName;
		_connectionFactory = ObjectContainer.<IDbConnectionFactory>Resolve();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Insert the first published event version of aggregate.
	 
	 @param aggregateRootId
	*/
	public final void InsertFirstPublishedVersion(String aggregateRootId)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			var count = connection.GetCount(new { AggregateRootId = aggregateRootId }, _tableName);
			if (count == 0)
			{
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
				connection.Insert(new { AggregateRootId = aggregateRootId, PublishedEventStreamVersion = 1 }, _tableName);
			}
		}
	   );
	}
	/** Update the published event version of aggregate.
	 
	 @param aggregateRootId
	 @param version
	*/
	public final void UpdatePublishedVersion(String aggregateRootId, long version)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			connection.Update(new { PublishedEventStreamVersion = version }, new { AggregateRootId = aggregateRootId }, _tableName);
		}
	   );
	}
	/** Get the current event published version for the specified aggregate.
	 
	 @param aggregateRootId
	 @return 
	*/
	public final long GetEventPublishedVersion(String aggregateRootId)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
		return _connectionFactory.CreateConnection(_connectionString).TryExecute(connection => connection.<Long>GetValue(new { AggregateRootId = aggregateRootId }, _tableName, "PublishedEventStreamVersion"));
	}
}