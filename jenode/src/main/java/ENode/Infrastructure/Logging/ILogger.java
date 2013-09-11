package ENode.Infrastructure.Logging;

/** Represents a logger interface.
 
*/
public interface ILogger
{
	/** Indicates whether the logger can write debug level log messages.
	 
	*/
	boolean isDebugEnabled();
	/** Write a debug level log message.
	 
	 @param message
	*/
	void Debug(Object message);
	/** Write a debug level log message.
	 
	 @param format
	 @param args
	*/
	void DebugFormat(String format, Object... args);
	/** Write a debug level log message.
	 
	 @param message
	 @param exception
	*/
	void Debug(Object message, RuntimeException exception);

	/** Write a info level log message.
	 
	 @param message
	*/
	void Info(Object message);
	/** Write a info level log message.
	 
	 @param format
	 @param args
	*/
	void InfoFormat(String format, Object... args);
	/** Write a info level log message.
	 
	 @param message
	 @param exception
	*/
	void Info(Object message, RuntimeException exception);

	/** Write an error level log message.
	 
	 @param message
	*/
	void Error(Object message);
	/** Write an error level log message.
	 
	 @param format
	 @param args
	*/
	void ErrorFormat(String format, Object... args);
	/** Write an error level log message.
	 
	 @param message
	 @param exception
	*/
	void Error(Object message, RuntimeException exception);

	/** Write a warnning level log message.
	 
	 @param message
	*/
	void Warn(Object message);
	/** Write a warnning level log message.
	 
	 @param format
	 @param args
	*/
	void WarnFormat(String format, Object... args);
	/** Write a warnning level log message.
	 
	 @param message
	 @param exception
	*/
	void Warn(Object message, RuntimeException exception);

	/** Write a fatal level log message.
	 
	 @param message
	*/
	void Fatal(Object message);
	/** Write a fatal level log message.
	 
	 @param format
	 @param args
	*/
	void FatalFormat(String format, Object... args);
	/** Write a fatal level log message.
	 
	 @param message
	 @param exception
	*/
	void Fatal(Object message, RuntimeException exception);
}