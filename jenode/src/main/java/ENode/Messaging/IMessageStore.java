package ENode.Messaging;

/** Represents a message store which used to persist queue messages.
 
*/
public interface IMessageStore
{
	/** Initialize the given message queue.
	 
	 @param queueName The name of the queue.
	*/
	void Initialize(String queueName);
	/** Persist a new message to the queue.
	 
	 @param queueName The name of the queue.
	 @param message The message object.
	*/
	void AddMessage(String queueName, IMessage message);
	/** Remove a existing message from the queue.
	 
	 @param queueName The name of the queue.
	 @param message The message object.
	*/
	void RemoveMessage(String queueName, IMessage message);
	/** Get all the existing messages of the queue.
	 
	 <typeparam name="T">The type of the message.</typeparam>
	 @param queueName The name of the queue.
	 @return Returns all the existing messages.
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	<T extends  IMessage> Iterable<T> GetMessages(String queueName);
}