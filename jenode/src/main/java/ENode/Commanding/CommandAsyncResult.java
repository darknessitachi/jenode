package ENode.Commanding;

import ENode.Infrastructure.*;

/** Represents the command execution async result.
 
*/
public class CommandAsyncResult
{
	private ManualResetEvent _waitHandle;
	private Action<CommandAsyncResult> _callback;

	/** Represents whether the command execution is completed.
	 
	*/
	private boolean privateIsCompleted;
	public final boolean getIsCompleted()
	{
		return privateIsCompleted;
	}
	private void setIsCompleted(boolean value)
	{
		privateIsCompleted = value;
	}
	/** Represents the id of aggregate root which was created or updated by the command.
	 Can be null if the command not effect any aggregate root.
	 
	*/
	private String privateAggregateRootId;
	public final String getAggregateRootId()
	{
		return privateAggregateRootId;
	}
	private void setAggregateRootId(String value)
	{
		privateAggregateRootId = value;
	}
	/** Error message generated when executing the command.
	 
	*/
	private ErrorInfo privateErrorInfo;
	public final ErrorInfo getErrorInfo()
	{
		return privateErrorInfo;
	}
	private void setErrorInfo(ErrorInfo value)
	{
		privateErrorInfo = value;
	}

	/** Parameterized constructor.
	 
	 @param waitHandle
	*/
	public CommandAsyncResult(ManualResetEvent waitHandle)
	{
		if (waitHandle == null)
		{
			throw new ArgumentNullException("waitHandle");
		}
		_waitHandle = waitHandle;
	}
	/** Parameterized constructor.
	 
	 @param callback
	*/
	public CommandAsyncResult(Action<CommandAsyncResult> callback)
	{
		_callback = callback;
	}

	/** Complete the command execution async result.
	 
	 @param aggregateRootId
	 @param errorInfo
	*/
	public final void Complete(String aggregateRootId, ErrorInfo errorInfo)
	{
		setIsCompleted(true);
		setAggregateRootId(aggregateRootId);
		setErrorInfo(errorInfo);

		if (_waitHandle != null)
		{
			_waitHandle.Set();
		}
		else if (_callback != null)
		{
			_callback(this);
		}
	}
}