package ENode.Commanding.Impl;

/** The default implementation of ICommandService.
 
*/
public class DefaultCommandService implements ICommandService
{
	private ICommandQueueRouter _commandQueueRouter;
	private ICommandAsyncResultManager _commandAsyncResultManager;

	/** Parameterized constructor.
	 
	 @param commandQueueRouter
	 @param commandAsyncResultManager
	*/
	public DefaultCommandService(ICommandQueueRouter commandQueueRouter, ICommandAsyncResultManager commandAsyncResultManager)
	{
		_commandQueueRouter = commandQueueRouter;
		_commandAsyncResultManager = commandAsyncResultManager;
	}

	/** Send the given command to command queue and return immediately, the command will be handle asynchronously.
	 
	 @param command The command to send.
	 @param callback The callback method when the command was handled.
	 @exception ArgumentNullException Throwed when the command is null.
	 @exception CommandQueueNotFoundException Throwed when the command queue cannot be routed.
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public void Send(ICommand command, Action<CommandAsyncResult> callback = null)
	public final void Send(ICommand command, Action<CommandAsyncResult> callback)
	{
		if (command == null)
		{
			throw new ArgumentNullException("command");
		}

		ICommandQueue commandQueue = _commandQueueRouter.Route(command);
		if (commandQueue == null)
		{
			throw new CommandQueueNotFoundException(command.getClass());
		}

		if (callback != null)
		{
			_commandAsyncResultManager.Add(command.getId(), new CommandAsyncResult(callback));
		}
		commandQueue.Enqueue(command);
	}
	/** Send the given command to command queue, and block the current thread until the command was handled or timeout.
	 
	 @param command The command to send.
	 @return The command execution result.
	 @exception ArgumentNullException Throwed when the command is null.
	 @exception CommandQueueNotFoundException Throwed when the command queue cannot be routed.
	 @exception CommandTimeoutException Throwed when the command execution timeout.
	 @exception CommandExecutionException Throwed when the command execution has any error.
	*/
	public final CommandAsyncResult Execute(ICommand command)
	{
		if (command == null)
		{
			throw new ArgumentNullException("command");
		}

		ICommandQueue commandQueue = _commandQueueRouter.Route(command);
		if (commandQueue == null)
		{
			throw new CommandQueueNotFoundException(command.getClass());
		}

		ManualResetEvent waitHandle = new ManualResetEvent(false);
		CommandAsyncResult commandAsyncResult = new CommandAsyncResult(waitHandle);

		_commandAsyncResultManager.Add(command.getId(), commandAsyncResult);
		commandQueue.Enqueue(command);
		waitHandle.WaitOne(command.getMillisecondsTimeout());
		_commandAsyncResultManager.Remove(command.getId());

		if (!commandAsyncResult.getIsCompleted())
		{
			throw new CommandTimeoutException(command.getId(), command.getClass());
		}
		if (commandAsyncResult.getErrorInfo() == null)
		{
			return commandAsyncResult;
		}

		ErrorInfo errorInfo = commandAsyncResult.getErrorInfo();
		if (errorInfo.getErrorMessage() != null && errorInfo.getException() != null)
		{
			throw new CommandExecutionException(command.getId(), command.getClass(), errorInfo.getErrorMessage(), errorInfo.getException());
		}
		if (errorInfo.getErrorMessage() != null)
		{
			throw new CommandExecutionException(command.getId(), command.getClass(), errorInfo.getErrorMessage());
		}
		if (errorInfo.getException() != null)
		{
			throw new CommandExecutionException(command.getId(), command.getClass(), errorInfo.getException());
		}

		return commandAsyncResult;
	}
}