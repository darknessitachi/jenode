package ENode.Infrastructure;

/** A simple object which contains some error information.
 
*/
public class ErrorInfo implements Serializable
{
	/** The error message.
	 
	*/
	private String privateErrorMessage;
	public final String getErrorMessage()
	{
		return privateErrorMessage;
	}
	private void setErrorMessage(String value)
	{
		privateErrorMessage = value;
	}
	/** The exception object.
	 
	*/
	private RuntimeException privateException;
	public final RuntimeException getException()
	{
		return privateException;
	}
	private void setException(RuntimeException value)
	{
		privateException = value;
	}

	/** Parameterized constructor.
	 
	 @param errorMessage
	*/
	public ErrorInfo(String errorMessage)
	{
		this(errorMessage, null);
	}
	/** Parameterized constructor.
	 
	 @param errorMessage
	 @param exception
	 @exception Exception
	*/
	public ErrorInfo(String errorMessage, RuntimeException exception)
	{
		if (DotNetToJavaStringHelper.isNullOrEmpty(errorMessage) && exception == null)
		{
			throw new Exception("Invalid error info.");
		}
		setErrorMessage(errorMessage);
		setException(exception);
	}

	/** Returns the error message.
	 
	 @return 
	*/
	public final String GetErrorMessage()
	{
		return !DotNetToJavaStringHelper.isNullOrEmpty(getErrorMessage()) ? getErrorMessage() : getException().getMessage();
	}
}