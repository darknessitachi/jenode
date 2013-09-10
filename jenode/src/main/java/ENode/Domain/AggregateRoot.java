package ENode.Domain;

import ENode.Eventing.*;
import ENode.Infrastructure.*;
import ENode.Snapshoting.*;

/** Abstract base aggregate root class.
 
*/
public abstract class AggregateRoot implements Serializable
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private java.util.LinkedList<IEvent> _uncommittedEvents;
	private static IAggregateRootInternalHandlerProvider _eventHandlerProvider = ObjectContainer.<IAggregateRootInternalHandlerProvider>Resolve();

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructurs

	/** Default constructor.
	 
	*/
	protected AggregateRoot()
	{
		_uncommittedEvents = new java.util.LinkedList<IEvent>();
	}
	/** Parameterized constructor with an uniqueId.
	 
	 @param uniqueId The string uniqueId.
	*/
	protected AggregateRoot(String uniqueId)
	{
		this();
		setUniqueId(uniqueId);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Properties

	/** Represents the uniqueId of the aggregate root.
	 
	*/
	private String privateUniqueId;
	public final String getUniqueId()
	{
		return privateUniqueId;
	}
	protected final void setUniqueId(String value)
	{
		privateUniqueId = value;
	}
	/** Represents the current event stream version of the aggregate root.
	 
	 This version record the total event stream count of the current aggregate root, this version is always continuous.
	 
	 
	*/
	private long privateVersion;
	public final long getVersion()
	{
		return privateVersion;
	}
	private void setVersion(long value)
	{
		privateVersion = value;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Public Methods

	/** Used for DCI pattern support. This method will make the aggregate root act as a specified role interface.
	 
	 Note: the aggregate must implement the role interface, otherwise exception will be raised.
	 
	 
	 <typeparam name="TRole">The role interface type.</typeparam>
	 @return Returns the current aggregate root which its type is converted to the role interface.
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <TRole extends class> TRole ActAs()
	{
		if (!TRole.class.IsInterface)
		{
			throw new RuntimeException(String.format("TRole '%1$s' must be an interface.", TRole.class.FullName));
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var role = (TRole)((this instanceof TRole) ? this : null);

		if (role == null)
		{
			throw new RuntimeException(String.format("AggregateRoot '%1$s' can not act as role '%2$s'.", getClass().FullName, TRole.class.FullName));
		}

		return role;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Protected Methods

	/** Raise a domain event.
	 
	 The event first will be handled by the current aggregate root, and then be queued in the local queue of the current aggregate root.
	 
	 
	 @param evnt The domain event to be raised.
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	protected final <T extends class & IEvent> void RaiseEvent(T evnt)
	{
		HandleEvent(evnt);
		QueueEvent(evnt);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Internal Methods

	/** Get all the uncommitted events of the current aggregate root.
	 
	*/
	public final Iterable<IEvent> GetUncommittedEvents()
	{
		return _uncommittedEvents;
	}
	/** Replay the given event stream.
	 
	 @param eventStream
	*/
	public final void ReplayEventStream(EventStream eventStream)
	{
		ReplayEventStreams(new EventStream[] { eventStream });
	}
	/** Replay the given event streams.
	 
	*/
	public final void ReplayEventStreams(Iterable<EventStream> eventStreams)
	{
		if (_uncommittedEvents.Any())
		{
			_uncommittedEvents.clear();
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var eventStream : eventStreams)
		{
			if (eventStream.Version == 1)
			{
				setUniqueId(eventStream.AggregateRootId);
			}
			VerifyEvent(eventStream);
			ApplyEvent(eventStream);
		}
	}
	/** Initialize from the given snapshot.
	 
	*/
	public final void InitializeFromSnapshot(Snapshot snapshot)
	{
		setUniqueId(snapshot.getAggregateRootId());
		setVersion(snapshot.getVersion());
		_uncommittedEvents = new java.util.LinkedList<IEvent>();
		if (_eventHandlerProvider == null)
		{
			_eventHandlerProvider = ObjectContainer.<IAggregateRootInternalHandlerProvider>Resolve();
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	/** Handle the given event and update the aggregate root status.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	private <T extends class & IEvent> void HandleEvent(T evnt)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var eventHandler = (IEventHandler<T>)((this instanceof IEventHandler<T>) ? this : null);
		if (eventHandler != null)
		{
			eventHandler.Handle(evnt);
		}
		else
		{
			Action<AggregateRoot, Object> handler = _eventHandlerProvider.GetInternalEventHandler(getClass(), evnt.getClass());
			if (handler == null)
			{
				throw new RuntimeException(String.format("Event handler not found on %1$s for %2$s.", getClass().FullName, evnt.getClass().FullName));
			}

			handler(this, evnt);
		}
	}
	/** Verify whether the given event stream can be applied on the current aggregate root.
	 
	*/
	private void VerifyEvent(EventStream eventStream)
	{
		if (!getUniqueId().equals(eventStream.getAggregateRootId()))
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var errorMessage = String.format("Cannot apply event stream to aggregate root as the AggregateRootId not matched. EventStream Id:%1$s, AggregateRootId:%2$s; Current AggregateRootId:%3$s", eventStream.getId(), eventStream.getAggregateRootId(), getUniqueId());
			throw new RuntimeException(errorMessage);
		}

		if (eventStream.getVersion() != getVersion() + 1)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var errorMessage = String.format("Cannot apply event stream to aggregate root as the version not matched. EventStream Id:%1$s, Version:%2$s; Current AggregateRoot Version:%3$s", eventStream.getId(), eventStream.getVersion(), getVersion());
			throw new RuntimeException(errorMessage);
		}
	}
	/** Apply all the events of the given event stream to the current aggregate root.
	 
	 @param eventStream
	*/
	private void ApplyEvent(EventStream eventStream)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var evnt : eventStream.getEvents())
		{
			HandleEvent(evnt);
		}
		setVersion(eventStream.getVersion());
	}
	/** Queue a uncommitted event into the local event queue.
	 
	*/
	private void QueueEvent(IEvent uncommittedEvent)
	{
		if (_uncommittedEvents == null)
		{
			_uncommittedEvents = new java.util.LinkedList<IEvent>();
		}
		_uncommittedEvents.offer(uncommittedEvent);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}