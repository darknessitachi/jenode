package ENode.Infrastructure;

import ENode.Domain.*;

/** A utility class provides type related methods.
 
*/
public class TypeUtils
{
	/** Check whether a type is a component type.
	 
	*/
	public static boolean IsComponent(java.lang.Class type)
	{
		return type != null && type.IsClass && type.GetCustomAttributes(ComponentAttribute.class, false).Any();
	}
	/** Check whether a type is an aggregate root type.
	 
	*/
	public static boolean IsAggregateRoot(java.lang.Class type)
	{
		return type.IsClass && !type.IsAbstract && AggregateRoot.class.IsAssignableFrom(type);
	}
}