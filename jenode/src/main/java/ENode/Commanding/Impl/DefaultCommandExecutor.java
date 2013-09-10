package ENode.Commanding.Impl;

import ENode.Domain.*;
import ENode.Eventing.*;
import ENode.Infrastructure.*;
import ENode.Infrastructure.Logging.*;
import ENode.Infrastructure.Retring.*;
import ENode.Messaging.*;
import ENode.Messaging.Impl.*;

/** The default implementation of command executor interface.
 
*/
public class DefaultCommandExecutor extends MessageExecutor<ICommand> implements ICommandExecutor
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private IProcessingCommandCache _processingCommandCache;
	private ICommandAsyncResultManager _commandAsyncResultManager;
	private ICommandHandlerProvider _commandHandlerProvider;
	private IAggregateRootTypeProvider _aggregateRootTypeProvider;
	private IEventSender _eventSender;
	private IRetryService _retryService;
	private ICommandContext _commandContext;
	private ITrackingContext _trackingContext;
	private ILogger _logger;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param processingCommandCache
	 @param commandAsyncResultManager
	 @param commandHandlerProvider
	 @param aggregateRootTypeProvider
	 @param eventSender
	 @param retryService
	 @param commandContext
	 @param loggerFactory
	 @exception Exception
	*/
	public DefaultCommandExecutor(IProcessingCommandCache processingCommandCache, ICommandAsyncResultManager commandAsyncResultManager, ICommandHandlerProvider commandHandlerProvider, IAggregateRootTypeProvider aggregateRootTypeProvider, IEventSender eventSender, IRetryService retryService, ICommandContext commandContext, ILoggerFactory loggerFactory)
	{
		_processingCommandCache = processingCommandCache;
		_commandAsyncResultManager = commandAsyncResultManager;
		_commandHandlerProvider = commandHandlerProvider;
		_aggregateRootTypeProvider = aggregateRootTypeProvider;
		_eventSender = eventSender;
		_retryService = retryService;
		_commandContext = commandContext;
		_trackingContext = (ITrackingContext)((commandContext instanceof ITrackingContext) ? commandContext : null);
		_logger = loggerFactory.Create(getClass().getName());

		if (_trackingContext == null)
		{
			throw new RuntimeException("Command context must also implement ITrackingContext interface.");
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Execute the given command message.
	 
	 @param message The command message.
	 @param queue The queue which the command message belongs to.
	*/
	@Override
	public void Execute(ICommand message, IMessageQueue<ICommand> queue)
	{
		ICommand command = message;
		ICommandHandler commandHandler = _commandHandlerProvider.GetCommandHandler(command);

		if (commandHandler == null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var errorMessage = String.format("Command handler not found for %1$s", command.getClass().FullName);
			_logger.Fatal(errorMessage);
			_commandAsyncResultManager.TryComplete(command.getId(), null, new ErrorInfo(errorMessage));
			FinishExecution(command, queue);
			return;
		}

		try
		{
			_trackingContext.Clear();
			_processingCommandCache.Add(command);
			commandHandler.Handle(_commandContext, command);
			AggregateRoot dirtyAggregate = GetDirtyAggregate(_trackingContext);
			if (dirtyAggregate != null)
			{
				CommitAggregate(dirtyAggregate, command, queue);
			}
			else
			{
				_logger.Info("No dirty aggregate found, finish the command execution directly.");
				_commandAsyncResultManager.TryComplete(command.getId(), null);
				FinishExecution(command, queue);
			}
		}
		catch (RuntimeException ex)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var commandHandlerType = commandHandler.GetInnerCommandHandler().getClass();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var errorMessage = String.format("Exception raised when %1$s handling %2$s, command id:%3$s.", commandHandlerType.getName(), command.getClass().getName(), command.getId());
			_logger.Error(errorMessage, ex);
			_commandAsyncResultManager.TryComplete(command.getId(), null, new ErrorInfo(errorMessage, ex));
			FinishExecution(command, queue);
		}
		finally
		{
			_trackingContext.Clear();
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	private static AggregateRoot GetDirtyAggregate(ITrackingContext trackingContext)
	{
		Iterable<AggregateRoot> trackedAggregateRoots = trackingContext.GetTrackedAggregateRoots();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var dirtyAggregateRoots = trackedAggregateRoots.Where(x => x.GetUncommittedEvents().Any()).ToList();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var dirtyAggregateRootCount = dirtyAggregateRoots.Count();

		if (dirtyAggregateRootCount == 0)
		{
			return null;
		}
		if (dirtyAggregateRootCount > 1)
		{
			throw new RuntimeException("Detected more than one dirty aggregates.");
		}

		return dirtyAggregateRoots.Single();
	}
	private EventStream BuildEvents(AggregateRoot aggregateRoot, ICommand command)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var uncommittedEvents = aggregateRoot.GetUncommittedEvents().ToList();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var aggregateRootType = aggregateRoot.getClass();
		String aggregateRootName = _aggregateRootTypeProvider.GetAggregateRootTypeName(aggregateRootType);

		return new EventStream(aggregateRoot.getUniqueId(), aggregateRootName, aggregateRoot.getVersion() + 1, command.getId(), java.util.Date.UtcNow, uncommittedEvents);
	}
	private void CommitAggregate(AggregateRoot dirtyAggregate, ICommand command, IMessageQueue<ICommand> queue)
	{
		EventStream eventStream = BuildEvents(dirtyAggregate, command);
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_retryService.TryAction("TrySendEvent", () => TrySendEvent(eventStream), 3, () => FinishExecution(command, queue));
	}
	private boolean TrySendEvent(EventStream eventStream)
	{
		try
		{
			_eventSender.Send(eventStream);
			return true;
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when tring to send events, events info:%1$s.", eventStream.GetStreamInformation()), ex);
			return false;
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}