package ENode.Messaging.Impl.SQL;

import ENode.Infrastructure.*;
import ENode.Infrastructure.Dapper.*;
import ENode.Infrastructure.Serializing.*;
import ENode.Infrastructure.Sql.*;

/** The SQL implementation of IMessageStore.
 
*/
public class SqlMessageStore implements IMessageStore
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private String _connectionString;
	private IDbConnectionFactory _connectionFactory;
	private IQueueTableNameProvider _queueTableNameProvider;
	private IBinarySerializer _binarySerializer;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param connectionString
	 @exception ArgumentNullException
	*/
	public SqlMessageStore(String connectionString)
	{
		if (DotNetToJavaStringHelper.isNullOrEmpty(connectionString))
		{
			throw new ArgumentNullException("connectionString");
		}

		_connectionString = connectionString;
		_queueTableNameProvider = ObjectContainer.<IQueueTableNameProvider>Resolve();
		_binarySerializer = ObjectContainer.<IBinarySerializer>Resolve();
		_connectionFactory = ObjectContainer.<IDbConnectionFactory>Resolve();
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Initialize the message store with the given queue name.
	 
	 @param queueName
	*/
	public final void Initialize(String queueName)
	{
	}
	/** Persist a new message to the queue.
	 
	 @param queueName
	 @param message
	*/
	public final void AddMessage(String queueName, IMessage message)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
			String tableName = _queueTableNameProvider.GetTable(queueName);
			byte[] messageData = _binarySerializer.Serialize(message);
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			connection.Insert(new { MessageId = message.getId(), MessageData = messageData }, tableName);
		}
	   );
	}
	/** Remove a existing message from the queue.
	 
	 @param queueName
	 @param message
	*/
	public final void RemoveMessage(String queueName, IMessage message)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
			String tableName = _queueTableNameProvider.GetTable(queueName);
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			connection.Delete(new { MessageId = message.getId() }, tableName);
		}
	   );
	}
	/** Get all the existing messages of the queue.
	 
	 <typeparam name="T"></typeparam>
	 @param queueName
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <T extends class & IMessage> Iterable<T> GetMessages(String queueName)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _connectionFactory.CreateConnection(_connectionString).<Iterable<T>>TryExecute(connection =>
		{
			String tableName = _queueTableNameProvider.GetTable(queueName);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var items = connection.QueryAll(tableName);
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			return items.Select(item => _binarySerializer.getDeserialize()<T>((byte[]) item.MessageData)).ToList();
		}
	   );
	}
}