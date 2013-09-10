package ENode.Eventing.Impl;

/** The default implementation of IEventHandler.
 
 <typeparam name="T"></typeparam>
*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public class EventHandlerWrapper<T extends class & IEvent> implements IEventHandler
{
	private IEventHandler<T> _eventHandler;

	/** Parameterized constructor.
	 
	 @param eventHandler
	*/
	public EventHandlerWrapper(IEventHandler<T> eventHandler)
	{
		_eventHandler = eventHandler;
	}

	/** Handle the given event.
	 
	 @param evnt
	*/
	public final void Handle(Object evnt)
	{
		_eventHandler.Handle((T)((evnt instanceof T) ? evnt : null));
	}

	/** Get the inner event handler.
	 
	 @return 
	*/
	public final Object GetInnerEventHandler()
	{
		return _eventHandler;
	}
}