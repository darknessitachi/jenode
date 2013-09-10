package ENode.Infrastructure;

/** Represents an object container.
*/
public class ObjectContainer
{
	/** Represents the current object container.
	*/
	private static IObjectContainer privateCurrent;
	public static IObjectContainer getCurrent()
	{
		return privateCurrent;
	}
	private static void setCurrent(IObjectContainer value)
	{
		privateCurrent = value;
	}

	/** Set the object container.
	 
	 @param container
	*/
	public static void SetContainer(IObjectContainer container)
	{
		setCurrent(container);
	}

	/** Register a implementation type.
	 
	 @param implementationType The implementation type.
	 @param life The life cycle of the implementer type.
	*/

	public static void RegisterType(java.lang.Class implementationType)
	{
		RegisterType(implementationType, LifeStyle.Singleton);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static void RegisterType(Type implementationType, LifeStyle life = LifeStyle.Singleton)
	public static void RegisterType(java.lang.Class implementationType, LifeStyle life)
	{
		getCurrent().RegisterType(implementationType, life);
	}
	/** Register a implementer type as a service implementation.
	 
	 @param serviceType The implementation type.
	 @param implementationType The implementation type.
	 @param life The life cycle of the implementer type.
	*/

	public static void RegisterType(java.lang.Class serviceType, java.lang.Class implementationType)
	{
		RegisterType(serviceType, implementationType, LifeStyle.Singleton);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static void RegisterType(Type serviceType, Type implementationType, LifeStyle life = LifeStyle.Singleton)
	public static void RegisterType(java.lang.Class serviceType, java.lang.Class implementationType, LifeStyle life)
	{
		getCurrent().RegisterType(serviceType, implementationType, life);
	}
	/** Register a implementer type as a service implementation.
	 
	 <typeparam name="TService">The service type.</typeparam>
	 <typeparam name="TImplementer">The implementer type.</typeparam>
	 @param life The life cycle of the implementer type.
	*/

	public static <TService, TImplementer extends TService> void Register()
	{
		Register(LifeStyle.Singleton);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public static void Register<TService, TImplementer>(LifeStyle life = LifeStyle.Singleton) where TService : class where TImplementer : class, TService
	public static <TService, TImplementer extends TService> void Register(LifeStyle life)
	{
		getCurrent().<TService, TImplementer>Register(life);
	}
	/** Register a implementer type instance as a service implementation.
	 
	 <typeparam name="TService">The service type.</typeparam>
	 <typeparam name="TImplementer">The implementer type.</typeparam>
	 @param instance The implementer type instance.
	*/
	public static <TService, TImplementer extends TService> void RegisterInstance(TImplementer instance)
	{
		getCurrent().<TService, TImplementer>RegisterInstance(instance);
	}
	/** Resolve a service.
	 
	 @param serviceType The service type.
	 @return The component instance that provides the service.
	*/
	public static <T> T Resolve(java.lang.Class<T> serviceType)
	{
		return (T)getCurrent().Resolve(serviceType);
	}
}