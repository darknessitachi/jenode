package ENode.Commanding;

/** Represents an exception when executing a command.
 
*/
public class CommandExecutionException extends RuntimeException implements Serializable
{
	/** Parameterized constructor.
	 
	 @param commandId
	 @param commandType
	 @param errorMessage
	*/
	public CommandExecutionException(Guid commandId, java.lang.Class commandType, String errorMessage)
	{
		super(String.format("%1$s execute error, command Id:%2$s, error message:%3$s.", commandType.getName(), commandId, errorMessage));
	}
	/** Parameterized constructor.
	 
	 @param commandId
	 @param commandType
	 @param innerException
	*/
	public CommandExecutionException(Guid commandId, java.lang.Class commandType, RuntimeException innerException)
	{
		super(String.format("%1$s execute error, command Id:%2$s.", commandType.getName(), commandId), innerException);
	}
	/** Parameterized constructor.
	 
	 @param commandId
	 @param commandType
	 @param errorMessage
	 @param innerException
	*/
	public CommandExecutionException(Guid commandId, java.lang.Class commandType, String errorMessage, RuntimeException innerException)
	{
		super(String.format("%1$s execute error, command Id:%2$s, error message:%3$s", commandType.getName(), commandId, errorMessage), innerException);
	}
}