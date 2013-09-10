package ENode.Messaging.Impl;

import java.util.ArrayList;

import ENode.Messaging.IMessage;
import ENode.Messaging.IMessageStore;

/** A empty message store which always not store message, which only used when unit testing.
 
*/
public class EmptyMessageStore implements IMessageStore
{
	/** Initialize the message store.
	 
	 @param queueName
	*/
	public final void Initialize(String queueName)
	{
	}
	/** Persist a new message to the queue.
	 
	 @param queueName
	 @param message
	*/
	public final void AddMessage(String queueName, IMessage message)
	{
	}
	/** Remove a existing message from the queue.
	 
	 @param queueName
	 @param message
	*/
	public final void RemoveMessage(String queueName, IMessage message)
	{
	}
	/** Get all the existing messages of the queue.
	 
	 @param queueName
	 <typeparam name="T"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	public final <T extends  IMessage> Iterable<T> GetMessages(String queueName)
	{
		return new ArrayList<T>();
	}
}