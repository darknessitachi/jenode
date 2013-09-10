package ENode.Eventing.Impl;

import ENode.Infrastructure.*;

/** The default implementation of IEventSynchronizerProvider.
 
*/
public class DefaultEventSynchronizerProvider implements IEventSynchronizerProvider, IAssemblyInitializer
{
	private final java.util.Map<java.lang.Class, java.util.List<IEventSynchronizer>> _eventSynchronizerDict = new java.util.HashMap<java.lang.Class, java.util.List<IEventSynchronizer>>();

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
			for (var synchronizerType : assembly.GetTypes().Where(IsEventSynchronizer))
			{
				if (!TypeUtils.IsComponent(synchronizerType))
				{
					throw new RuntimeException(String.format("%1$s should be marked as component.", synchronizerType.FullName));
				}
				RegisterSynchronizer(synchronizerType);
			}
		}
	}

	/** Get all the event synchronizers for the given event type.
	 
	 @param eventType
	 @return 
	*/
	public final Iterable<IEventSynchronizer> GetSynchronizers(java.lang.Class eventType)
	{
		java.util.ArrayList<IEventSynchronizer> eventSynchronizers = new java.util.ArrayList<IEventSynchronizer>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		for (var key : _eventSynchronizerDict.keySet().Where(key => key.IsAssignableFrom(eventType)))
		{
			eventSynchronizers.addAll(_eventSynchronizerDict.get(key));
		}
		return eventSynchronizers;
	}

	private void RegisterSynchronizer(java.lang.Class synchronizerType)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var synchronizerInterface : ScanSynchronizerInterfaces(synchronizerType))
		{
			java.lang.Class eventType = GetEventType(synchronizerInterface);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var synchronizerWrapperType = EventSynchronizerWrapper<>.class.MakeGenericType(eventType);
			java.util.List<IEventSynchronizer> eventSynchronizers = null;
			if (!((eventSynchronizers = _eventSynchronizerDict.get(eventType)) != null))
			{
				eventSynchronizers = new java.util.ArrayList<IEventSynchronizer>();
				_eventSynchronizerDict.put(eventType, eventSynchronizers);
			}

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			if (eventSynchronizers.Any(x => x.GetInnerSynchronizer().getClass() == synchronizerType))
			{
				continue;
			}

			TService synchronizer = ObjectContainer.Resolve(synchronizerType);
			Object tempVar = Activator.CreateInstance(synchronizerWrapperType, new TService[] { synchronizer });
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var synchronizerWrapper = (IEventSynchronizer)((tempVar instanceof IEventSynchronizer) ? tempVar : null);
			eventSynchronizers.add(synchronizerWrapper);
		}
	}
	private static boolean IsEventSynchronizer(java.lang.Class type)
	{
		return type.IsClass && !type.IsAbstract && ScanSynchronizerInterfaces(type).Any();
	}
	private static Iterable<java.lang.Class> ScanSynchronizerInterfaces(java.lang.Class type)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return type.GetInterfaces().Where(x => x.IsGenericType && x.GetGenericTypeDefinition() == IEventSynchronizer<>.class);
	}
	private static java.lang.Class GetEventType(java.lang.Class synchronizerInterface)
	{
		return synchronizerInterface.GetGenericArguments().Single();
	}
}