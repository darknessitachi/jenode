package ENode.Infrastructure;

/** An attribute to indicate a class is a component.
 
*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//[AttributeUsage(AttributeTargets.Class)]
public class ComponentAttribute extends Attribute
{
	/** The lifetime of the component.
	 
	*/
	private LifeStyle privateLifeStyle = LifeStyle.forValue(0);
	public final LifeStyle getLifeStyle()
	{
		return privateLifeStyle;
	}
	private void setLifeStyle(LifeStyle value)
	{
		privateLifeStyle = value;
	}
	/** Default constructor.
	 
	*/
	public ComponentAttribute()
	{
		this(LifeStyle.Transient);
	}
	/** Parameterized constructor.
	 
	 @param lifeStyle
	*/
	public ComponentAttribute(LifeStyle lifeStyle)
	{
		setLifeStyle(lifeStyle);
	}
}