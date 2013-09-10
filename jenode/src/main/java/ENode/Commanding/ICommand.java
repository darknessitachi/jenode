package ENode.Commanding;

import ENode.Messaging.*;

/** Represents a command.
 
*/
public interface ICommand extends IMessage
{
	/** Command executing waiting milliseconds.
	 
	*/
	int getMillisecondsTimeout();
	/** How many times the command should retry.
	 
	*/
	int getRetryCount();
}