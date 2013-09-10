package ENode.Messaging.Impl;

import ENode.Infrastructure.Action;
import ENode.Infrastructure.ObjectContainer;
import ENode.Infrastructure.Worker;
import ENode.Infrastructure.Logging.ILogger;
import ENode.Infrastructure.Logging.ILoggerFactory;
import ENode.Messaging.IMessage;
import ENode.Messaging.IMessageExecutor;
import ENode.Messaging.IMessageProcessor;
import ENode.Messaging.IMessageQueue;

/** The abstract base message processor implementation of IMessageProcessor.
 
 <typeparam name="TQueue">The type of the message queue.</typeparam>
 <typeparam name="TMessageExecutor">The type of the message executor.</typeparam>
 <typeparam name="TMessage">The type of the message.</typeparam>
*/
public abstract class MessageProcessor<TQueue extends IMessageQueue<TMessage>, TMessageExecutor extends IMessageExecutor<TMessage>, TMessage extends IMessage> implements IMessageProcessor<TQueue, TMessage>
{
	private java.util.List<Worker> _workers;
	private TQueue _bindingQueue;
	private ILogger _logger;
	private boolean _started;

	/** The binding queue of the message processor.
	*/
	public final TQueue getBindingQueue()
	{
		return _bindingQueue;
	}

	/** Parameterized constructor.
	 
	 @param bindingQueue
	 @param messageExecutorCount
	 @exception ArgumentNullException
	 @exception Exception
	*/
	protected MessageProcessor(TQueue bindingQueue, int messageExecutorCount)
	{
		if (bindingQueue == null)
		{
			throw new IllegalArgumentException("bindingQueue");
		}
		if (messageExecutorCount <= 0)
		{
			throw new RuntimeException(String.format("There must at least one message executor for %1$s.", getClass().getName()));
		}

		_bindingQueue = bindingQueue;
		_workers = new java.util.ArrayList<Worker>();

		final Class<TMessageExecutor> TMessageExecutorClass = null;
		
		for (int index = 0; index < messageExecutorCount; index++)
		{
			_workers.add(new Worker(new Action() {
				
				@Override
				public void execute() {
					ProcessMessage(ObjectContainer.Resolve(TMessageExecutorClass));
				}
			}));
		}

		_logger = ObjectContainer.Resolve(ILoggerFactory.class).Create(getClass().getName());
		_started = false;
	}

	/** Initialize the message processor.
	*/
	public final void Initialize()
	{
		_bindingQueue.Initialize();
	}
	/** Start the message processor.
	*/
	public final void Start()
	{
		if (_started)
		{
			return;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (Worker worker : _workers)
		{
			worker.Start();
		}
		_started = true;
		_logger.InfoFormat("Processor started, binding queue {0}, worker count:{1}.", _bindingQueue.getName(), _workers.size());
	}

	private void ProcessMessage(TMessageExecutor messageExecutor)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		TMessage message = _bindingQueue.Dequeue();
		if (message == null)
		{
			return;
		}
		try
		{
			messageExecutor.Execute(message, _bindingQueue);
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when handling queue message:%1$s.", message), ex);
		}
	}
}