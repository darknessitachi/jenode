package ENode.Infrastructure.Logging;

/** An empty logger which log nothing.
 
*/
public class EmptyLogger implements ILogger
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region ILogger Members

	/** Returns false.
	 
	*/
	public final boolean getIsDebugEnabled()
	{
		return false;
	}
	/** Do nothing.
	 
	 @param message
	*/
	public final void Debug(Object message)
	{
	}
	/** Do nothing.
	 
	 @param format
	 @param args
	*/
	public final void DebugFormat(String format, Object... args)
	{
	}
	/** Do nothing.
	 
	 @param message
	 @param exception
	*/
	public final void Debug(Object message, RuntimeException exception)
	{
	}

	/** Do nothing.
	 
	 @param message
	*/
	public final void Info(Object message)
	{
	}
	/** Do nothing.
	 
	 @param format
	 @param args
	*/
	public final void InfoFormat(String format, Object... args)
	{
	}
	/** Do nothing.
	 
	 @param message
	 @param exception
	*/
	public final void Info(Object message, RuntimeException exception)
	{
	}

	/** Do nothing.
	 
	 @param message
	*/
	public final void Error(Object message)
	{
	}
	/** Do nothing.
	 
	 @param format
	 @param args
	*/
	public final void ErrorFormat(String format, Object... args)
	{
	}
	/** Do nothing.
	 
	 @param message
	 @param exception
	*/
	public final void Error(Object message, RuntimeException exception)
	{
	}

	/** Do nothing.
	 
	 @param message
	*/
	public final void Warn(Object message)
	{
	}
	/** Do nothing.
	 
	 @param format
	 @param args
	*/
	public final void WarnFormat(String format, Object... args)
	{
	}
	/** Do nothing.
	 
	 @param message
	 @param exception
	*/
	public final void Warn(Object message, RuntimeException exception)
	{
	}

	/** Do nothing.
	 
	 @param message
	*/
	public final void Fatal(Object message)
	{
	}
	/** Do nothing.
	 
	 @param format
	 @param args
	*/
	public final void FatalFormat(String format, Object... args)
	{
	}
	/** Do nothing.
	 
	 @param message
	 @param exception
	*/
	public final void Fatal(Object message, RuntimeException exception)
	{
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}