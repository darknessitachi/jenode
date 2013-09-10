package ENode.Commanding.Impl;

import ENode.Infrastructure.*;
import ENode.Infrastructure.Retring.*;

/** The default implementation of ICommandAsyncResultManager.
 
*/
public class DefaultCommandAsyncResultManager implements ICommandAsyncResultManager
{
	private IRetryService _retryService;
	private final ConcurrentDictionary<Guid, CommandAsyncResult> _commandAsyncResultDict = new ConcurrentDictionary<Guid, CommandAsyncResult>();

	/** Parameterized constructor.
	 
	 @param retryService
	*/
	public DefaultCommandAsyncResultManager(IRetryService retryService)
	{
		_retryService = retryService;
	}

	/** Add the command async result for a command.
	 
	 @param commandId The commandId.
	 @param commandAsyncResult The command async result.
	*/
	public final void Add(Guid commandId, CommandAsyncResult commandAsyncResult)
	{
		if (!_commandAsyncResultDict.TryAdd(commandId, commandAsyncResult))
		{
			throw new RuntimeException(String.format("Command with id '%1$s' is already exist.", commandId));
		}
	}
	/** Remove the specified command async result for the given commandId.
	 
	 @param commandId The commandId.
	*/
	public final void Remove(Guid commandId)
	{
		CommandAsyncResult commandAsyncResult = null;
		RefObject<CommandAsyncResult> tempRef_commandAsyncResult = new RefObject<CommandAsyncResult>(commandAsyncResult);
		_commandAsyncResultDict.TryRemove(commandId, tempRef_commandAsyncResult);
		commandAsyncResult = tempRef_commandAsyncResult.argvalue;
	}
	/** Try to complete the command async result for the given commandId.
	 
	 @param commandId The commandId.
	 @param aggregateRootId The id of the aggregate which was created or updated by the command.
	*/
	public final void TryComplete(Guid commandId, String aggregateRootId)
	{
		TryComplete(commandId, aggregateRootId, null);
	}
	/** Try to complete the command async result for the given commandId.
	 
	 @param commandId The commandId.
	 @param aggregateRootId The id of the aggregate which was created or updated by the command.
	 @param errorInfo The error info if the command execution has any error.
	*/
	public final void TryComplete(Guid commandId, String aggregateRootId, ErrorInfo errorInfo)
	{
		CommandAsyncResult commandAsyncResult = null;
		RefObject<CommandAsyncResult> tempRef_commandAsyncResult = new RefObject<CommandAsyncResult>(commandAsyncResult);
		boolean tempVar = !_commandAsyncResultDict.TryRemove(commandId, tempRef_commandAsyncResult);
			commandAsyncResult = tempRef_commandAsyncResult.argvalue;
		if (tempVar)
		{
			return;
		}
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		_retryService.TryAction("TryCompleteCommandAsyncResult", () => commandAsyncResult.Complete(aggregateRootId, errorInfo), 3, null);
	}
}