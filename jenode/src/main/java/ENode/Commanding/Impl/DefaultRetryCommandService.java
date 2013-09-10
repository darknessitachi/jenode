package ENode.Commanding.Impl;

import ENode.Eventing.*;
import ENode.Infrastructure.*;
import ENode.Infrastructure.Logging.*;
import ENode.Infrastructure.Retring.*;

/** The default implementation of IRetryCommandService.
 
*/
public class DefaultRetryCommandService implements IRetryCommandService
{
	private ICommandQueue _retryCommandQueue;
	private ICommandAsyncResultManager _commandAsyncResultManager;
	private IRetryService _retryService;
	private ILogger _logger;

	/** Parameterized costructor.
	 
	 @param commandAsyncResultManager
	 @param retryService
	 @param loggerFactory
	*/
	public DefaultRetryCommandService(ICommandAsyncResultManager commandAsyncResultManager, IRetryService retryService, ILoggerFactory loggerFactory)
	{
		_commandAsyncResultManager = commandAsyncResultManager;
		_retryService = retryService;
		_logger = loggerFactory.Create(getClass().getName());
	}

	/** Retry the given command.
	 
	 @param commandInfo
	 @param eventStream
	 @param errorInfo
	 @param retrySuccessCallbackAction
	*/
	public final void RetryCommand(CommandInfo commandInfo, EventStream eventStream, ErrorInfo errorInfo, Action retrySuccessCallbackAction)
	{
		if (_retryCommandQueue == null)
		{
			_retryCommandQueue = Configuration.getInstance().GetRetryCommandQueue();
		}
		ICommand command = commandInfo.getCommand();

		if (commandInfo.getRetriedCount() < command.getRetryCount())
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			_retryService.TryAction("TryEnqueueCommand", () => TryEnqueueCommand(commandInfo), 3, retrySuccessCallbackAction);
		}
		else
		{
			_commandAsyncResultManager.TryComplete(command.getId(), eventStream.getAggregateRootId(), errorInfo);
			_logger.InfoFormat("{0} retried count reached to its max retry count {1}.", command.getClass().getName(), command.getRetryCount());
			if (retrySuccessCallbackAction != null)
			{
				retrySuccessCallbackAction();
			}
		}
	}

	private boolean TryEnqueueCommand(CommandInfo commandInfo)
	{
		try
		{
			_retryCommandQueue.Enqueue(commandInfo.getCommand());
			commandInfo.IncreaseRetriedCount();
			_logger.InfoFormat("Sent {0} to command retry queue for {1} time.", commandInfo.getCommand().getClass().getName(), commandInfo.getRetriedCount());
			return true;
		}
		catch (RuntimeException ex)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var errorMessage = String.format("Exception raised when tring to enqueue the command to the retry command queue. commandType%1$s, commandId:%2$s", commandInfo.getCommand().getClass().getName(), commandInfo.getCommand().getId());
			_logger.Error(errorMessage, ex);
			return false;
		}
	}
}