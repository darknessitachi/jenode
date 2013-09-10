package ENode.Eventing.Impl;

import ENode.Infrastructure.Logging.*;
import ENode.Infrastructure.Retring.*;
import ENode.Messaging.*;
import ENode.Messaging.Impl.*;

/** 
 The default implementation of ICommittedEventExecutor.
 
*/
public class DefaultCommittedEventExecutor extends MessageExecutor<EventStream> implements ICommittedEventExecutor
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private IEventHandlerProvider _eventHandlerProvider;
	private IEventPublishInfoStore _eventPublishInfoStore;
	private IEventHandleInfoStore _eventHandleInfoStore;
	private IRetryService _retryService;
	private ILogger _logger;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param eventHandlerProvider
	 @param eventPublishInfoStore
	 @param eventHandleInfoStore
	 @param retryService
	 @param loggerFactory
	*/
	public DefaultCommittedEventExecutor(IEventHandlerProvider eventHandlerProvider, IEventPublishInfoStore eventPublishInfoStore, IEventHandleInfoStore eventHandleInfoStore, IRetryService retryService, ILoggerFactory loggerFactory)
	{
		_eventHandlerProvider = eventHandlerProvider;
		_eventPublishInfoStore = eventPublishInfoStore;
		_eventHandleInfoStore = eventHandleInfoStore;
		_retryService = retryService;
		_logger = loggerFactory.Create(getClass().getName());
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Execute the given event stream.
	 
	 @param eventStream
	 @param queue
	*/
	@Override
	public void Execute(EventStream eventStream, IMessageQueue<EventStream> queue)
	{
		EventStreamContext tempVar = new EventStreamContext();
		tempVar.setEventStream(eventStream);
		tempVar.java.util.LinkedList = queue;
		TryDispatchEventsToEventHandlers(tempVar);
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	private void TryDispatchEventsToEventHandlers(EventStreamContext context)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		Func<Boolean> tryDispatchEvents = () =>
		{
			EventStream eventStream = context.getEventStream();
			switch (eventStream.getVersion())
			{
				case 1:
					DispatchEventsToHandlers(eventStream);
					return true;
				default:
					long lastPublishedVersion = _eventPublishInfoStore.GetEventPublishedVersion(eventStream.getAggregateRootId());
					if (lastPublishedVersion + 1 == eventStream.getVersion())
					{
						DispatchEventsToHandlers(eventStream);
						return true;
					}
					return lastPublishedVersion + 1 > eventStream.getVersion();
			}
		}

		try
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			_retryService.TryAction("TryDispatchEvents", tryDispatchEvents, 3, () => Clear(context));
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when dispatching events:%1$s", context.getEventStream().GetStreamInformation()), ex);
		}
	}
	private void DispatchEventsToHandlers(EventStream stream)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var evnt : stream.getEvents())
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var handler : _eventHandlerProvider.GetEventHandlers(evnt.getClass()))
			{
				var currentEvent = evnt;
				var currentHandler = handler;
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				_retryService.TryAction("DispatchEventToHandler", () => DispatchEventToHandler(currentEvent, currentHandler), 3, () => { });
			}
		}
	}
	private boolean DispatchEventToHandler(IEvent evnt, IEventHandler handler)
	{
		try
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var eventHandlerTypeName = handler.GetInnerEventHandler().getClass().FullName;
			if (_eventHandleInfoStore.IsEventHandleInfoExist(evnt.getId(), eventHandlerTypeName))
			{
				return true;
			}

			handler.Handle(evnt);
			_eventHandleInfoStore.AddEventHandleInfo(evnt.getId(), eventHandlerTypeName);
			return true;
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when %1$s handling %2$s.", handler.GetInnerEventHandler().getClass().getName(), evnt.getClass().getName()), ex);
			return false;
		}
	}
	private void UpdatePublishedEventStreamVersion(EventStream stream)
	{
		if (stream.getVersion() == 1)
		{
			_eventPublishInfoStore.InsertFirstPublishedVersion(stream.getAggregateRootId());
		}
		else
		{
			_eventPublishInfoStore.UpdatePublishedVersion(stream.getAggregateRootId(), stream.getVersion());
		}
	}
	private void Clear(EventStreamContext context)
	{
		UpdatePublishedEventStreamVersion(context.getEventStream());
		FinishExecution(context.getEventStream(), context.getQueue());
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}