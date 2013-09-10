package ENode.Messaging.Impl;

import ENode.Infrastructure.ObjectContainer;
import ENode.Infrastructure.Logging.ILogger;
import ENode.Infrastructure.Logging.ILoggerFactory;
import ENode.Messaging.IMessage;
import ENode.Messaging.IMessageQueue;
import ENode.Messaging.IMessageStore;

/** The abstract base message queue implementation of IMessageQueue.
 
 <typeparam name="T">The type of the message.</typeparam>
*/
public abstract class MessageQueue<T extends IMessage> implements IMessageQueue<T>
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private IMessageStore _messageStore;
	private final BlockingCollection<T> _queue = new BlockingCollection<T>(new ConcurrentQueue<T>());
	private final ReaderWriterLockSlim _enqueueLocker = new ReaderWriterLockSlim();
	private final ReaderWriterLockSlim _dequeueLocker = new ReaderWriterLockSlim();

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** The name of the queue.
	*/
	private String privateName;
	public final String getName()
	{
		return privateName;
	}
	private void setName(String value)
	{
		privateName = value;
	}
	/** The logger which maybe used by the message queue.
	*/
	private ILogger privateLogger;
	protected final ILogger getLogger()
	{
		return privateLogger;
	}
	private void setLogger(ILogger value)
	{
		privateLogger = value;
	}

	/** Parameterized constructor.
	 
	 @param name The name of the queue.
	 @exception ArgumentNullException Throw when the queue name is null or empty.
	*/
	protected MessageQueue(String name)
	{
		if (tangible.DotNetToJavaStringHelper.isNullOrEmpty(name))
		{
			throw new IllegalArgumentException("name");
		}

		setName(name);
		_messageStore = ObjectContainer.<IMessageStore>Resolve();
		setLogger(ObjectContainer.<ILoggerFactory>Resolve().Create(getClass().getName()));
	}

	/** Initialize the message queue.
	*/
	public final void Initialize()
	{
		_messageStore.Initialize(getName());
		java.util.ArrayList<Object> messages = _messageStore.<T>GetMessages(getName()).ToList();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var message : messages)
		{
			message.MarkAsRestoreFromStorage();
			_queue.Add(message);
			getLogger().InfoFormat("{0} recovered, id:{1}", message.toString(), message.Id);
		}
		OnInitialized(messages);
	}
	/** Called after the messages were recovered from the message store.
	 
	 @param initialQueueMessages
	*/
	protected void OnInitialized(Iterable<T> initialQueueMessages)
	{
	}
	/** Enqueue the given message to the message queue. First add the message to message store, second enqueue the message to memory queue.
	 
	 @param message The message to enqueue.
	*/
	public final void Enqueue(T message)
	{
		_enqueueLocker.AtomWrite(() =>
		{
			_messageStore.AddMessage(getName(), message);
			_queue.Add(message);
			if (getLogger().IsDebugEnabled)
			{
				getLogger().DebugFormat("{0} enqueued, id:{1}", message.toString(), message.Id);
			}
		}
	   );
	}
	/** Dequeue the message from memory queue.
	 
	 @return 
	*/
	public final T Dequeue()
	{
		return _queue.Take();
	}
	/** Remove the message from message store.
	 
	 @param message
	*/
	public final void Complete(T message)
	{
		_dequeueLocker.AtomWrite(() => _messageStore.RemoveMessage(getName(), message));
	}
}
