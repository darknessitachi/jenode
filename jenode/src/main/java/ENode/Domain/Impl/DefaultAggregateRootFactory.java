package ENode.Domain.Impl;

/** The default implementation of IAggregateRootFactory.
 
*/
public class DefaultAggregateRootFactory implements IAggregateRootFactory
{
	private final ConcurrentDictionary<java.lang.Class, java.lang.reflect.Constructor> _constructorInfoDict = new ConcurrentDictionary<java.lang.Class, java.lang.reflect.Constructor>();
	private static final BindingFlags Flags = BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic;

	/** Create an empty aggregate root with the given type.
	 
	 @param aggregateRootType
	 @return 
	 @exception Exception
	*/
	public final AggregateRoot CreateAggregateRoot(java.lang.Class aggregateRootType)
	{
		java.lang.reflect.Constructor constructor;

		if (_constructorInfoDict.ContainsKey(aggregateRootType))
		{
			constructor = _constructorInfoDict[aggregateRootType];
		}
		else
		{
			if (!AggregateRoot.class.IsAssignableFrom(aggregateRootType))
			{
				throw new RuntimeException(String.format("Invalid aggregate root type %1$s", aggregateRootType.FullName));
			}

			constructor = aggregateRootType.getConstructor(Flags, null, java.lang.Class.EmptyTypes, null);
			if (constructor == null)
			{
				throw new RuntimeException(String.format("Could not found a default constructor on aggregate root type %1$s", aggregateRootType.FullName));
			}

			_constructorInfoDict[aggregateRootType] = constructor;
		}

		Object tempVar = constructor.newInstance(null);
		return (AggregateRoot)((tempVar instanceof AggregateRoot) ? tempVar : null);
	}
}