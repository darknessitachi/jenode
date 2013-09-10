package ENode.Eventing;

/** Represents a router to route a available uncommitted event queue for event stream message.
 
*/
public interface IUncommittedEventQueueRouter
{
	/** Route a available uncommitted event queue for the given event stream message.
	 
	 @param stream
	 @return 
	*/
	IUncommittedEventQueue Route(EventStream stream);
}