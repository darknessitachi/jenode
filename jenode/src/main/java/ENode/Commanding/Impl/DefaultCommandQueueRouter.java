package ENode.Commanding.Impl;

/** The default implementation of ICommandQueueRouter.
 
*/
public class DefaultCommandQueueRouter implements ICommandQueueRouter
{
	private ICommandQueue[] _commandQueues;
	private int _index;

	/** Route the given command to a specified command queue.
	 
	 @param command The command
	 @return Returns the routed command queue.
	*/
	public final ICommandQueue Route(ICommand command)
	{
		if (_commandQueues == null)
		{
			_commandQueues = Configuration.getInstance().GetCommandQueues().toArray();
		}

		RefObject<Integer> tempRef__index = new RefObject<Integer>(_index);
		ICommandQueue tempVar = _commandQueues.length > 0 ? _commandQueues[(Interlocked.Increment(tempRef__index) - 1) % _commandQueues.length] : null;
		_index = tempRef__index.argvalue;
		return tempVar;
	}
}