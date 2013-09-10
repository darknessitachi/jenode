package ENode.Eventing.Impl;

import ENode.Messaging.Impl.*;

/** The default implementation of IUncommittedEventQueue.
 
*/
public class DefaultUncommittedEventQueue extends MessageQueue<EventStream> implements IUncommittedEventQueue
{
	/** Parameterized constructor.
	 
	 @param queueName
	*/
	public DefaultUncommittedEventQueue(String queueName)
	{
		super(queueName);
	}
}