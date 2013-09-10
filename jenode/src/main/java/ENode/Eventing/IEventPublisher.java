package ENode.Eventing;

/** Represents an event publisher to publish the committed event stream to event handlers.
 
*/
public interface IEventPublisher
{
	/** Publish a given committed event stream to all the event handlers.
	 
	*/
	void Publish(EventStream stream);
}