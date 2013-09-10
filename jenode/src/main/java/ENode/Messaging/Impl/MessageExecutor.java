package ENode.Messaging.Impl;

import ENode.Messaging.IMessage;
import ENode.Messaging.IMessageExecutor;
import ENode.Messaging.IMessageQueue;

/** The abstract base implementation of IMessageExecutor.
 
 <typeparam name="TMessage"></typeparam>
*/
public abstract class MessageExecutor<TMessage extends IMessage> implements IMessageExecutor<TMessage>
{
	/** Execute the given message.
	 
	 @param message
	 @param queue
	*/
	public abstract void Execute(TMessage message, IMessageQueue<TMessage> queue);
	/** Finish the message execution, the message will be removed from the message queue.
	 
	 @param message
	 @param queue
	*/
	protected void FinishExecution(TMessage message, IMessageQueue<TMessage> queue)
	{
		queue.Complete(message);
	}
}