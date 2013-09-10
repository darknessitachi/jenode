package ENode.Commanding;

/** Represents a memory cache for caching all the processing commands.
 
*/
public interface IProcessingCommandCache
{
	/** Add a command into memory cache.
	 
	 @param command
	*/
	void Add(ICommand command);
	/** Try to remove a command from memory cache.
	 
	 @param commandId
	*/
	void TryRemove(Guid commandId);
	/** Get the command info from memory cache.
	 
	 @param commandId
	 @return 
	*/
	CommandInfo Get(Guid commandId);
}