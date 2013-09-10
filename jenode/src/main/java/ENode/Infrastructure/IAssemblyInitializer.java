package ENode.Infrastructure;

/** Represents a initializer which can initialize from the given assemblies.
 
*/
public interface IAssemblyInitializer
{
	/** Initialize from the given assemblies.
	 
	 @param assemblies The assemblies.
	*/
	void Initialize(Assembly... assemblies);
}