package ENode.Eventing.Impl;

/** The default implementation of IEventSender.
 
*/
public class DefaultEventSender implements IEventSender
{
	private IUncommittedEventQueueRouter _eventQueueRouter;

	/** Parameterized constructor.
	 
	 @param eventQueueRouter
	*/
	public DefaultEventSender(IUncommittedEventQueueRouter eventQueueRouter)
	{
		_eventQueueRouter = eventQueueRouter;
	}
	/** Send the uncommitted event stream to process asynchronously.
	 
	 @param eventStream
	*/
	public final void Send(EventStream eventStream)
	{
		IUncommittedEventQueue eventQueue = _eventQueueRouter.Route(eventStream);
		if (eventQueue == null)
		{
			throw new RuntimeException("Could not route event stream to an appropriate uncommitted event queue.");
		}

		eventQueue.Enqueue(eventStream);
	}
}