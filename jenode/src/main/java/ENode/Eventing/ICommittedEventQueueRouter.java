package ENode.Eventing;

/** Represents a router to route an available committed event queue for the given event stream.
 
*/
public interface ICommittedEventQueueRouter
{
	/** Route an available committed event queue for the given event stream.
	 
	 @param stream
	 @return 
	*/
	ICommittedEventQueue Route(EventStream stream);
}