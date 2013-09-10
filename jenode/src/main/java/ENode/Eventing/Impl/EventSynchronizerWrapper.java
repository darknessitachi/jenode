package ENode.Eventing.Impl;

/** The default implementation of IEventSynchronizer.
 
 <typeparam name="T"></typeparam>
*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
public class EventSynchronizerWrapper<T extends class & IEvent> implements IEventSynchronizer
{
	private IEventSynchronizer<T> _synchronizer;

	/** Parameterized constructor.
	 
	 @param synchronizer
	*/
	public EventSynchronizerWrapper(IEventSynchronizer<T> synchronizer)
	{
		_synchronizer = synchronizer;
	}

	/** Executed before persisting the event.
	 
	 @param evnt
	*/
	public final void OnBeforePersisting(IEvent evnt)
	{
		_synchronizer.OnBeforePersisting((T)((evnt instanceof T) ? evnt : null));
	}
	/** Executed after the event was persisted.
	 
	 @param evnt
	*/
	public final void OnAfterPersisted(IEvent evnt)
	{
		_synchronizer.OnAfterPersisted((T)((evnt instanceof T) ? evnt : null));
	}
	/** Represents the inner generic IEventSynchronizer.
	 
	 @return 
	*/
	public final Object GetInnerSynchronizer()
	{
		return _synchronizer;
	}
}