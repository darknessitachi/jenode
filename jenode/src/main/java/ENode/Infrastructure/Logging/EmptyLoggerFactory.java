package ENode.Infrastructure.Logging;

/** An empty implementation of ILoggerFactory.
 
*/
public class EmptyLoggerFactory implements ILoggerFactory
{
	private static final EmptyLogger Logger = new EmptyLogger();
	/** Create an empty logger instance by name.
	 
	 @param name
	 @return 
	*/
	public final ILogger Create(String name)
	{
		return Logger;
	}
	/** Create an empty logger instance by type.
	 
	 @param type
	 @return 
	*/
	public final ILogger Create(java.lang.Class type)
	{
		return Logger;
	}
}