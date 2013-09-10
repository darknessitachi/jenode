package ENode.Eventing;

import ENode.Messaging.*;

/** Represents a base domain event.
 
*/
public class Event extends Message implements IEvent, Serializable
{
	/** Default constructor.
	 
	*/
	public Event()
	{
		super(Guid.NewGuid());
	}
}