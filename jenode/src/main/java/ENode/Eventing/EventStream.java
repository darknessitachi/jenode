package ENode.Eventing;

import ENode.Messaging.*;

/** Represents a stream of domain event.
 
 One stream may contains several domain events, but they must belong to a single aggregate.
 
 
*/
public class EventStream extends Message implements Serializable
{
	/** Parameterized constructor.
	 
	 @param aggregateRootId
	 @param aggregateRootName
	 @param version
	 @param commandId
	 @param timestamp
	 @param events
	*/
	public EventStream(String aggregateRootId, String aggregateRootName, long version, Guid commandId, java.util.Date timestamp, Iterable<IEvent> events)
	{
		this(Guid.NewGuid(), aggregateRootId, aggregateRootName, version, commandId, timestamp, events);
	}
	/** Parameterized constructor.
	 
	 @param id
	 @param aggregateRootId
	 @param aggregateRootName
	 @param version
	 @param commandId
	 @param timestamp
	 @param events
	*/
	public EventStream(Guid id, String aggregateRootId, String aggregateRootName, long version, Guid commandId, java.util.Date timestamp, Iterable<IEvent> events)
	{
		super(id);
		setAggregateRootId(aggregateRootId);
		setAggregateRootName(aggregateRootName);
		setCommandId(commandId);
		setVersion(version);
		setTimestamp(timestamp);
		setEvents((events != null) ? events : new java.util.ArrayList<IEvent>());
	}

	/** The aggregate root id.
	 
	*/
	private String privateAggregateRootId;
	public final String getAggregateRootId()
	{
		return privateAggregateRootId;
	}
	private void setAggregateRootId(String value)
	{
		privateAggregateRootId = value;
	}
	/** The aggregate root name.
	 
	*/
	private String privateAggregateRootName;
	public final String getAggregateRootName()
	{
		return privateAggregateRootName;
	}
	private void setAggregateRootName(String value)
	{
		privateAggregateRootName = value;
	}
	/** The command id.
	 
	*/
	private Guid privateCommandId = new Guid();
	public final Guid getCommandId()
	{
		return privateCommandId;
	}
	private void setCommandId(Guid value)
	{
		privateCommandId = value;
	}
	/** The version of the event stream.
	 
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
	/** The occurred time of the event stream.
	 
	*/
	private java.util.Date privateTimestamp = new java.util.Date(0);
	public final java.util.Date getTimestamp()
	{
		return privateTimestamp;
	}
	private void setTimestamp(java.util.Date value)
	{
		privateTimestamp = value;
	}
	/** The domain events of the event stream.
	 
	*/
	private Iterable<IEvent> privateEvents;
	public final Iterable<IEvent> getEvents()
	{
		return privateEvents;
	}
	private void setEvents(Iterable<IEvent> value)
	{
		privateEvents = value;
	}

	/** Check if a given type of domain event exist in the current event stream.
	 
	 <typeparam name="TEvent"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <TEvent extends class & IEvent> boolean HasEvent()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return getEvents().Any(x => x.getClass() == TEvent.class);
	}
	/** Find a domain event with the given event type from the current event stream.
	 
	 <typeparam name="TEvent"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <TEvent extends class & IEvent> TEvent FindEvent()
	{
		Object tempVar = getEvents().SingleOrDefault(x => x.getClass() == TEvent.class);
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return (TEvent)((tempVar instanceof TEvent) ? tempVar : null);
	}
	/** Get all the event type names, sperated by | character.
	 
	 @return 
	*/
	public final String GetEventInformation()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return DotNetToJavaStringHelper.join("|", getEvents().Select(x => x.getClass().getName()));
	}
	/** Get the whole event stream string information.
	 
	 @return 
	*/
	public final String GetStreamInformation()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		java.util.ArrayList<Object> items = new java.util.ArrayList<Object>(java.util.Arrays.asList(new Object[] { getId(), getAggregateRootName(), getAggregateRootId(), getVersion(), DotNetToJavaStringHelper.join("-", getEvents().Select(x => x.getClass().getName())), getCommandId(), getTimestamp() }));
		return DotNetToJavaStringHelper.join("|", items);
	}
	/** Overrides to return the whole event stream information.
	 
	 @return 
	*/
	@Override
	public String toString()
	{
		return GetEventInformation();
	}
}