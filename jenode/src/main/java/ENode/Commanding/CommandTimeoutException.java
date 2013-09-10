package ENode.Commanding;

/** Represents an exception when a command execution timeout.
 
*/
public class CommandTimeoutException extends RuntimeException implements Serializable
{
	/** Parameterized constructor.
	 
	 @param commandId
	 @param commandType
	*/
	public CommandTimeoutException(Guid commandId, java.lang.Class commandType)
	{
		super(String.format("Handle %1$s timeout, command Id:%2$s", commandType.getName(), commandId));
	}
}