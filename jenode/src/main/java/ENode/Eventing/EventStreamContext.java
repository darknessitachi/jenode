package ENode.Eventing;

import ENode.Infrastructure.*;
import ENode.Infrastructure.Concurrent.*;
import ENode.Messaging.*;

/** An internal class to contains the context information when processing an event stream.
 
*/
public class EventStreamContext
{
	private EventStream privateEventStream;
	public final EventStream getEventStream()
	{
		return privateEventStream;
	}
	public final void setEventStream(EventStream value)
	{
		privateEventStream = value;
	}
	private IMessageQueue<EventStream> privateQueue;
	public final IMessageQueue<EventStream> getQueue()
	{
		return privateQueue;
	}
	public final void setQueue(IMessageQueue<EventStream> value)
	{
		privateQueue = value;
	}
	private boolean privateHasConcurrentException;
	public final boolean getHasConcurrentException()
	{
		return privateHasConcurrentException;
	}
	private void setHasConcurrentException(boolean value)
	{
		privateHasConcurrentException = value;
	}
	private ErrorInfo privateErrorInfo;
	public final ErrorInfo getErrorInfo()
	{
		return privateErrorInfo;
	}
	private void setErrorInfo(ErrorInfo value)
	{
		privateErrorInfo = value;
	}

	public final void SetConcurrentException(ErrorInfo errorInfo)
	{
		if (errorInfo == null)
		{
			throw new ArgumentNullException("errorInfo");
		}
		if (!(errorInfo.getException() instanceof ConcurrentException))
		{
			throw new InvalidOperationException(String.format("Unknown exception %1$s cannot be set as concurrent exception.", errorInfo.getException().getClass().getName()));
		}
		setHasConcurrentException(true);
		setErrorInfo(errorInfo);
	}
}