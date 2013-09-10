package ENode;

import ENode.Commanding.*;
import ENode.Commanding.Impl.*;
import ENode.Domain.*;
import ENode.Domain.Impl.*;
import ENode.Eventing.*;
import ENode.Eventing.Impl.*;
import ENode.Eventing.Impl.InMemory.*;
import ENode.Eventing.Impl.SQL.*;
import ENode.Infrastructure.*;
import ENode.Infrastructure.Logging.*;
import ENode.Infrastructure.Retring.*;
import ENode.Infrastructure.Serializing.*;
import ENode.Infrastructure.Sql.*;
import ENode.Messaging.*;
import ENode.Messaging.Impl.*;
import ENode.Messaging.Impl.SQL.*;
import ENode.Snapshoting.*;
import ENode.Snapshoting.Impl.*;

/** ENode framework global configuration entry point.
 
*/
public class Configuration
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Vairables

	private java.util.List<java.lang.Class> _assemblyInitializerServiceTypes;
	private java.util.List<ICommandProcessor> _commandProcessors;
	private ICommandProcessor _retryCommandProcessor;
	private java.util.List<IUncommittedEventProcessor> _uncommittedEventProcessors;
	private java.util.List<ICommittedEventProcessor> _committedEventProcessors;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** The single access point of the configuration.
	 
	*/
	private static Configuration privateInstance;
	public static Configuration getInstance()
	{
		return privateInstance;
	}
	private static void setInstance(Configuration value)
	{
		privateInstance = value;
	}

	/** Get all the command queues.
	 
	*/
	public final Iterable<ICommandQueue> GetCommandQueues()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _commandProcessors.Select(x => x.BindingQueue);
	}
	/** Get the retry command queue.
	 
	*/
	public final ICommandQueue GetRetryCommandQueue()
	{
		if (_retryCommandProcessor == null)
		{
			throw new RuntimeException("The command queue for command retring is not configured.");
		}
		return _retryCommandProcessor.BindingQueue;
	}
	/** Get all the uncommitted event queues.
	 
	*/
	public final Iterable<IUncommittedEventQueue> GetUncommitedEventQueues()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _uncommittedEventProcessors.Select(x => x.BindingQueue);
	}
	/** Get all the committed event queues.
	 
	*/
	public final Iterable<ICommittedEventQueue> GetCommitedEventQueues()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return _committedEventProcessors.Select(x => x.BindingQueue);
	}

	/** Private constructor, for implementation of singleton pattern.
	 
	*/
	private Configuration()
	{
		_assemblyInitializerServiceTypes = new java.util.ArrayList<java.lang.Class>();
		_commandProcessors = new java.util.ArrayList<ICommandProcessor>();
		_uncommittedEventProcessors = new java.util.ArrayList<IUncommittedEventProcessor>();
		_committedEventProcessors = new java.util.ArrayList<ICommittedEventProcessor>();
	}

	/** Create a new instance of configuration.
	 
	 @return 
	*/
	public static Configuration Create()
	{
		if (getInstance() != null)
		{
			throw new RuntimeException("Could not create configuration instance twice.");
		}
		setInstance(new Configuration());
		return getInstance();
	}

	/** Register a implementer type as a service implementation.
	 
	 <typeparam name="TService">The service type.</typeparam>
	 <typeparam name="TImplementer">The implementer type.</typeparam>
	 @param life The life cycle of the implementer type.
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public Configuration Register<TService, TImplementer>(LifeStyle life = LifeStyle.Singleton) where TService : class where TImplementer : class, TService
	public final <TService extends class, TImplementer extends class & TService> Configuration Register(LifeStyle life)
	{
		ObjectContainer.<TService, TImplementer>Register(life);
		if (IsAssemblyInitializer<TImplementer>())
		{
			_assemblyInitializerServiceTypes.add(TService.class);
		}
		return this;
	}
	/** Set the default service instance.
	 
	 The life cycle of the instance is singleton.
	 
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <TService extends class, TImplementer extends class & TService> Configuration SetDefault(TImplementer instance)
	{
		ObjectContainer.<TService, TImplementer>RegisterInstance(instance);
		if (IsAssemblyInitializer(instance))
		{
			_assemblyInitializerServiceTypes.add(TService.class);
		}
		return this;
	}
	/** Register all the default components of enode framework.
	 
	*/
	public final Configuration RegisterFrameworkComponents()
	{
		Register<ILoggerFactory, EmptyLoggerFactory>();
		Register<IBinarySerializer, DefaultBinarySerializer>();
		Register<IStringSerializer, DefaultStringSerializer>();
		Register<IDbConnectionFactory, DefaultDbConnectionFactory>();
		Register<IMessageStore, EmptyMessageStore>();

		Register<IAggregateRootTypeProvider, DefaultAggregateRootTypeProvider>();
		Register<IAggregateRootInternalHandlerProvider, DefaultAggregateRootInternalHandlerProvider>();
		Register<IAggregateRootFactory, DefaultAggregateRootFactory>();
		Register<IMemoryCache, DefaultMemoryCache>();
		Register<IRepository, EventSourcingRepository>();
		Register<IMemoryCacheRebuilder, DefaultMemoryCacheRebuilder>();

		Register<ISnapshotter, DefaultSnapshotter>();
		Register<ISnapshotPolicy, NoSnapshotPolicy>();
		Register<ISnapshotStore, EmptySnapshotStore>();

		Register<ICommandHandlerProvider, DefaultCommandHandlerProvider>();
		Register<ICommandQueueRouter, DefaultCommandQueueRouter>();
		Register<IProcessingCommandCache, DefaultProcessingCommandCache>();
		Register<ICommandAsyncResultManager, DefaultCommandAsyncResultManager>();
		Register<ICommandService, DefaultCommandService>();
		Register<IRetryCommandService, DefaultRetryCommandService>();

		Register<IEventHandlerProvider, DefaultEventHandlerProvider>();
		Register<IEventSynchronizerProvider, DefaultEventSynchronizerProvider>();
		Register<IEventStore, InMemoryEventStore>();
		Register<IEventPublishInfoStore, InMemoryEventPublishInfoStore>();
		Register<IEventHandleInfoStore, InMemoryEventHandleInfoStore>();
		Register<IUncommittedEventQueueRouter, DefaultUncommittedEventQueueRouter>();
		Register<ICommittedEventQueueRouter, DefaultCommittedEventQueueRouter>();
		Register<IEventTableNameProvider, AggregatePerEventTableNameProvider>();
		Register<IEventSender, DefaultEventSender>();
		Register<IEventPublisher, DefaultEventPublisher>();

		Register<IRetryService, DefaultRetryService>(LifeStyle.Transient);
		Register<ICommandContext, DefaultCommandContext>(LifeStyle.Transient);
		Register<ICommandExecutor, DefaultCommandExecutor>(LifeStyle.Transient);
		Register<IUncommittedEventExecutor, DefaultUncommittedEventExecutor>(LifeStyle.Transient);
		Register<ICommittedEventExecutor, DefaultCommittedEventExecutor>(LifeStyle.Transient);

		return this;
	}
	/** Register all the business components from the given assemblies.
	 
	*/
	public final Configuration RegisterBusinessComponents(Assembly... assemblies)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var assembly : assemblies)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
			for (var type : assembly.GetTypes().Where(TypeUtils.IsComponent))
			{
				LifeStyle life = ParseLife(type);
				ObjectContainer.RegisterType(type, life);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				for (var interfaceType : type.GetInterfaces())
				{
					ObjectContainer.RegisterType(interfaceType, type, life);
				}
				if (IsAssemblyInitializer(type))
				{
					_assemblyInitializerServiceTypes.add(type);
				}
			}
		}
		return this;
	}
	/** Use SQL DB as the storage of the whole framework.
	 
	 @param connectionString The connection string of the DB.
	 @return 
	*/
	public final Configuration UseSql(String connectionString)
	{
		return UseSql(connectionString, "Event", null, "EventPublishInfo", "EventHandleInfo");
	}
	/** Use SQL DB as the storage of the whole framework.
	 
	 @param connectionString The connection string of the DB.
	 @param eventTable The table used to store all the domain events.
	 @param queueNameFormat The format of the queue name.
	 @param eventPublishInfoTable The table used to store all the event publish information.
	 @param eventHandleInfoTable The table used to store all the event handle information.
	 @return 
	*/
	public final Configuration UseSql(String connectionString, String eventTable, String queueNameFormat, String eventPublishInfoTable, String eventHandleInfoTable)
	{
		SetDefault<IEventTableNameProvider, DefaultEventTableNameProvider>(new DefaultEventTableNameProvider(eventTable));
		SetDefault<IQueueTableNameProvider, DefaultQueueTableNameProvider>(new DefaultQueueTableNameProvider(queueNameFormat));
		SetDefault<IMessageStore, SqlMessageStore>(new SqlMessageStore(connectionString));
		SetDefault<IEventStore, SqlEventStore>(new SqlEventStore(connectionString));
		SetDefault<IEventPublishInfoStore, SqlEventPublishInfoStore>(new SqlEventPublishInfoStore(connectionString, eventPublishInfoTable));
		SetDefault<IEventHandleInfoStore, SqlEventHandleInfoStore>(new SqlEventHandleInfoStore(connectionString, eventHandleInfoTable));
		return this;
	}
	/** Use the default sql querydb connection factory.
	 
	 @param connectionString The connection string of the SQL DB.
	 @return 
	*/
	public final Configuration UseDefaultSqlQueryDbConnectionFactory(String connectionString)
	{
		SetDefault<ISqlQueryDbConnectionFactory, DefaultSqlQueryDbConnectionFactory>(new DefaultSqlQueryDbConnectionFactory(connectionString));
		return this;
	}

	/** Add a command processor.
	 
	 @param commandProcessor
	 @return 
	*/
	public final Configuration AddCommandProcessor(ICommandProcessor commandProcessor)
	{
		_commandProcessors.add(commandProcessor);
		return this;
	}
	/** Set the command processor to process the retried command.
	 
	 @param commandProcessor
	 @return 
	*/
	public final Configuration SetRetryCommandProcessor(ICommandProcessor commandProcessor)
	{
		_retryCommandProcessor = commandProcessor;
		return this;
	}
	/** Add an uncommitted event processor.
	 
	 @param eventProcessor
	 @return 
	*/
	public final Configuration AddUncommittedEventProcessor(IUncommittedEventProcessor eventProcessor)
	{
		_uncommittedEventProcessors.add(eventProcessor);
		return this;
	}
	/** Add a committed event processor.
	 
	 @param eventProcessor
	 @return 
	*/
	public final Configuration AddCommittedEventProcessor(ICommittedEventProcessor eventProcessor)
	{
		_committedEventProcessors.add(eventProcessor);
		return this;
	}
	/** Create all the message processors with the default queue names at once.
	 
	 @return 
	*/
	public final Configuration CreateAllDefaultProcessors()
	{
		return CreateAllDefaultProcessors(new String[] { "CommandQueue" }, "RetryCommandQueue", new String[] { "UncommittedEventQueue" }, new String[] { "CommittedEventQueue" });
	}

	/** Create all the message processors with the given queue names at once.
	 
	 @param commandQueueNames Represents the command queue names.
	 @param retryCommandQueueName Represents the retry command queue name.
	 @param uncommittedEventQueueNames Represents the uncommitted event queue names.
	 @param committedEventQueueNames Represents the committed event queue names.
	 @param option The message processor creation option.
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public Configuration CreateAllDefaultProcessors(IEnumerable<string> commandQueueNames, string retryCommandQueueName, IEnumerable<string> uncommittedEventQueueNames, IEnumerable<string> committedEventQueueNames, MessageProcessorOption option = null)
	public final Configuration CreateAllDefaultProcessors(Iterable<String> commandQueueNames, String retryCommandQueueName, Iterable<String> uncommittedEventQueueNames, Iterable<String> committedEventQueueNames, MessageProcessorOption option)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var messageProcessorOption = (option != null) ? option : MessageProcessorOption.Default;

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var queueName : commandQueueNames)
		{
			_commandProcessors.add(new DefaultCommandProcessor(new DefaultCommandQueue(queueName), messageProcessorOption.CommandExecutorCount));
		}
		_retryCommandProcessor = new DefaultCommandProcessor(new DefaultCommandQueue(retryCommandQueueName), messageProcessorOption.RetryCommandExecutorCount);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var queueName : uncommittedEventQueueNames)
		{
			_uncommittedEventProcessors.add(new DefaultUncommittedEventProcessor(new DefaultUncommittedEventQueue(queueName), messageProcessorOption.UncommittedEventExecutorCount));
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var queueName : committedEventQueueNames)
		{
			_committedEventProcessors.add(new DefaultCommittedEventProcessor(new DefaultCommittedEventQueue(queueName), messageProcessorOption.CommittedEventExecutorCount));
		}

		return this;
	}
	/** Initialize all the assembly initializers with the given assemblies.
	 
	 @return 
	*/
	public final Configuration Initialize(Assembly... assemblies)
	{
		ValidateMessages(assemblies);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var assemblyInitializer : _assemblyInitializerServiceTypes.Select(ObjectContainer.Resolve).<IAssemblyInitializer>OfType())
		{
			assemblyInitializer.Initialize(assemblies);
		}
		return this;
	}
	/** Start the enode framework.
	 
	 @return 
	*/
	public final Configuration Start()
	{
		ValidateProcessors();
		InitializeProcessors();
		StartProcessors();
		ObjectContainer.<ILoggerFactory>Resolve().Create(getClass().getName()).Info("enode framework started...");

		return this;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Methods

	private static void ValidateMessages(Assembly... assemblies)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var assembly : assemblies)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			for (var type : assembly.GetTypes().Where(x => x.IsClass && IMessage.class.IsAssignableFrom(x)))
			{
				if (!type.IsSerializable)
				{
					throw new RuntimeException(String.format("%1$s should be marked as serializable.", type.FullName));
				}
			}
		}
	}
	private void ValidateProcessors()
	{
		if (_commandProcessors.isEmpty())
		{
			throw new RuntimeException("Command processor count cannot be zero.");
		}
		if (_retryCommandProcessor == null)
		{
			throw new RuntimeException("Retry command processor count cannot be null.");
		}
		if (_uncommittedEventProcessors.isEmpty())
		{
			throw new RuntimeException("Uncommitted event processor count cannot be zero.");
		}
		if (_committedEventProcessors.isEmpty())
		{
			throw new RuntimeException("Committed event processor count cannot be zero.");
		}
	}
	private void InitializeProcessors()
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var commandProcessor : _commandProcessors)
		{
			commandProcessor.Initialize();
		}
		_retryCommandProcessor.Initialize();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var eventProcessor : _uncommittedEventProcessors)
		{
			eventProcessor.Initialize();
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var eventProcessor : _committedEventProcessors)
		{
			eventProcessor.Initialize();
		}
	}
	private void StartProcessors()
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var commandProcessor : _commandProcessors)
		{
			commandProcessor.Start();
		}
		_retryCommandProcessor.Start();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var eventProcessor : _uncommittedEventProcessors)
		{
			eventProcessor.Start();
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var eventProcessor : _committedEventProcessors)
		{
			eventProcessor.Start();
		}
	}

	private static LifeStyle ParseLife(java.lang.Class type)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var componentAttributes = type.GetCustomAttributes(ComponentAttribute.class, false);
		return !componentAttributes.Any() ? LifeStyle.Transient : ((ComponentAttribute) componentAttributes[0]).getLifeStyle();
	}
	private static <T> boolean IsAssemblyInitializer()
	{
		return IsAssemblyInitializer(T.class);
	}
	private static boolean IsAssemblyInitializer(java.lang.Class type)
	{
		return type.IsClass && !type.IsAbstract && IAssemblyInitializer.class.IsAssignableFrom(type);
	}
	private static boolean IsAssemblyInitializer(Object instance)
	{
		return instance instanceof IAssemblyInitializer;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion
}