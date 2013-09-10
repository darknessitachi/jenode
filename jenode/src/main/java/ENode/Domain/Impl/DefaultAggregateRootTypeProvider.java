package ENode.Domain.Impl;

import ENode.Infrastructure.*;

/** Default implementation of IAggregateRootTypeProvider and IAssemblyInitializer.
 
*/
public class DefaultAggregateRootTypeProvider implements IAggregateRootTypeProvider, IAssemblyInitializer
{
	private final java.util.Map<String, java.lang.Class> _mappings = new java.util.HashMap<String, java.lang.Class>();

	/** Initialize from the given assemblies.
	 
	 @param assemblies
	 @exception Exception
	*/
	public final void Initialize(Assembly... assemblies)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var assembly : assemblies)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
			for (var type : assembly.GetTypes().Where(TypeUtils.IsAggregateRoot))
			{
				if (!type.IsSerializable)
				{
					throw new RuntimeException(String.format("%1$s should be marked as serializable.", type.FullName));
				}
				_mappings.put(type.FullName, type);
			}
		}
	}
	/** Get the aggregate root type name by the aggregate root type.
	 
	 @param aggregateRootType
	 @return 
	*/
	public final String GetAggregateRootTypeName(java.lang.Class aggregateRootType)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _mappings.Single(x => x.getValue() == aggregateRootType).getKey();
	}
	/** Get the aggregate root type by the aggregate root type name.
	 
	 @param name
	 @return 
	*/
	public final java.lang.Class GetAggregateRootType(String name)
	{
		return _mappings.containsKey(name) ? _mappings.get(name) : null;
	}
	/** Get all the aggregate root types.
	 
	 @return 
	*/
	public final Iterable<java.lang.Class> GetAllAggregateRootTypes()
	{
		return _mappings.values();
	}
}