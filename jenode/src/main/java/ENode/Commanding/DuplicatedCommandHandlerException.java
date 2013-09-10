package ENode.Commanding;

/** Represents an exception when found a duplicated command handler of command.
 
*/
public class DuplicatedCommandHandlerException extends RuntimeException implements Serializable
{
	private static final String ExceptionMessage = "Found duplicated command handler {0} of {1}.";

	/** Parameterized constructor.
	 
	 @param commandType The command type.
	 @param commandHandlerType The command handler type.
	*/
	public DuplicatedCommandHandlerException(java.lang.Class commandType, java.lang.Class commandHandlerType)
	{
		super(String.format(ExceptionMessage, commandHandlerType.getName(), commandType.getName()));
	}
}