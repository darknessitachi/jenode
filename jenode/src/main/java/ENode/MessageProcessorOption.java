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

/** Represents an option when creating the message processors.
 
*/
public class MessageProcessorOption
{
	/** Represents the default message processor option.
	 
	*/
	public static final MessageProcessorOption Default = new MessageProcessorOption();

	/** The command executor count.
	 
	*/
	private int privateCommandExecutorCount;
	public final int getCommandExecutorCount()
	{
		return privateCommandExecutorCount;
	}
	public final void setCommandExecutorCount(int value)
	{
		privateCommandExecutorCount = value;
	}
	/** The retry command executor count.
	 
	*/
	private int privateRetryCommandExecutorCount;
	public final int getRetryCommandExecutorCount()
	{
		return privateRetryCommandExecutorCount;
	}
	public final void setRetryCommandExecutorCount(int value)
	{
		privateRetryCommandExecutorCount = value;
	}
	/** The uncommitted event executor count.
	 
	*/
	private int privateUncommittedEventExecutorCount;
	public final int getUncommittedEventExecutorCount()
	{
		return privateUncommittedEventExecutorCount;
	}
	public final void setUncommittedEventExecutorCount(int value)
	{
		privateUncommittedEventExecutorCount = value;
	}
	/** The committed event executor count.
	 
	*/
	private int privateCommittedEventExecutorCount;
	public final int getCommittedEventExecutorCount()
	{
		return privateCommittedEventExecutorCount;
	}
	public final void setCommittedEventExecutorCount(int value)
	{
		privateCommittedEventExecutorCount = value;
	}

	/** Parameterized constructor.
	 
	 @param commandExecutorCount
	 @param retryCommandExecutorCount
	 @param uncommittedEventExecutorCount
	 @param committedEventExecutorCount
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public MessageProcessorOption(int commandExecutorCount = 1, int retryCommandExecutorCount = 1, int uncommittedEventExecutorCount = 1, int committedEventExecutorCount = 1)
	public MessageProcessorOption(int commandExecutorCount, int retryCommandExecutorCount, int uncommittedEventExecutorCount, int committedEventExecutorCount)
	{
		setCommandExecutorCount(commandExecutorCount);
		setRetryCommandExecutorCount(retryCommandExecutorCount);
		setUncommittedEventExecutorCount(uncommittedEventExecutorCount);
		setCommittedEventExecutorCount(committedEventExecutorCount);
	}
}