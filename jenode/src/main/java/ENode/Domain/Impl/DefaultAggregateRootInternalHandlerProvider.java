package ENode.Domain.Impl;

import ENode.Eventing.*;
import ENode.Infrastructure.*;

/** The default implementation of IAggregateRootInternalHandlerProvider and IAssemblyInitializer.
 
*/
public class DefaultAggregateRootInternalHandlerProvider implements IAggregateRootInternalHandlerProvider, IAssemblyInitializer
{
	private final java.util.Map<java.lang.Class, java.util.Map<java.lang.Class, java.lang.reflect.Method>> _mappings = new java.util.HashMap<java.lang.Class, java.util.Map<java.lang.Class, java.lang.reflect.Method>>();

	/** Initialize from the given assemblies.
	 
	 @param assemblies
	*/
	public final void Initialize(Assembly... assemblies)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var assembly : assemblies)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
			for (var aggregateRootType : assembly.GetTypes().Where(TypeUtils.IsAggregateRoot))
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				for (var eventHandlerInterface : ScanEventHandlerInterfaces(aggregateRootType))
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var mapping = aggregateRootType.GetInterfaceMap(eventHandlerInterface);
					java.lang.Class eventType = GetEventType(eventHandlerInterface);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var method = mapping.TargetMethods.Single();
					RegisterInternalHandler(aggregateRootType, eventType, method);
				}
			}
		}
	}

	/** Get the internal event handler within the aggregate.
	 
	 @param aggregateRootType
	 @param eventType
	 @return 
	*/
	public final Action<AggregateRoot, Object> GetInternalEventHandler(java.lang.Class aggregateRootType, java.lang.Class eventType)
	{
		java.util.Map<java.lang.Class, java.lang.reflect.Method> eventHandlerDic = null;
		if (!((eventHandlerDic = _mappings.get(aggregateRootType)) != null))
		{
			return null;
		}
		java.lang.reflect.Method eventHandler = null;
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return (eventHandler = eventHandlerDic.get(eventType)) != null ? new Action<AggregateRoot, Object>((aggregateRoot, evnt) => eventHandler.invoke(aggregateRoot, new Object[] { evnt })) : null;
	}

	private void RegisterInternalHandler(java.lang.Class aggregateRootType, java.lang.Class eventType, java.lang.reflect.Method eventHandler)
	{
		java.util.Map<java.lang.Class, java.lang.reflect.Method> eventHandlerDic = null;

		if (!((eventHandlerDic = _mappings.get(aggregateRootType)) != null))
		{
			eventHandlerDic = new java.util.HashMap<java.lang.Class, java.lang.reflect.Method>();
			_mappings.put(aggregateRootType, eventHandlerDic);
		}

		if (eventHandlerDic.containsKey(eventType))
		{
			throw new RuntimeException(String.format("Found duplicated event handler on aggregate. Aggregate type:%1$s, event type:%2$s", aggregateRootType.FullName, eventType.FullName));
		}

		eventHandlerDic.put(eventType, eventHandler);
	}

	private static java.lang.Class GetEventType(java.lang.Class eventHandlerInterface)
	{
		return eventHandlerInterface.GetGenericArguments().Single();
	}
	private static Iterable<java.lang.Class> ScanEventHandlerInterfaces(java.lang.Class eventHandlerType)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return eventHandlerType.GetInterfaces().Where(x => x.IsGenericType && x.GetGenericTypeDefinition() == IEventHandler<>.class);
	}
}