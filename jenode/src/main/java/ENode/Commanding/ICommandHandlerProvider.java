package ENode.Commanding;

/** Represents a provider which provide the command handler for command.
 
*/
public interface ICommandHandlerProvider
{
	/** Get the command handler for the given command.
	 
	 @param command
	 @return 
	*/
	ICommandHandler GetCommandHandler(ICommand command);
	/** Check whether a given type is a command handler type.
	 
	 @param type
	 @return 
	*/
	boolean IsCommandHandler(java.lang.Class type);
}