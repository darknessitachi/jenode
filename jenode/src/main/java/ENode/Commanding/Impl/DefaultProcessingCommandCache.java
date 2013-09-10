package ENode.Commanding.Impl;

/** The default implementation of IProcessingCommandCache.
 
*/
public class DefaultProcessingCommandCache implements IProcessingCommandCache
{
	private final ConcurrentDictionary<Guid, CommandInfo> _commandInfoDict = new ConcurrentDictionary<Guid, CommandInfo>();

	/** Add a command to memory cache.
	 
	 @param command
	*/
	public final void Add(ICommand command)
	{
		_commandInfoDict.TryAdd(command.getId(), new CommandInfo(command));
	}
	/** Remove a command from memory cache.
	 
	 @param commandId
	*/
	public final void TryRemove(Guid commandId)
	{
		CommandInfo commandInfo = null;
		RefObject<CommandInfo> tempRef_commandInfo = new RefObject<CommandInfo>(commandInfo);
		_commandInfoDict.TryRemove(commandId, tempRef_commandInfo);
		commandInfo = tempRef_commandInfo.argvalue;
	}
	/** Try to get the command info from memory cache.
	 
	 @param commandId
	 @return 
	*/
	public final CommandInfo Get(Guid commandId)
	{
		CommandInfo commandInfo = null;
		RefObject<CommandInfo> tempRef_commandInfo = new RefObject<CommandInfo>(commandInfo);
		CommandInfo tempVar = _commandInfoDict.TryGetValue(commandId, tempRef_commandInfo) ? commandInfo : null;
		commandInfo = tempRef_commandInfo.argvalue;
		return tempVar;
	}
}