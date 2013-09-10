package ENode.Commanding.Impl;

import ENode.Infrastructure.*;

/** The default implementation of ICommandHandlerProvider.
 
*/
public class DefaultCommandHandlerProvider implements ICommandHandlerProvider, IAssemblyInitializer
{
	private final ConcurrentDictionary<java.lang.Class, ICommandHandler> _commandHandlerDict = new ConcurrentDictionary<java.lang.Class, ICommandHandler>();

	/** Initialize the provider with the given assemblies.
	 
	 @param assemblies The assemblies to initialize.
	*/
	public final void Initialize(Assembly... assemblies)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var assembly : assemblies)
		{
			RegisterAllCommandHandlersInAssembly(assembly);
		}
	}
	/** Get the command handler for the given command.
	 
	 @param command
	 @return 
	*/
	public final ICommandHandler GetCommandHandler(ICommand command)
	{
		ICommandHandler commandHandler = null;
		RefObject<ICommandHandler> tempRef_commandHandler = new RefObject<ICommandHandler>(commandHandler);
		ICommandHandler tempVar = _commandHandlerDict.TryGetValue(command.getClass(), tempRef_commandHandler) ? commandHandler : null;
		commandHandler = tempRef_commandHandler.argvalue;
		return tempVar;
	}
	/** Check whether the given type is a command handler type.
	 
	 @param type
	 @return 
	*/
	public final boolean IsCommandHandler(java.lang.Class type)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return type.IsInterface == false && type.IsAbstract == false && type.GetInterfaces().Any(x => x.IsGenericType && x.GetGenericTypeDefinition() == ICommandHandler<>.class);
	}

	private void RegisterAllCommandHandlersInAssembly(Assembly assembly)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
		for (var commandHandlerType : assembly.GetTypes().Where(IsCommandHandler))
		{
			if (!TypeUtils.IsComponent(commandHandlerType))
			{
				throw new RuntimeException(String.format("%1$s should be marked as component.", commandHandlerType.FullName));
			}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			var handlerTypes = commandHandlerType.GetInterfaces().Where(x => x.IsGenericType && x.GetGenericTypeDefinition() == ICommandHandler<>.class);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var handlerType : handlerTypes)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var commandType = handlerType.GetGenericArguments().Single();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var commandHandlerWrapperType = CommandHandlerWrapper<>.class.MakeGenericType(commandType);
				TService commandHandler = ObjectContainer.Resolve(commandHandlerType);
				Object tempVar = Activator.CreateInstance(commandHandlerWrapperType, new TService[] { commandHandler });
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var commandHandlerWrapper = (ICommandHandler)((tempVar instanceof ICommandHandler) ? tempVar : null);
				RegisterCommandHandler(commandType, commandHandlerWrapper);
			}
		}
	}
	private void RegisterCommandHandler(java.lang.Class commandType, ICommandHandler commandHandler)
	{
		if (_commandHandlerDict.TryAdd(commandType, commandHandler))
		{
			return;
		}

		if (_commandHandlerDict.ContainsKey(commandType))
		{
			throw new DuplicatedCommandHandlerException(commandType, commandHandler.GetInnerCommandHandler().getClass());
		}
		throw new ENodeException("Error occurred when registering {0} for {1} command.", commandHandler.getClass().getName(), commandType.getName());
	}
}