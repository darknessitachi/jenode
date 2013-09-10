package ENode.Infrastructure;

/** Represents a common exception of enode framework.
 
*/
public class ENodeException extends RuntimeException implements Serializable
{
	/** Default constructor.
	 
	*/
	public ENodeException()
	{
	}
	/** Parameterized constructor.
	 
	 @param message
	*/
	public ENodeException(String message)
	{
		super(message);
	}
	/** Parameterized constructor.
	 
	 @param message
	 @param innerException
	*/
	public ENodeException(String message, RuntimeException innerException)
	{
		super(message, innerException);
	}
	/** Parameterized constructor.
	 
	 @param message
	 @param args
	*/
	public ENodeException(String message, Object... args)
	{
		super(String.format(message, args));
	}
}