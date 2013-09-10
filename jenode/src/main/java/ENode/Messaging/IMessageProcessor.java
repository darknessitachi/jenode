package ENode.Messaging;

/** Represents a message processor.
*/
//C# TO JAVA CONVERTER TODO TASK: Java does not allow specifying covariance or contravariance in a generic type list:
//ORIGINAL LINE: public interface IMessageProcessor<out TQueue, TMessage> where TQueue : class, IMessageQueue<TMessage> where TMessage : class, IMessage
public interface IMessageProcessor<TQueue extends IMessageQueue<TMessage>, TMessage extends IMessage>
{
	/** Represents the binding message queue.
	*/
	TQueue getBindingQueue();
	/** Initialize the message processor.
	*/
	void Initialize();
	/** Start the message processor, and it will start to fetch message from the binding message queue.
	*/
	void Start();
}