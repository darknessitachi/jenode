package ENode.Eventing.Impl;

import ENode.Messaging.Impl.*;

/** The default implementation of IUncommittedEventProcessor.
 
*/
public class DefaultUncommittedEventProcessor extends MessageProcessor<IUncommittedEventQueue, IUncommittedEventExecutor, EventStream> implements IUncommittedEventProcessor
{
	/** Parameterized constructor.
	 
	 @param bindingQueue
	 @param workerCount
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public DefaultUncommittedEventProcessor(IUncommittedEventQueue bindingQueue, int workerCount = 1)
	public DefaultUncommittedEventProcessor(IUncommittedEventQueue bindingQueue, int workerCount)
	{
		super(bindingQueue, workerCount);
	}
}