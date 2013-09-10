package ENode.Commanding.Impl;

import ENode.Messaging.Impl.*;

/** The default implementation of ICommandQueue.
 
*/
public class DefaultCommandQueue extends MessageQueue<ICommand> implements ICommandQueue
{
	/** Parameterized constructor.
	 
	 @param queueName The name of the queue.
	*/
	public DefaultCommandQueue(String queueName)
	{
		super(queueName);
	}
}