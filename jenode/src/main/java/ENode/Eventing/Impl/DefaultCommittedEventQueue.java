package ENode.Eventing.Impl;

import ENode.Messaging.Impl.*;

/** The default implementation of ICommittedEventQueue.
 
*/
public class DefaultCommittedEventQueue extends MessageQueue<EventStream> implements ICommittedEventQueue
{
	/** Parameterized constructor.
	 
	 @param queueName
	*/
	public DefaultCommittedEventQueue(String queueName)
	{
		super(queueName);
	}
}