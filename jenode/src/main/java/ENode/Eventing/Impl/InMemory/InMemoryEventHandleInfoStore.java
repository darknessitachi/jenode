package ENode.Eventing.Impl.InMemory;

/** Local in-memory implementation of IEventHandleInfoStore using ConcurrentDictionary.
 
*/
public class InMemoryEventHandleInfoStore implements IEventHandleInfoStore
{
	private final ConcurrentDictionary<EventHandleInfo, Integer> _versionDict = new ConcurrentDictionary<EventHandleInfo, Integer>();

	/** Insert an event handle info.
	 
	 @param eventId
	 @param eventHandlerTypeName
	*/
	public final void AddEventHandleInfo(Guid eventId, String eventHandlerTypeName)
	{
		_versionDict.TryAdd(new EventHandleInfo(eventId, eventHandlerTypeName), 0);
	}
	/** Check whether the given event was handled by the given event handler.
	 
	 @param eventId
	 @param eventHandlerTypeName
	 @return 
	*/
	public final boolean IsEventHandleInfoExist(Guid eventId, String eventHandlerTypeName)
	{
		return _versionDict.ContainsKey(new EventHandleInfo(eventId, eventHandlerTypeName));
	}

	private static class EventHandleInfo
	{
		private Guid privateEventId = new Guid();
		private Guid getEventId()
		{
			return privateEventId;
		}
		private void setEventId(Guid value)
		{
			privateEventId = value;
		}
		private String privateEventHandlerTypeName;
		private String getEventHandlerTypeName()
		{
			return privateEventHandlerTypeName;
		}
		private void setEventHandlerTypeName(String value)
		{
			privateEventHandlerTypeName = value;
		}

		public EventHandleInfo(Guid eventId, String eventHandlerTypeName)
		{
			setEventId(eventId);
			setEventHandlerTypeName(eventHandlerTypeName);
		}

		@Override
		public boolean equals(Object obj)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var another = (EventHandleInfo)((obj instanceof EventHandleInfo) ? obj : null);

			if (another == null)
			{
				return false;
			}
			if (another == this)
			{
				return true;
			}

			return getEventId().equals(another.EventId) && getEventHandlerTypeName().equals(another.EventHandlerTypeName);
		}
		@Override
		public int hashCode()
		{
			return getEventId().hashCode() + getEventHandlerTypeName().hashCode();
		}
	}
}