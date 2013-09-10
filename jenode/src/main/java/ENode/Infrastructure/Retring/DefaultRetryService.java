package ENode.Infrastructure.Retring;

import ENode.Infrastructure.Logging.*;

/** The default implementation of IRetryService;
 
*/
public class DefaultRetryService implements IRetryService
{
	private static final long DefaultPeriod = 5000;
	private final BlockingCollection<ActionInfo> _retryQueue = new BlockingCollection<ActionInfo>(new ConcurrentQueue<ActionInfo>());
	private Timer _timer;
	private ILogger _logger;
	private boolean _looping;

	/** Parameterized constructor.
	 
	 @param loggerFactory
	*/
	public DefaultRetryService(ILoggerFactory loggerFactory)
	{
		_logger = loggerFactory.Create(getClass().getName());
		_timer = new Timer(Loop, null, 0, DefaultPeriod);
	}

	/** Initialize the retry service.
	 
	 @param period
	*/
	public final void Initialize(long period)
	{
		_timer.Change(0, period);
	}
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	public final void TryAction(String actionName, Action action, int maxRetryCount, Action nextAction)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		TryAction(actionName, () =>
		{
			action();
			return true;
		}
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
	   , maxRetryCount, nextAction != null new ActionInfo("NextActionOf" + actionName, obj =>
		{
			nextAction();
			return true;
		}
	   , null, null) : null);
	}
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	public final void TryAction(String actionName, Func<Boolean> action, int maxRetryCount, Action nextAction)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		TryAction(actionName, action, maxRetryCount, nextAction != null new ActionInfo("NextActionOf" + actionName, obj =>
		{
			nextAction();
			return true;
		}
	   , null, null) : null);
	}
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	public final void TryAction(String actionName, Func<Boolean> action, int maxRetryCount, Func<Boolean> nextAction)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		TryAction(actionName, action, maxRetryCount, nextAction != null ? new ActionInfo("NextActionOf" + actionName, obj => nextAction(), null, null) : null);
	}
	/** Try to execute the given action with the given max retry count.
	 If the action execute still failed within the max retry count, then put the action into the retry queue;
	 
	 @param actionName
	 @param action
	 @param maxRetryCount
	 @param nextAction
	*/
	public final void TryAction(String actionName, Func<Boolean> action, int maxRetryCount, ActionInfo nextAction)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		if (TryRecursively(actionName, (x, y, z) => action(), 0, maxRetryCount))
		{
			TryAction(nextAction);
		}
		else
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			_retryQueue.Add(new ActionInfo(actionName, obj => action(), null, nextAction));
		}
	}

	private void Loop(Object data)
	{
		try
		{
			if (_looping)
			{
				return;
			}
			_looping = true;
			TryAction(_retryQueue.Take());
			_looping = false;
		}
		catch (RuntimeException ex)
		{
			_logger.Error("Exception raised when retring action.", ex);
			_looping = false;
		}
	}
	private boolean TryRecursively(String actionName, Func<String, int, int, Boolean> action, int retriedCount, int maxRetryCount)
	{
		boolean success = false;
		try
		{
			success = action(actionName, retriedCount, maxRetryCount);
			if (retriedCount > 0)
			{
				_logger.InfoFormat("Retried action {0} for {1} times.", actionName, retriedCount);
			}
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when tring action %1$s, retrid count %2$s.", actionName, retriedCount), ex);
		}

		if (success)
		{
			return true;
		}
		if (retriedCount < maxRetryCount)
		{
			return TryRecursively(actionName, action, retriedCount + 1, maxRetryCount);
		}
		return false;
	}
	private void TryAction(ActionInfo actionInfo)
	{
		if (actionInfo == null)
		{
			return;
		}
		boolean success = false;
		try
		{
			success = actionInfo.Action(actionInfo.getData());
			_logger.InfoFormat("Executed action {0}.", actionInfo.getName());
		}
		catch (RuntimeException ex)
		{
			_logger.Error(String.format("Exception raised when executing action %1$s.", actionInfo.getName()), ex);
		}
		finally
		{
			if (success)
			{
				if (actionInfo.getNext() != null)
				{
					_retryQueue.Add(actionInfo.getNext());
				}
			}
			else
			{
				_retryQueue.Add(actionInfo);
			}
		}
	}
}