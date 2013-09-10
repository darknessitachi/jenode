package ENode.Infrastructure;

/** Represents an object container interface.
*/
public interface IObjectContainer
{
	/** Register a implementation type.
	 
	 @param implementationType The implementation type.
	 @param life The life cycle of the implementer type.
	*/

	void RegisterType(java.lang.Class implementationType);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: void RegisterType(Type implementationType, LifeStyle life = LifeStyle.Singleton);
	void RegisterType(java.lang.Class implementationType, LifeStyle life);
	/** Register a implementer type as a service implementation.
	 
	 @param serviceType The service type.
	 @param implementationType The implementation type.
	 @param life The life cycle of the implementer type.
	*/

	void RegisterType(java.lang.Class serviceType, java.lang.Class implementationType);
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: void RegisterType(Type serviceType, Type implementationType, LifeStyle life = LifeStyle.Singleton);
	void RegisterType(java.lang.Class serviceType, java.lang.Class implementationType, LifeStyle life);
	/** Register a implementer type as a service implementation.
	 
	 <typeparam name="TService">The service type.</typeparam>
	 <typeparam name="TImplementer">The implementer type.</typeparam>
	 @param life The life cycle of the implementer type.
	*/

	<TService, TImplementer extends TService> void Register();
//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: void Register<TService, TImplementer>(LifeStyle life = LifeStyle.Singleton) where TService : class where TImplementer : class, TService;
	<TService, TImplementer extends TService> void Register(LifeStyle life);
	/** Register a implementer type instance as a service implementation.
	 
	 <typeparam name="TService">The service type.</typeparam>
	 <typeparam name="TImplementer">The implementer type.</typeparam>
	 @param instance The implementer type instance.
	*/
	<TService, TImplementer extends TService> void RegisterInstance(TImplementer instance);
	
	/** Resolve a service.
	 
	 @param serviceType The service type.
	 @return The component instance that provides the service.
	*/
	Object Resolve(java.lang.Class serviceType);
}