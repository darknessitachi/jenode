package ENode.Commanding;

/** Represents a service to process command synchronizely or asyncronizely.
 
*/
public interface ICommandService
{
	/** Send the given command to command queue and return immediately, the command will be handle asynchronously.
	 
	 @param command The command to send.
	 @param callback The callback method when the command was handled.
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: void Send(ICommand command, Action<CommandAsyncResult> callback = null);
	void Send(ICommand command, Action<CommandAsyncResult> callback);
	/** Send the given command to command queue, and block the current thread until the command was handled or timeout.
	 
	 @param command The command to execute.
	 @return The command execute result.
	*/
	CommandAsyncResult Execute(ICommand command);
}