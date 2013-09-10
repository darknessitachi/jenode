package ENode.Eventing.Impl;

import ENode.Messaging.Impl.*;

/** The default implementation of ICommittedEventProcessor.
 
*/
public class DefaultCommittedEventProcessor extends MessageProcessor<ICommittedEventQueue, ICommittedEventExecutor, EventStream> implements ICommittedEventProcessor
{
	/** Parameterized constructor.
	 
	 @param bindingQueue
	 @param workerCount
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public DefaultCommittedEventProcessor(ICommittedEventQueue bindingQueue, int workerCount = 1)
	public DefaultCommittedEventProcessor(ICommittedEventQueue bindingQueue, int workerCount)
	{
		super(bindingQueue, workerCount);
	}
}