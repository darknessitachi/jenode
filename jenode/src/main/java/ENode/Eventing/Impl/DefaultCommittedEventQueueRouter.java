package ENode.Eventing.Impl;

/** The default implementation of ICommittedEventQueueRouter.
 
*/
public class DefaultCommittedEventQueueRouter implements ICommittedEventQueueRouter
{
	private ICommittedEventQueue[] _eventQueues;
	private int _index;

	/** Parameterized constructor.
	 
	 @param stream
	 @return 
	*/
	public final ICommittedEventQueue Route(EventStream stream)
	{
		if (_eventQueues == null)
		{
			_eventQueues = Configuration.getInstance().GetCommitedEventQueues().toArray();
		}

		RefObject<Integer> tempRef__index = new RefObject<Integer>(_index);
		ICommittedEventQueue tempVar = _eventQueues.length > 0 ? _eventQueues[(Interlocked.Increment(tempRef__index) - 1) % _eventQueues.length] : null;
		_index = tempRef__index.argvalue;
		return tempVar;
	}
}