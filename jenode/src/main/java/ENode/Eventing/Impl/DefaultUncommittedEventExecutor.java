package ENode.Eventing.Impl;

import ENode.Commanding.*;
import ENode.Domain.*;
import ENode.Infrastructure.*;
import ENode.Infrastructure.Concurrent.*;
import ENode.Infrastructure.Logging.*;
import ENode.Infrastructure.Retring.*;
import ENode.Messaging.*;
import ENode.Messaging.Impl.*;

/** The default implementation of IUncommittedEventExecutor.
 
*/
public class DefaultUncommittedEventExecutor extends MessageExecutor<EventStream> implements IUncommittedEventExecutor
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private IProcessingCommandCache _processingCommandCache;
	private ICommandAsyncResultManager _commandAsyncResultManager;
	private IAggregateRootTypeProvider _aggregateRootTypeProvider;
	private IAggregateRootFactory _aggregateRootFactory;
	private IMemoryCache _memoryCache;
	private IRepository _repository;
	private IRetryCommandService _retryCommandService;
	private IEventStore _eventStore;
	private IEventPublisher _eventPublisher;
	private IRetryService _retryService;
	private IEventSynchronizerProvider _eventSynchronizerProvider;
	private ILogger _logger;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param processingCommandCache
	 @param commandAsyncResultManager
	 @param aggregateRootTypeProvider
	 @param aggregateRootFactory
	 @param memoryCache
	 @param repository
	 @param retryCommandService
	 @param eventStore
	 @param eventPublisher
	 @param retryService
	 @param eventSynchronizerProvider
	 @param loggerFactory
	*/
	public DefaultUncommittedEventExecutor(IProcessingCommandCache processingCommandCache, ICommandAsyncResultManager commandAsyncResultManager, IAggregateRootTypeProvider aggregateRootTypeProvider, IAggregateRootFactory aggregateRootFactory, IMemoryCache memoryCache, IRepository repository, IRetryCommandService retryCommandService, IEventStore eventStore, IEventPublisher eventPublisher, IRetryService retryService, IEventSynchronizerProvider eventSynchronizerProvider, ILoggerFactory loggerFactory)
	{
		_processingCommandCache = processingCommandCache;
		_commandAsyncResultManager = commandAsyncResultManager;
		_aggregateRootTypeProvider = aggregateRootTypeProvider;
		_aggregateRootFactory = aggregateRootFactory;
		_memoryCache = memoryCache;
		_repository = repository;
		_retryCommandService = retryCommandService;
		_eventStore = eventStore;
		_eventPublisher = eventPublisher;
		_retryService = retryService;
		_eventSynchronizerProvider = eventSynchronizerProvider;
		_logger = loggerFactory.Create(getClass().getName());
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Execute the given event stream.
	 
	 @param message
	 @param queue
	*/
	@Override
	public void Execute(EventStream message, IMessageQueue<EventStream> queue)
	{
		EventStreamContext tempVar = new EventStreamContext();
		tempVar.setEventStream(message);
		tempVar.java.util.LinkedList = queue;
		EventStreamContext context = tempVar;

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		Func<Boolean> tryCommitEvents = () =>
		{
			try
			{
				return CommitEvents(context);
			}
			catch (RuntimeException ex)
			{
				_logger.Error(String.format("Exception raised when committing events:%1$s.", context.getEventStream().GetStreamInformation()), ex);
				return false;
			}
		}

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_retryService.TryAction("TryCommitEvents", tryCommitEvents, 3, () =>
		{
		}
	   );
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	private boolean CommitEvents(EventStreamContext context)
	{
		SynchronizeResult synchronizeResult = TryCallSynchronizersBeforeEventPersisting(context.getEventStream());

		switch (synchronizeResult.getStatus())
		{
			case SynchronizerConcurrentException:
				return false;
			case Failed:
				Clear(context, synchronizeResult.getErrorInfo());
				return true;
			default:
			{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				Action persistSuccessAction = () =>
				{
					if (context.getHasConcurrentException())
					{
						TryRefreshMemoryCache(context.getEventStream());
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
						RetryCommand(context, context.getErrorInfo(), () => FinishExecution(context.getEventStream(), context.getQueue()));
					}
					else
					{
						TryRefreshMemoryCache(context.getEventStream());
						TryCallSynchronizersAfterEventPersisted(context.getEventStream());
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
						TryPublishEvents(context.getEventStream(), () => Clear(context));
					}
				}

				TryPersistEvents(context, persistSuccessAction);

				return true;
			}
		}
	}
	private boolean IsEventStreamCommitted(EventStream eventStream)
	{
		return _eventStore.IsEventStreamExist(eventStream.getAggregateRootId(), _aggregateRootTypeProvider.GetAggregateRootType(eventStream.getAggregateRootName()), eventStream.getId());
	}
	private void TryPersistEvents(EventStreamContext context, Action successAction)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		Func<Boolean> tryPersistEvents = () =>
		{
			try
			{
				_eventStore.Append(context.getEventStream());
				return true;
			}
			catch (RuntimeException ex)
			{
				if (ex instanceof ConcurrentException && IsEventStreamCommitted(context.getEventStream()))
				{
					return true;
				}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var errorMessage = String.format("%1$s raised when persisting events:%2$s", ex.getClass().getName(), context.getEventStream().GetStreamInformation());
				_logger.Error(errorMessage, ex);

				if (ex instanceof ConcurrentException)
				{
					context.SetConcurrentException(new ErrorInfo(errorMessage, ex));
					return true;
				}

				return false;
			}
		}

		_retryService.TryAction("TryPersistEvents", tryPersistEvents, 3, successAction);
	}
	private void TryRefreshMemoryCache(EventStream eventStream)
	{
		try
		{
			RefreshMemoryCache(eventStream);
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when refreshing memory cache by event stream:%1$s", eventStream.GetStreamInformation()), ex);
		}
	}
	private void RefreshMemoryCache(EventStream eventStream)
	{
		java.lang.Class aggregateRootType = _aggregateRootTypeProvider.GetAggregateRootType(eventStream.getAggregateRootName());

		if (aggregateRootType == null)
		{
			throw new RuntimeException(String.format("Could not find aggregate root type by aggregate root name %1$s", eventStream.getAggregateRootName()));
		}

		if (eventStream.getVersion() == 1)
		{
			AggregateRoot aggregateRoot = _aggregateRootFactory.CreateAggregateRoot(aggregateRootType);
			aggregateRoot.ReplayEventStream(eventStream);
			_memoryCache.Set(aggregateRoot);
		}
		else if (eventStream.getVersion() > 1)
		{
			AggregateRoot aggregateRoot = _memoryCache.Get(eventStream.getAggregateRootId());
			if (aggregateRoot == null)
			{
				aggregateRoot = _repository.getGet()(aggregateRootType, eventStream.getAggregateRootId());
				if (aggregateRoot != null)
				{
					_memoryCache.Set(aggregateRoot);
				}
			}
			else if (aggregateRoot.getVersion() + 1 == eventStream.getVersion())
			{
				aggregateRoot.ReplayEventStream(eventStream);
				_memoryCache.Set(aggregateRoot);
			}
			else if (aggregateRoot.getVersion() + 1 < eventStream.getVersion())
			{
				aggregateRoot = _repository.getGet()(aggregateRootType, eventStream.getAggregateRootId());
				if (aggregateRoot != null)
				{
					_memoryCache.Set(aggregateRoot);
				}
			}
		}
	}
	private void TryPublishEvents(EventStream eventStream, Action successAction)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		Func<Boolean> tryPublishEvents = () =>
		{
			try
			{
				_eventPublisher.Publish(eventStream);
				return true;
			}
			catch (RuntimeException ex)
			{
				_logger.Error(String.format("Exception raised when publishing events:%1$s", eventStream.GetStreamInformation()), ex);
				return false;
			}
		}

		_retryService.TryAction("TryPublishEvents", tryPublishEvents, 3, successAction);
	}
	private SynchronizeResult TryCallSynchronizersBeforeEventPersisting(EventStream eventStream)
	{
		SynchronizeResult tempVar = new SynchronizeResult();
		tempVar.setStatus(SynchronizeStatus.Success);
		SynchronizeResult result = tempVar;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var evnt : eventStream.getEvents())
		{
			Iterable<IEventSynchronizer> synchronizers = _eventSynchronizerProvider.GetSynchronizers(evnt.getClass());
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var synchronizer : synchronizers)
			{
				try
				{
					synchronizer.OnBeforePersisting(evnt);
				}
				catch (RuntimeException ex)
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var errorMessage = String.format("Exception raised when calling synchronizer's OnBeforePersisting method. synchronizer:%1$s, events:%2$s", synchronizer.GetInnerSynchronizer().getClass().getName(), eventStream.GetStreamInformation());
					_logger.Error(errorMessage, ex);
					result.setErrorInfo(new ErrorInfo(errorMessage, ex));
					if (ex instanceof ConcurrentException)
					{
						result.setStatus(SynchronizeStatus.SynchronizerConcurrentException);
						return result;
					}
					result.setStatus(SynchronizeStatus.Failed);
					return result;
				}
			}
		}

		return result;
	}
	private void TryCallSynchronizersAfterEventPersisted(EventStream eventStream)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var evnt : eventStream.getEvents())
		{
			Iterable<IEventSynchronizer> synchronizers = _eventSynchronizerProvider.GetSynchronizers(evnt.getClass());
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var synchronizer : synchronizers)
			{
				try
				{
					synchronizer.OnAfterPersisted(evnt);
				}
				catch (RuntimeException ex)
				{
					_logger.Error(String.format("Exception raised when calling synchronizer's OnAfterPersisted method. synchronizer:%1$s, events:%2$s", synchronizer.GetInnerSynchronizer().getClass().getName(), eventStream.GetStreamInformation()), ex);
				}
			}
		}
	}
	private void RetryCommand(EventStreamContext context, ErrorInfo errorInfo, Action successAction)
	{
		EventStream eventStream = context.getEventStream();
		if (!eventStream.IsRestoreFromStorage())
		{
			CommandInfo commandInfo = _processingCommandCache.Get(eventStream.getCommandId());
			if (commandInfo != null)
			{
				_retryCommandService.RetryCommand(commandInfo, eventStream, errorInfo, successAction);
			}
			else
			{
				_logger.ErrorFormat("The command need to retry cannot be found from command processing cache, commandId:{0}", eventStream.getCommandId());
			}
		}
		else
		{
			_logger.InfoFormat("The command with id {0} will not be retry as the current event stream is restored from the message store.", eventStream.getCommandId());
		}
	}
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: private void Clear(EventStreamContext context, ErrorInfo errorInfo = null)
	private void Clear(EventStreamContext context, ErrorInfo errorInfo)
	{
		_commandAsyncResultManager.TryComplete(context.getEventStream().getCommandId(), context.getEventStream().getAggregateRootId(), errorInfo);
		_processingCommandCache.TryRemove(context.getEventStream().getCommandId());
		FinishExecution(context.getEventStream(), context.getQueue());
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	private static class SynchronizeResult
	{
		private SynchronizeStatus privateStatus = SynchronizeStatus.forValue(0);
		public final SynchronizeStatus getStatus()
		{
			return privateStatus;
		}
		public final void setStatus(SynchronizeStatus value)
		{
			privateStatus = value;
		}
		private ErrorInfo privateErrorInfo;
		public final ErrorInfo getErrorInfo()
		{
			return privateErrorInfo;
		}
		public final void setErrorInfo(ErrorInfo value)
		{
			privateErrorInfo = value;
		}
	}
	private enum SynchronizeStatus
	{
		Success,
		SynchronizerConcurrentException,
		Failed;

		public int getValue()
		{
			return this.ordinal();
		}

		public static SynchronizeStatus forValue(int value)
		{
			return values()[value];
		}
	}
}