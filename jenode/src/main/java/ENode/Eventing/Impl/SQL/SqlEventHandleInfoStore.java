package ENode.Eventing.Impl.SQL;

import ENode.Infrastructure.*;
import ENode.Infrastructure.Dapper.*;
import ENode.Infrastructure.Sql.*;

/** The SQL implementation of IEventHandleInfoStore.
 
*/
public class SqlEventHandleInfoStore implements IEventHandleInfoStore
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
	public SqlEventHandleInfoStore(String connectionString, String tableName)
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

	/** Insert an event handle info.
	 
	 @param eventId
	 @param eventHandlerTypeName
	*/
	public final void AddEventHandleInfo(Guid eventId, String eventHandlerTypeName)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			var key = new { EventHandlerTypeName = eventHandlerTypeName, EventId = eventId };
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var count = connection.GetCount(key, _tableName);
			if (count == 0)
			{
				connection.Insert(key, _tableName);
			}
		}
	   );
	}
	/** Check whether the given event was handled by the given event handler.
	 
	 @param eventId
	 @param eventHandlerTypeName
	 @return 
	*/
	public final boolean IsEventHandleInfoExist(Guid eventId, String eventHandlerTypeName)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
		return _connectionFactory.CreateConnection(_connectionString).TryExecute(connection => connection.GetCount(new { EventHandlerTypeName = eventHandlerTypeName, EventId = eventId }, _tableName) > 0);
	}
}