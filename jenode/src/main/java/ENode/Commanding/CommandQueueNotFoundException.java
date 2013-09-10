package ENode.Commanding;

/** Represents an exception when the command queue cannot be routed.
 
*/
public class CommandQueueNotFoundException extends RuntimeException implements Serializable
{
	private static final String ExceptionMessage = "Cannot route an available command queue for command {0}.";

	/** Parameterized constructor.
	 
	 @param commandType The command type.
	*/
	public CommandQueueNotFoundException(java.lang.Class commandType)
	{
		super(String.format(ExceptionMessage, commandType.getName()));
	}
}