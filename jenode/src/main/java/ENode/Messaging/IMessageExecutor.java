package ENode.Messaging;

/** Represents a message executor.
*/
public interface IMessageExecutor<TMessage extends IMessage>
{
	/** Execute the given queue message.
	 
	 @param message
	 @param queue
	 @return 
	*/
	void Execute(TMessage message, IMessageQueue<TMessage> queue);
}