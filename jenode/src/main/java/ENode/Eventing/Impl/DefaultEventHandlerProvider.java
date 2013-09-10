package ENode.Eventing.Impl;

import ENode.Domain.*;
import ENode.Infrastructure.*;

/** The default implementation of IEventHandlerProvider.
 
*/
public class DefaultEventHandlerProvider implements IEventHandlerProvider, IAssemblyInitializer
{
	private final java.util.Map<java.lang.Class, java.util.List<IEventHandler>> _eventHandlerDict = new java.util.HashMap<java.lang.Class, java.util.List<IEventHandler>>();

	/** Initialize from the given assemblies.
	 
	 @param assemblies
	 @exception Exception
	*/
	public final void Initialize(Assembly... assemblies)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		for (var handlerType : assemblies.SelectMany(assembly => assembly.GetTypes().Where(IsEventHandler)))
		{
			if (!TypeUtils.IsComponent(handlerType))
			{
				throw new RuntimeException(String.format("%1$s should be marked as component.", handlerType.FullName));
			}
			RegisterEventHandler(handlerType);
		}
	}

	/** Get all the event handlers for the given event type.
	 
	 @param eventType
	 @return 
	*/
	public final Iterable<IEventHandler> GetEventHandlers(java.lang.Class eventType)
	{
		java.util.ArrayList<IEventHandler> eventHandlers = new java.util.ArrayList<IEventHandler>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		for (var key : _eventHandlerDict.keySet().Where(key => key.IsAssignableFrom(eventType)))
		{
			eventHandlers.addAll(_eventHandlerDict.get(key));
		}
		return eventHandlers;
	}

	/** Check whether a given type is a event handler type.
	 
	 @param type
	 @return 
	*/
	public final boolean IsEventHandler(java.lang.Class type)
	{
		return type != null && type.IsClass && !type.IsAbstract && ScanEventHandlerInterfaces(type).Any() && !AggregateRoot.class.IsAssignableFrom(type);
	}

	private void RegisterEventHandler(java.lang.Class eventHandlerType)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var eventHandlerInterface : ScanEventHandlerInterfaces(eventHandlerType))
		{
			java.lang.Class eventType = GetEventType(eventHandlerInterface);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var eventHandlerWrapperType = EventHandlerWrapper<>.class.MakeGenericType(eventType);
			java.util.List<IEventHandler> eventHandlers = null;
			if (!((eventHandlers = _eventHandlerDict.get(eventType)) != null))
			{
				eventHandlers = new java.util.ArrayList<IEventHandler>();
				_eventHandlerDict.put(eventType, eventHandlers);
			}

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			if (eventHandlers.Any(x => x.GetInnerEventHandler().getClass() == eventHandlerType))
			{
				continue;
			}

			TService eventHandler = ObjectContainer.Resolve(eventHandlerType);
			Object tempVar = Activator.CreateInstance(eventHandlerWrapperType, new TService[] { eventHandler });
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var eventHandlerWrapper = (IEventHandler)((tempVar instanceof IEventHandler) ? tempVar : null);
			eventHandlers.add(eventHandlerWrapper);
		}
	}
	private static Iterable<java.lang.Class> ScanEventHandlerInterfaces(java.lang.Class eventHandlerType)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return eventHandlerType.GetInterfaces().Where(x => x.IsGenericType && x.GetGenericTypeDefinition() == IEventHandler<>.class);
	}
	private static java.lang.Class GetEventType(java.lang.Class eventHandlerInterface)
	{
		return eventHandlerInterface.GetGenericArguments().Single();
	}
}