package ENode.Commanding;

import ENode.Messaging.*;

/** Represents an abstract base command.
 
*/
public abstract class Command extends Message implements ICommand, Serializable
{
	private int _retryCount;
	private static final int DefaultMillisecondsTimeout = 10000;
	private static final int DefaultRetryCount = 3;
	private static final int MaxRetryCount = 5;

	/** Get or set command executing waiting milliseconds.
	 
	*/
	private int privateMillisecondsTimeout;
	public final int getMillisecondsTimeout()
	{
		return privateMillisecondsTimeout;
	}
	public final void setMillisecondsTimeout(int value)
	{
		privateMillisecondsTimeout = value;
	}
	/** Get or set times which the command should be retry. The retry count must small than 5;
	 
	*/
	public final int getRetryCount()
	{
		return _retryCount;
	}
	public final void setRetryCount(int value)
	{
		if (value > MaxRetryCount)
		{
			throw new RuntimeException(String.format("Command max retry count cannot exceed %1$s.", MaxRetryCount));
		}
		_retryCount = value;
	}

	/** Default constructor.
	 
	*/
	protected Command()
	{
		this(DefaultMillisecondsTimeout, DefaultRetryCount);
	}
	/** Parameterized constructor.
	 
	 @param millisecondsTimeout
	 @param retryCount
	*/
	protected Command(int millisecondsTimeout, int retryCount)
	{
		super(Guid.NewGuid());
		setMillisecondsTimeout(millisecondsTimeout);
		setRetryCount(retryCount);
	}

	/** Returns the command type name.
	 
	 @return 
	*/
	@Override
	public String toString()
	{
		return getClass().getName();
	}
}