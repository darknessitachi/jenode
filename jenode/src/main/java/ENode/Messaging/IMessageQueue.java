package ENode.Messaging;

/** Represents a message queue for storing messages and also support transaction and durable messages.
*/
public interface IMessageQueue<T extends IMessage>
{
	/** The name of the queue.
	*/
	String getName();
	/** Initialize the queue.
	*/
	void Initialize();
	/** Add an message to the queue.
	 
	 The message will be persisted first, and then be added to in-memory queue.
	 
	 
	 @param message
	*/
	void Enqueue(T message);
	/** Remove the top message from the queue.
	*/
	T Dequeue();
	/** Notify the queue that the given message has been handled, and it can be removed from queue.
	 
	 @param message
	*/
	void Complete(T message);
}