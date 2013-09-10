package ENode.Commanding.Impl;

import ENode.Messaging.Impl.*;

/** The default implementation of ICommandProcessor.
 
*/
public class DefaultCommandProcessor extends MessageProcessor<ICommandQueue, ICommandExecutor, ICommand> implements ICommandProcessor
{
	/** Parameterized constructor.
	 
	 @param bindingQueue
	 @param workerCount
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public DefaultCommandProcessor(ICommandQueue bindingQueue, int workerCount = 1)
	public DefaultCommandProcessor(ICommandQueue bindingQueue, int workerCount)
	{
		super(bindingQueue, workerCount);
	}
}