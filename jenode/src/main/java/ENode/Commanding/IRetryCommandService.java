package ENode.Commanding;

import ENode.Eventing.*;
import ENode.Infrastructure.*;

/** Represents a command retry service.
 
*/
public interface IRetryCommandService
{
	/** Retry the given command.
	 
	*/
	void RetryCommand(CommandInfo commandInfo, EventStream eventStream, ErrorInfo errorInfo, Action retrySuccessCallbackAction);
}