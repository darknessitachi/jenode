package ENode.Infrastructure;

/** An enum to description the lifetime of a component.
 
*/
public enum LifeStyle
{
	/** Represents a component is a transient component.
	 
	*/
	Transient,
	/** Represents a component is a singleton component.
	 
	*/
	Singleton;

	public int getValue()
	{
		return this.ordinal();
	}

	public static LifeStyle forValue(int value)
	{
		return values()[value];
	}
}