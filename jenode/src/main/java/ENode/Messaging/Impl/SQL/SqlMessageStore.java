package ENode.Messaging.Impl.SQL;

import ENode.ArgumentNullException;
import ENode.Infrastructure.ObjectContainer;
import ENode.Infrastructure.Serializing.IBinarySerializer;
import ENode.Infrastructure.Sql.IDbConnectionFactory;
import ENode.Messaging.IMessage;
import ENode.Messaging.IMessageStore;
import ENode.Messaging.IQueueTableNameProvider;

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
		if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(connectionString))
		{
			throw new IllegalArgumentException("connectionString");
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
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var tableName = _queueTableNameProvider.GetTable(queueName);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var messageData = _binarySerializer.Serialize(message);
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			connection.Insert(new {MessageId = message.Id, MessageData = messageData}, tableName);
		}
	   );
	}
	/** Remove a existing message from the queue.
	 
	 @param queueName
	 @param message
	*/
	public final void RemoveMessage(String queueName, IMessage message)
	{
		_connectionFactory.CreateConnection(_connectionString).TryExecute(connection =>
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var tableName = _queueTableNameProvider.GetTable(queueName);
//C# TO JAVA CONVERTER TODO TASK: This type of object initializer has no direct Java equivalent:
			connection.Delete(new {MessageId = message.Id}, tableName);
		}
	   );
	}
	/** Get all the existing messages of the queue.
	 
	 <typeparam name="T"></typeparam>
	 @param queueName
	 @return 
	*/
	public final <T extends IMessage> Iterable<T> GetMessages(String queueName)
	{
		return _connectionFactory.CreateConnection(_connectionString).<Iterable<T>>TryExecute(connection =>
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var tableName = _queueTableNameProvider.GetTable(queueName);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var items = connection.QueryAll(tableName);
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: return items.Select(item => _binarySerializer.Deserialize<T>((byte[]) item.MessageData)).ToList();
			return items.Select(item => _binarySerializer.<T>Deserialize((byte[]) item.MessageData)).ToList();
		}
	   );
	}
}
