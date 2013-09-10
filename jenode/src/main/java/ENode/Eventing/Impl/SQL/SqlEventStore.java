package ENode.Eventing.Impl.SQL;

import ENode.Domain.*;
import ENode.Infrastructure.*;
import ENode.Infrastructure.Concurrent.*;
import ENode.Infrastructure.Dapper.*;
import ENode.Infrastructure.Serializing.*;
import ENode.Infrastructure.Sql.*;

/** The SQL implementation of IEventStore.
 
*/
public class SqlEventStore implements IEventStore
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private String _connectionString;
	private IEventTableNameProvider _eventTableProvider;
	private IJsonSerializer _jsonSerializer;
	private IDbConnectionFactory _connectionFactory;
	private IAggregateRootTypeProvider _aggregateRootTypeProvider;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param connectionString
	 @exception ArgumentNullException
	*/
	public SqlEventStore(String connectionString)
	{
		if (DotNetToJavaStringHelper.isNullOrEmpty(connectionString))
		{
			throw new ArgumentNullException("connectionString");
		}
		_connectionString = connectionString;
		_eventTableProvider = ObjectContainer.<IEventTableNameProvider>Resolve();
		_jsonSerializer = ObjectContainer.<IJsonSerializer>Resolve();
		_connectionFactory = ObjectContainer.<IDbConnectionFactory>Resolve();
		_aggregateRootTypeProvider = ObjectContainer.<IAggregateRootTypeProvider>Resolve();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Methods

	/** Append the event stream to the event store.
	 
	 @param stream
	*/
	public final void Append(EventStream stream)
	{
		if (stream == null)
		{
			return;
		}

		java.lang.Class aggregateRootType = _aggregateRootTypeProvider.GetAggregateRootType(stream.getAggregateRootName());
		IDbConnection connection = _connectionFactory.CreateConnection(_connectionString);
		String eventTable = _eventTableProvider.GetTable(stream.getAggregateRootId(), aggregateRootType);

		try
		{
			connection.Open();
			connection.Insert(BuildSqlEventStreamFrom(stream), eventTable);
		}
		catch (SqlException e)
		{
			if (connection.State == ConnectionState.Open)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
				var count = connection.GetCount(new { AggregateRootId = stream.getAggregateRootId(), Version = stream.getVersion() }, eventTable);
				if (count > 0)
				{
					throw new ConcurrentException();
				}
				else
				{
					throw e;
				}
			}
			else
			{
				throw e;
			}
		}
		finally
		{
			connection.Close();
		}
	}
	/** Query event streams from event store.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @param minStreamVersion
	 @param maxStreamVersion
	 @return 
	*/
	public final Iterable<EventStream> Query(String aggregateRootId, java.lang.Class aggregateRootType, long minStreamVersion, long maxStreamVersion)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _connectionFactory.CreateConnection(_connectionString).<Iterable<EventStream>>TryExecute((connection) =>
		{
			String eventTable = _eventTableProvider.GetTable(aggregateRootId, aggregateRootType);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var sql = String.format("select * from [%1$s] where AggregateRootId = @AggregateRootId and Version >= @MinStreamVersion and Version <= @MaxStreamVersion order by Version asc", eventTable);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			var sqlEventStreams = connection.<SqlEventStream>Query(sql, new { AggregateRootId = aggregateRootId, MinStreamVersion = minStreamVersion, MaxStreamVersion = maxStreamVersion });
			return sqlEventStreams.Select(BuildEventStreamFrom).ToList();
		}
	   );
	}
	/** Check whether an event stream is exist in the event store.
	 
	 @param aggregateRootId
	 @param aggregateRootType
	 @param id
	 @return 
	*/
	public final boolean IsEventStreamExist(String aggregateRootId, java.lang.Class aggregateRootType, Guid id)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _connectionFactory.CreateConnection(_connectionString).TryExecute((connection) =>
		{
			String eventTable = _eventTableProvider.GetTable(aggregateRootId, aggregateRootType);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			var count = connection.GetCount(new { Id = id }, eventTable);
			return count > 0;
		}
	   );
	}
	/** Query all the event streams from the event store.
	 
	 @return 
	*/
	public final Iterable<EventStream> QueryAll()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _connectionFactory.CreateConnection(_connectionString).<Iterable<EventStream>>TryExecute(connection =>
		{
			Iterable<String> eventTables = _eventTableProvider.GetAllTables();
			java.util.ArrayList<EventStream> streams = new java.util.ArrayList<EventStream>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			for (var sqlEventStreams : eventTables.Select(eventTable => String.format("select * from [%1$s] order by AggregateRootId, Version asc", eventTable)).Select(sql => connection.<SqlEventStream>Query(sql)))
			{
				streams.addAll(sqlEventStreams.Select(BuildEventStreamFrom));
			}
			return streams;
		}
	   );
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	private EventStream BuildEventStreamFrom(SqlEventStream sqlEventStream)
	{
		return new EventStream(sqlEventStream.getId(), sqlEventStream.getAggregateRootId(), sqlEventStream.getAggregateRootName(), sqlEventStream.getVersion(), sqlEventStream.getCommandId(), sqlEventStream.getTimestamp(), _jsonSerializer.getDeserialize()<Iterable<IEvent>>(sqlEventStream.getEvents()));
	}
	private SqlEventStream BuildSqlEventStreamFrom(EventStream eventStream)
	{
		SqlEventStream tempVar = new SqlEventStream();
		tempVar.setId(eventStream.getId());
		tempVar.setAggregateRootId(eventStream.getAggregateRootId());
		tempVar.setAggregateRootName(eventStream.getAggregateRootName());
		tempVar.setCommandId(eventStream.getCommandId());
		tempVar.setVersion(eventStream.getVersion());
		tempVar.setTimestamp(eventStream.getTimestamp());
		tempVar.setEvents(_jsonSerializer.Serialize(eventStream.getEvents()));
		return tempVar;
	}

	private static class SqlEventStream
	{
		private Guid privateId = new Guid();
		public final Guid getId()
		{
			return privateId;
		}
		public final void setId(Guid value)
		{
			privateId = value;
		}
		private String privateAggregateRootId;
		public final String getAggregateRootId()
		{
			return privateAggregateRootId;
		}
		public final void setAggregateRootId(String value)
		{
			privateAggregateRootId = value;
		}
		private String privateAggregateRootName;
		public final String getAggregateRootName()
		{
			return privateAggregateRootName;
		}
		public final void setAggregateRootName(String value)
		{
			privateAggregateRootName = value;
		}
		private Guid privateCommandId = new Guid();
		public final Guid getCommandId()
		{
			return privateCommandId;
		}
		public final void setCommandId(Guid value)
		{
			privateCommandId = value;
		}
		private long privateVersion;
		public final long getVersion()
		{
			return privateVersion;
		}
		public final void setVersion(long value)
		{
			privateVersion = value;
		}
		private java.util.Date privateTimestamp = new java.util.Date(0);
		public final java.util.Date getTimestamp()
		{
			return privateTimestamp;
		}
		public final void setTimestamp(java.util.Date value)
		{
			privateTimestamp = value;
		}
		private String privateEvents;
		public final String getEvents()
		{
			return privateEvents;
		}
		public final void setEvents(String value)
		{
			privateEvents = value;
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}