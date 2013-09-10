package ENode.Infrastructure.Logging;

/** Represents a logger factory.
 
*/
public interface ILoggerFactory
{
	/** Create a logger with the given logger name.
	 
	*/
	ILogger Create(String name);
	/** Create a logger with the given type.
	 
	*/
	ILogger Create(java.lang.Class type);
}