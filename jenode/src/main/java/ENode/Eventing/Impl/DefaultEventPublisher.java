package ENode.Eventing.Impl;

/** The default implementation of IEventPublisher.
 
*/
public class DefaultEventPublisher implements IEventPublisher
{
	private ICommittedEventQueueRouter _eventQueueRouter;

	/** Parameterized constructor.
	 
	 @param eventQueueRouter
	*/
	public DefaultEventPublisher(ICommittedEventQueueRouter eventQueueRouter)
	{
		_eventQueueRouter = eventQueueRouter;
	}

	/** Publish a given committed event stream to all the event handlers.
	 
	 @param stream
	*/
	public final void Publish(EventStream stream)
	{
		ICommittedEventQueue eventQueue = _eventQueueRouter.Route(stream);
		if (eventQueue == null)
		{
			throw new RuntimeException("Could not route event stream to an appropriate committed event queue.");
		}

		eventQueue.Enqueue(stream);
	}
}