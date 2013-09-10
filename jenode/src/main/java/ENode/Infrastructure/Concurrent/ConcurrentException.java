package ENode.Infrastructure.Concurrent;

/** Represents a concurrent exception.
 
*/
public class ConcurrentException extends RuntimeException implements Serializable
{
	/** Default constructor.
	 
	*/
	public ConcurrentException()
	{
	}
	/** Parameterized constructor.
	 
	 @param message
	*/
	public ConcurrentException(String message)
	{
		super(message);
	}
	/** Parameterized constructor.
	 
	 @param message
	 @param innerException
	*/
	public ConcurrentException(String message, RuntimeException innerException)
	{
		super(message, innerException);
	}
	/** Parameterized constructor.
	 
	 @param message
	 @param args
	*/
	public ConcurrentException(String message, Object... args)
	{
		super(String.format(message, args));
	}
}