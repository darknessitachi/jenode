package ENode.Messaging;

/** Represents a provider to provide the queue table name.
 
*/
public interface IQueueTableNameProvider
{
	/** Get table for the given queue.
	 
	 @param queueName
	 @return 
	*/
	String GetTable(String queueName);
}