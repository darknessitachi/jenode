package ENode.Infrastructure;

import ENode.Infrastructure.Logging.*;

/** Represent a background worker that will repeatedly execute a specific method.
 
*/
public class Worker
{
	private boolean _stopped;
	private Action _action;
	private Thread _thread;
	private ILogger _logger;

	/** Return the IsAlive status of the current worker.
	 
	*/
	public final boolean getIsAlive()
	{
		return _thread.isAlive();
	}

	/** Initialize a new Worker for the specified method to run.
	 
	 @param action The delegate method to execute in a loop.
	*/
	public Worker(Action action)
	{
		_action = action;
		Thread tempVar = new Thread();
//		tempVar.run/
//		tempVar.IsBackground = true;
		_thread = tempVar;
		_thread.setName(String.format("Worker thread %1$s", _thread.getId()));
		
		_logger = ObjectContainer.Resolve(ILoggerFactory.class).Create(_thread.getName());
	}

	/** Start the worker.
	 
	*/
	public final Worker Start()
	{
		if (!_thread.isAlive())
		{
			_thread.start();
		}
		return this;
	}
	/** Stop the worker.
	 
	*/
	public final Worker Stop()
	{
		_stopped = true;
		return this;
	}

	/** Executes the delegate method until the <see cref="Stop"/> method is called.
	 
	*/
	private void Loop()
	{
		while (!_stopped)
		{
			try
			{
				_action.execute();
			}
			catch (RuntimeException abortException)
			{
				_logger.Error("caught ThreadAbortException - resetting.", abortException);
				Thread.interrupted();
				_logger.Info("ThreadAbortException resetted.");
//			}
//			catch (RuntimeException ex)
//			{
				_logger.Error("Exception raised when executing worker delegate.", abortException);
			}
		}
	}
}