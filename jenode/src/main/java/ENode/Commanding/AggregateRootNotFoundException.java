package ENode.Commanding;

/** Represents an exception when tring to get a not existing aggregate root.
 
*/
public class AggregateRootNotFoundException extends RuntimeException implements Serializable
{
	private static final String ExceptionMessage = "Cannot find the aggregate root {0} of id {1}.";

	/** Parameterized constructor.
	 
	 @param id The aggregate root id.
	 @param type The aggregate root type.
	*/
	public AggregateRootNotFoundException(String id, java.lang.Class type)
	{
		super(String.format(ExceptionMessage, type.getName(), id));
	}
}