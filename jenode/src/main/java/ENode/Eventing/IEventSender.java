package ENode.Eventing;

/** Represents an event sender to send the uncommitted event stream to process asynchronously.
 
*/
public interface IEventSender
{
	/** Send the uncommitted event stream to process asynchronously.
	 
	*/
	void Send(EventStream eventStream);
}