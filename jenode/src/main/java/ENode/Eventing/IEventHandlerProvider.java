package ENode.Eventing;

/** Represents a provider to provide the event handler information.
 
*/
public interface IEventHandlerProvider
{
	/** Get all the event handlers for the given event type.
	 
	 @param eventType
	 @return 
	*/
	Iterable<IEventHandler> GetEventHandlers(java.lang.Class eventType);
	/** Check whether a given type is a event handler type.
	 
	 @param type
	 @return 
	*/
	boolean IsEventHandler(java.lang.Class type);
}