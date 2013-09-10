package ENode.Eventing.Impl;

/** The default implementation of IUncommittedEventQueueRouter.
 
*/
public class DefaultUncommittedEventQueueRouter implements IUncommittedEventQueueRouter
{
	private IUncommittedEventQueue[] _eventQueues;
	private int _index;

	/** Route a available uncommitted event queue for the given event stream message.
	 
	 @param stream
	 @return 
	*/
	public final IUncommittedEventQueue Route(EventStream stream)
	{
		if (_eventQueues == null)
		{
			_eventQueues = Configuration.getInstance().GetUncommitedEventQueues().toArray();
		}

		RefObject<Integer> tempRef__index = new RefObject<Integer>(_index);
		IUncommittedEventQueue tempVar = _eventQueues.length > 0 ? _eventQueues[(Interlocked.Increment(tempRef__index) - 1) % _eventQueues.length] : null;
		_index = tempRef__index.argvalue;
		return tempVar;
	}
}